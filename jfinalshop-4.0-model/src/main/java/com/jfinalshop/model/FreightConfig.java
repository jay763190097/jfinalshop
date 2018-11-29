package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseFreightConfig;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 运费配置
 * 
 * 
 */
public class FreightConfig extends BaseFreightConfig<FreightConfig> {
	private static final long serialVersionUID = 7945120509410282597L;
	public static final FreightConfig dao = new FreightConfig();
	
	/** 地区 */
	private Area area;

	/** 配送方式 */
	private ShippingMethod shippingMethod;
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (ObjectUtils.isEmpty(area)) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public ShippingMethod getShippingMethod() {
		if (ObjectUtils.isEmpty(shippingMethod)) {
			shippingMethod = ShippingMethod.dao.findById(getShippingMethodId());
		}
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

}
