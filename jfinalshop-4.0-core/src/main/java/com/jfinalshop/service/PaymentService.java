package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.PaymentDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 收款单
 * 
 * 
 */
@Singleton
public class PaymentService extends BaseService<Payment> {

	/**
	 * 构造方法
	 */
	public PaymentService() {
		super(Payment.class);
	}
	
	@Inject
	private PaymentDao paymentDao;
	@Inject
	private SnDao snDao;
	
	/**
	 * 根据编号查找收款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	public Payment findBySn(String sn) {
		return paymentDao.findBySn(sn);
	}

	public Payment save(Payment payment) {
		Assert.notNull(payment);

		payment.setSn(snDao.generate(Sn.Type.payment));

		return super.save(payment);
	}
}