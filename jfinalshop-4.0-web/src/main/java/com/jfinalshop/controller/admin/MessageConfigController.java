package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.MessageConfig;
import com.jfinalshop.service.MessageConfigService;

/**
 * Controller - 消息配置
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/message_config")
public class MessageConfigController extends BaseController {

	@Inject
	private MessageConfigService messageConfigService;

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("messageConfig", messageConfigService.find(id));
		render("/admin/message_config/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		MessageConfig messageConfig = getModel(MessageConfig.class);
		Boolean isMailEnabled = getParaToBoolean("isMailEnabled", false);
		Boolean isSmsEnabled = getParaToBoolean("isSmsEnabled", false);
		messageConfig.setIsMailEnabled(isMailEnabled);
		messageConfig.setIsSmsEnabled(isSmsEnabled);
		messageConfig.remove("type");
		messageConfigService.update(messageConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/message_config/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		setAttr("messageConfigs", messageConfigService.findAll());
		render("/admin/message_config/list.ftl");
	}

}