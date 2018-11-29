package com.jfinalshop.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.ShippingDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Service - 发货单
 * 
 * 
 */
@Singleton
public class ShippingService extends BaseService<Shipping> {

	/**
	 * 构造方法
	 */
	public ShippingService() {
		super(Shipping.class);
	}
	
	@Inject
	private ShippingDao shippingDao;
	@Inject
	private SnDao snDao;
	
	/**
	 * 根据编号查找发货单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 发货单，若不存在则返回null
	 */
	public Shipping findBySn(String sn) {
		return shippingDao.findBySn(sn);
	}

	/**
	 * 获取物流动态
	 * 
	 * @param shipping
	 *            发货单
	 * @return 物流动态
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getTransitSteps(Shipping shipping) {
		Assert.notNull(shipping);

		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(shipping.getDeliveryCorpCode()) || StringUtils.isEmpty(shipping.getTrackingNo())) {
			return Collections.emptyList();
		}
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("id", setting.getKuaidi100Key());
		parameterMap.put("com", shipping.getDeliveryCorpCode());
		parameterMap.put("nu", shipping.getTrackingNo());
		parameterMap.put("show", "0");
		parameterMap.put("muti", "1");
		parameterMap.put("order", "asc");
		String content = WebUtils.get("http://api.kuaidi100.com/api", parameterMap);
		Map<String, Object> data = JsonUtils.toObject(content, new TypeReference<Map<String, Object>>() {
		});
		if (!StringUtils.equals(String.valueOf(data.get("status")), "1")) {
			return Collections.emptyList();
		}
		return (List<Map<String, String>>) data.get("data");
	}

	public Shipping save(Shipping shipping) {
		Assert.notNull(shipping);

		shipping.setSn(snDao.generate(Sn.Type.shipping));

		return super.save(shipping);
	}
}