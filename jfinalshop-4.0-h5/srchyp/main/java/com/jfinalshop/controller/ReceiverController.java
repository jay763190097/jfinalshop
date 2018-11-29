package com.jfinalshop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinalshop.config.LoginInterceptor;
import com.jfinalshop.domains.AddressResponse;
import com.jfinalshop.kit.Checker;
import com.jfinalshop.kit.HttpUtils;
import com.jfinalshop.kit.PhoneSpecification;
import com.jfinalshop.model.Receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Before(LoginInterceptor.class)
@ControllerBind(controllerKey = "/receiver")
public class ReceiverController extends BaseController {
    Cache cache = Redis.use();
    public void index(){
        String token = getSession().getAttribute("username_token").toString();
        String username = (String) getSession().getAttribute("username");
        //String token = "ae8b877045964fb39c831b7aeaf800466gbd4l";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",token);
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("addresslist_url"),map);
            AddressResponse addressResponse = JSON.parseObject(res,AddressResponse.class);

            if (addressResponse.getData().size() == 0){
                setAttr("address",null);
            }else {
               // String username = (String) getSession().getAttribute("username");
                setAttr("address",addressResponse.getData());
                cache.set(username+"_address",addressResponse.getData());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        render("/pages/shipping-address.html");
    }

    public void addAddress(){
        render("/pages/add-address.html");
    }

    public void addSubmit(){
        String member_name= getPara("member_name");
        String phone= getPara("phone");
        String area_name= getPara("area_name");
        String address= getPara("address");
        String check_switch= getPara("check_switch");
        if (!Checker.checkIntegrity(member_name,area_name,address)||!PhoneSpecification.isPhoneLegal(phone)){
            setAttr("error","参数不能为空或手机号格式不正确");
            render("/pages/add-address.html");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receiver.consignee",member_name);
        map.put("receiver.phone",phone);
        map.put("receiver.area_name",area_name);
        map.put("receiver.address",address);
        if ("on".equals(check_switch)) {
            map.put("isDefault", true);
        }
        //正式
       map.put("token",getSession().getAttribute("username_token"));
        // map.put("token","ae8b877045964fb39c831b7aeaf800466gbd4l");
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("add_address_url"),map);
            renderJson(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void modifyAddress(){
        String username = (String) getSession().getAttribute("username");
        List<Receiver> data= cache.get(username+"_address");
        long id = getParaToLong("id");
        for (Receiver x:data) {
            if (x.getId() == id){
                    setAttr("address",x);
            }
        render("/pages/modify-address.html");
        }
    }

    public void modifySubmit(){
        String member_name= getPara("member_name");
        String phone= getPara("phone");
        String area_name= getPara("area_name");
        String address= getPara("address");
        String check_switch= getPara("check_switch");
        String  receiverid=getPara("id");
        if (!Checker.checkIntegrity(member_name,area_name,address)||!PhoneSpecification.isPhoneLegal(phone)){
            setAttr("error","参数不能为空或手机号格式不正确");
            render("/pages/modify-address.html");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receiver.consignee",member_name);
        map.put("receiver.phone",phone);
        map.put("receiver.area_name",area_name);
        map.put("receiver.address",address);
        map.put("receiver.id",receiverid);
        if ("on".equals(check_switch)) {
            map.put("isDefault", true);
        }
        //正式
        map.put("token",getSession().getAttribute("username_token"));
        // map.put("token","ae8b877045964fb39c831b7aeaf800466gbd4l");
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("modify_address_url"),map);
            renderJson(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleAddress(){
        String id = getPara("id");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id",id);
        //正式
        map.put("token",getSession().getAttribute("username_token"));
        //map.put("token","ae8b877045964fb39c831b7aeaf800466gbd4l");
        String res = null;
        try {
            res = HttpUtils.POSTMethod(PropKit.get("dele_address_url"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderJson(res);
    }

    public void myProfile(){
        String token = getSession().getAttribute("username_token").toString();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",token);
        String res = null;
        try {
            res = HttpUtils.POSTMethod(PropKit.get("myProfile_adress"),map);
            JSONObject jsonObject = JSONObject.parseObject(res);
            String code = jsonObject.getString("code");
            String datum = jsonObject.getString("datum");
            JSONObject result = JSONObject.parseObject(datum);
            String url = result.getString("avatar");
            String gender = result.getString("gender");
            String nickname = result.getString("nickname");
            String username = result.getString("username");
            setAttr("url",url);
            setAttr("gender",gender);
            setAttr("token",token);
            setAttr("nickname",nickname);
            setAttr("username",username);
            render("/pages/host-infor.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public void edProfile(){
        String token = getSession().getAttribute("username_token").toString();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",token);
        String response = null;
        try {
            response = HttpUtils.POSTMethod(PropKit.get("edProfile_adress"),map);
            JSONObject jsonObject = JSONObject.parseObject(response);
            String datum = jsonObject.getString("datum");
            JSONObject result = JSONObject.parseObject(datum);
            String url = result.getString("avatar");
            String gender = result.getString("gender");
            String nickname = result.getString("nickname");
            String username = result.getString("username");
            setAttr("url",url);
            setAttr("gender",gender);
            setAttr("nickname",nickname);
            setAttr("username",username);
            render("/pages/host-infor.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
