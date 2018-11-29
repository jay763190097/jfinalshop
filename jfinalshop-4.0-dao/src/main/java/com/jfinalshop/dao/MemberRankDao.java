package com.jfinalshop.dao;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.util.Assert;

/**
 * Dao - 会员等级
 * 
 * 
 */
public class MemberRankDao extends BaseDao<MemberRank> {
	
	/**
	 * 构造方法
	 */
	public MemberRankDao() {
		super(MemberRank.class);
	}
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member_rank WHERE name = LOWER(?)";
		Long count = Db.queryLong(sql, name);
		return count > 0;
	}

	/**
	 * 判断消费金额是否存在
	 * 
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否存在
	 */
	public boolean amountExists(BigDecimal amount) {
		if (amount == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member_rank WHERE amount = ?";
		Long count = Db.queryLong(sql, amount);
		return count > 0;
	}

	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		try {
			String sql = "SELECT * FROM member_rank WHERE is_default = true";
			return modelManager.findFirst(sql);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		String sql = "SELECT * FROM member_rank WHERE is_special = false AND amount <= ? ORDER BY amount DESC";
		return modelManager.findFirst(sql, amount);
	}

	/**
	 * 设置默认会员等级
	 * 
	 * @param memberRank
	 *            会员等级
	 */
	public void setDefault(MemberRank memberRank) {
		Assert.notNull(memberRank);

		memberRank.setIsDefault(true);
		if (memberRank.getId() == null) {
			String sql = "UPDATE member_rank SET is_default = false WHERE is_default = true";
			Db.update(sql);
		} else {
			String sql = "UPDATE member_rank SET is_default = false WHERE is_default = true AND id != ?";
			Db.update(sql, memberRank.getId());
		}
	}

}