package com.jfinalshop.api.controller.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 评论
 * 
 */
@ControllerBind(controllerKey = "/api/member/review")
@Before(TokenInterceptor.class)
public class ReviewAPIController extends BaseAPIController {
    private static Logger logger = LoggerFactory.getLogger(ReviewAPIController.class);
	// 保留
}
