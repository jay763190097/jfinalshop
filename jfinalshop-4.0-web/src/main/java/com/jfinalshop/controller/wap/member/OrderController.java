package com.jfinalshop.controller.wap.member;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.util.SystemUtils;


/**
 * Controller - 会员中心 - 订单
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/order")
@Before(WapMemberInterceptor.class)
public class OrderController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private OrderService orderService;
	private Res resZh = I18n.use();
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		String statusName = getPara("status");
		Order.Status status = StrKit.notBlank(statusName) ? Order.Status.valueOf(statusName) : null;
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pages", orderService.findPage(null, status, null, null, member, null, null, null, null, null, null, null, pageable, null));
		setAttr("status", status == null ? "all" : status);
		setAttr("title" , "我的订单 - 会员中心");
		render("/wap/member/order/list.ftl");
	}
	
	/**
	 * 查看
	 */
	public void view() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		if (order == null) {
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", order);
		setAttr("title" , "订单详情 - 会员中心");
		render("/wap/member/order/view.ftl");
	}
	
	/**
	 * 取消
	 */
	public void cancel() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		Map<String, String> map = new HashMap<String, String>();
		if (order == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单未找到!");
			renderJson(map);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单创建人与当前用户不同!");
			renderJson(map);
			return;
		}
		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单过期或状态非等待付款!");
			renderJson(map);
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.member.order.locked"));
			renderJson(map);
		}
		orderService.cancel(order);
		// 存在已支付，要退款。
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			Refunds refunds = new Refunds();
			refunds.setAmount(order.getAmountPaid());
			refunds.setMethod(Refunds.Method.deposit.ordinal());
			refunds.setMemo("取消订单：" + order.getSn() + ",退回到余额：【" + order.getAmountPaid() + "】元");
			orderService.refunds(order, refunds, null);
		}
		map.put(STATUS, SUCCESS);
		renderJson(map);
	}
	
	/**
	 * 物流跟踪
	 */
	public void track() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		setAttr("order", order);
		setAttr("title" , "订单详情 - 物流跟踪");
		render("/wap/member/order/track.ftl");
	}
	
	/**
	 * 确认收货
	 */
	public void receive() {
		String sn = getPara("sn");
		Order order = orderService.findBySn(sn);
		Map<String, String> map = new HashMap<String, String>();
		if (order == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单未找到!");
			renderJson(map);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(order.getMember())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单创建人与当前用户不同!");
			renderJson(map);
			return;
		}
		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatusName())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "订单过期或状态非已发货!");
			renderJson(map);
			return;
		}
		if (orderService.isLocked(order, member, true)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.member.order.locked"));
			renderJson(map);
		}
		orderService.receive(order, null);
		map.put(STATUS, SUCCESS);
		renderJson(map);
	}
	
}
