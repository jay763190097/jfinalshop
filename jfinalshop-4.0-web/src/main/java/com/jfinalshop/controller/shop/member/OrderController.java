package com.jfinalshop.controller.shop.member;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ShippingService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 会员中心 - 订单
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/order")
@Before(MemberInterceptor.class)
public class OrderController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private OrderService orderService;
	@Inject
	private ShippingService shippingService;

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
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderJson(Message.warn("shop.member.order.locked"));
			return;
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 物流动态
	 */
	public void transitStep() {
		String shippingSn = getPara("shippingSn");
		Map<String, Object> data = new HashMap<String, Object>();
		Shipping shipping = shippingService.findBySn(shippingSn);
		if (shipping == null || shipping.getOrder() == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(shipping.getOrder().getMember())) {
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
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		String channel = getPara("channel", PropKit.get("channelcode"));
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", orderService.findPagebyChannel(null, null, null, null, member, null, null, null, null, null, null, null, pageable, null,channel));
		setAttr("member", member);
		render("/shop/${theme}/member/order/list.ftl");
	}

	/**
	 * 查看
	 */
	public void view() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			redirect(ERROR_VIEW);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", order);
		render("/shop/${theme}/member/order/view.ftl");
	}

	/**
	 * 取消
	 */
	@Before(Tx.class)
	public void cancel() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderJson(Message.warn("shop.member.order.locked"));
		}
		orderService.cancel(order);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 收货
	 */
	@Before(Tx.class)
	public void receive() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatusName())) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			renderJson(Message.warn("shop.member.order.locked"));
		}
		orderService.receive(order, null);
		renderJson(SUCCESS_MESSAGE);
	}

}