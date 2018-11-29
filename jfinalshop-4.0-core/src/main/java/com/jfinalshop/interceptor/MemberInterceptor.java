package com.jfinalshop.interceptor;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hasor.core.AppContext;
import net.hasor.core.Hasor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.render.Render;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.Principal;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.SystemUtils;

/**
 * Interceptor - 会员权限
 * 
 * 
 */
public class MemberInterceptor implements Interceptor {

	/** 重定向视图名称前缀 */
	private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	/** "重定向URL"参数名称 */
	private static final String REDIRECT_URL_PARAMETER_NAME = "redirectUrl";

	/** "会员"属性名称 */
	private static final String MEMBER_ATTRIBUTE_NAME = "member";

	/** 默认登录URL */
	private static final String DEFAULT_LOGIN_URL = "login.jhtml";

	/** 登录URL */
	private String loginUrl = DEFAULT_LOGIN_URL;

	Prop prop = PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
	private String urlEscapingCharset = prop.get("url_escaping_charset");

	public static final AppContext appContext = Hasor.createAppContext();
	private MemberService memberService = appContext.getInstance(MemberService.class);

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
		RequestContextHolder.setRequestAttributes(controller.getRequest());
		Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		if (principal != null) {
			inv.invoke();
			Render render = controller.getRender();
			String view = render.getView();
			if (StringUtils.containsAny(view, CommonAttributes.THEME_NAME)) {
				Setting setting = SystemUtils.getSetting();
				String theme = setting.getTheme();
				controller.setAttr("theme", theme);
				render.setView(StringUtils.replace(render.getView(), CommonAttributes.THEME_NAME, theme));
			}
		} else {
			try {
				String requestType = request.getHeader("X-Requested-With");
				if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
					response.addHeader("loginStatus", "accessDenied");
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					controller.renderJson(false);
					return;
				} else {
					if (request.getMethod().equalsIgnoreCase("GET")) {
						String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
						response.sendRedirect(request.getContextPath() + "/"+loginUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
					} else {
						response.sendRedirect(request.getContextPath() + loginUrl);
					}
					controller.renderJson(false);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		String viewName = inv.getViewPath();
		if (!StringUtils.startsWith(viewName, REDIRECT_VIEW_NAME_PREFIX)) {
			controller.setAttr(MEMBER_ATTRIBUTE_NAME, memberService.getCurrent());
		}
		
	}

	/**
	 * 获取登录URL
	 * 
	 * @return 登录URL
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * 设置登录URL
	 * 
	 * @param loginUrl
	 *            登录URL
	 */
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	

}