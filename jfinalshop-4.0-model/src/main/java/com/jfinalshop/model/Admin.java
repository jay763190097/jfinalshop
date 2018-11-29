package com.jfinalshop.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseAdmin;

/**
 * Model - 管理员
 * 
 * 
 */
public class Admin extends BaseAdmin<Admin> {
	private static final long serialVersionUID = -4448350375489279691L;
	public static final Admin dao = new Admin();
	
	/** "登录令牌"Cookie名称 */
	public static final String LOGIN_TOKEN_COOKIE_NAME = "adminLoginToken";
	
	/** 角色 */
	private List<Role> roles = new ArrayList<Role>();
	
	/**
	 * 根据用户名查找
	 * @param username
	 * @return
	 */
	public Admin getAdminByLoginName(String username) {
		String sql = "SELECT * FROM admin WHERE username = ? ";
		return findFirst(sql, username);
	}
	
	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		if (CollectionUtils.isEmpty(roles)) {
			String sql = "SELECT r.*  FROM role r LEFT JOIN admin_role ar ON r.id = ar.roles WHERE ar.admins = ?";
			roles = Role.dao.find(sql, getId());
		}
		return roles;
	}
	
	/**
	 * 设置角色
	 * 
	 * @param roles
	 *            角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	
}
