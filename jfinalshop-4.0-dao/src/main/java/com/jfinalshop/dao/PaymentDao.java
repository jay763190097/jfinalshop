package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.Payment;

/**
 * Dao - 收款单
 * 
 * 
 */
public class PaymentDao extends BaseDao<Payment> {
	
	/**
	 * 构造方法
	 */
	public PaymentDao() {
		super(Payment.class);
	}
	
	/**
	 * 根据编号查找收款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	public Payment findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String sql = "SELECT * FROM payment WHERE sn = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}

}