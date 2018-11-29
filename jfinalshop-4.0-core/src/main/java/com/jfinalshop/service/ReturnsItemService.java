package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.ReturnsItemDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ReturnsItem;

/**
 * Service - 退款行
 * 
 * 
 */
@Singleton
public class ReturnsItemService extends BaseService<ReturnsItem> {

	/**
	 * 构造方法
	 */
	public ReturnsItemService() {
		super(ReturnsItem.class);
	}
	
	@Inject
	private ReturnsItemDao returnsItemDao;
	
	/**
	 * 根据会员查找退货申请
	 * 
	 * @param member
	 *            用户名(忽略大小写)
	 * @return 退货项，若不存在则返回null
	 */
	public List<ReturnsItem> findReturnsItems(Member member, Integer status) {
		return returnsItemDao.findReturnsItems(member, status);
	}
}
