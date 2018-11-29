package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.GoodsService;

/**
 * Controller - 商品排名
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/goods_ranking")
public class GoodsRankingController extends BaseController {

	@Inject
	private GoodsService goodsService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		String rankingTypeName = getPara("rankingType");
		Goods.RankingType rankingType = StrKit.notBlank(rankingTypeName) ? Goods.RankingType.valueOf(rankingTypeName) : null;
		if (rankingType == null) {
			rankingType = Goods.RankingType.sales;
		}
		setAttr("rankingTypes", Goods.RankingType.values());
		setAttr("rankingType", rankingType);
		setAttr("pageable", pageable);
		setAttr("page", goodsService.findPage(rankingType, pageable));
		render("/admin/goods_ranking/list.ftl");
	}

}