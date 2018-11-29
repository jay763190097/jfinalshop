package com.jfinalshop.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * Interceptor - 验证
 * 
 * 
 */
public class ValidateInterceptor implements Interceptor {

	/** 错误消息 */
	private static final String ERROR_MESSAGE = "illegal access!";

	/** 默认白名单 */
	private static final Whitelist DEFAULT_WHITELIST = Whitelist.none();

	/** 白名单 */
	private Whitelist whitelist = DEFAULT_WHITELIST;

	/**
	 * 请求前处理
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            处理器
	 * @return 是否继续执行
	 */
	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		HttpServletRequest request = controller.getRequest();
		HttpServletResponse response = controller.getResponse();
		if (!isValid(request)) {
			String requestType = request.getHeader("X-Requested-With");
			if (StringUtils.equalsIgnoreCase(requestType, "XMLHttpRequest")) {
				response.addHeader("validateStatus", "accessDenied");
			}
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			controller.renderJson(false);
			return;
		}
		controller.renderJson(true);
	}
	
	
	/**
	 * 白名单验证
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 验证是否通过
	 */
	private boolean isValid(HttpServletRequest request) {
		for (Object values : request.getParameterMap().values()) {
			if (values != null) {
				for (String value : (String[]) values) {
					if (!Jsoup.isValid(value, whitelist)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 获取白名单
	 * 
	 * @return 白名单
	 */
	public Whitelist getWhitelist() {
		return whitelist;
	}

	/**
	 * 设置白名单
	 * 
	 * @param whitelist
	 *            白名单
	 */
	public void setWhitelist(Whitelist whitelist) {
		this.whitelist = whitelist;
	}

	

}