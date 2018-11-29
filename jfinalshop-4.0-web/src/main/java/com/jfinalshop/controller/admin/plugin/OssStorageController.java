package com.jfinalshop.controller.admin.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.ossStorage.OssStoragePlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 阿里云存储
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/storage_plugin/oss_storage")
public class OssStorageController extends BaseController {

	private OssStoragePlugin ossStoragePlugin = new OssStoragePlugin();
	private PluginConfigService pluginConfigService = enhance(PluginConfigService.class);

	/**
	 * 安装
	 */
	public void install() {
		if (!ossStoragePlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(ossStoragePlugin.getId());
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
		if (ossStoragePlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(ossStoragePlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = ossStoragePlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/admin/plugin/ossStorage/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String endpoint = getPara("endpoint");
		String accessId = getPara("accessId");
		String accessKey = getPara("accessKey");
		String bucketName = getPara("bucketName");
		String urlPrefix = getPara("urlPrefix");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("orders");
		
		PluginConfig pluginConfig = ossStoragePlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("endpoint", endpoint);
		attributes.put("accessId", accessId);
		attributes.put("accessKey", accessKey);
		attributes.put("bucketName", bucketName);
		attributes.put("urlPrefix", StringUtils.removeEnd(urlPrefix, "/"));
		pluginConfig.setAttributes(attributes);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storage_plugin/list.jhtml");
	}

}