package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.FreightConfig;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.ShippingPaymentMethod;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 配送方式
 * 
 * 
 */
@Singleton
public class ShippingMethodService extends BaseService<ShippingMethod> {
	
	/**
	 * 构造方法
	 */
	public ShippingMethodService() {
		super(ShippingMethod.class);
	}
	

	/**
	 * 计算运费
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param area
	 *            地区
	 * @param weight
	 *            重量
	 * @return 运费
	 */
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Area area, Integer weight) {
		Assert.notNull(shippingMethod);

		Setting setting = SystemUtils.getSetting();
		BigDecimal firstPrice = shippingMethod.getDefaultFirstPrice();
		BigDecimal continuePrice = shippingMethod.getDefaultContinuePrice();
		if (area != null && CollectionUtils.isNotEmpty(shippingMethod.getFreightConfigs())) {
			List<Area> areas = new ArrayList<Area>();
			areas.addAll(area.getParents());
			areas.add(area);
			for (int i = areas.size() - 1; i >= 0; i--) {
				FreightConfig freightConfig = shippingMethod.getFreightConfig(areas.get(i));
				if (freightConfig != null) {
					firstPrice = freightConfig.getFirstPrice();
					continuePrice = freightConfig.getContinuePrice();
					break;
				}
			}
		}
		if (weight == null || weight <= shippingMethod.getFirstWeight() || continuePrice.compareTo(BigDecimal.ZERO) == 0) {
			return setting.setScale(firstPrice);
		} else {
			double contiuneWeightCount = Math.ceil((weight - shippingMethod.getFirstWeight()) / (double) shippingMethod.getContinueWeight());
			return setting.setScale(firstPrice.add(continuePrice.multiply(new BigDecimal(String.valueOf(contiuneWeightCount)))));
		}
	}

	/**
	 * 计算运费
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param receiver
	 *            收货地址
	 * @param weight
	 *            重量
	 * @return 运费
	 */
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Receiver receiver, Integer weight) {
		return calculateFreight(shippingMethod, receiver != null ? receiver.getArea() : null, weight);
	}

	/**
	 * 保存
	 * 
	 */
	@Before(Tx.class)
	public ShippingMethod save(ShippingMethod shippingMethod) {
		Assert.notNull(shippingMethod);
		super.save(shippingMethod);
		List<PaymentMethod> paymentMethods = shippingMethod.getPaymentMethods();
		if (CollectionUtils.isNotEmpty(paymentMethods)) {
			for (PaymentMethod paymentMethod : paymentMethods) {
				ShippingPaymentMethod shippingPaymentMethod = new ShippingPaymentMethod();
				shippingPaymentMethod.setPaymentMethods(paymentMethod.getId());
				shippingPaymentMethod.setShippingMethods(shippingMethod.getId());
				shippingPaymentMethod.save();
			}
				
		}
		return shippingMethod;
	}
	
	
	/**
	 * 更新
	 * 
	 */
	@Before(Tx.class)
	public ShippingMethod update(ShippingMethod shippingMethod) {
		Assert.notNull(shippingMethod);
		super.update(shippingMethod);
		List<PaymentMethod> paymentMethods = shippingMethod.getPaymentMethods();
		if (CollectionUtils.isNotEmpty(paymentMethods)) {
			ShippingPaymentMethod.dao.delete(shippingMethod.getId());
			for (PaymentMethod paymentMethod : paymentMethods) {
				ShippingPaymentMethod shippingPaymentMethod = new ShippingPaymentMethod();
				shippingPaymentMethod.setPaymentMethods(paymentMethod.getId());
				shippingPaymentMethod.setShippingMethods(shippingMethod.getId());
				shippingPaymentMethod.save();
			}
				
		}
		return shippingMethod;
	}
	
	/**
	 * 删除
	 * 
	 */
	public void delete(Long... ids) {
		if (ids != null) {
			for (Long id : ids) {
				ShippingPaymentMethod.dao.delete(id);
				super.delete(id);
			}
		}
	}
	
}