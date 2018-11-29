package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.BooleanUtils;

import com.jfinalshop.dao.DeliveryTemplateDao;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.util.Assert;

/**
 * Service - 快递单模板
 * 
 * 
 */
@Singleton
public class DeliveryTemplateService extends BaseService<DeliveryTemplate> {

	/**
	 * 构造方法
	 */
	public DeliveryTemplateService() {
		super(DeliveryTemplate.class);
	}
	
	@Inject
	private DeliveryTemplateDao deliveryTemplateDao;
	
	/**
	 * 查找默认快递单模板
	 * 
	 * @return 默认快递单模板，若不存在则返回null
	 */
	public DeliveryTemplate findDefault() {
		return deliveryTemplateDao.findDefault();
	}

	public DeliveryTemplate save(DeliveryTemplate deliveryTemplate) {
		Assert.notNull(deliveryTemplate);

		if (BooleanUtils.isTrue(deliveryTemplate.getIsDefault())) {
			deliveryTemplateDao.setDefault(deliveryTemplate);
		}
		return super.save(deliveryTemplate);
	}

	public DeliveryTemplate update(DeliveryTemplate deliveryTemplate) {
		Assert.notNull(deliveryTemplate);

		if (BooleanUtils.isTrue(deliveryTemplate.getIsDefault())) {
			deliveryTemplateDao.setDefault(deliveryTemplate);
		}
		return super.update(deliveryTemplate);
	}
}