package com.jfinalshop.controller;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jfinal.kit.PropKit;
import com.jfinalshop.Utils.HttpUtils;
import com.jfinalshop.domains.DatumResponse;
import com.jfinal.ext.route.ControllerBind;

/**
 * Controller - 货品
 *
 *
 */
@ControllerBind(controllerKey = "/goods")
//@Before(ThemeInterceptor.class)
public class GoodsController extends BaseController {

	public void index(){
		render("/pages/index.html");
	}
	/**
	 * 详情
	 */
	public void detail() throws Exception {
		String id = getPara("id");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id",id);
		String res = HttpUtils.POSTMethod(PropKit.get("goodDetail"),map);
		JSONObject respons = JSONObject.parseObject(res);
		String datum = respons.getString("datum");
		JSONObject datums = JSONObject.parseObject(datum);
		String gooods = datums.getString("goods");
		JSONObject goood = JSONObject.parseObject(gooods);
		String specification_items = goood.getString("specification_items");
		JSONArray items = JSONArray.parseArray(specification_items);
		setAttr("datum", datums);
		setAttr("items", items);
		render("/pages/shops-detail.html");
	}

	/**
	 * 搜索
	 */

}