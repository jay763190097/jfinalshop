package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderItemService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 评论
 * 
 *
 */
@ControllerBind(controllerKey = "/wap/member/review")
@Before(WapMemberInterceptor.class)
public class ReviewController extends BaseController {
	
	@Inject
	private FileService fileService;
	@Inject
	private MemberService memberService;
	@Inject
	private OrderItemService orderItemService;
	@Inject
	private ReviewService reviewService;
	
	/**
	 * 上传
	 */
	public void upload() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		
		Map<String, Object> data = new HashMap<String, Object>();
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
		data.put(MESSAGE, "上传成功!");
		data.put(STATUS, SUCCESS);
		data.put("url", url);
		renderJson(data);
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Boolean isReview = getParaToBoolean("isReview", false);
		
		Pageable pageable = new Pageable();
		Member member = memberService.getCurrent();
		setAttr("orderItems", reviewService.findPendingOrderItems(member, isReview, pageable));
		setAttr("isReview", isReview);
		setAttr("title" , "待评价交易 - 会员中心");
		render("/wap/member/review/list.ftl");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		Long id = getParaToLong("id");
		OrderItem orderItem = orderItemService.find(id);
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		if (orderItem == null) {
			throw new ResourceNotFoundException();
		}
		
		setAttr("orderItem", orderItem);
		setAttr("goods", orderItem.getProduct().getGoods());
		setAttr("title" , "评价交易 - 会员中心");
		render("/wap/member/review/add.ftl");
	}
	
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Long id = getParaToLong("id");
		Integer score = getParaToInt("score");
		String content = getPara("content");
		String [] images = getParaValues("images");
		
		OrderItem orderItem = orderItemService.find(id);
		
		Res resZh = I18n.use();
		Setting setting = SystemUtils.getSetting();
		Map<String, String> map = new HashMap<String, String>();
		if (!setting.getIsReviewEnabled()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.review.disabled"));
			renderJson(map);
			return;
		}
		
		if (orderItem == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "评价商品不能为空!");
			renderJson(map);
			return;
		}
		
		Member member = memberService.getCurrent();
		if (!Setting.ReviewAuthority.anyone.equals(setting.getReviewAuthority()) && member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.review.accessDenied"));
			renderJson(map);
			return;
		}
		if (orderItem.getIsReview()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.review.noPermission"));
			renderJson(map);
			return;
		}
		orderItem.setIsReview(true);
		orderItem.update();
		
		Review review = new Review();
		review.setScore(score);
		review.setContent(content);
		review.setImages(JSONArray.toJSONString(images));
		review.setIp(getRequest().getRemoteAddr());
		review.setMemberId(member.getId());
		review.setProductId(orderItem.getProductId());
		review.setGoodsId(orderItem.getProduct().getGoodsId());
		review.setOrderItemId(orderItem.getId());
		if (setting.getIsReviewCheck()) {
			review.setIsShow(false);
			reviewService.save(review);
			map.put(STATUS, SUCCESS);
			map.put(MESSAGE, resZh.format("shop.review.check"));
			renderJson(map);
		} else {
			review.setIsShow(true);
			reviewService.save(review);
			map.put(STATUS, SUCCESS);
			map.put(MESSAGE, resZh.format("shop.review.success"));
			renderJson(map);
		}
	}
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Review review = reviewService.find(id);
		
		setAttr("review", review);
		setAttr("title" , "评价详情 - 会员中心");
		render("/wap/member/review/view.ftl");
	}
	
	
}
