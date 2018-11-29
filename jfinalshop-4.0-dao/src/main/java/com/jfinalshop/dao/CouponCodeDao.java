package com.jfinalshop.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.util.DateUtils;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 优惠码
 * 
 * 
 */
public class CouponCodeDao extends BaseDao<CouponCode> {
	
	/**
	 * 构造方法
	 */
	public CouponCodeDao() {
		super(CouponCode.class);
	}
	
	/**
	 * 查找优惠码数量
	 * 
	 * @param member
	 *            会员
	 * @param hasExpired
	 *            是否已过期
	 * @param isUsed
	 *            是否已使用
	 * @return 优惠码
	 */
	public List<CouponCode> findCouponCodes(Member member, Boolean hasExpired, Boolean isUsed) {
		String sql = ""
				+ "SELECT "
				+ "	cc.id , "
				+ "	cc.code , "
				+ "	c.name , "
				+ "	c.begin_date , "
				+ "	c.end_date , "
				+ "	c.minimum_price, "
				+ "	substring_index(replace(c.price_expression,' ',''),'-',-1) AS amount "
				+ "FROM  coupon_code cc LEFT JOIN coupon c ON cc.`coupon_id` = c.`id` "
				+ "WHERE 1 = 1 ";
		
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND c.end_date IS NOT NULL AND c.end_date <= '" + DateUtil.now() + "'";
			} else {
				sql += " AND (c.end_date IS NULL OR c.end_date > '" + DateUtil.now() + "') ";
			}
		}
		if (isUsed != null) {
			sql += " AND is_used = " + isUsed;
		}
		return modelManager.find(sql);
	}
	
	/**
	 * 查找优惠码分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 优惠码分页
	 */
	public Page<CouponCode> findPageApi(Member member, Pageable pageable, Boolean isUsed) {
		String select = ""
			+ "SELECT "
			+ " IF((c.end_date IS NULL OR c.end_date < now()) , TRUE , FALSE) AS has_expired ," // 是否过期
			+ "	cc.id , "
			+ "	cc.code , "
			+ "	c.name , "
			+ "	c.begin_date , "
			+ "	c.end_date , "
			+ "	c.minimum_price, "
			+ "	substring_index(replace(c.price_expression,' ',''),'-',-1) AS amount ";
		String sqlExceptSelect = "FROM coupon_code cc LEFT JOIN coupon c ON cc.`coupon_id` = c.`id` WHERE 1 = 1";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (isUsed != null) {
			sqlExceptSelect += " AND is_used = " + isUsed;
		}
		sqlExceptSelect += " ORDER BY c.end_date DESC";
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}

	
	/**
	 * 判断优惠码是否存在
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码是否存在
	 */
	public boolean codeExists(String code) {
		if (StringUtils.isEmpty(code)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM coupon_code WHERE LOWER(code) = LOWER(?)";
		Long count = Db.queryLong(sql, code);
		return count > 0;
	}

	/**
	 * 根据号码查找优惠码
	 * 
	 * @param code
	 *            号码(忽略大小写)
	 * @return 优惠码，若不存在则返回null
	 */
	public CouponCode findByCode(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM coupon_code WHERE LOWER(code) = LOWER(?)";
			return modelManager.findFirst(sql, code);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 查找优惠码分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 优惠码分页
	 */
	public Page<CouponCode> findPage(Member member, Pageable pageable, Boolean isUsed) {
		String sqlExceptSelect = "FROM coupon_code WHERE 1 = 1";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (isUsed != null) {
			sqlExceptSelect += " AND is_used = " + isUsed + " ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找优惠码数量
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @param hasBegun
	 *            是否已开始
	 * @param hasExpired
	 *            是否已过期
	 * @param isUsed
	 *            是否已使用
	 * @return 优惠码数量
	 */
	public Long count(Coupon coupon, Member member, Boolean hasBegun, Boolean hasExpired, Boolean isUsed) {
		String sqlExceptSelect = "FROM coupon_code cc LEFT JOIN coupon c ON cc.`coupon_id` = c.`id` WHERE 1 = 1 ";
		if (coupon != null) {
			   sqlExceptSelect += " AND coupon_id = " + coupon.getId();
		}
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (hasBegun != null) {
			if (hasBegun) {
				sqlExceptSelect += " AND (begin_date IS NULL OR begin_date <= '" + DateUtils.getDateTime()+ "') ";
			} else {
				sqlExceptSelect += " AND begin_date IS NOT NULL AND begin_date > '" + DateUtils.getDateTime()+ "' ";
			}
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND end_date IS NOT NULL AND end_date <= '" + DateUtils.getDateTime()+ "' ";
			} else {
				sqlExceptSelect += " AND (end_date IS NULL OR end_date > '" + DateUtils.getDateTime()+ "') ";
			}
		}
		if (isUsed != null) {
			sqlExceptSelect += " AND is_used = " + isUsed + " ";
		}
		return super.count(sqlExceptSelect);
	}

}