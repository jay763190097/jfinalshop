package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.model.Tag;

/**
 * Dao - 标签
 * 
 * 
 */
public class TagDao extends OrderEntity<Tag> {
	
	/**
	 * 构造方法
	 */
	public TagDao() {
		super(Tag.class);
	}
	
	/**
	 * 查找标签
	 * 
	 * @param type
	 *            类型
	 * @return 标签
	 */
	public List<Tag> findList(Tag.Type type) {
		String sql = "SELECT * FROM tag WHERE 1 = 1 ";
		if (type != null) {
			sql += "AND type = " + type.ordinal();
		} 
		sql += " ORDER BY orders ASC ";
		return modelManager.find(sql);
	}

}