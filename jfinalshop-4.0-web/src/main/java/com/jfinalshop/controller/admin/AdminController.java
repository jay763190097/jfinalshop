package com.jfinalshop.controller.admin;

import java.util.List;
import java.util.UUID;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 管理员
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/admin")
public class AdminController extends BaseController {

	@Inject
	private AdminService adminService;
	@Inject
	private RoleService roleService;

	/**
	 * 检查用户名是否存在
	 */
	public void checkUsername() {
		String username = getPara("admin.username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		renderJson(!adminService.usernameExists(username));
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("roles", roleService.findAll());
		render("/admin/admin/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Admin admin = getModel(Admin.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		admin.setIsEnabled(isEnabled);
		Boolean isLocked = getParaToBoolean("isLocked", false);
		admin.setIsLocked(isLocked);
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		if (adminService.usernameExists(admin.getUsername())) {
			redirect(ERROR_VIEW);
			return;
		}
		HasherInfo hasherInfo = HasherKit.hash(admin.getPassword(), Hasher.DEFAULT);
		admin.setPassword(hasherInfo.getHashResult());
		admin.setHasher(hasherInfo.getHasher().value());
		admin.setSalt(hasherInfo.getSalt());
		
		//admin.setPassword(DigestUtils.md5Hex(admin.getPassword()));
		admin.setIsLocked(false);
		admin.setLoginFailureCount(0);
		admin.setLockedDate(null);
		admin.setLoginDate(null);
		admin.setLoginIp(null);
		admin.setUsername(StringUtils.lowerCase(admin.getUsername()));
		admin.setEmail(StringUtils.lowerCase(admin.getEmail()));
		admin.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		adminService.save(admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("roles", roleService.findAll());
		setAttr("admin", adminService.find(id));
		render("/admin/admin/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Admin admin = getModel(Admin.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		admin.setIsEnabled(isEnabled);
		Boolean isLocked = getParaToBoolean("isLocked", false);
		admin.setIsLocked(isLocked);
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		Admin pAdmin = adminService.find(admin.getId());
		if (pAdmin == null) {
			redirect(ERROR_VIEW);
			return;
		}
		
		//String rePassword = getPara("rePassword", "");
		if (StringUtils.isNotEmpty(admin.getPassword())) {
			HasherInfo hasherInfo = HasherKit.hash(admin.getPassword(), Hasher.DEFAULT);
			admin.setPassword(hasherInfo.getHashResult());
			admin.setHasher(hasherInfo.getHasher().value());
			admin.setSalt(hasherInfo.getSalt());
			//admin.setPassword(DigestUtils.md5Hex(admin.getPassword()));
		} else {
			admin.setPassword(pAdmin.getPassword());
		}
		if (pAdmin.getIsLocked() && !admin.getIsLocked()) {
			admin.setLoginFailureCount(0);
			admin.setLockedDate(null);
		} else {
			admin.setIsLocked(pAdmin.getIsLocked());
			admin.setLoginFailureCount(pAdmin.getLoginFailureCount());
			admin.setLockedDate(pAdmin.getLockedDate());
		}
		admin.remove("username", "login_date", "login_ip", "lock_key");
		admin.setEmail(StringUtils.lowerCase(admin.getEmail()));
		adminService.update(admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", adminService.findPage(pageable));
		render("/admin/admin/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length >= adminService.count()) {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
			return;
		}
		adminService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}