package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.Shipping;

/**
 * Dao - 发货单
 * 
 * 
 */
public class ShippingDao extends BaseDao<Shipping> {
	
	/**
	 * 构造方法
	 */
	public ShippingDao() {
		super(Shipping.class);
	}
	
	/**
	 * 根据编号查找发货单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 发货单，若不存在则返回null
	 */
	public Shipping findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String sql = "SELECT * FROM shipping WHERE sn = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}

}