package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingItem;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.service.ShippingService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 订单
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/order")
public class OrderController extends BaseController {
	
	@Inject
	private AdminService adminService;
	@Inject
	private AreaService areaService;
	@Inject
	private OrderService orderService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private DeliveryCorpService deliveryCorpService;
	@Inject
	private ShippingService shippingService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查锁定
	 */
	public void checkLock() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			renderJson(Message.warn("admin.order.locked"));
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 计算
	 */
	public void calculate() {
		Long id = getParaToLong("id");
		String freightName = getPara("freight");
		BigDecimal freight = StrKit.notBlank(freightName) ? new BigDecimal(freightName) : BigDecimal.ZERO;
		
		String taxName = getPara("tax");
		BigDecimal tax = StrKit.notBlank(taxName) ? new BigDecimal(taxName) : BigDecimal.ZERO;
		
		String offsetAmountName = getPara("offsetAmount");
		BigDecimal offsetAmount = StrKit.notBlank(offsetAmountName) ? new BigDecimal(offsetAmountName) : BigDecimal.ZERO;
		Map<String, Object> data = new HashMap<String, Object>();
		Order order = orderService.find(id);
		if (order == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("amount", orderService.calculateAmount(order.getPrice(), order.getFee(), freight, tax, order.getPromotionDiscount(), order.getCouponDiscount(), offsetAmount));
		renderJson(data);
	}

	/**
	 * 物流动态
	 */
	public void transitStep() {
		Long shippingId = getParaToLong("shippingId");
		Map<String, Object> data = new HashMap<String, Object>();
		Shipping shipping = shippingService.find(shippingId);
		if (shipping == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(shipping.getDeliveryCorpCode()) || StringUtils.isEmpty(shipping.getTrackingNo())) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("transitSteps", shippingService.getTransitSteps(shipping));
		renderJson(data);
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			redirect(ERROR_VIEW);
			return;
		}
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("order", order);
		render("/admin/order/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		String channel="IOS";
		Long id = getParaToLong("id");
		//Long areaId = getParaToLong("areaId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		
		String freightName = getPara("freight");
		BigDecimal freight = StrKit.notBlank(freightName) ? new BigDecimal(freightName) : BigDecimal.ZERO;
		
		String taxName = getPara("tax");
		BigDecimal tax = StrKit.notBlank(taxName) ? new BigDecimal(taxName) : BigDecimal.ZERO;
		
		String offsetAmountName = getPara("offsetAmount");
		BigDecimal offsetAmount = StrKit.notBlank(offsetAmountName) ? new BigDecimal(offsetAmountName) : BigDecimal.ZERO;
		
		Long rewardPoint = getParaToLong("rewardPoint", 0L);
		String consignee = getPara("consignee");
		String address = getPara("address");
		String zipCode = getPara("zipCode");
		String phone = getPara("phone");
		String invoiceTitle = getPara("invoiceTitle");
		String memo = getPara("memo");
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);

		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			redirect("ERROR_VIEW");
			return;
		}
		//Invoice invoice = StrKit.notBlank(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		order.setTax(StrKit.notBlank(invoiceTitle) ? tax : BigDecimal.ZERO);
		order.setOffsetAmount(offsetAmount);
		order.setRewardPoint(rewardPoint);
		order.setChannel(channel);
		order.setInvoiceTitle(invoiceTitle);
		order.setPaymentMethod(paymentMethod);
		if (order.getIsDelivery()) {
			order.setFreight(freight);
			order.setConsignee(consignee);
			order.setAddress(address);
			order.setZipCode(zipCode);
			order.setPhone(phone);
			order.setShippingMethod(shippingMethod);
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setConsignee(null);
			order.setAreaName(null);
			order.setAddress(null);
			order.setZipCode(null);
			order.setPhone(null);
			order.setShippingMethodName(null);
			order.setArea(null);
			order.setShippingMethod(null);
		}

		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
		}
		orderService.update(order, admin);

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/list.jhtml");
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Setting setting = SystemUtils.getSetting();
		setAttr("methods", Payment.Method.values());
		setAttr("refundsMethods", Refunds.Method.values());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", orderService.find(id));
		render("/admin/order/view.ftl");
	}

	/**
	 * 审核
	 */
	@Before(Tx.class)
	public void review() {
		Long id = getParaToLong("id");
		Boolean passed = getParaToBoolean("passed", false);
		
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || !Order.Status.pendingReview.equals(order.getStatusName())) {
			setSessionAttr("errorMessage","订单已过期");
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			setSessionAttr("errorMessage","订单锁定中");
			redirect(ERROR_VIEW);
			return;
		}
		orderService.review(order, passed, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + id);
	}

	/**
	 * 收款
	 */
	@Before(Tx.class)
	public void payment() {
		Payment payment = getModel(Payment.class);
		Long orderId = getParaToLong("orderId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		
		String methodName = getPara("method");
		Payment.Method method = StrKit.notBlank(methodName) ? Payment.Method.valueOf(methodName) : null;
		if (method != null) {
			payment.setMethod(method.ordinal());
		}
		
		Order order = orderService.find(orderId);
		if (order == null) {
			setSessionAttr("errorMessage", "订单不能为空！");
			redirect(ERROR_VIEW);
			return;
		}
		payment.setOrderId(order.getId());
		payment.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			setSessionAttr("errorMessage", "订单锁定中...");
			redirect(ERROR_VIEW);
			return;
		}
		Member member = order.getMember();
		if (Payment.Method.deposit.equals(payment.getMethodName()) && payment.getAmount().compareTo(member.getBalance()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		payment.setFee(BigDecimal.ZERO);
		payment.setOperator(admin);
		orderService.payment(order, payment, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + orderId);
	}

	/**
	 * 退款
	 */
	@Before(Tx.class)
	public void refunds() {
		Refunds refunds = getModel(Refunds.class);
		Long orderId = getParaToLong("orderId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		
		String methodName = getPara("method");
		Payment.Method method = StrKit.notBlank(methodName) ? Payment.Method.valueOf(methodName) : null;
		if (method != null) {
			refunds.setMethod(method.ordinal());
		}
		
		Order order = orderService.find(orderId);
		if (order == null || order.getRefundableAmount().compareTo(BigDecimal.ZERO) <= 0) {
			redirect(ERROR_VIEW);
			return;
		}
		refunds.setOrderId(order.getId());
		refunds.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		refunds.setOperator(admin);
		orderService.refunds(order, refunds, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + orderId);
	}

	/**
	 * 发货
	 */
	@Before(Tx.class)
	public void shipping() {
		Shipping shipping = getModel(Shipping.class);
		Long orderId = getParaToLong("orderId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		
		Order order = orderService.find(orderId);
		if (order == null || order.getShippableQuantity() <= 0) {
			redirect(ERROR_VIEW);
			return;
		}
		
		List<ShippingItem> shippingItems = getBeans(ShippingItem.class, "shippingItems");
		shipping.setShippingItems(shippingItems);
		
		boolean isDelivery = false; // 是否交货
		for (Iterator<ShippingItem> iterator = shipping.getShippingItems().iterator(); iterator.hasNext();) {
			ShippingItem shippingItem = iterator.next();
			if (shippingItem == null || StringUtils.isEmpty(shippingItem.getSn()) || shippingItem.getQuantity() == null || shippingItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null || shippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				redirect(ERROR_VIEW);
				return;
			}
			Product product = orderItem.getProduct();
			if (product != null && shippingItem.getQuantity() > product.getStock()) {
				redirect(ERROR_VIEW);
				return;
			}
			shippingItem.setName(orderItem.getName());
			shippingItem.setIsDelivery(orderItem.getIsDelivery());
			shippingItem.setProductId(product.getId());
			shippingItem.setShipping(shipping);
			shippingItem.setSpecifications(orderItem.getSpecifications());
			if (orderItem.getIsDelivery()) {
				isDelivery = true;
			}
		}
		shipping.setOrderId(order.getId());
		shipping.setShippingMethod(shippingMethodService.find(shippingMethodId));
		shipping.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		shipping.setArea(areaService.find(areaId).getName());
		if (!isDelivery) {
			shipping.setShippingMethod((String) null);
			shipping.setDeliveryCorp((String) null);
			shipping.setDeliveryCorpUrl(null);
			shipping.setDeliveryCorpCode(null);
			shipping.setTrackingNo(null);
			shipping.setFreight(null);
			shipping.setConsignee(null);
			shipping.setArea((String) null);
			shipping.setAddress(null);
			shipping.setZipCode(null);
			shipping.setPhone(null);
		}

		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		shipping.setOperator(admin);
		orderService.shipping(order, shipping, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + orderId);
	}

	/**
	 * 退货
	 */
	@Before(Tx.class)
	public void returns() {
		Returns returns = getModel(Returns.class);
		Long orderId = getParaToLong("orderId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		
		Order order = orderService.find(orderId);
		if (order == null || order.getReturnableQuantity() <= 0) {
			redirect(ERROR_VIEW);
			return;
		}
		List<ReturnsItem> returnsItems = getBeans(ReturnsItem.class, "returnsItems");
		returns.setReturnsItems(returnsItems);
		
		for (Iterator<ReturnsItem> iterator = returns.getReturnsItems().iterator(); iterator.hasNext();) {
			ReturnsItem returnsItem = iterator.next();
			if (returnsItem == null || StringUtils.isEmpty(returnsItem.getSn()) || returnsItem.getQuantity() == null || returnsItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem == null || returnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				redirect(ERROR_VIEW);
				return;
			}
			returnsItem.setName(orderItem.getName());
			returnsItem.setReturns(returns);
			returnsItem.setSpecifications(orderItem.getSpecifications());
			returnsItem.setStatus(ReturnsItem.Status.pendingReview.ordinal());
			returnsItem.setProductId(orderItem.getProductId());
		}
		returns.setOrderId(order.getId());
		returns.setShippingMethod(shippingMethodService.find(shippingMethodId));
		returns.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		returns.setArea(areaService.find(areaId).getName());
		
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		returns.setOperator(admin);
		orderService.returns(order, returns, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + orderId);
	}

	/**
	 * 收货
	 */
	@Before(Tx.class)
	public void receive() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || !Order.Status.shipped.equals(order.getStatusName())) {
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		orderService.receive(order, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + id);
	}

	/**
	 * 完成
	 */
	@Before(Tx.class)
	public void complete() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || !Order.Status.received.equals(order.getStatusName())) {
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		orderService.complete(order, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + id);
	}

	/**
	 * 失败
	 */
	@Before(Tx.class)
	public void fail() {
		Long id = getParaToLong("id");
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || (!Order.Status.pendingShipment.equals(order.getStatusName()) && !Order.Status.shipped.equals(order.getStatusName()) && !Order.Status.received.equals(order.getStatusName()))) {
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		if (orderService.isLocked(order, admin, true)) {
			redirect(ERROR_VIEW);
			return;
		}
		orderService.fail(order, admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/order/view.jhtml?id=" + id);
	}

	/**
	 * 列表
	 */
	public void list() {
		String typeName = getPara("type");
		Order.Type type = StrKit.notBlank(typeName) ? Order.Type.valueOf(typeName) : null;
		
		String statusName = getPara("status");
		Order.Status status = StrKit.notBlank(statusName) ? Order.Status.valueOf(statusName) : null;
		
		String sourceName = getPara("source");
		Order.Source source = StrKit.notBlank(sourceName) ? Order.Source.valueOf(sourceName) : null;
		
		String memberUsername = getPara("memberUsername");
		Boolean isPendingReceive = getParaToBoolean("isPendingReceive");
		Boolean isPendingRefunds = getParaToBoolean("isPendingRefunds");
		Boolean isAllocatedStock = getParaToBoolean("isAllocatedStock");
		Boolean hasExpired = getParaToBoolean("hasExpired");
		Pageable pageable = getBean(Pageable.class);

		setAttr("types", Order.Type.values());
		setAttr("statuses", Order.Status.values());
		setAttr("sources", Order.Source.values());
		setAttr("type", type);
		setAttr("status", status);
		setAttr("source", source);
		setAttr("memberUsername", memberUsername);
		setAttr("isPendingReceive", isPendingReceive);
		setAttr("isPendingRefunds", isPendingRefunds);
		setAttr("isAllocatedStock", isAllocatedStock);
		setAttr("hasExpired", hasExpired);
		setAttr("pageable", pageable);

		Member member = memberService.findByUsername(memberUsername);
		if (StringUtils.isNotEmpty(memberUsername) && member == null) {
			setAttr("page", "");
		} else {
			setAttr("page", orderService.findPage(type, status, source, null,  member, null, isPendingReceive, isPendingRefunds, null, null, isAllocatedStock, hasExpired, pageable, null));
		}
		render("/admin/order/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			Admin admin = adminService.getCurrent();
			for (Long id : ids) {
				Order order = orderService.find(id);
				if (order != null && orderService.isLocked(order, admin, true)) {
					renderJson(Message.error("admin.order.deleteLockedNotAllowed", order.getSn()));
					return;
				}
				if (Order.Status.completed.equals(order.getStatusName())) {
					renderJson(Message.error("admin.order.deleteStatusNotAllowed", order.getSn()));
					return;
				}
				if (CollectionUtils.isNotEmpty(order.getPayments())) {
					renderJson(Message.error("admin.order.deletePaymentsNotAllowed", order.getSn()));
					return;
				}
				if (CollectionUtils.isNotEmpty(order.getReturns())) {
					renderJson(Message.error("admin.order.deleteReturnsNotAllowed", order.getSn()));
					return;
				}
				if (CollectionUtils.isNotEmpty(order.getShippings())) {
					renderJson(Message.error("admin.order.deleteShippingsNotAllowed", order.getSn()));
					return;
				}
				orderService.delete(order);
			}
		}
		renderJson(SUCCESS_MESSAGE);
	}

}