package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinalshop.dao.PaymentLogDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 支付记录
 * 
 * 
 */
@Singleton
public class PaymentLogService extends BaseService<PaymentLog> {
	private static Logger logger = LoggerFactory.getLogger(PaymentLogService.class);
	/**
	 * 构造方法
	 */
	public PaymentLogService() {
		super(PaymentLog.class);
	}
	
	private PaymentLogDao paymentLogDao = new PaymentLogDao();
	@Inject
	private SnDao snDao;
	@Inject
	private MemberService memberService;
	@Inject
	private OrderService orderService;
	
	/**
	 * 根据编号查找支付记录
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 支付记录，若不存在则返回null
	 */
	public PaymentLog findBySn(String sn) {
		return paymentLogDao.findBySn(sn);
	}

	/**
	 * 支付处理
	 * 
	 * @param paymentLog
	 *            支付记录
	 */
	public void handle(PaymentLog paymentLog) {
		Assert.notNull(paymentLog);
		Assert.notNull(paymentLog.getType());

		if (!PaymentLog.Status.wait.equals(paymentLog.getStatusName())) {
			return;
		}
        logger.info("订单类型"+paymentLog.getTypeName());
		switch (paymentLog.getTypeName()) {
		case recharge:
			Member member = paymentLog.getMember();
			if (member != null) {
				memberService.addBalance(member, paymentLog.getEffectiveAmount(), DepositLog.Type.recharge, null, null);
			}
			break;
		case payment:
			Order order = paymentLog.getOrder();
			if (order != null) {
				Payment payment = new Payment();
				payment.setMethod(Payment.Method.online.ordinal());
				payment.setPaymentMethod(paymentLog.getPaymentPluginName());
				payment.setFee(paymentLog.getFee());
				payment.setAmount(paymentLog.getAmount());
				payment.setOrderId(order.getId());
				orderService.payment(order, payment, null);
			}
			break;
		}
		paymentLog.setStatus(PaymentLog.Status.success.ordinal());
		logger.info("更改后的订单状态"+paymentLog.getStatus());
		super.update(paymentLog);
	}
	
	/**
	 * 支付处理 H5专用
	 * 
	 * @param paymentLog
	 *            支付记录
	 */
	public void handleH5(PaymentLog paymentLog) {
		Assert.notNull(paymentLog);
		Assert.notNull(paymentLog.getType());

		if (!PaymentLog.Status.wait.equals(paymentLog.getStatusName())) {
			return;
		}
        logger.info("订单类型"+paymentLog.getTypeName());
		switch (paymentLog.getTypeName()) {
		case recharge:
			Member member = paymentLog.getMember();
			if (member != null) {
				memberService.addBalance(member, paymentLog.getEffectiveAmount(), DepositLog.Type.recharge, null, null);
			}
			break;
		case payment:
			Order order = paymentLog.getOrder();
			if (order != null) {
				Payment payment = new Payment();
				payment.setMethod(Payment.Method.online.ordinal());
				payment.setPaymentMethod(paymentLog.getPaymentPluginName());
				payment.setFee(paymentLog.getFee());
				payment.setAmount(paymentLog.getAmount());
				payment.setOrderId(order.getId());
				orderService.paymentH5(order, payment, null);
			}
			break;
		}
		paymentLog.setStatus(PaymentLog.Status.success.ordinal());
		logger.info("更改后的订单状态"+paymentLog.getStatus());
		super.update(paymentLog);
	}

	public PaymentLog save(PaymentLog paymentLog) {
		Assert.notNull(paymentLog);

		paymentLog.setSn(snDao.generate(Sn.Type.paymentLog));
        paymentLog.setSn(paymentLog.getSn());
		return super.save(paymentLog);
	}
	//新增交易日志入库
	public PaymentLog saveH5(PaymentLog paymentLog) {
		Assert.notNull(paymentLog);

		//paymentLog.setSn(snDao.generate(Sn.Type.paymentLog));
        paymentLog.setSn(paymentLog.getSn());
		return super.save(paymentLog);
	}

}