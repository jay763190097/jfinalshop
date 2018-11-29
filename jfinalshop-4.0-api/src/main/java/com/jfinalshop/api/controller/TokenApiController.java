package com.jfinalshop.api.controller;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 微信 AccessToken
 * 
 */
@ControllerBind(controllerKey = "/api/tokenApi")
public class TokenApiController extends Controller {
	private static Logger logger = LoggerFactory.getLogger(TokenApiController.class);
	@Inject
	private PluginService pluginService;
	@Inject
	private MemberService memberService;
	
	/**
	 * 授权回调页面域名
	 * 
	 */
	public void codeNotify() {
		String code = getPara("code"); 
		String state = getPara("state");
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		
		// 通过code获取access_token
		SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(pluginConfig.getAttribute("appid"), pluginConfig.getAttribute("appSecret"), code);
		String openId = snsAccessToken.getOpenid();
		
		Member member = null;
		int index = StringUtils.indexOf(state, ",");
		if (StrKit.notBlank(openId) && 0 < index) {
			Long id = Long.valueOf(StringUtils.substring(state, index + 1));
			member = memberService.find(id);
		}
		if (member != null) {
			member.setOpenId(openId);
			memberService.update(member);
		}
		redirect("/web/?#/" + StringUtils.substring(state, 0, index));
	}
}
