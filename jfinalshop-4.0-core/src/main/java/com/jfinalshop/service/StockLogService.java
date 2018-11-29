package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.model.StockLog;

/**
 * Service - 库存记录
 * 
 * 
 */
@Singleton
public class StockLogService extends BaseService<StockLog> {

	/**
	 * 构造方法
	 */
	public StockLogService() {
		super(StockLog.class);
	}
	
	@Inject
	private StockLogDao stockLogDao;
	
	/**
	 * 查找实体对象分页
	 * 
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	public Page<StockLog> findPage(Pageable pageable) {
		return stockLogDao.findPage(pageable);
	}
}