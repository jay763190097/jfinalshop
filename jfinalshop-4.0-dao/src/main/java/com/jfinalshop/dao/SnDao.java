package com.jfinalshop.dao;

import java.io.IOException;

import com.jfinal.aop.Before;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.template.TemplateException;

/**
 * Dao - 序列号
 * 
 * 
 */
public class SnDao extends BaseDao<Sn> {
	
	/**
	 * 构造方法
	 */
	public SnDao() {
		super(Sn.class);
	}

	Prop prop = PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
	private String goodsPrefix = prop.get("sn.goods.prefix");
	private int goodsMaxLo = prop.getInt("sn.goods.maxLo");
	private String orderPrefix = prop.get("sn.order.prefix");
	private int orderMaxLo = prop.getInt("sn.order.maxLo");
	private String paymentLogPrefix = prop.get("sn.paymentLog.prefix");
	private int paymentLogMaxLo = prop.getInt("sn.paymentLog.maxLo");
	private String paymentPrefix = prop.get("sn.payment.prefix");
	private int paymentMaxLo = prop.getInt("sn.payment.maxLo");
	private String refundsPrefix = prop.get("sn.refunds.prefix");
	private int refundsMaxLo = prop.getInt("sn.refunds.maxLo");
	private String shippingPrefix = prop.get("sn.shipping.prefix");
	private int shippingMaxLo = prop.getInt("sn.shipping.maxLo");
	private String returnsPrefix = prop.get("sn.returns.prefix");
	private int returnsMaxLo = prop.getInt("sn.returns.maxLo");

	/** 货品编号生成器 */
	private HiloOptimizer goodsHiloOptimizer = new HiloOptimizer(Sn.Type.goods, goodsPrefix, goodsMaxLo);
	
	/** 订单编号生成器 */
	private HiloOptimizer orderHiloOptimizer = new HiloOptimizer(Sn.Type.order, orderPrefix, orderMaxLo);

	/** 支付记录编号生成器 */
	private HiloOptimizer paymentLogHiloOptimizer = new HiloOptimizer(Sn.Type.paymentLog, paymentLogPrefix, paymentLogMaxLo);

	/** 收款单编号生成器 */
	private HiloOptimizer paymentHiloOptimizer = new HiloOptimizer(Sn.Type.payment, paymentPrefix, paymentMaxLo);

	/** 退款单编号生成器 */
	private HiloOptimizer refundsHiloOptimizer = new HiloOptimizer(Sn.Type.refunds, refundsPrefix, refundsMaxLo);

	/** 发货单编号生成器 */
	private HiloOptimizer shippingHiloOptimizer = new HiloOptimizer(Sn.Type.shipping, shippingPrefix, shippingMaxLo);

	/** 退货单编号生成器 */
	private HiloOptimizer returnsHiloOptimizer = new HiloOptimizer(Sn.Type.returns, returnsPrefix, returnsMaxLo);
	
	
	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	public String generate(Sn.Type type) {
		Assert.notNull(type);

		switch (type) {
		case goods:
			return goodsHiloOptimizer.generate();
		case order:
			return orderHiloOptimizer.generate();
		case paymentLog:
			return paymentLogHiloOptimizer.generate();
		case payment:
			return paymentHiloOptimizer.generate();
		case refunds:
			return refundsHiloOptimizer.generate();
		case shipping:
			return shippingHiloOptimizer.generate();
		case returns:
			return returnsHiloOptimizer.generate();
		}
		return null;
	}

	/**
	 * 获取末值
	 * 
	 * @param type
	 *            类型
	 * @return 末值
	 */
	@Before(Tx.class)
	private long getLastValue(Sn.Type type) {
		String sql = "SELECT * FROM sn WHERE type = ?";
		Sn sn = modelManager.findFirst(sql, type.ordinal());
		long lastValue = sn.getLastValue();
		String updateSql = "UPDATE sn SET last_value = ? WHERE type = ? AND last_value = ?";
		int result = Db.update(updateSql, lastValue + 1, type.ordinal(), lastValue);
		return 0 < result ? lastValue : getLastValue(type);
	}

	
	/**
	 * 高低位算法生成器
	 */
	private class HiloOptimizer {

		/** 类型 */
		private Sn.Type type;

		/** 前缀 */
		private String prefix;

		/** 最大低位值 */
		private int maxLo;

		/** 低位值 */
		private int lo;

		/** 高位值 */
		private long hi;

		/** 末值 */
		private long lastValue;

		/**
		 * 构造方法
		 * 
		 * @param type
		 *            类型
		 * @param prefix
		 *            前缀
		 * @param maxLo
		 *            最大低位值
		 */
		public HiloOptimizer(Sn.Type type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix != null ? prefix.replace("{", "${") : "";
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		/**
		 * 生成序列号
		 * 
		 * @return 序列号
		 */
		public synchronized String generate() {
			if (lo > maxLo) {
				lastValue = getLastValue(type);
				lo = lastValue == 0 ? 1 : 0;
				hi = lastValue * (maxLo + 1);
			}
			try {
				return FreeMarkerUtils.process(prefix) + (hi + lo++);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (TemplateException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
}