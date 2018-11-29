package com.jfinalshop.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerBind(controllerKey = "/search")
public class SearchController extends BaseController {
    public static List searlist = new ArrayList();
    public void index(){
        render("/pages/search.html");
    }

    public void secrchKey(){
        String keyword = getPara("keyword");
        System.out.println(keyword);
        Map<String,String> map = new LinkedHashMap<String,String>(10);
        map.put("keyword",keyword);
        map.put("channel",PropKit.get("resource"));
        String response = HttpKit.post(PropKit.get("serch_url"), map, null);
        JSONObject jsonObject =JSONObject.parseObject(response);
        String datas = jsonObject.getString("data");
        JSONArray data = JSONArray.parseArray(datas);
        renderJson("message",data);
    }
}
