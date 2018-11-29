package com.jfinalshop.controller.wap;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.LogKit;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinalshop.interceptor.WapInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.PluginService;


/**
 * Controller - 微信openId
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/token")
@Before(WapInterceptor.class)
public class TokenController extends BaseController {
	
	@Inject
	private PluginService pluginService;

	/**
	 * 授权回调页面域名
	 * 
	 */
	public void tokenNotify() {
		String code = getPara("code"); // 微信CODE
		String url_forward = getPara("state");
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		
		// 通过code获取access_token
		SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(pluginConfig.getAttribute("appid"), pluginConfig.getAttribute("appSecret"), code);
		String openId = snsAccessToken.getOpenid();
		LogKit.info("tokenNotify -> openId:" + openId);
		setSessionAttr(Member.OPEN_ID, openId);
		redirect(url_forward);
	}
	
	
}
