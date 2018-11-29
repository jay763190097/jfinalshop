package com.jfinalshop.interceptor;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hasor.core.AppContext;
import net.hasor.core.Hasor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.Principal;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.util.SystemUtils;

public class WapMemberInterceptor implements Interceptor {
	
	/** 重定向视图名称前缀 */
	private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	/** "重定向URL"参数名称 */
	private static final String REDIRECT_URL_PARAMETER_NAME = "url_forward";

	/** "会员"属性名称 */
	private static final String MEMBER_ATTRIBUTE_NAME = "member";

	/** 默认登录URL */
	private static final String DEFAULT_LOGIN_URL = "/wap/login.jhtml";

	/** 登录URL */
	private String loginUrl = DEFAULT_LOGIN_URL;

	Prop prop = PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
	private String urlEscapingCharset = prop.get("url_escaping_charset");

	public static final AppContext appContext = Hasor.createAppContext();
	private MemberService memberService = appContext.getInstance(MemberService.class);
	//private PluginService pluginService = appContext.getInstance(PluginService.class);
	
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.setAttr("base", c.getRequest().getContextPath());
		
		HttpServletRequest request = c.getRequest();
		HttpServletResponse response = c.getResponse();
		RequestContextHolder.setRequestAttributes(c.getRequest());
		
		Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		String openId = (String) request.getSession().getAttribute(Member.OPEN_ID);
		
		Member member = null;
		if (StrKit.notBlank(openId)) {
			member = memberService.findByOpenId(openId);
			if (member != null) {
				c.setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			}
		}
		
		if (principal != null || member != null) {
			inv.invoke();
		} else {
			try {
				String requestType = request.getHeader("X-Requested-With");
				if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
					response.addHeader("loginStatus", "accessDenied");
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					c.renderJson(false);
					return;
				} else {
					if (request.getMethod().equalsIgnoreCase("GET")) {
						String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
						//String url = getUrl(request.getContextPath() + loginUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
						//response.sendRedirect(url);
						response.sendRedirect(request.getContextPath() + "/"+ loginUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
					} else {
						//String url = getUrl(request.getContextPath() + loginUrl);
						//response.sendRedirect(url);
						response.sendRedirect(request.getContextPath() + loginUrl);
					}
					c.renderJson(false);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String viewName = inv.getViewPath();
		if (!StringUtils.startsWith(viewName, REDIRECT_VIEW_NAME_PREFIX)) {
			c.setAttr(MEMBER_ATTRIBUTE_NAME, memberService.getCurrent());
		}
	}

	/**
	 * 
	 * @param urlForward
	 * @return
	 */
	/*private String getUrl(String urlForward) {
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		
		Setting setting = SystemUtils.getSetting();
		String notify_url = setting.getSiteUrl() + "/wap/token/tokenNotify.jhtml";
		String url = SnsAccessTokenApi.getAuthorizeURL(pluginConfig.getAttribute("appid"), notify_url, urlForward, true);
		return url;
	}*/
	
}
