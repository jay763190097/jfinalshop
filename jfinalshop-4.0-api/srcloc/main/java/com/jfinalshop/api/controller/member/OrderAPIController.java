package com.jfinalshop.api.controller.member;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderLog;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.service.OrderLogService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 订单
 * 
 */
@ControllerBind(controllerKey = "/api/member/order")
@Before(TokenInterceptor.class)
public class OrderAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(OrderAPIController.class);

	@Inject
	private OrderService orderService;
	@Inject
	private OrderLogService orderLogService;
	@Inject
	private PaymentMethodService paymentMethodService;
	private Res res = I18n.use();
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		String statusName = getPara("status");
		Order.Status status = StrKit.notBlank(statusName) ? Order.Status.valueOf(statusName) : null;
		Member member = getMember();
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<Order> pages = orderService.findPage(null, status, null, null, member, null, null, null, null, null, null, null, pageable, false);
		List<Order> orders = pages.getList();
		for (Order order : orders) {
			if (CollectionUtils.isEmpty(order.getRefunds())) {
				order.put("refunds_id", "");
			} else {
				order.put("refunds_id", order.getRefunds().get(0).getId());
			}
			order.put("order_items", convertOrderItem(order.getOrderItems()));
			order.put("status_name", res.format("Order.Status." + order.getStatusName()));
		}
		DatumResponse datumResponse = new DatumResponse();
		datumResponse.setDatum(pages);
		renderJson(datumResponse);
	}
	
	/**
	 * 退款
	 */
	public void refunds() {
		String sn = getPara("sn");
		String content = getPara("content","");
		String memo = getPara("memo","");
		
		Order order = orderService.findBySn(sn);
		if (order == null || order.getRefundableAmount().compareTo(BigDecimal.ZERO) <= 0) {
			renderArgumentError("订单不存在或可退金额等于零!");
			return;
		}
		if (PaymentMethod.Method.online.ordinal() != order.getPaymentMethodType()) {
			renderArgumentError("非在线支付，不能退款!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		
		Refunds refunds = new Refunds();
		refunds.setMethod(Payment.Method.online.ordinal());
		refunds.setPaymentMethod(paymentMethodService.find(1L));
		refunds.setAmount(order.getRefundableAmount());
		refunds.setMemberId(member.getId());
		refunds.setMemo(content + "," + memo);
		
		Admin operator = new Admin();
		operator.setUsername(member.getUsername());
		orderService.refunds(order, refunds, operator);
		renderJson(new DatumResponse("退款申请提交成功!"));
	}
	
	/**
	 * 获取退款原因内容
	 */
	public void refundsReason() {
		String[] refundsReasons = new String[]{"商品选错了","地址填错了","忘记使用优惠了","临时有事需要取消","不想要了","商家告知我缺货","商家未及时发货","未收到货","商品有质量问题","商品错发、漏发,告诉我超区无法配送","其他"};
		renderJson(new DatumResponse(refundsReasons));
	}
	
	/**
	 * 取消
	 */
	public void cancel() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		if (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName())) {
			renderArgumentError("订单过期或状态非等待付款!");
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderArgumentError(res.format("shop.member.order.locked"));
			return;
		}
		orderService.cancel(order);
		// 存在已支付，要退款。
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			Refunds refunds = new Refunds();
			refunds.setMethod(Payment.Method.online.ordinal());
			refunds.setPaymentMethod(paymentMethodService.find(1L));
			refunds.setAmount(order.getRefundableAmount());
			refunds.setMemberId(member.getId());
			
			Admin operator = new Admin();
			operator.setUsername(member.getUsername());
			orderService.refunds(order, refunds, operator);
		}
		renderJson(new DatumResponse("取消成功!"));
	}
	
	/**
	 * 获取取消原因内容
	 */
	public void reason() {
		String[] reasons = new String[]{"商品选错了","地址填错了","忘记使用优惠了","临时有事需要取消","不想要了"};
		renderJson(new DatumResponse(reasons));
	}
	
	/**
	 * 提交取消原因
	 */
	public void submit() {
		Long orderId = getParaToLong("orderId");
		String content = getPara("content");
		if (orderId == null) {
			renderArgumentError("订单id不能为空!");
			return;
		}
		OrderLog orderLog = orderLogService.findOrderLog(orderId);
		if (orderLog == null) {
			renderArgumentError("订单不存在或没有取消记录!");
			return;
		}
		if (StrKit.notBlank(orderLog.getContent())) {
			renderArgumentError("已经提交过取消原因了!");
			return;
		}
		orderLog.setContent(content);
		orderLogService.update(orderLog);
		renderJson(new DatumResponse("提交成功!"));
	}
	
	
	/**
	 * 确认收货
	 */
	public void receive() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		if (!Order.Status.shipped.equals(order.getStatusName())) {
			renderArgumentError("状态非已发货!");
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderArgumentError(res.format("shop.member.order.locked"));
			return;
		}
		orderService.receive(order, null);
		orderService.complete(order, null);
		renderJson(new DatumResponse("确认成功!"));
	}
	
	/**
	 * 查看
	 */
	public void view() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		// 处理订单日志
		List<OrderLog> orderLogs = order.getOrderLogs();
		for (OrderLog orderLog: orderLogs) {
			orderLog.put("typeName", res.format("OrderLog.Type." + orderLog.getTypeName()));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		order.put("order_items", convertOrderItem(order.getOrderItems()));
		order.put("status_name", order.getStatusName());
		order.put("order_total", order.getOrderItemTotal());
		order.put("refunds", order.getRefunds());
		map.put("order", order);
		map.put("order_log", orderLogs);
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 删除
	 * 
	 */
	public void delete() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		if (!Order.Status.completed.equals(order.getStatusName()) && !Order.Status.canceled.equals(order.getStatusName())) {
			renderArgumentError("状态非等待付款!");
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderArgumentError(res.format("shop.member.order.locked"));
			return;
		}
		order.setDeleteFlag(true);
		orderService.update(order);
		renderJson(new DatumResponse("删除成功!"));
	}
}
