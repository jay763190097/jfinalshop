package com.jfinalshop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinalshop.Utils.HttpUtils;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@ControllerBind(controllerKey = "/cart")
public class CartController extends BaseController {
     MemberService memberService = new MemberService();
    private static Logger logger = LoggerFactory.getLogger(CartController.class);
    //添加购物车并返回cart_key
    public void add() {
        Cache cache = Redis.use();
        String productId = getPara("productId");
        String quantity = getPara("quantity");
        String cartKey = getPara("cartKey");
        Object username = getSession().getAttribute("username");
        if(getSession().getAttribute("username")==null){
            cartKey = WebUtils.getCookie(getRequest(),"cartKey");
        }else {
            cartKey = cache.get(username+"_cartkey");
        }
        if(cartKey==null){
            cartKey="undefined";
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productId",productId);
        map.put("quantity",quantity);
        map.put("cartKey",cartKey);
        map.put("channel",PropKit.get("resource"));
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("addCart_url"),map);
            String code = JSON.parseObject(res).getString("code");
            String message = JSON.parseObject(res).getString("message");
            if("2".equals(code)){
                renderJson("message",message);
                return;
            }
            String datum = JSON.parseObject(res).getString("datum");
            cartKey= JSONObject.parseObject(datum).getString("cart_key");
            if (getSession().getAttribute("username")==null) {
                WebUtils.addCookies(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME, cartKey, Cart.TIMEOUT);
            }else{
                //向redis添加购物车cart_key
                cache.set(username+"_cartkey",cartKey);
            }
            renderJson("message","添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("",e);
        }
    }
    //查询购物车
    public void index() {
        Cache cache = Redis.use();
        String cartKey = null;
        Object username=getSession().getAttribute("username");
        if(getSession().getAttribute("username")==null){
            cartKey = WebUtils.getCookie(getRequest(),"cartKey");
        }else {
            cartKey = cache.get(username+"_cartkey");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        if(cartKey==null){
            cartKey="undefined";
        }
        map.put("cartKey",cartKey);
        map.put("channel",PropKit.get("resource"));
        try {
            String res = HttpUtils.POSTMethod(PropKit.get("cartlist_url"), map);
            String datum = JSON.parseObject(res).getString("datum");
            if(datum==null){
                setAttr("cart", "1");
            }else {
                setAttr("cart", JSON.parseObject(datum));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
        }
        render("/pages/shoppingcart.html");
    }
}
