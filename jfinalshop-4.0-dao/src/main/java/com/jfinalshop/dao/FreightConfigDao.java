package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.FreightConfig;
import com.jfinalshop.model.ShippingMethod;

/**
 * Dao - 运费配置
 * 
 * 
 */
public class FreightConfigDao extends BaseDao<FreightConfig> {
	
	/**
	 * 构造方法
	 */
	public FreightConfigDao() {
		super(FreightConfig.class);
	}
	
	/**
	 * 判断运费配置是否存在
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param area
	 *            地区
	 * @return 运费配置是否存在
	 */
	public boolean exists(ShippingMethod shippingMethod, Area area) {
		if (shippingMethod == null || area == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM freight_config WHERE shipping_method_id = ? AND area_id = ?";
		Long count = Db.queryLong(sql, shippingMethod.getId(), area.getId());
		return count > 0;
	}

	/**
	 * 查找运费配置分页
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param pageable
	 *            分页信息
	 * @return 运费配置分页
	 */
	public Page<FreightConfig> findPage(ShippingMethod shippingMethod, Pageable pageable) {
		String sqlExceptSelect = "FROM freight_config WHERE 1 = 1 ";
		if (shippingMethod != null) {
			sqlExceptSelect += "AND shipping_method_id = " + shippingMethod.getId();
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

}