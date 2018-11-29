package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseAdminRole;

/**
 * Model - 用户角色
 * 
 * 
 */
public class AdminRole extends BaseAdminRole<AdminRole> {
	private static final long serialVersionUID = 2636136668999664047L;
	public static final AdminRole dao = new AdminRole();
	
	/**
	 * 根据admins删除角色
	 * @param admins
	 * @return
	 */
	public boolean delete(Long admins) {
		return Db.deleteById("admin_role", "admins", admins);
	}
}
