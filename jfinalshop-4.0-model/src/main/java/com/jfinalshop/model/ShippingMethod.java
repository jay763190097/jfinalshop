package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseShippingMethod;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 配送方式
 * 
 * 
 */
public class ShippingMethod extends BaseShippingMethod<ShippingMethod> {
	private static final long serialVersionUID = 6946157114728483227L;
	public static final ShippingMethod dao = new ShippingMethod();
	
	/** 默认物流公司 */
	private DeliveryCorp defaultDeliveryCorp;
	
	/** 支持支付方式 */
	private List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

	/** 运费配置 */
	private List<FreightConfig> freightConfigs = new ArrayList<FreightConfig>();

	/** 订单 */
	private List<Order> order = new ArrayList<Order>();
	
	/**
	 * 获取默认物流公司
	 * 
	 * @return 默认物流公司
	 */
	public DeliveryCorp getDefaultDeliveryCorp() {
		if (ObjectUtils.isEmpty(defaultDeliveryCorp)) {
			defaultDeliveryCorp = DeliveryCorp.dao.findById(getDefaultDeliveryCorpId());
		}
		return defaultDeliveryCorp;
	}

	/**
	 * 设置默认物流公司
	 * 
	 * @param defaultDeliveryCorp
	 *            默认物流公司
	 */
	public void setDefaultDeliveryCorp(DeliveryCorp defaultDeliveryCorp) {
		this.defaultDeliveryCorp = defaultDeliveryCorp;
	}
	
	/**
	 * 获取支持支付方式
	 * 
	 * @return 支持支付方式
	 */
	public List<PaymentMethod> getPaymentMethods() {
		if (CollectionUtils.isEmpty(paymentMethods)) {
			String sql = "SELECT pm.* FROM `shipping_payment_method` spm LEFT JOIN `payment_method` pm ON spm.`payment_methods` = pm.`id` WHERE spm.`shipping_methods` = ? ORDER BY pm.`orders` ASC";
			paymentMethods = PaymentMethod.dao.find(sql, getId());
		}
		return paymentMethods;
	}

	/**
	 * 设置支持支付方式
	 * 
	 * @param paymentMethods
	 *            支持支付方式
	 */
	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	/**
	 * 获取运费配置
	 * 
	 * @return 运费配置
	 */
	public List<FreightConfig> getFreightConfigs() {
		if (CollectionUtils.isEmpty(freightConfigs)) {
			String sql = "SELECT * FROM freight_config WHERE shipping_method_id = ?";
			freightConfigs = FreightConfig.dao.find(sql, getId());
		}
		return freightConfigs;
	}

	/**
	 * 设置运费配置
	 * 
	 * @param freightConfigs
	 *            运费配置
	 */
	public void setFreightConfigs(List<FreightConfig> freightConfigs) {
		this.freightConfigs = freightConfigs;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(order)) {
			String sql = "SELECT * FROM `order` WHERE shipping_method_id = ?";
			order = Order.dao.find(sql, getId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrder(List<Order> orders) {
		this.order = orders;
	}

	/**
	 * 判断是否支持支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 * @return 是否支持支付方式
	 */
	public boolean isSupported(PaymentMethod paymentMethod) {
		return paymentMethod == null || (getPaymentMethods() != null && getPaymentMethods().contains(paymentMethod));
	}

	/**
	 * 获取运费配置
	 * 
	 * @param area
	 *            地区
	 * @return 运费配置
	 */
	public FreightConfig getFreightConfig(Area area) {
		if (area == null || CollectionUtils.isEmpty(getFreightConfigs())) {
			return null;
		}

		for (FreightConfig freightConfig : getFreightConfigs()) {
			if (freightConfig.getArea() != null && freightConfig.getArea().equals(area)) {
				return freightConfig;
			}
		}
		return null;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Order> orders = getOrder();
		if (orders != null) {
			for (Order order : orders) {
				order.setShippingMethod(null);
			}
		}
	}
	
	
}
