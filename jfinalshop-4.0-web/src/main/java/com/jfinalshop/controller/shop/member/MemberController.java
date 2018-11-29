package com.jfinalshop.controller.shop.member;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 会员中心
 * 
 * 
 */
@ControllerBind(controllerKey = "/member")
@Before(MemberInterceptor.class)
public class MemberController extends BaseController {

	/** 最新订单数 */
	private static final int NEW_ORDER_COUNT = 6;

	@Inject
	private MemberService memberService;
	@Inject
	private OrderService orderService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private MessageService messageService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private ReviewService reviewService;
	@Inject
	private ConsultationService consultationService;

	/**
	 * 首页
	 */
	public void index() {
		//Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		String channel = getPara("channel", PropKit.get("channelcode"));
		setAttr("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, member, null, null, null, null, null, null, false));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, member, null, null, null, null, null, null, null));
		setAttr("messageCount", messageService.count(member, false));
		setAttr("couponCodeCount", couponCodeService.count(null, member, null, false, false));
		setAttr("favoriteCount", goodsService.count(null, member, null, null, null, null, null));
		setAttr("productNotifyCount", productNotifyService.count(member, null, null, null));
		setAttr("reviewCount", reviewService.count(member, null, null, null));
		setAttr("consultationCount", consultationService.count(member, null, null));
		setAttr("newOrders", orderService.findListbyChannel(null, null, member, null, null, null, null, null, null, null, NEW_ORDER_COUNT, null, null,channel));
		setAttr("member", member);
		render("/shop/${theme}/member/index.ftl");
	}

}