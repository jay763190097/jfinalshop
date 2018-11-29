package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.service.TemplateService;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.Configuration;

/**
 * Controller - 模板
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/template")
public class TemplateController extends BaseController {

	private Configuration freeMarkerConfigurer = FreeMarkerRender.getConfiguration();
	@Inject
	private TemplateService templateService;

	/**
	 * 编辑
	 */
	public void edit() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			redirect(ERROR_VIEW);
			return;
		}
		setAttr("templateConfig", SystemUtils.getTemplateConfig(id));
		setAttr("content", templateService.read(id));
		render("/admin/template/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String id = getPara("id");
		String content = getPara("content");
		if (StringUtils.isEmpty(id) || content == null) {
			redirect(ERROR_VIEW);
			return;
		}
		templateService.write(id, content);
		freeMarkerConfigurer.clearTemplateCache();
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/template/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		String typeName = getPara("type");
		TemplateConfig.Type type = StrKit.notBlank(typeName) ? TemplateConfig.Type.valueOf(typeName) : null;
		setAttr("type", type);
		setAttr("templateConfigs", SystemUtils.getTemplateConfigs(type));
		setAttr("types", TemplateConfig.Type.values());
		render("/admin/template/list.ftl");
	}

}