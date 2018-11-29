package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.util.Assert;

/**
 * Dao - 快递单模板
 * 
 * 
 */
public class DeliveryTemplateDao extends BaseDao<DeliveryTemplate> {
	
	/**
	 * 构造方法
	 */
	public DeliveryTemplateDao() {
		super(DeliveryTemplate.class);
	}

	/**
	 * 查找默认快递单模板
	 * 
	 * @return 默认快递单模板，若不存在则返回null
	 */
	public DeliveryTemplate findDefault() {
		try {
			String sql = "SELECT * FROM delivery_template WHERE is_default = true";
			return modelManager.findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置默认快递单模板
	 * 
	 * @param deliveryTemplate
	 *            快递单模板
	 */
	public void setDefault(DeliveryTemplate deliveryTemplate) {
		Assert.notNull(deliveryTemplate);

		deliveryTemplate.setIsDefault(true);
		if (deliveryTemplate.getId() == null) {
			String sql = "UPDATE delivery_template SET is_default = false WHERE is_default = true";
			Db.update(sql);
		} else {
			String sql = "UPDATE delivery_template SET is_default = false WHERE is_default = true AND id != ?";
			Db.update(sql, deliveryTemplate.getId());
		}
	}

}