package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.PaymentLog;

/**
 * Dao - 支付记录
 * 
 * 
 */
public class PaymentLogDao extends BaseDao<PaymentLog> {
	
	/**
	 * 构造方法
	 */
	public PaymentLogDao() {
		super(PaymentLog.class);
	}

	/**
	 * 删除
	 */
	public void deleteByOrderId(Long orderId) {
		modelManager.deleteByOrderId(orderId);
	}
	
	/**
	 * 根据编号查找支付记录
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 支付记录，若不存在则返回null
	 */
	public PaymentLog findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String sql = "SELECT * FROM payment_log WHERE sn = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}

}