package com.jfinalshop.controller;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController{

    public void index(){
        render("/pages/index.html");
    }


}
