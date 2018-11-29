package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ReviewService;

@ControllerBind(controllerKey = "/wap/member")
@Before(WapMemberInterceptor.class)
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
	@Inject
	private FileService fileService;
	
	/**
	 * 首页
	 */
	public void index() {
		Member member = memberService.getCurrent();
		setAttr("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, member, null, null, null, null, null, null, false));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, member, null, null, null, null, null, null, null));
		setAttr("messageCount", messageService.count(member, false));
		setAttr("couponCodeCount", couponCodeService.count(null, member, null, null, false));
		setAttr("favoriteCount", goodsService.count(null, member, null, null, null, null, null));
		setAttr("productNotifyCount", productNotifyService.count(member, null, null, null));
		setAttr("reviewCount", reviewService.count(member, null, null, null));
		setAttr("consultationCount", consultationService.count(member, null, null));
		setAttr("newOrders", orderService.findList(null, null, member, null, null, null, null, null, null, null, NEW_ORDER_COUNT, null, null));
		// wap
		setAttr("memberPendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, member, null, null, null, null, null, null, null));
		setAttr("memberPendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, member, null, null, null, null, null, null, null));
		setAttr("memberReceivedOrderCount", orderService.count(null, Order.Status.received, member, null, null, null, null, null, null, null));
		setAttr("pendingReviewCount", reviewService.count(member, false));
		setAttr("member", member);
		setAttr("title" , "会员中心");
		render("/wap/member/index.ftl");
	}
	
	
	/**
	 * 上传
	 */
	public void upload() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		
		Member member = memberService.getCurrent();
		Map<String, Object> data = new HashMap<String, Object>();
		if (member == null) {
			data.put(MESSAGE, "当前用户不能为空!");
			data.put(STATUS, ERROR);
			renderJson(data);
			return;
		}
		if (fileType == null || file == null || file.getFile().length() <= 0) {
			data.put(MESSAGE, "请选择选图片");
			data.put(STATUS, ERROR);
			renderJson(data);
			return;
		}
		if (!fileService.isValid(fileType, file)) {
			data.put(MESSAGE, Message.warn("admin.upload.invalid"));
			data.put(STATUS, ERROR);
			renderJson(data);
			return;
		}
		String url = fileService.upload(fileType, file, false);
		if (StringUtils.isEmpty(url)) {
			data.put(MESSAGE, Message.warn("admin.upload.error"));
			data.put(STATUS, ERROR);
			renderJson(data);
			return;
		}
		member.setAvatar(url);
		memberService.update(member);
		data.put(MESSAGE, "上传成功!");
		data.put(STATUS, SUCCESS);
		renderJson(data);
	}
}
