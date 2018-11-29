package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Admin;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 个人资料
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/profile")
public class ProfileController extends BaseController {

	@Inject
	private AdminService adminService;

	/**
	 * 验证当前密码
	 */
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		if (StringUtils.isEmpty(currentPassword)) {
			renderJson(false);
			return;
		}
		Admin admin = adminService.getCurrent();
		renderJson(HasherKit.match(currentPassword, admin.getPassword()));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("admin", adminService.getCurrent());
		render("/admin/profile/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		String email = getPara("email");
		
		Admin pAdmin = adminService.getCurrent();
		if (StringUtils.isNotEmpty(currentPassword) && StringUtils.isNotEmpty(password)) {
			if (!HasherKit.match(currentPassword, pAdmin.getPassword())) {
				redirect(ERROR_VIEW);
				return;
			}
			HasherInfo passwordInfo = HasherKit.hash(password, Hasher.DEFAULT);
			pAdmin.setPassword(passwordInfo.getHashResult());
			pAdmin.setHasher(passwordInfo.getHasher().value());
			pAdmin.setSalt(passwordInfo.getSalt());
		}
		pAdmin.setEmail(email);
		adminService.update(pAdmin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/profile/edit.jhtml");
	}

}