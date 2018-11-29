package com.jfinalshop.controller.wap;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Order;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.GoodsService;

@ControllerBind(controllerKey = "/wap")
public class IndexController extends BaseController {

	@Inject
	private AdPositionService adPositionService;
	@Inject
	private GoodsService goodsService;
	
	
	/**
	 * 首页
	 */
	public void index() {
		List<Order> orders = new ArrayList<Order>();
		AdPosition adPosition = adPositionService.find(1L);
		//List<Goods> goodsList = goodsService.findList(Goods.Type.general, null, null, null, null, null, null, null, true, true, null, null, null, null, null, null, null, orders, false);
		setAttr("adPosition", adPosition);
		//setAttr("goodsList", goodsList);
		render("/wap/index.ftl");
	}
	
}
