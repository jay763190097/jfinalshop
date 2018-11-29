package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.FreightConfigDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.FreightConfig;
import com.jfinalshop.model.ShippingMethod;

/**
 * Service - 运费配置
 * 
 * 
 */
@Singleton
public class FreightConfigService extends BaseService<FreightConfig> {

	/**
	 * 构造方法
	 */
	public FreightConfigService() {
		super(FreightConfig.class);
	}
	
	@Inject
	private FreightConfigDao freightConfigDao;
	
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
		return freightConfigDao.exists(shippingMethod, area);
	}

	/**
	 * 判断运费配置是否唯一
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param previousArea
	 *            修改前地区
	 * @param currentArea
	 *            当前地区
	 * @return 运费配置是否唯一
	 */
	public boolean unique(ShippingMethod shippingMethod, Area previousArea, Area currentArea) {
		if (previousArea != null && previousArea.equals(currentArea)) {
			return true;
		}
		return !freightConfigDao.exists(shippingMethod, currentArea);
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
		return freightConfigDao.findPage(shippingMethod, pageable);
	}

}