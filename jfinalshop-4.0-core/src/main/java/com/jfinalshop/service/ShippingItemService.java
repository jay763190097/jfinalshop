package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.ShippingItem;

/**
 * Service - 发货项
 * 
 * 
 */
@Singleton
public class ShippingItemService extends BaseService<ShippingItem> {

	/**
	 * 构造方法
	 */
	public ShippingItemService() {
		super(ShippingItem.class);
	}
}