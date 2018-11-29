package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.Permission;
import com.jfinalshop.model.PermissionRole;
import com.jfinalshop.model.Role;

/**
 * Service - 角色
 * 
 * 
 */
@Singleton
public class RoleService extends BaseService<Role> {

	/**
	 * 构造方法
	 */
	public RoleService() {
		super(Role.class);
	}
	
	/**
	 * 保存
	 * 
	 */
	public Role save(Role role) {
		super.save(role);
		List<Permission> permissions = role.getPermissions();
		if (CollectionUtils.isNotEmpty(permissions)) {
			for (Permission permission : role.getPermissions()) {
				PermissionRole permissionRole = new PermissionRole();
				permissionRole.setPermissions(permission.getId());
				permissionRole.setRoles(role.getId());
				permissionRole.save();
			}
		}
		return role;
	}

	/**
	 * 更新
	 * 
	 */
	public Role update(Role role) {
		super.update(role);
		List<Permission> permissions = role.getPermissions();
		if (CollectionUtils.isNotEmpty(permissions)) {
			PermissionRole.dao.delete(role.getId());
			for (Permission permission : role.getPermissions()) {
				PermissionRole permissionRole = new PermissionRole();
				permissionRole.setPermissions(permission.getId());
				permissionRole.setRoles(role.getId());
				permissionRole.save();
			}
		}
		return role;
	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(Role role) {
		super.delete(role);
	}

}