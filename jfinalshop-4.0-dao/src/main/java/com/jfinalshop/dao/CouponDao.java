package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.util.DateUtils;

/**
 * Dao - 优惠券
 * 
 * 
 */
public class CouponDao extends BaseDao<Coupon> {
	
	/**
	 * 构造方法
	 */
	public CouponDao() {
		super(Coupon.class);
	}
	
	
	/**
	 * 查找优惠券分页
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param isExchange
	 *            是否允许积分兑换
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 优惠券分页
	 */
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange, Boolean hasExpired, Pageable pageable) {
		String sqlExceptSelect = "FROM coupon WHERE 1 = 1 ";
		if (isEnabled != null) {
			sqlExceptSelect += " AND is_enabled = " + isEnabled;
		}
		if (isExchange != null) {
			sqlExceptSelect += " AND is_exchange = " + isExchange;
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND end_date IS NOT NULL AND end_date <= '" + DateUtils.getDateTime()+ "' ";
			} else {
				sqlExceptSelect += " AND end_date IS NULL OR end_date > '" + DateUtils.getDateTime()+ "' ";
			}
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

}