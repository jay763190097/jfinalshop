package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Permission;
import com.jfinalshop.service.PermissionService;

/**
 * Controller - 权限
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/permission")
public class PermissionController extends BaseController {
	
	@Inject
	private PermissionService permissionService;
	private Permission permission;
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", permissionService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/permission/list.ftl");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/permission/add.ftl");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		permission = getModel(Permission.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		permission.setIsEnabled(isEnabled);
		permissionService.save(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/permission/list.jhtml");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("permission", permissionService.find(id));
		render("/admin/permission/edit.ftl");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		permission = getModel(Permission.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		permission.setIsEnabled(isEnabled);
		permissionService.update(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/permission/list.jhtml");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Permission permission = permissionService.find(id);
				if (permission != null && (permission.getRoles() != null && !permission.getRoles().isEmpty())) {
					renderJson(Message.error("admin.permission.deleteExistNotAllowed", permission.getName()));
					return;
				}
			}
			permissionService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}
