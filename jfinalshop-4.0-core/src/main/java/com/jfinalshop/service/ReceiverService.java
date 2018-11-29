package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.BooleanUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ReceiverDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.util.Assert;

/**
 * Service - 收货地址
 * 
 * 
 */
@Singleton
public class ReceiverService extends BaseService<Receiver> {

	/**
	 * 构造方法
	 */
	public ReceiverService() {
		super(Receiver.class);
	}
	
	@Inject
	private ReceiverDao receiverDao;
	
	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		return receiverDao.findDefault(member);
	}

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return receiverDao.findPage(member, pageable);
	}
	
	public Receiver save(Receiver receiver) {
		Assert.notNull(receiver);

		if (BooleanUtils.isTrue(receiver.getIsDefault())) {
			receiverDao.setDefault(receiver);
		}
		return super.save(receiver);
	}

	public Receiver update(Receiver receiver) {
		Assert.notNull(receiver);

		if (BooleanUtils.isTrue(receiver.getIsDefault())) {
			receiverDao.setDefault(receiver);
		}
		return super.update(receiver);
	}

}