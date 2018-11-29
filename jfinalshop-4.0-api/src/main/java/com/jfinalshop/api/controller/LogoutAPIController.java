package com.jfinalshop.api.controller;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员注销
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/logout")
@Before(TokenInterceptor.class)
public class LogoutAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(LogoutAPIController.class);
	
	/**
	 * 注销
	 * 
	 */
	public void index() {
		String token = getPara("token");
		if (StrKit.notBlank(token)) {
			TokenManager.getMe().remove(token);
		}
		renderJson(new DatumResponse("退出成功!"));
	}
	
}
