//package com.jfinalshop.controller.wap;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import net.hasor.core.Inject;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.jfinal.aop.Before;
//import com.jfinal.ext.route.ControllerBind;
//import com.jfinal.i18n.I18n;
//import com.jfinal.i18n.Res;
//import com.jfinal.kit.StrKit;
//import com.jfinal.plugin.ehcache.CacheKit;
//import com.jfinalshop.Pageable;
//import com.jfinalshop.entity.Invoice;
//import com.jfinalshop.interceptor.WapMemberInterceptor;
//import com.jfinalshop.model.Cart;
//import com.jfinalshop.model.CartItem;
//import com.jfinalshop.model.CouponCode;
//import com.jfinalshop.model.Member;
//import com.jfinalshop.model.Order;
//import com.jfinalshop.model.PaymentMethod;
//import com.jfinalshop.model.Product;
//import com.jfinalshop.model.Receiver;
//import com.jfinalshop.model.ShippingMethod;
//import com.jfinalshop.plugin.PaymentPlugin;
//import com.jfinalshop.service.CartService;
//import com.jfinalshop.service.CouponCodeService;
//import com.jfinalshop.service.MemberService;
//import com.jfinalshop.service.OrderService;
//import com.jfinalshop.service.PaymentMethodService;
//import com.jfinalshop.service.PluginService;
//import com.jfinalshop.service.ProductService;
//import com.jfinalshop.service.ReceiverService;
//import com.jfinalshop.service.ShippingMethodService;
//import com.xiaoleilu.hutool.date.DateUtil;
//
//
///**
// * Controller - 订单
// *
// *
// */
//@ControllerBind(controllerKey = "/wap/order")
//@Before(WapMemberInterceptor.class)
//public class OrderController extends BaseController {
//
//	@Inject
//	private CartService cartService;
//	@Inject
//	private ProductService productService;
//	@Inject
//	private MemberService memberService;
//	@Inject
//	private ReceiverService receiverService;
//	@Inject
//	private OrderService orderService;
//	@Inject
//	private PaymentMethodService paymentMethodService;
//	@Inject
//	private CouponCodeService couponCodeService;
//	@Inject
//	private ShippingMethodService shippingMethodService;
//	@Inject
//	private PluginService pluginService;
//
//	private Res resZh = I18n.use();
//
//	/** 发票 */
//	private String invCacheName = "invoice";
//
//	/** 收货地址 */
//	private String addrCacheName = "address";
//
//	/** 支付配送 */
//	private String deliveryCacheName = "delivery";
//
//	/** 优惠券 */
//	private String couponCodeCacheName = "couponCode";
//
//	/** 每页记录数 */
//	private static final int PAGE_SIZE = 10;
//
//	/**
//	 * 普通订单-结算
//	 */
//	public void checkout() {
//		String[] values = StringUtils.split(getPara("skuids"), ",");
//		Long[] skuids = values == null ? null :convertToLong(values);
//
//		List<Product> products = productService.findList(skuids);
//		Cart cart = cartService.getCurrent();
//		if (cart == null || cart.isEmpty()) {
//			redirect("/wap/cart/list.jhtml");
//			return;
//		}
//		List<CartItem> cartItems = cart.getCartItems();
//		if (CollectionUtils.isNotEmpty(products) && (CollectionUtils.isNotEmpty(cartItems))) {
//			List<CartItem> selectedItems = new ArrayList<CartItem>();
//			for (CartItem cartItem : cartItems) {
//				if (products.contains(cartItem.getProduct())) {
//					selectedItems.add(cartItem);
//				}
//			}
//			cart.setCartItems(selectedItems);
//		}
//
//		Member member = memberService.getCurrent();
//		String addrCacheObject = "addr_" + getSession().getId(); // 地址取值 key
//		Receiver defaultReceiver = CacheKit.get(addrCacheName, addrCacheObject);
//		if (defaultReceiver == null) {
//			defaultReceiver = receiverService.findDefault(member);
//		}
//
//		String deliveryCacheObject = "dev_" + getSession().getId(); // 支付配送取值 key
//		PaymentMethod paymentMethod = CacheKit.get(deliveryCacheName, deliveryCacheObject);
//		if (paymentMethod == null) {
//			paymentMethod = paymentMethodService.find(1L); // 默认网上支付
//		}
//
//		String invCacheObject = "inv_" + getSession().getId(); // 发票取值 key
//		Invoice invoice = CacheKit.get(invCacheName, invCacheObject);
//		if (invoice == null) {
//			invoice = new Invoice("", "不开发票"); // 默认不开票
//		}
//
//		String couponCodeObject = "code_" + getSession().getId(); // 优惠券取值 key
//		CouponCode couponCode = CacheKit.get(couponCodeCacheName, couponCodeObject);
//
//		String redirectUrl = getRequest().getQueryString() != null ? getRequest().getRequestURI() + "?" + getRequest().getQueryString() : getRequest().getRequestURI();
//		Order order = orderService.generate(Order.Type.general, cart, defaultReceiver, null, null, couponCode, null, null, null);
//		setSessionAttr("cart", cart);
//		setAttr("order", order);
//		setAttr("member", member);
//		setAttr("cartToken", cart.getToken());
//		setAttr("defaultReceiver", defaultReceiver);
//		setAttr("paymentMethod", paymentMethod);
//		setAttr("invoice",  invoice);
//		setAttr("couponCode", couponCode);
//		setAttr("couponCodeCount", couponCodeService.count(null, member, null, false, false));
//		setAttr("redirectUrl", redirectUrl);
//		setAttr("title" , "核对订单信息");
//		render("/wap/order/checkout.ftl");
//	}
//
//	/**
//	 * 收货地址
//	 */
//	public void address() {
//		Integer pageNumber = getParaToInt("pageNumber");
//		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
//
//		Long receiverId = getParaToLong("receiverId");
//		String redirectUrl = getPara("url_forward");
//
//		Member member = memberService.getCurrent();
//		String curRedirectUrl = getRequest().getQueryString() != null ? getRequest().getRequestURI() + "?" + getRequest().getQueryString() : getRequest().getRequestURI();
//		setAttr("pages", receiverService.findPage(member, pageable));
//		setAttr("title" , "选择收货地址");
//		setAttr("receiverId", receiverId);
//		setSessionAttr("url_forward", redirectUrl);
//		setAttr("curRedirectUrl", curRedirectUrl);
//		render("/wap/order/address.ftl");
//	}
//
//	/**
//	 * 增加地址到缓存
//	 */
//	public void addAddressCache() {
//		Long receiverId = getParaToLong("receiverId");
//		String url_forward = getSessionAttr("url_forward");
//		Receiver receiver = receiverService.find(receiverId);
//
//		String addrCacheObject = "addr_" + getSession().getId(); // 地址取值 key
//		CacheKit.put(addrCacheName, addrCacheObject, receiver);
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put(STATUS, SUCCESS);
//		map.put("referer", url_forward);
//		renderJson(map);
//	}
//
//	/**
//	 * 订单物流
//	 */
//	public void delivery() {
//		String url_forward = getPara("url_forward");
//		Long paymentMethodId = getParaToLong("paymentMethodId");
//		setAttr("paymentMethods", paymentMethodService.findAll());
//		//setAttr("shippingMethods", shippingMethodService.findAll());
//		setAttr("title" , "选择支付配送");
//		setSessionAttr("url_forward", url_forward);
//		setAttr("paymentMethodId", paymentMethodId);
//		render("/wap/order/delivery.ftl");
//	}
//
//	/**
//	 * 增加支付配送到缓存
//	 */
//	public void addDeliveryCache() {
//		String url_forward = getSessionAttr("url_forward");
//		Long paymentMethodId = getParaToLong("paymentMethodId");
//		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
//
//		String deliveryCacheObject = "dev_" + getSession().getId(); // 支付配送取值 key
//		CacheKit.put(deliveryCacheName, deliveryCacheObject, paymentMethod);
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put(STATUS, SUCCESS);
//		map.put("referer", url_forward);
//		renderJson(map);
//	}
//
//	/**
//	 * 订单发票
//	 */
//	public void invoice() {
//		String url_forward = getPara("url_forward");
//		setAttr("title" , "发票信息");
//		setSessionAttr("url_forward", url_forward);
//		render("/wap/order/invoice.ftl");
//	}
//
//	/**
//	 * 增加发票到缓存,然后再调用checkut时再从缓存中取。
//	 *
//	 */
//	public void addInvoiceCache() {
//		String title = getPara("invoiceTitle");
//		String content = getPara("invoiceContent");
//		String invCacheObject = "inv_" + getSession().getId(); // 发票取值 key
//
//		Invoice invoice = new Invoice(title, content);
//		CacheKit.put(invCacheName, invCacheObject, invoice);
//		String url_forward = getSessionAttr("url_forward");
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put(STATUS, SUCCESS);
//		map.put("referer", url_forward);
//		renderJson(map);
//	}
//
//	/**
//	 * 优惠券
//	 */
//	public void couponCode() {
//		String url_forward = getPara("url_forward");
//		// 查出用户可用的优惠券
//		Member member = memberService.getCurrent();
//		List<CouponCode> couponCodes = couponCodeService.findCouponCodes(member, false, false);
//
//		setAttr("title" , "优惠券");
//		setAttr("couponCodes", couponCodes);
//		setSessionAttr("url_forward", url_forward);
//		render("/wap/order/coupon_code.ftl");
//	}
//
//	/**
//	 * 增加优惠券到缓存
//	 */
//	public void addCouponCodeCache() {
//		Long couponCodeId = getParaToLong("couponCodeId");
//		String couponCodeObject = "code_" + getSession().getId(); // 优惠券取值 key
//
//		CouponCode couponCode = couponCodeService.find(couponCodeId);
//		CacheKit.put(couponCodeCacheName, couponCodeObject, couponCode);
//		String url_forward = getSessionAttr("url_forward");
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put(STATUS, SUCCESS);
//		map.put("referer", url_forward);
//		renderJson(map);
//	}
//
//	/**
//	 * 订单-创建
//	 */
//	public void create() {
//		String cartToken = getPara("cartToken");
//		Long receiverId = getParaToLong("receiverId");
//		Long paymentMethodId = getParaToLong("paymentMethodId");
//		Long shippingMethodId = getParaToLong("shippingMethodId", 1L);
//		String code = getPara("code");
//		String invoiceTitle = getPara("title");
//		String invoiceContent = getPara("content");
//		String memo = getPara("memo");
//		Boolean isBalance = getParaToBoolean("is_balance", false);
//		String shippingDateStr = getPara("shippingDate", null);
//
//		Map<String, String> map = new HashMap<String, String>();
//		Cart cart = getSessionAttr("cart");
//		if (cart == null || cart.isEmpty()) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, "购物车不能为空!");
//			renderJson(map);
//			return;
//		}
//
//		if (!StringUtils.equals(cart.getToken(), cartToken)) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, resZh.format("shop.order.cartHasChanged"));
//			renderJson(map);
//			return;
//		}
//		if (cart.hasNotMarketable()) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, resZh.format("shop.order.hasNotMarketable"));
//			renderJson(map);
//			return;
//		}
//		if (cart.getIsLowStock()) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, resZh.format("shop.order.cartLowStock"));
//			renderJson(map);
//			return;
//		}
//
//		Date shippingDate = null;
//		if (StrKit.notBlank(shippingDateStr)) {
//			shippingDateStr += ":00";
//			shippingDate = DateUtil.parseDateTime(shippingDateStr);
//		}
//
//		Member member = memberService.getCurrent();
//		Receiver receiver = null;
//		ShippingMethod shippingMethod = null;
//		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
//		if (cart.getIsDelivery()) {
//			receiver = receiverService.find(receiverId);
//			if (receiver == null || !member.equals(receiver.getMember())) {
//				map.put(STATUS, ERROR);
//				map.put(MESSAGE, "地址与收货人不对应");
//				renderJson(map);
//				return;
//			}
//			shippingMethod = shippingMethodService.find(shippingMethodId);
//			if (shippingMethod == null) {
//				map.put(STATUS, ERROR);
//				map.put(MESSAGE, "配送方式为空");
//				renderJson(map);
//				return;
//			}
//
//		}
//
//		CouponCode couponCode = couponCodeService.findByCode(code);
//		if (couponCode != null && !cart.isValid(couponCode)) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, "优惠券异常");
//			renderJson(map);
//			return;
//		}
//
//		BigDecimal balance = null;
//		if (isBalance) {
//			balance = member.getBalance();
//		}
//		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, "余额小于等于0");
//			renderJson(map);
//			return;
//		}
//		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, resZh.format("shop.order.insufficientBalance"));
//			renderJson(map);
//			return;
//		}
//		Invoice invoice = StrKit.notBlank(invoiceTitle) ? new Invoice(invoiceTitle, invoiceContent) : null;
//		Order order = orderService.create(Order.Type.general, Order.Source.H5, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo, shippingDate);
//
//		/** 购物车 */
//		removeSessionAttr("cart");
//
//		/** 优惠券 */
//		String couponCodeObject = "code_" + getSession().getId(); // 优惠券取值 key
//	    CacheKit.remove(couponCodeCacheName, couponCodeObject);
//
//		if (order.getStatusName().equals(Order.Status.pendingReview)) {
//			map.put("referer", "/wap/payment/success.jhtml?success=true&sn=" + order.getSn());
//		} else if (StrKit.isBlank(member.getOpenId())) {
//			map.put("referer", "/wap/payment/getWeixinCode.jhtml?sn=" + order.getSn());
//		} else {
//			map.put("referer", "/wap/order/payment.jhtml?sn=" + order.getSn());
//		}
//		map.put(STATUS, SUCCESS);
//		renderJson(map);
//	}
//
//
//	/**
//	 * 支付
//	 */
//	public void payment() {
//		String sn = getPara("sn");
//		Order order = orderService.findBySn(sn);
//		Member member = memberService.getCurrent();
//		Map<String, String> map = new HashMap<String, String>();
//		if (order == null || !member.equals(order.getMember()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
//			map.put(STATUS, ERROR);
//			map.put(MESSAGE, "订单异常");
//			renderJson(map);
//			return;
//		}
//		if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
//			if (orderService.isLocked(order, member, true)) {
//				map.put(STATUS, ERROR);
//				map.put(MESSAGE, resZh.format("shop.order.locked"));
//				renderJson(map);
//				redirect("/member/order/view.jhtml?sn=" + sn + ".jhtml");
//				return;
//			}
//			List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
//			if (CollectionUtils.isNotEmpty(paymentPlugins)) {
//				PaymentPlugin defaultPaymentPlugin = paymentPlugins.get(0);
//				setAttr("fee", defaultPaymentPlugin.calculateFee(order.getAmountPayable()));
//				setAttr("amount", defaultPaymentPlugin.calculateAmount(order.getAmountPayable()));
//				setAttr("defaultPaymentPlugin", defaultPaymentPlugin);
//				setAttr("paymentPlugins", paymentPlugins);
//			}
//		}
//		setAttr("member", member);
//		setAttr("order", order);
//		setAttr("title" , "订单支付");
//		render("/wap/order/payment.ftl");
//	}
//
//}
