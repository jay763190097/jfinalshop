package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.model.Navigation;

/**
 * Dao - 导航
 * 
 * 
 */
public class NavigationDao extends OrderEntity<Navigation> {
	
	/**
	 * 构造方法
	 */
	public NavigationDao() {
		super(Navigation.class);
	}
	
	
	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @return 导航
	 */
	public List<Navigation> findList(Navigation.Position position) {
		String sql = "SELECT * FROM `" + getTableName() + "` WHERE 1 = 1 ";
		if (position != null) {
			sql += "AND position = " + position.ordinal();
		} 
		sql += " ORDER BY orders ASC";
		return modelManager.find(sql);
	}

}