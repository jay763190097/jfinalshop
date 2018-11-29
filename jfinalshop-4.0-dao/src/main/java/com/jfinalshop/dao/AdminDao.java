package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Admin;


/**
 * Dao - 管理员
 * 
 * 
 */
public class AdminDao extends BaseDao<Admin> {
	
	/**
	 * 构造方法
	 */
	public AdminDao() {
		super(Admin.class);
	}

	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM admin WHERE LOWER(username) = LOWER(?)";
		Long count = Db.queryLong(sql, username);
		return count > 0;
	}

	/**
	 * 根据用户名查找管理员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	public Admin findByUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM admin WHERE LOWER(username) = LOWER(?)";
			return modelManager.findFirst(sql, username);
		} catch (Exception e) {
			return null;
		}
	}

}