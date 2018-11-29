package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseDeliveryCenter;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 发货点
 * 
 * 
 */
public class DeliveryCenter extends BaseDeliveryCenter<DeliveryCenter> {
	private static final long serialVersionUID = -4284520832959174798L;
	public static final DeliveryCenter dao = new DeliveryCenter();
	
	/** 地区 */
	private Area area;
	
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
	 * 持久化前处理
	 */
	public void prePersist() {
		if (getArea() != null) {
			setAreaName(Area.dao.findById(getArea()).getFullName());
		}
	}

	/**
	 * 更新前处理
	 */
	public void preUpdate() {
		if (getArea() != null) {
			setAreaName(Area.dao.findById(getArea()).getFullName());
		}
	}
}
