package com.jfinalshop.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinalshop.config.LoginInterceptor;

import java.util.LinkedHashMap;
import java.util.Map;
@Before(LoginInterceptor.class)
@ControllerBind(controllerKey = "/member")
public class MemberController extends BaseController {

    public void index(){
        setAttr("username",getSession().getAttribute("username"));
        render("/pages/account.html");
    }
    public void list() {
        Cache cache = Redis.use();
        String token = getSessionAttr("username_token");
        String status = getPara("status");
        if(status==null){
            status="";
        }
        Map<String,String> map = new LinkedHashMap<String,String>(10);
        map.put("token",token);
        map.put("status",status);
        map.put("channel",PropKit.get("resource"));
        try {
            String res = HttpKit.post(PropKit.get("order_detail"),map,null);
            JSONObject respons = JSONObject.parseObject(res);
            String code = respons.getString("code");
            String message = respons.getString("message");
            if("2".equals(code)){
                renderJson("message",message);
                render("/pages/my-order.html");
                return;
            }
            String siteName = respons.getString("siteName");
            String datum = respons.getString("datum");
            String  totalRow = JSONObject.parseObject(datum).getString("totalRow");
            String  pageNumber = JSONObject.parseObject(datum).getString("pageNumber");
            String  lastPage = JSONObject.parseObject(datum).getString("lastPage");
            String  firstPage = JSONObject.parseObject(datum).getString("firstPage");
            String  totalPage = JSONObject.parseObject(datum).getString("totalPage");
            String  pageSize = JSONObject.parseObject(datum).getString("pageSize");
            JSONArray list = JSONObject.parseObject(datum).getJSONArray("list");
            setAttr("totalRow",totalRow);
            setAttr("pageNumber",pageNumber);
            setAttr("lastPage",lastPage);
            setAttr("firstPage",firstPage);
            setAttr("totalPage",totalPage);
            setAttr("pageSize",pageSize);
            setAttr("list",list);
            setAttr("token",token);
            setAttr("siteName",siteName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        render("/pages/my-order.html");

    }

    public void viwe(){
        String token = getSessionAttr("username_token");
        String sn = getPara("sn");
        String status = getPara("status");
        Map<String,String> map = new LinkedHashMap<String,String>(10);
        map.put("token",token);
        map.put("sn",sn);
        try {
            String res = HttpKit.post(PropKit.get("orderViwe_url"),map,null);
            JSONObject respons = JSONObject.parseObject(res);
            String datums = respons.getString("datum");
            String datum = JSONObject.parseObject(datums).getString("order");
            String  amount = JSONObject.parseObject(datum).getString("amount");
            String  address = JSONObject.parseObject(datum).getString("address");
            String  name = JSONObject.parseObject(datum).getString("consignee");
            String  phone = JSONObject.parseObject(datum).getString("phone");
            String  shipping_method_name = JSONObject.parseObject(datum).getString("shipping_method_name");
            String  sns = JSONObject.parseObject(datum).getString("sn");
            String items = JSONObject.parseObject(datum).getString("order_items");
            JSONArray jsonArray =JSONArray.parseArray(items);
            setAttr("amount",amount);
            setAttr("status",status);
            setAttr("address",address);
            setAttr("name",name);
            setAttr("phone",phone);
            setAttr("path",shipping_method_name);
            setAttr("sn",sns);
            setAttr("items",jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        render("/pages/order-detail.html");
    }



}
