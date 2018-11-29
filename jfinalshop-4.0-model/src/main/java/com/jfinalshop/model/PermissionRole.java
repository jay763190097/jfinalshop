package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePermissionRole;

/**
 * 权限角色
 * 
 */
public class PermissionRole extends BasePermissionRole<PermissionRole> {
	private static final long	serialVersionUID	= 6281329610876121763L;
	public static final PermissionRole dao = new PermissionRole();
	
	/**
	 * 根据角色删除
	 * @param articles
	 * @return
	 */
	public boolean delete(Long roles) {
		return Db.deleteById("permission_role", "roles", roles);
	}
}
