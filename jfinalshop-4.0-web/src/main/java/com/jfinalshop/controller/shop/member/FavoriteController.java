package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberFavoriteGoods;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 商品收藏
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/favorite")
@Before(MemberInterceptor.class)
public class FavoriteController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private GoodsService goodsService;

	/**
	 * 添加
	 */
	public void add() {
		Long goodsId = getParaToLong("goodsId");
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}

		Member member = memberService.getCurrent();
		if (member.getFavoriteGoods().contains(goods)) {
			renderJson(Message.warn("shop.member.favorite.exist"));
			return;
		}
		if (Member.MAX_FAVORITE_COUNT != null && member.getFavoriteGoods().size() >= Member.MAX_FAVORITE_COUNT) {
			renderJson(Message.warn("shop.member.favorite.addCountNotAllowed", Member.MAX_FAVORITE_COUNT));
			return;
		}
		
		MemberFavoriteGoods memberFavoriteGoods = new MemberFavoriteGoods();
		memberFavoriteGoods.setFavoriteGoods(goods.getId());
		memberFavoriteGoods.setFavoriteMembers(member.getId());
		memberFavoriteGoods.save();
		renderJson(Message.success("shop.member.favorite.success"));
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", goodsService.findPage(member, pageable, null, null));
		setAttr("member", member);
		render("/shop/${theme}/member/favorite/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Goods goods = goodsService.find(id);
		if (goods == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}

		Member member = memberService.getCurrent();
		if (!member.getFavoriteGoods().contains(goods)) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Db.deleteById("member_favorite_goods", "favorite_goods", goods.getId());
		renderJson(SUCCESS_MESSAGE);
	}

}