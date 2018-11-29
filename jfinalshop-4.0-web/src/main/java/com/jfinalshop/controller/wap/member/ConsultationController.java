package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 咨询
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/consultation")
@Before(WapMemberInterceptor.class)
public class ConsultationController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;
	
	@Inject
	private MemberService memberService;;
	@Inject
	private ConsultationService consultationService;;
	@Inject
	private GoodsService goodsService;
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pages", consultationService.findPage(member, null, null, pageable));
		setAttr("title" , "我的咨询 - 会员中心");
		render("/wap/member/consultation/list.ftl");
	}
	
	
	/**
	 * 增加咨询
	 */
	public void add() {
		Long id = getParaToLong("id");
		Goods goods = goodsService.find(id);
		setAttr("goods", goods);
		setAttr("title" , "商品咨询");
		render("/wap/member/consultation/add.ftl");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long id = getParaToLong("id");
		String content = getPara("question");
		
		Map<String, String> map = new HashMap<String, String>();
		Member member = memberService.getCurrent();
		if (member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前用户不能是空!");
			renderJson(map);
			return;
		}
		
		Goods goods = goodsService.find(id);
		if (goods == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前商品不能是空!");
			renderJson(map);
			return;
		}
		Consultation consultation = new Consultation();
		consultation.setGoodsId(goods.getId());
		consultation.setContent(content);
		consultation.setMemberId(member.getId());
		consultation.setIsShow(true);
		consultation.setIp(getRequest().getRemoteHost());
		consultationService.save(consultation);
		map.put(STATUS, SUCCESS);
		map.put("referer", "/wap/goods/detail.jhtml?id=" + goods.getId());
		renderJson(map);
	}
	

}
