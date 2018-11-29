package com.jfinalshop.controller.shop;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 评论
 * 
 *
 */
@ControllerBind(controllerKey = "/review")
@Before(ThemeInterceptor.class)
public class ReviewController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ReviewService reviewService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private MemberService memberService;

	/**
	 * 发表
	 */
	public void add() {
		Long goodsId = getParaToLong(0);
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			throw new ResourceNotFoundException();
		}

		setAttr("goods", goods);
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/${theme}/review/add.ftl");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long goodsId = getParaToLong(0);
		Integer pageNumber = getParaToInt("pageNumber");
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("goods", goods);
		setAttr("page", reviewService.findPage(null, goods, null, true, pageable));
		render("/shop/${theme}/review/content.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		String captcha = getPara("captcha");
		Long goodsId = getParaToLong("goodsId");
		Integer score = getParaToInt("score");
		String content = getPara("content");
		HttpServletRequest request = getRequest();
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			renderJson(Message.error("shop.review.disabled"));
			return;
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}

		Member member = memberService.getCurrent();
		if (!Setting.ReviewAuthority.anyone.equals(setting.getReviewAuthority()) && member == null) {
			renderJson(Message.error("shop.review.accessDenied"));
			return;
		}
		if (member != null && !reviewService.hasPermission(member, goods)) {
			renderJson(Message.error("shop.review.noPermission"));
			return;
		}

		Review review = new Review();
		review.setScore(score);
		review.setContent(content);
		review.setIp(request.getRemoteAddr());
		review.setMemberId(member.getId());
		review.setProductId(goods.getId());
		if (setting.getIsReviewCheck()) {
			review.setIsShow(false);
			reviewService.save(review);
			renderJson(Message.success("shop.review.check"));
		} else {
			review.setIsShow(true);
			reviewService.save(review);
			renderJson(Message.success("shop.review.success"));
		}
	}

}