package com.jfinalshop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.JFinal;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinalshop.domains.LoginResponse;
import com.jfinalshop.kit.Checker;
import com.jfinalshop.kit.HttpUtils;
import com.jfinalshop.util.WebUtils;

import java.util.HashMap;
import java.util.Map;

@ControllerBind(controllerKey = "/account")
public class AccountController extends BaseController  {
    Cache cache = Redis.use();
    public void index(){
        render("/pages/login.html");
    }
    public void logon(){
        render("/pages/login-phone.html");
    }
    //登陆提交
    public void submit(){
        String username = getPara("username");
        String smscode = getPara("smscode");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!Checker.checkIntegrity(username,smscode)){
           setAttr("error","手机号或验证码不能为空");
           render("/pages/login-phone.html");
           return;
        }
        map.put("username",username);
        map.put("smsCode",smscode);
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("login_url"),map);
            LoginResponse loginResponse = JSON.parseObject(res,LoginResponse.class);
            if (2 == loginResponse.getCode()){
                setAttr("error",loginResponse.getMessage());
                render("/pages/login-phone.html");;
                return;
            } if(1 ==loginResponse.getCode()){
                getSession().setAttribute("username",username);
                getSession().setAttribute("username_id",loginResponse.getInfo().getId());
                getSession().setAttribute("username_token",loginResponse.getToken());
                String cartkeys = loginResponse.getCartkey();
                cache.set(username+"_cartkey",cartkeys);
                String cartKey = WebUtils.getCookie(getRequest(),"cartKey")== null?null:WebUtils.getCookie(getRequest(),"cartKey");
                if(cartKey==null){
                    cartKey="undefined";
                }
                map.clear();
                map.put("token",loginResponse.getToken());
                map.put("cartKey",cartKey);
                res = cartKey ==null?null:HttpUtils.POSTMethod(PropKit.get("megcart_url"),map);
                JSONObject json =  JSONObject.parseObject(res);
                String code = json.getString("code");
                if (res!=null&&"1".equals(code)){
                    WebUtils.removeCookies(getRequest(),getResponse(),"cartKey");
                }
                redirect("/index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void loginOut(){
        cache.del(getSession().getAttribute("username")+"_address");
        getSession().removeAttribute("username");
        getSession().removeAttribute("username_id");
        getSession().removeAttribute("username_token");
        redirect("/index");
    }


}
