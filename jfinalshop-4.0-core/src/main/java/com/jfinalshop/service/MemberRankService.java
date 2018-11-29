package com.jfinalshop.service;

import java.math.BigDecimal;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.util.Assert;

/**
 * Service - 会员等级
 * 
 * 
 */
@Singleton
public class MemberRankService extends BaseService<MemberRank> {

	/**
	 * 构造方法
	 */
	public MemberRankService() {
		super(MemberRank.class);
	}
	
	@Inject
	private MemberRankDao memberRankDao;
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		return memberRankDao.nameExists(name);
	}

	/**
	 * 判断名称是否唯一
	 * 
	 * @param previousName
	 *            修改前名称(忽略大小写)
	 * @param currentName
	 *            当前名称(忽略大小写)
	 * @return 名称是否唯一
	 */
	public boolean nameUnique(String previousName, String currentName) {
		return StringUtils.equalsIgnoreCase(previousName, currentName) || !memberRankDao.nameExists(currentName);
	}

	/**
	 * 判断消费金额是否存在
	 * 
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否存在
	 */
	public boolean amountExists(BigDecimal amount) {
		return memberRankDao.amountExists(amount);
	}

	/**
	 * 判断消费金额是否唯一
	 * 
	 * @param previousAmount
	 *            修改前消费金额
	 * @param currentAmount
	 *            当前消费金额
	 * @return 消费金额是否唯一
	 */
	public boolean amountUnique(BigDecimal previousAmount, BigDecimal currentAmount) {
		return (previousAmount != null && previousAmount.compareTo(currentAmount) == 0) || !memberRankDao.amountExists(currentAmount);
	}

	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		return memberRankDao.findDefault();
	}

	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		return memberRankDao.findByAmount(amount);
	}

	public MemberRank save(MemberRank memberRank) {
		Assert.notNull(memberRank);

		if (BooleanUtils.isTrue(memberRank.getIsDefault())) {
			memberRankDao.setDefault(memberRank);
		}
		return super.save(memberRank);
	}

	public MemberRank update(MemberRank memberRank) {
		Assert.notNull(memberRank);

		if (BooleanUtils.isTrue(memberRank.getIsDefault())) {
			memberRankDao.setDefault(memberRank);
		}
		return super.update(memberRank);
	}
}