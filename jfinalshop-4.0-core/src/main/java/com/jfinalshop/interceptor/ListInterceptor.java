package com.jfinalshop.interceptor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinalshop.util.WebUtils;

/**
 * Interceptor - 列表查询
 * 
 * 
 */
public class ListInterceptor implements Interceptor {

	/** 重定向视图名称前缀 */
	private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	/** 列表查询Cookie名称 */
	private static final String LIST_QUERY_COOKIE_NAME = "listQuery";

	/**
	 * 请求后处理
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            处理器
	 * @param modelAndView
	 *            数据视图
	 */
	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		String viewName = inv.getViewPath();
		if (StringUtils.startsWith(viewName, REDIRECT_VIEW_NAME_PREFIX)) {
			String listQuery = WebUtils.getCookie(controller.getRequest(), LIST_QUERY_COOKIE_NAME);
			if (StringUtils.isNotEmpty(listQuery)) {
				if (StringUtils.startsWith(listQuery, "?")) {
					listQuery = listQuery.substring(1);
				}
				if (StringUtils.contains(viewName, "?")) {
					inv.setReturnValue(viewName + "&" + listQuery);
					//modelAndView.setViewName(viewName + "&" + listQuery);
				} else {
					inv.setReturnValue(viewName + "?" + listQuery);
					//modelAndView.setViewName(viewName + "?" + listQuery);
				}
				WebUtils.removeCookie(controller.getRequest(), controller.getResponse(), LIST_QUERY_COOKIE_NAME);
			}
		}
	}

}