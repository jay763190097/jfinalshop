package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.RefundsDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 退款单
 * 
 * 
 */
@Singleton
public class RefundsService extends BaseService<Refunds> {

	/**
	 * 构造方法
	 */
	public RefundsService() {
		super(Refunds.class);
	}
	
	@Inject
	private SnDao snDao;
	@Inject
	private RefundsDao refundsDao;
	
	/**
	 * 查找退款单分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 退款单分页
	 */
	public Page<Refunds> findPage(Member member, Pageable pageable) {
		return refundsDao.findPage(member, pageable);
	}
	
	public Refunds save(Refunds refunds) {
		Assert.notNull(refunds);

		refunds.setSn(snDao.generate(Sn.Type.refunds));

		return super.save(refunds);
	}
}