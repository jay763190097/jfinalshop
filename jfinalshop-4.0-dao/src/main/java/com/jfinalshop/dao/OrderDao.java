package com.jfinalshop.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.util.DateUtils;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 订单
 * 
 * 
 */
public class OrderDao extends BaseDao<Order> {
	
	/**
	 * 构造方法
	 */
	public OrderDao() {
		super(Order.class);
	}
	
	/**
	 * 根据编号查找订单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 订单，若不存在则返回null
	 */
	public Order findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String sql = "SELECT * FROM `order` WHERE sn = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 查找订单
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<Order> findListbyChannel(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
			List<com.jfinalshop.Order> orders,String channel) {
		String sql = "SELECT * FROM `order` o WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND o.type = " + type.ordinal();
		}
		if (status != null) {
			sql += " AND o.status = " + status.ordinal();
		}
		if (member != null) {
			sql += " AND o.member_id = " + member.getId();
		}
		if (channel != null) {
			sql += " AND o.channel = " + "'"+channel+"'";
		}
		if (goods != null) {
			sql += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.product_id IN (SELECT p.id FROM `product` p LEFT JOIN `goods` g ON p.goods_id = g.id AND g.id = " + goods.getId() + ")) ";
		}
		if (isPendingReceive != null) {
			String subQuery = ""
				+ " ((o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "') AND o.payment_method_name = " + PaymentMethod.Type.cashOnDelivery.ordinal()
				+ " AND o.`status` != " + Order.Status.completed.ordinal() 
				+ " AND o.`status` != " + Order.Status.failed.ordinal() 
				+ " AND o.`status` != " + Order.Status.canceled.ordinal() 
				+ " AND o.`status` != " + Order.Status.denied.ordinal()
				+ " AND o.amount_paid < o.amount) ";
			if (isPendingReceive) {
				sql += " AND" + subQuery;
			} else {
			}
		}
		if (isPendingRefunds != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtils.formatDateTime(new Date()) + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			
			if (isPendingRefunds) {
				sql += " OR " + subQuery;
			} else {
				
			}
		}
		if (isUseCouponCode != null) {
			sql += " AND o.is_use_coupon_code = " + isUseCouponCode;
		}
		if (isExchangePoint != null) {
			sql += " AND o.is_exchange_point = " + isExchangePoint;
		}
		if (isAllocatedStock != null) {
			sql += " AND o.is_allocated_stock = " + isAllocatedStock;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND o.expire IS NOT NULL AND o.expire <= '" + DateUtils.formatDateTime(new Date()) + "' ";
			} else {
				sql += " AND o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "' ";
			}
		}
		return super.findList(sql, null, count, filters, orders);
	}


	public List<Order> findList(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
								List<com.jfinalshop.Order> orders) {
		String sql = "SELECT * FROM `order` o WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND o.type = " + type.ordinal();
		}
		if (status != null) {
			sql += " AND o.status = " + status.ordinal();
		}
		if (member != null) {
			sql += " AND o.member_id = " + member.getId();
		}
		if (goods != null) {
			sql += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.product_id IN (SELECT p.id FROM `product` p LEFT JOIN `goods` g ON p.goods_id = g.id AND g.id = " + goods.getId() + ")) ";
		}
		if (isPendingReceive != null) {
			String subQuery = ""
					+ " ((o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "') AND o.payment_method_name = " + PaymentMethod.Type.cashOnDelivery.ordinal()
					+ " AND o.`status` != " + Order.Status.completed.ordinal()
					+ " AND o.`status` != " + Order.Status.failed.ordinal()
					+ " AND o.`status` != " + Order.Status.canceled.ordinal()
					+ " AND o.`status` != " + Order.Status.denied.ordinal()
					+ " AND o.amount_paid < o.amount) ";
			if (isPendingReceive) {
				sql += " AND" + subQuery;
			} else {
			}
		}
		if (isPendingRefunds != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtils.formatDateTime(new Date()) + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal()
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal()
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";

			if (isPendingRefunds) {
				sql += " OR " + subQuery;
			} else {

			}
		}
		if (isUseCouponCode != null) {
			sql += " AND o.is_use_coupon_code = " + isUseCouponCode;
		}
		if (isExchangePoint != null) {
			sql += " AND o.is_exchange_point = " + isExchangePoint;
		}
		if (isAllocatedStock != null) {
			sql += " AND o.is_allocated_stock = " + isAllocatedStock;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND o.expire IS NOT NULL AND o.expire <= '" + DateUtils.formatDateTime(new Date()) + "' ";
			} else {
				sql += " AND o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "' ";
			}
		}
		return super.findList(sql, null, count, filters, orders);
	}

	/**
	 * 查找订单分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Order.Type type, Order.Status status, Order.Source source, Goods.Type goodsType, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, Boolean deleteFlag) {
		String sqlExceptSelect = "FROM `order` o WHERE 1 = 1 ";
		if (type != null) {
			sqlExceptSelect += " AND o.type = " + type.ordinal();
		}
		if (status != null) {
			if (Order.Status.unfinished.equals(status)) {
				sqlExceptSelect += " AND o.status in (0, 1, 2, 3) ";
			} else if (Order.Status.allCanceled.equals(status)) {
				sqlExceptSelect += " AND o.status in (6, 7, 8, 10, 11) ";
			} else if (Order.Status.completed.equals(status)) {
				sqlExceptSelect += " AND o.status in (5, 13) ";
			} else {
				sqlExceptSelect += " AND o.status = " + status.ordinal();
			}
		}
		if (source != null) {
			sqlExceptSelect += " AND o.source = " + source.ordinal();
		}
		if (member != null) {
			sqlExceptSelect += " AND o.member_id = " + member.getId();
			System.out.println("当前订单的用户ID"+member.getId());
		}
		if (goods != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.product_id IN (SELECT p.id FROM `product` p LEFT JOIN `goods` g ON p.goods_id = g.id AND g.id = " + goods.getId() + ")) ";
		}
		if (isPendingReceive != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtil.now() + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			if (isPendingReceive) {
				sqlExceptSelect += " OR " + subQuery;
			} else {
				
			}
		}
		if (isPendingRefunds != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtils.formatDateTime(new Date()) + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			if (isPendingRefunds) {
				sqlExceptSelect += " OR " + subQuery;
			} else {
				
			}
		}
		if (isUseCouponCode != null) {
			sqlExceptSelect += " AND o.is_use_coupon_code = " + isUseCouponCode;
		}
		if (isExchangePoint != null) {
			sqlExceptSelect += " AND o.is_exchange_point = " + isExchangePoint;
		}
		if (isAllocatedStock != null) {
			sqlExceptSelect += " AND o.is_allocated_stock = " + isAllocatedStock;
		}
		if (deleteFlag != null) {
			sqlExceptSelect += " AND o.delete_flag = " + deleteFlag;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND o.expire IS NOT NULL AND o.expire <= '" + DateUtil.now() + "'";
			} else {
				sqlExceptSelect += " AND o.expire IS NULL OR o.expire > '" + DateUtil.now() + "' ";
			}
		}
		if (goodsType != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.type = " + goodsType.ordinal() + ") ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}
	
	/**
	 * 查找订单分页     H5专用
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Order.Type type, Order.Status status, Order.Source source, Goods.Type goodsType, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, Boolean deleteFlag, String channel) {
		String sqlExceptSelect = "FROM `order` o WHERE 1 = 1 ";
		String channel1 = "'"+channel+"'";
		if (type != null) {
			sqlExceptSelect += " AND o.type = " + type.ordinal();
		}
		if (status != null) {
			if (Order.Status.unfinished.equals(status)) {
				sqlExceptSelect += " AND o.status in (0, 1, 2, 3) ";
			} else if (Order.Status.allCanceled.equals(status)) {
				sqlExceptSelect += " AND o.status in (6, 7, 8, 10, 11) ";
			} else if (Order.Status.completed.equals(status)) {
				sqlExceptSelect += " AND o.status in (5, 13) ";
			} else {
				sqlExceptSelect += " AND o.status = " + status.ordinal();
			}
		}
		if (source != null) {
			sqlExceptSelect += " AND o.source = " + source.ordinal();
		}
		if (channel != null) {
			sqlExceptSelect += " AND o.channel = " + channel1;
		}
		if (member != null) {
			sqlExceptSelect += " AND o.member_id = " + member.getId();
			System.out.println("当前订单的用户ID"+member.getId());
		}
		if (goods != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.product_id IN (SELECT p.id FROM `product` p LEFT JOIN `goods` g ON p.goods_id = g.id AND g.id = " + goods.getId() + ")) ";
		}
		if (isPendingReceive != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtil.now() + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			if (isPendingReceive) {
				sqlExceptSelect += " OR " + subQuery;
			} else {
				
			}
		}
		if (isPendingRefunds != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtils.formatDateTime(new Date()) + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			if (isPendingRefunds) {
				sqlExceptSelect += " OR " + subQuery;
			} else {
				
			}
		}
		if (isUseCouponCode != null) {
			sqlExceptSelect += " AND o.is_use_coupon_code = " + isUseCouponCode;
		}
		if (isExchangePoint != null) {
			sqlExceptSelect += " AND o.is_exchange_point = " + isExchangePoint;
		}
		if (isAllocatedStock != null) {
			sqlExceptSelect += " AND o.is_allocated_stock = " + isAllocatedStock;
		}
		if (deleteFlag != null) {
			sqlExceptSelect += " AND o.delete_flag = " + deleteFlag;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND o.expire IS NOT NULL AND o.expire <= '" + DateUtil.now() + "'";
			} else {
				sqlExceptSelect += " AND o.expire IS NULL OR o.expire > '" + DateUtil.now() + "' ";
			}
		}
		if (goodsType != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.type = " + goodsType.ordinal() + ") ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}
	
	/**
	 * 查询订单数量
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @return 订单数量
	 */
	public Long count(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		String sql = "FROM `order` o WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND o.type = " + type.ordinal();
		}
		if (status != null) {
			sql += " AND o.status = " + status.ordinal();
		}
		if (member != null) {
			sql += " AND o.member_id = " + member.getId();
		}
		if (goods != null) {
			sql += " AND EXISTS (SELECT 1 FROM `order_item` oi WHERE o.id = oi.order_id AND oi.product_id IN (SELECT p.id FROM `product` p LEFT JOIN `goods` g ON p.goods_id = g.id AND g.id = " + goods.getId() + ")) ";
		}
		if (isPendingReceive != null) {
			String subQuery = ""
					+ " ((o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "') AND o.payment_method_name = " + PaymentMethod.Type.cashOnDelivery.ordinal()
					+ " AND o.`status` != " + Order.Status.completed.ordinal() 
					+ " AND o.`status` != " + Order.Status.failed.ordinal() 
					+ " AND o.`status` != " + Order.Status.canceled.ordinal() 
					+ " AND o.`status` != " + Order.Status.denied.ordinal()
					+ " AND o.amount_paid < o.amount) ";
			if (isPendingReceive) {
				sql += " AND" + subQuery;
			} else {
				
			}
		}
		if (isPendingRefunds != null) {
			String subQuery = ""
					+ " ((o.expire IS NOT NULL OR o.expire <= '" + DateUtils.formatDateTime(new Date()) + "'"
					+ " OR  o.`status` = " + Order.Status.failed.ordinal() 
					+ " OR  o.`status` = " + Order.Status.canceled.ordinal() 
					+ " OR  o.`status` = " + Order.Status.denied.ordinal() + ")"
					+ " AND o.amount_paid > 0) "
					+ " AND o.`status` = " + Order.Status.completed.ordinal()
					+ " AND o.amount_paid > o.amount ";
			if (isPendingRefunds) {
				sql += " OR " + subQuery;
			} else {
				
			}
		}
		if (isUseCouponCode != null) {
			sql += " o.is_use_coupon_code = " + isUseCouponCode;
		}
		if (isExchangePoint != null) {
			sql += " o.is_exchange_point = " + isExchangePoint;
		}
		if (isAllocatedStock != null) {
			sql += " o.is_allocated_stock = " + isAllocatedStock;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND o.expire IS NOT NULL AND o.expire <= '" + DateUtils.formatDateTime(new Date()) + "' ";
			} else {
				sql += " AND o.expire IS NULL OR o.expire > '" + DateUtils.formatDateTime(new Date()) + "' ";
			}
		}
		return super.count(sql);
	}
	
	/**
	 * 查询订单创建数
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单创建数
	 */
	public Long createOrderCount(Date beginDate, Date endDate) {
		String sqlExceptSelect="FROM `order`  WHERE 1 = 1 ";
		
		if (beginDate != null) {
			sqlExceptSelect += " AND create_date >= '"+ DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sqlExceptSelect += " AND create_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		return super.count(sqlExceptSelect);
	}

	/**
	 * 查询订单完成数
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成数
	 */
	public Long completeOrderCount(Date beginDate, Date endDate) {
		String sqlExceptSelect="FROM `order`  WHERE 1 = 1 ";
		if (beginDate != null) {
			sqlExceptSelect += " AND complete_date >= '"+ DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sqlExceptSelect += " AND complete_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		return super.count(sqlExceptSelect);
	}


	/**
	 * 查询订单创建金额
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单创建金额
	 */
	public BigDecimal createOrderAmount(Date beginDate, Date endDate) {
		String sql = "SELECT SUM(amount) FROM `order`  WHERE 1 = 1 ";
		if (beginDate != null) {
			sql += " AND complete_date >= '"+ DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sql += " AND complete_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		BigDecimal result = Db.queryBigDecimal(sql);
		return result != null ? result : BigDecimal.ZERO;
	}

	/**
	 * 查询订单完成金额
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成金额
	 */
	public BigDecimal completeOrderAmount(Date beginDate, Date endDate) {
		String sql = "SELECT SUM(amount) FROM `order`  WHERE 1 = 1 ";
		if (beginDate != null) {
			sql += " AND complete_date >= '"+ DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sql += " AND complete_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		BigDecimal result = Db.queryBigDecimal(sql);
		return result != null ? result : BigDecimal.ZERO;
	}

}