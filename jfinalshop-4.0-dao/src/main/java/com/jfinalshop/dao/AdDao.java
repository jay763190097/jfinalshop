package com.jfinalshop.dao;

import com.jfinalshop.model.Ad;

/**
 * Dao - 广告
 * 
 * 
 */
public class AdDao extends OrderEntity<Ad> {
	
	/** 
	 * 构造方法
	 */
	public AdDao() {
		super(Ad.class);
	}
}