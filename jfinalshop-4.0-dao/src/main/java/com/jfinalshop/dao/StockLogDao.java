package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.StockLog;

/**
 * Dao - 库存记录
 * 
 * 
 */
public class StockLogDao extends BaseDao<StockLog> {
	
	/**
	 * 构造方法
	 */
	public StockLogDao() {
		super(StockLog.class);
	}
	
	/**
	 * 查找实体对象分页
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	public Page<StockLog> findPage(Pageable pageable) {
		if (pageable == null) {
			pageable = new Pageable();
		}
		String select = "SELECT * ";
		
		String sqlExceptSelect = "FROM `stock_log` l LEFT JOIN `product` p ON l.`product_id` = p.id WHERE 1 = 1";
		
		// 搜索属性、搜索值
		String searchProperty = pageable.getSearchProperty();
		String searchValue = pageable.getSearchValue();
		if (StringUtils.isNotEmpty(searchProperty) && StringUtils.isNotEmpty(searchValue)) {
			sqlExceptSelect += " AND " + searchProperty + " LIKE '%" + StringUtils.trim(searchValue) + "%' ";
		}
		// 排序属性、方向
		String orderProperty = pageable.getOrderProperty();
		Order.Direction orderDirection = pageable.getOrderDirection();
		if (StringUtils.isNotEmpty(orderProperty) && orderDirection != null) {
			switch (orderDirection) {
			case asc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " ASC ";
				break;
			case desc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " DESC ";
				break;
			}
		} 
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}
}