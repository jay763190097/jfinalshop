package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 退货单
 * 
 * 
 */
@Singleton
public class ReturnsService extends BaseService<Returns> {

	/**
	 * 构造方法
	 */
	public ReturnsService() {
		super(Returns.class);
	}
	
	@Inject
	private SnDao snDao;

	public Returns save(Returns returns) {
		Assert.notNull(returns);

		returns.setSn(snDao.generate(Sn.Type.returns));

		return super.save(returns);
	}
}