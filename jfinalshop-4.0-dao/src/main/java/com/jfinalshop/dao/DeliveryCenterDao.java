package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.util.Assert;

/**
 * Dao - 发货点
 * 
 * 
 */
public class DeliveryCenterDao extends BaseDao<DeliveryCenter> {
	
	/**
	 * 构造方法
	 */
	public DeliveryCenterDao() {
		super(DeliveryCenter.class);
	}
	
	/**
	 * 查找默认发货点
	 * 
	 * @return 默认发货点，若不存在则返回null
	 */
	public DeliveryCenter findDefault() {
		try {
			String sql = "SELECT * FROM delivery_center WHERE is_default = true";
			return modelManager.findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置默认发货点
	 * 
	 * @param deliveryCenter
	 *            发货点
	 */
	public void setDefault(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);

		deliveryCenter.setIsDefault(true);
		if (deliveryCenter.getId() == null) {
			String sql = "UPDATE delivery_center SET is_default = false WHERE is_default = true";
			Db.update(sql);
		} else {
			String sql = "UPDATE delivery_center SET is_default = false WHERE is_default = true AND id != ?";
			Db.update(sql, deliveryCenter.getId());
		}
	}

}