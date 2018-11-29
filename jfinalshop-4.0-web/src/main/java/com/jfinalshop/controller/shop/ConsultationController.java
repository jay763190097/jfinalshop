package com.jfinalshop.controller.shop;

import java.util.UUID;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 咨询
 * 
 * 
 */
@ControllerBind(controllerKey = "/consultation")
@Before(ThemeInterceptor.class)
public class ConsultationController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ConsultationService consultationService;
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
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			throw new ResourceNotFoundException();
		}

		setAttr("goods", goods);
		setAttr("captchaId", UUID.randomUUID().toString());
		render("/shop/${theme}/consultation/add.ftl");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long goodsId = getParaToLong(0);
		Integer pageNumber = getParaToInt("pageNumber");
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("goods", goods);
		setAttr("page", consultationService.findPage(null, goods, true, pageable));
		render("/shop/${theme}/consultation/content.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		String captcha = getPara("captcha");
		Long goodsId = getParaToLong("goodsId");
		String content = getPara("content");

		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsConsultationEnabled()) {
			renderJson(Message.error("shop.consultation.disabled"));
			return;
		}
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		
		Member member = memberService.getCurrent();
		if (!Setting.ConsultationAuthority.anyone.equals(setting.getConsultationAuthority()) && member == null) {
			renderJson(Message.error("shop.consultation.accessDenied"));
		}

		Consultation consultation = new Consultation();
		consultation.setContent(content);
		consultation.setIp(getRequest().getRemoteAddr());
		consultation.setMemberId(member.getId());
		consultation.setGoodsId(goods.getId());
		consultation.setForConsultation(null);
		consultation.setReplyConsultations(null);
		if (setting.getIsConsultationCheck()) {
			consultation.setIsShow(false);
			consultationService.save(consultation);
			renderJson(Message.success("shop.consultation.check"));
		} else {
			consultation.setIsShow(true);
			consultationService.save(consultation);
			renderJson(Message.success("shop.consultation.success"));
		}
	}

}