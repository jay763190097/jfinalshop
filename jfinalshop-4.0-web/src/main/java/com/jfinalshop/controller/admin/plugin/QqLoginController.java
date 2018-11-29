package com.jfinalshop.controller.admin.plugin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.qqLogin.QqLoginPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - QQ登录
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/login_plugin/qq_login")
public class QqLoginController extends BaseController {

	private QqLoginPlugin qqLoginPlugin = new QqLoginPlugin();
	private PluginConfigService pluginConfigService = new PluginConfigService();

	/**
	 * 安装
	 */
	public void install() {
		if (!qqLoginPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(qqLoginPlugin.getId());
			pluginConfig.setIsEnabled(false);
			//pluginConfig.setAttributes(null);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (qqLoginPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(qqLoginPlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = qqLoginPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/admin/plugin/qqLogin/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String loginMethodName = getPara("loginMethodName");
		String oauthKey = getPara("oauthKey");
		String oauthSecret = getPara("oauthSecret");
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer orders = getParaToInt("orders");
		
		PluginConfig pluginConfig = qqLoginPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(LoginPlugin.LOGIN_METHOD_NAME_ATTRIBUTE_NAME, loginMethodName);
		attributes.put("oauthKey", oauthKey);
		attributes.put("oauthSecret", oauthSecret);
		attributes.put(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		attributes.put(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setAttributes(JSON.toJSONString(attributes));
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(orders);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/login_plugin/list.jhtml");
	}

}