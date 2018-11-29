package com.jfinalshop.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sn;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReceiverService;
import com.jfinalshop.service.ShippingMethodService;
import com.xiaoleilu.hutool.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订单
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/order")
@Before(TokenInterceptor.class)
public class OrderAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(OrderAPIController.class);
	@Inject
	private ProductService productService;
	@Inject
	private ReceiverService receiverService;
	@Inject
	private OrderService orderService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PluginService pluginService;
	@Inject
	private SnDao snDao;

	private Res res = I18n.use();
	
	/** 收货地址 */
	private static final String cartCacheName = "cart";
	/** H5微信支付 */
	public static final String weixinPaymentPlugin = "weixinPaymentPlugin";
	
	/**
	 * 普通订单-结算
	 */
	public void checkout() {
		String channel = getPara("channel");
		String cartKey = getPara("cartKey");
		String productIds = getPara("productIds");
		
		if (StrKit.isBlank(productIds)) {
			renderArgumentError("productIds不能为空!");
			return;
		}
		
		Long[] ids = convertToLong(StringUtils.split(productIds, ","));
		List<Product> products = productService.findList(ids);
		if (CollectionUtils.isEmpty(products)) {
			renderArgumentError("选择的商品不能为空!");
			return;
		}
		
		Cart cart = getCurrent(cartKey,channel);
		if (cart == null || cart.isEmpty()) {
			renderArgumentError("购物车不能为空!");
			return;
		}
		
		List<CartItem> cartItems = cart.getCartItems();
		if (CollectionUtils.isEmpty(cartItems)) {
			renderArgumentError("购物车项不能为空!");
			return;
		}
		
		Member member = getMember();
		Receiver defaultReceiver = null;
		if (member != null) {
			defaultReceiver = receiverService.findDefault(member);
		}
		
		// 根据传过来的id，从用户的购物车中查找商品
		List<CartItem> pCartItems = new ArrayList<CartItem>();
		for (CartItem cartItem : cartItems) {
			if (products.contains(cartItem.getProduct())) {
				pCartItems.add(cartItem);
			}
		}
		if (CollectionUtils.isEmpty(pCartItems)) {
			renderArgumentError("购物车项中未找到商品!");
			return;
		}
		cart.setCartItems(pCartItems);
		cart.setMember(member);
		
		Order order = orderService.generate(Order.Type.general, cart, defaultReceiver, null, null, null, null, null, null);
		
		// 缓存购物车
		String cartCacheKey = "cart_" + getPara("token");
		CacheKit.put(cartCacheName, cartCacheKey, cart);
		
		
		// 支付方式
		List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<OrderItem> orderItems = order.getOrderItems();
		Boolean isCash = Boolean.FALSE;
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				Goods goods = orderItem.getProduct().getGoods();
				orderItem.put("unit", goods.getUnit());
				orderItem.put("brand", goods.getBrand() != null ? goods.getBrand().getName() : "");
				ProductCategory productCategory = orderItem.getProduct().getGoods().getProductCategory();
				if (productCategory.getIsCash()) {
					isCash = Boolean.TRUE;
				}
			}
			List<Filter> filters = new ArrayList<Filter>();
			if (isCash) {
				filters.add(Filter.eq("method", PaymentMethod.Method.offline.ordinal()));
				paymentMethods = paymentMethodService.findList(null, filters, null);
				paymentMethods.get(0).setIsDefault(true);
			} else {
				filters.add(Filter.eq("method", PaymentMethod.Method.online.ordinal()));
				paymentMethods = paymentMethodService.findList(null, filters, null);
				paymentMethods.get(0).setIsDefault(true);
			}
		}
		order.put("order_items", orderItems);
		// 订单合计
		order.put("orderTotal", currency(order.getOrderItemTotal(), false, false));
		map.put("order", order);
		map.put("cartToken", cart.getToken());
		map.put("defaultReceiver", defaultReceiver);
		map.put("paymentMethods", paymentMethods);
		map.put("isInvoiceEnabled", setting.getIsInvoiceEnabled());
		map.put("couponCodeCount", couponCodeService.count(null, member, null, false, false));
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 订单-创建
	 */
	public void create() {
		String cartToken = getPara("cartToken");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId", 1L);
		Long shippingMethodId = getParaToLong("shippingMethodId", 1L);
		String code = getPara("code");
		String invoiceTitle = getPara("title");
		String invoiceContent = getPara("content");
		String memo = getPara("memo");
		Boolean isBalance = getParaToBoolean("isBalance", false);
		String shippingDateStr = getPara("shippingDate", null);
		String sourceName = getPara("source");
		Order.Source source = StrKit.notBlank(sourceName) ? Order.Source.valueOf(StringUtils.upperCase(sourceName)) : null;
		if (source == null) {
			renderArgumentError("订单来源不能为空!");
			return;
		}
		
		// 从缓存取出当前用户的购物车
		String cartCacheKey = "cart_" + getPara("token");
		Cart cart = CacheKit.get(cartCacheName, cartCacheKey);
		if (cart == null || cart.isEmpty()) {
			renderArgumentError("购物车不能为空!");
			return;
		}
		
		if (cart.hasNotMarketable()) {
			renderArgumentError(res.format("shop.order.hasNotMarketable"));
			return;
		}
		if (cart.getIsLowStock()) {
			renderArgumentError(res.format("shop.order.cartLowStock"));
			return;
		}
		
		if (!StringUtils.equals(cart.getToken(), cartToken)) {
			renderArgumentError(res.format("shop.order.cartHasChanged"));
			return;
		}
		
		Date shippingDate = null;
		if (StrKit.notBlank(shippingDateStr)) {
			shippingDateStr += ":00";
			shippingDate = DateUtil.parseDateTime(shippingDateStr);
		}
		
		Member member = getMember();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (cart.getIsDelivery()) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.getId().equals(receiver.getMemberId())) {
				renderArgumentError("地址与收货人不对应");
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				renderArgumentError("配送方式为空");
				return;
			}
			
		}
		
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && !cart.isValid(couponCode)) {
			renderArgumentError("优惠券不满足使用条件");
			return;
		}
		
		BigDecimal balance = null;
		if (isBalance) {
			balance = member.getBalance();
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			renderArgumentError("余额小于等于0");
			return;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			renderArgumentError(res.format("shop.order.insufficientBalance"));
			return;
		}
		Invoice invoice = StrKit.notBlank(invoiceTitle) ? new Invoice(invoiceTitle, invoiceContent) : null;
		Order order = orderService.create(Order.Type.general, source, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo, shippingDate);
		
		// 移动缓存中的购物车
		CacheKit.remove(cartCacheName, cartCacheKey);
		
		// 返回支付方式
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sn", order.getSn());
		map.put("status", order.getStatus());
		map.put("amount", order.getAmount());
		map.put("paymentMethodName", order.getPaymentMethodName());
		map.put("shippingMethodName", order.getShippingMethodName());
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 支付
	 */
	public void payment() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		Member member = getMember();
		
		if (order == null || !member.getId().equals(order.getMemberId()) || order.getPaymentMethodId() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			renderArgumentError("支付订单异常 ");
			return;
		}
		// 返回支付方式
		Map<String, Object> map = new HashMap<String, Object>();
		if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
			if (orderService.isLocked(order, member, true)) {
				renderArgumentError(res.format("shop.order.locked"));
				return;
			}
			List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
			if (CollectionUtils.isNotEmpty(paymentPlugins)) {
				PaymentPlugin defaultPaymentPlugin = paymentPlugins.get(0);
				// H5支付插件
				PaymentPlugin h5PaymentPlugin = pluginService.getPaymentPlugin(weixinPaymentPlugin);
				paymentPlugins.remove(h5PaymentPlugin);
				// 应付金额
				map.put("amount", currency(defaultPaymentPlugin.calculateAmount(order.getAmountPayable()),false, false));
				map.put("defaultPaymentPlugin", defaultPaymentPlugin);
				map.put("paymentPlugins", paymentPlugins);
				map.put("weixinPaymentPlugin", h5PaymentPlugin);
			}
		}
		// 订单合计
		map.put("orderTotal", currency(order.getOrderItemTotal(), false, false));
		// 订单优惠
		map.put("couponDiscount", order.getCouponDiscount().add(order.getPromotionDiscount()));
		map.put("sn", order.getSn());
		DatumResponse datumResponse = new DatumResponse();
		datumResponse.setDatum(map);
		datumResponse.setImageUrl(setting.getImageUrl());
		renderJson(datumResponse);
	}
	/**
	 * 继续支付更新订单号
	 */
	public void updateSn() {
	   Member member = getMember();
	   String sn = getPara("sn");
	   logger.info("用户的订单号"+sn);
	   Order order = orderService.findBySn(sn);
	   order.setSn(snDao.generate(Sn.Type.order));
	   Order order1 = orderService.update(order);
	   logger.info("修改后的单号"+order1.getSn());
	   renderJson(new DatumResponse(order1));
	}
}
