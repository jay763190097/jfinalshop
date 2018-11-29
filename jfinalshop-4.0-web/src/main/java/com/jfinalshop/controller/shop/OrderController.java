package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.*;
import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReceiverService;
import com.jfinalshop.service.ShippingMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller - 订单
 * 
 * 
 */
@ControllerBind(controllerKey = "/order")
@Before({ThemeInterceptor.class, MemberInterceptor.class})
public class OrderController extends BaseController {
  private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Inject
	private ProductService productService;
	@Inject
	private MemberService memberService;
	@Inject
	private AreaService areaService;
	@Inject
	private ReceiverService receiverService;
	@Inject
	private CartService cartService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private OrderService orderService;
	@Inject
	private PluginService pluginService;
	@Inject
	private SnDao snDao;
	/**
	 * 检查积分兑换
	 */
	public void checkExchange() {
		Long productId = getParaToLong("productId"); 
		Integer quantity = getParaToInt("quantity");
		if (quantity == null || quantity < 1) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Product product = productService.find(productId);
		if (product == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (!product.getIsMarketable()) {
			renderJson("shop.order.productNotMarketable");
			return;
		}
		if (quantity > product.getAvailableStock()) {
			renderJson(Message.warn("shop.order.productLowStock"));
			return;
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			renderJson(Message.warn("shop.order.lowPoint"));
			return;
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 保存收货地址
	 */
	public void saveReceiver() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		
		Map<String, Object> data = new HashMap<String, Object>();
		Area area = areaService.find(areaId);
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}
		
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			data.put("message", Message.error("shop.order.addReceiverCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			renderJson(data);
			return;
		}
		receiver.setMemberId(member.getId());
		receiverService.save(receiver);
		data.put("message", SUCCESS_MESSAGE);
		data.put("id", receiver.getId());
		data.put("consignee", receiver.getConsignee());
		data.put("areaName", receiver.getAreaName());
		data.put("address", receiver.getAddress());
		data.put("zipCode", receiver.getZipCode());
		data.put("phone", receiver.getPhone());
		renderJson(data);
	}

	/**
	 * 订单锁定
	 */
	public void lock() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		Member member = memberService.getCurrent();
		if (order != null && member.equals(order.getMember()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0) {
			orderService.lock(order, member);
		}
	}

	/**
	 * 检查等待付款
	 */
	public void checkPendingPayment() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		Member member = memberService.getCurrent();
		renderJson(order != null && member.equals(order.getMember()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0);
	}

	/**
	 * 检查优惠券
	 */
	public void checkCoupon() {
		String channel=PropKit.get("channelcode");
		String code = getPara("code");
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (!cart.isCouponAllowed()) {
			data.put("message", Message.warn("shop.order.couponNotAllowed"));
			renderJson(data);
			return;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null) {
			Coupon coupon = couponCode.getCoupon();
			if (couponCode.getIsUsed()) {
				data.put("message", Message.warn("shop.order.couponCodeUsed"));
				renderJson(data);
				return;
			}
			if (!coupon.getIsEnabled()) {
				data.put("message", Message.warn("shop.order.couponDisabled"));
				renderJson(data);
				return;
			}
			if (!coupon.hasBegun()) {
				data.put("message", Message.warn("shop.order.couponNotBegin"));
				renderJson(data);
				return;
			}
			if (coupon.hasExpired()) {
				data.put("message", Message.warn("shop.order.couponHasExpired"));
				renderJson(data);
				return;
			}
			if (!cart.isValid(coupon)) {
				data.put("message", Message.warn("shop.order.couponInvalid"));
				renderJson(data);
				return;
			}
			data.put("message", SUCCESS_MESSAGE);
			data.put("couponName", coupon.getName());
			renderJson(data);
		} else {
			data.put("message", Message.warn("shop.order.couponCodeNotExist"));
			renderJson(data);
		}
	}

	/**
	 * 普通订单-结算
	 */
	public void checkout() {
		String channel=PropKit.get("channelcode");
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			redirect("/cart/list.jhtml");
			return;
		}

		Member member = memberService.getCurrent();
		Receiver defaultReceiver = receiverService.findDefault(member);
		Order order = orderService.generate(Order.Type.general, cart, defaultReceiver, null, null, null, null, null, channel);
		setAttr("order", order);
		setAttr("member", member);
		setAttr("defaultReceiver", defaultReceiver);
		setAttr("cartToken", cart.getToken());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/shop/${theme}/order/checkout.ftl");
	}

	/**
	 * 兑换订单-结算
	 */
	public void checkoutExchange() {
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		if (quantity == null || quantity < 1) {
			redirect(ERROR_VIEW);
			return;
		}
		Product product = productService.find(productId);
		if (product == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!product.getIsMarketable()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (quantity > product.getAvailableStock()) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			redirect(ERROR_VIEW);
			return;
		}
		List<CartItem> cartItems = new ArrayList<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		Receiver defaultReceiver = receiverService.findDefault(member);
		Order order = orderService.generate(Order.Type.exchange, cart, defaultReceiver, null, null, null, null, null, null);
		setAttr("productId", productId);
		setAttr("quantity", quantity);
		setAttr("order", order);
		setAttr("member", member);
		setAttr("defaultReceiver", defaultReceiver);
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/shop/${theme}/order/checkout.ftl");
	}
	
	/**
	 * 普通订单-计算
	 */
	public void calculate() {
		String typeName = getPara("type");
		Order.Type type = StrKit.notBlank(typeName) ? Order.Type.valueOf(typeName) : null;
		
		Map<String, Object> data = new HashMap<String, Object>();
		if (Order.Type.exchange.equals(type)) {
			data = calculateExchange();
		} else {
			data = calculateGeneral();
		}
		renderJson(data);
	}

	/**
	 * 普通订单-计算
	 */
	private Map<String, Object> calculateGeneral() {
		String channel=PropKit.get("channelcode");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		String invoiceTitle = getPara("invoiceTitle");
		String balanceValue = getPara("balance");
		BigDecimal balance = StrKit.notBlank(balanceValue) ? new BigDecimal(balanceValue) : null;
		String memo = getPara("memo");
		
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !member.equals(receiver.getMember())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}

		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		CouponCode couponCode = couponCodeService.findByCode(code);
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		Order order = orderService.generate(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);
		data.put("message", SUCCESS_MESSAGE);
		data.put("price", order.getPrice());
		data.put("fee", order.getFee());
		data.put("freight", order.getFreight());
		data.put("tax", order.getTax());
		data.put("promotionDiscount", order.getPromotionDiscount());
		data.put("couponDiscount", order.getCouponDiscount());
		data.put("amount", order.getAmount());
		data.put("amountPayable", order.getAmountPayable());
		return data;
	}

	/**
	 * 兑换订单-计算
	 */
	private Map<String, Object> calculateExchange() {
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String balanceValue = getPara("balance");
		BigDecimal balance = StrKit.notBlank(balanceValue) ? new BigDecimal(balanceValue) : null;
		String memo = getPara("memo");
		
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !member.equals(receiver.getMember())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		List<CartItem> cartItems = new ArrayList<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		Order order = orderService.generate(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, null, null, balance, memo);

		data.put("message", SUCCESS_MESSAGE);
		data.put("price", order.getPrice());
		data.put("fee", order.getFee());
		data.put("freight", order.getFreight());
		data.put("tax", order.getTax());
		data.put("promotionDiscount", order.getPromotionDiscount());
		data.put("couponDiscount", order.getCouponDiscount());
		data.put("amount", order.getAmount());
		data.put("amountPayable", order.getAmountPayable());
		return data;
	}

	/**
	 * 订单-创建
	 */
	public void create() {
		String typeName = getPara("type");
		Order.Type type = StrKit.notBlank(typeName) ? Order.Type.valueOf(typeName) : null;
		if (Order.Type.exchange.equals(type)) {
			createExchange();
		} else {
			createGeneral();
		}
	}
	
	/**
	 * 普通订单-创建
	 */
	private void createGeneral() {
		String cartToken = getPara("cartToken");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		String invoiceTitle = getPara("invoiceTitle");
		String balanceValue = getPara("balance");
		BigDecimal balance = StrKit.notBlank(balanceValue) ? new BigDecimal(balanceValue) : null;
		String memo = getPara("memo");
		String channel = getPara("channel", PropKit.get("channelcode"));
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (!StringUtils.equals(cart.getToken(), cartToken)) {
			data.put("message", Message.warn("shop.order.cartHasChanged"));
			renderJson(data);
			return;
		}
		if (cart.hasNotMarketable()) {
			data.put("message", Message.warn("shop.order.hasNotMarketable"));
			renderJson(data);
			return;
		}
		if (cart.getIsLowStock()) {
			data.put("message", Message.warn("shop.order.cartLowStock"));
			renderJson(data);
			return;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (cart.getIsDelivery()) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.equals(receiver.getMember())) {
				data.put("message", ERROR_MESSAGE);
				renderJson(data);
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				data.put("message", ERROR_MESSAGE);
				renderJson(data);
				return;
			}
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && !cart.isValid(couponCode)) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			renderJson(data);
			return;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		Order order = orderService.create(Order.Type.general, Order.Source.PC, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, channel, null);
		data.put("message", SUCCESS_MESSAGE);
		data.put("sn", order.getSn());
		renderJson(data);
	}

	/**
	 * 兑换订单-创建
	 */
	private void createExchange() {
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String balanceValue = getPara("balance");
		BigDecimal balance = StrKit.notBlank(balanceValue) ? new BigDecimal(balanceValue) : null;
		String memo = getPara("memo");
		
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("shop.order.productNotMarketable"));
			renderJson(data);
			return;
		}
		if (quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.order.productLowStock"));
			renderJson(data);
			return;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (product.getIsDelivery()) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.equals(receiver.getMember())) {
				data.put("message", ERROR_MESSAGE);
				renderJson(data);
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				data.put("message", ERROR_MESSAGE);
				renderJson(data);
				return;
			}
		}
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			data.put("message", Message.warn("shop.order.lowPoint"));
			renderJson(data);
			return;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			renderJson(data);
			return;
		}
		List<CartItem> cartItems = new ArrayList<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);

		Order order = orderService.create(Order.Type.exchange, Order.Source.PC, cart, receiver, paymentMethod, shippingMethod, null, null, balance, memo, null);
		
		data.put("message", SUCCESS_MESSAGE);
		data.put("sn", order.getSn());
		renderJson(data);
	}

	/**
	 * 支付
	 */
	public void payment() {
		String sn = getPara("sn");
		String code =getPara("channel");
		Order order = orderService.findBySn(sn);
		Order order1=null;
		Member member = memberService.getCurrent();
		if("again".equals(code)){
			order.setSn(snDao.generate(Sn.Type.order));
			 order1 = orderService.update(order);
			logger.info("修改后的单号"+order1.getSn());
		}
		if(order1!=null){
			 order = orderService.findBySn(order1.getSn());
		}

		if (order == null || !member.equals(order.getMember()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
			if (orderService.isLocked(order, member, true)) {
				addFlashMessage(Message.warn("shop.order.locked"));
				redirect("/member/order/view.jhtml?sn=" + sn + ".jhtml");
			}
			List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
			if (CollectionUtils.isNotEmpty(paymentPlugins)) {
				PaymentPlugin defaultPaymentPlugin = paymentPlugins.get(0);
				setAttr("fee", defaultPaymentPlugin.calculateFee(order.getAmountPayable()));
				setAttr("amount", defaultPaymentPlugin.calculateAmount(order.getAmountPayable()));
				setAttr("defaultPaymentPlugin", defaultPaymentPlugin);
				setAttr("paymentPlugins", paymentPlugins);
			}
		}
		setAttr("order", order);
		render("/shop/${theme}/order/payment.ftl");
	}

	/**
	 * 计算支付金额
	 */
	public void calculateAmount() {
		String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		Map<String, Object> data = new HashMap<String, Object>();
		Order order = orderService.findBySn(sn);
		Member member = memberService.getCurrent();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (order == null || !member.equals(order.getMember()) || paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("fee", paymentPlugin.calculateFee(order.getAmountPayable()));
		data.put("amount", paymentPlugin.calculateAmount(order.getAmountPayable()));
		renderJson(data);
	}

}