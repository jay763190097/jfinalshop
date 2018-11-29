package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.BooleanUtils;

import com.jfinalshop.model.base.BaseShipping;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 发货单
 * 
 * 
 */
public class Shipping extends BaseShipping<Shipping> {
	private static final long serialVersionUID = -3513526226673691332L;
	public static final Shipping dao = new Shipping();
	
	/** 订单 */
	private Order order;
	
	/** 发货项 */
	private List<ShippingItem> shippingItems = new ArrayList<ShippingItem>();
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (ObjectUtils.isEmpty(order)) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}
	/**
	 * 是否是新增 没有保存的
	 * @return
	 */
	public Boolean isNew(){
		if(getId()!=null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
	/**
	 * 获取发货项
	 * 
	 * @return 发货项
	 */
	public List<ShippingItem> getShippingItems() {
		if (CollectionUtils.isEmpty(shippingItems)) {
			String sql = "SELECT * FROM shipping_item WHERE shipping_id = ?";
			shippingItems = ShippingItem.dao.find(sql, getId());
		}
		return shippingItems;
	}

	/**
	 * 设置发货项
	 * 
	 * @param shippingItems
	 *            发货项
	 */
	public void setShippingItems(List<ShippingItem> shippingItems) {
		this.shippingItems = shippingItems;
	}

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getShippingItems() != null) {
			for (ShippingItem shippingItem : getShippingItems()) {
				if (shippingItem != null && shippingItem.getQuantity() != null) {
					quantity += shippingItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return CollectionUtils.exists(getShippingItems(), new Predicate() {
			public boolean evaluate(Object object) {
				ShippingItem shippingItem = (ShippingItem) object;
				return shippingItem != null && BooleanUtils.isTrue(shippingItem.getIsDelivery());
			}
		});
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		setShippingMethod(shippingMethod != null ? shippingMethod.getName() : null);
	}

	/**
	 * 设置物流公司
	 * 
	 * @param deliveryCorp
	 *            物流公司
	 */
	public void setDeliveryCorp(DeliveryCorp deliveryCorp) {
		setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName() : null);
		setDeliveryCorpUrl(deliveryCorp != null ? deliveryCorp.getUrl() : null);
		setDeliveryCorpCode(deliveryCorp != null ? deliveryCorp.getCode() : null);
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		setArea(area != null ? area.getFullName() : null);
	}
	
	/**
	 * 设置操作员
	 * 
	 * @param operator
	 *            操作员
	 */
	public void setOperator(Admin operator) {
		setOperator(operator != null ? operator.getUsername() : null);
	}

}
