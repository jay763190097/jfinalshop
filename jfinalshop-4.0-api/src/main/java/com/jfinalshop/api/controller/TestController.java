package com.jfinalshop.api.controller;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.api.common.bean.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerBind(controllerKey = "/api/test")
public class TestController extends BaseAPIController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    public void index(){
        logger.info("===================");
        renderJson(new BaseResponse("测试成功！"));
    }

}

