package com.jfinalshop.controller.wap.member;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Pageable;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberFavoriteGoods;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.DateUtils;

/**
 * Controller - 会员中心 - 商品收藏
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/favorite")
@Before(WapMemberInterceptor.class)
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
	@Clear
	public void add() {
		Long goodsId = getParaToLong("sku_id");
		String redirectUrl = getPara("url_forward");
		
		Goods goods = goodsService.find(goodsId);
		Res resZh = I18n.use();
		Map<String, String> map = new HashMap<String, String>();
		if (goods == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前产品为空!");
			map.put("referer", redirectUrl);
			renderJson(map);
			return;
		}

		RequestContextHolder.setRequestAttributes(getRequest());
		Member member = memberService.getCurrent();
		if (member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "请登录后操作");
			map.put("referer", "/wap/login.jhtml?url_forward=" + redirectUrl);
			renderJson(map);
			return;
		}
		if (member.getFavoriteGoods().contains(goods)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.member.favorite.exist"));
			map.put("referer", redirectUrl);
			renderJson(map);
			return;
		}
		if (Member.MAX_FAVORITE_COUNT != null && member.getFavoriteGoods().size() >= Member.MAX_FAVORITE_COUNT) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.member.favorite.addCountNotAllowed", Member.MAX_FAVORITE_COUNT));
			map.put("referer", redirectUrl);
			renderJson(map);
			return;
		}
		
		MemberFavoriteGoods memberFavoriteGoods = new MemberFavoriteGoods();
		memberFavoriteGoods.setFavoriteGoods(goods.getId());
		memberFavoriteGoods.setFavoriteMembers(member.getId());
		memberFavoriteGoods.save();
		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, resZh.format("shop.member.favorite.success"));
		renderJson(map);
	}
	
	/**
	 * 列表
	 */
	public void list() {
		String periodName = getPara("period");
		MemberFavoriteGoods.Period period = StrKit.notBlank(periodName) ? MemberFavoriteGoods.Period.valueOf(periodName) : null;
		
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		
		Date beginDate = null;
		Date endDate = null;
		switch (period) {
		case lastyear:
			beginDate = new Date(0);
			endDate = DateUtils.getEndDayOfYear();
			break;
		case year:
			beginDate = DateUtils.getBeginDayOfYear();
			endDate = DateUtils.getEndDayOfYear();
			break;
		case month:
			beginDate = DateUtils.getBeginDayOfMonth();
			endDate = DateUtils.getEndDayOfMonth();
			break;
		case week:
			beginDate = DateUtils.getBeginDayOfWeek();
			endDate = DateUtils.getEndDayOfWeek();
			break;
		case day:
			beginDate = DateUtils.getDayBegin();
			endDate = DateUtils.getDayEnd();
			break;
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", goodsService.findPage(member, pageable, beginDate, endDate));
		setAttr("closing", period);
		setAttr("title" , "我的收藏夹 - 会员中心");
		render("/wap/member/favorite/list.ftl");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("sku_id");
		Goods goods = goodsService.find(id);
		Map<String, String> map = new HashMap<String, String>();
		if (goods == null) {
			map.put(STATUS, ERROR);
			renderJson(map);
			return;
		}

		Member member = memberService.getCurrent();
		if (!member.getFavoriteGoods().contains(goods)) {
			map.put(STATUS, ERROR);
			renderJson(map);
			return;
		}
		Db.deleteById("member_favorite_goods", "favorite_goods", goods.getId());
		map.put(STATUS, SUCCESS);
		renderJson(map);
	}
	
}
