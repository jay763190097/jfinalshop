package com.jfinalshop.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jfinalshop.model.Ad;
import com.jfinalshop.model.AdPosition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.GenericsUtils;

/**
 * Dao - 基类
 * 
 * 
 */
public class BaseDao <M extends Model<M>> {
	
	/** "ID"属性名称 */
	public static final String ID = "id";

	/** "创建日期"属性名称 */
	public static final String CREATE_DATE = "create_date";

	/** "修改日期"属性名称 */
	public static final String MODIFY_DATE = "modify_date";

	/** "版本"属性名称 */
	public static final String VERSION = "version";

	/** 实体类类型 */
	private Class<M> modelClass;
	
	protected M modelManager;
	
	public Class<M> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<M> modelClass) {
		this.modelClass = modelClass;
	}
	
	/**
	 * 构造方法
	 */
	@SuppressWarnings("unchecked")
	public BaseDao(Class<M> entityClass) {
		this.setModelClass(GenericsUtils.getSuperClassGenricType(entityClass));
		try {
			modelManager = modelClass.newInstance();
		} catch (InstantiationException e) {
			LogKit.error("instance model fail!" + e);
		} catch (IllegalAccessException e) {
			LogKit.error("instance model fail!" + e);
		}
	}
	
	public String getTableName() {
		Table table = TableMapping.me().getTable(getModelClass());
		return table.getName();
	}
	
	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	public M find(Long id) {
		if (id == null) {
			return null;
		}
		return modelManager.findById(id);
	}
	public List<AdPosition> find(Long id, String channel) {
		if (id == null) {
			return null;
		}
		System.out.println(AdPosition.dao.findFirst("SELECT * FROM ad WHERE `ad_position_id` =  "+id+" and  channel = "+"'"+channel+"'"));
		return  AdPosition.dao.find("SELECT * FROM ad WHERE `ad_position_id` =  "+id+" and  channel = "+"'"+channel+"'");
	}
	/**
	 * 查找产品实体对象列表
	 * 
	 * @param goodsId
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	public List<M> findSpecifications(Long goodsId) {
		String sql = "SELECT * FROM PRODUCT WHERE GOODS_ID=" + goodsId;
		return modelManager.find(sql);
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	public List<M> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders,String channel) {
		String sql = "SELECT * FROM `" + getTableName() + "` WHERE 1 = 1 ";
		String value =  filters.get(0).getValue().toString();
		System.out.println(value);
		if(channel!=null&&"1".equals(value)){
			sql+= " AND channel = " +"'"+channel+"'";
		}

		return findList(sql, first, count, filters, orders);
	}

	public List<M> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM `" + getTableName() + "` WHERE 1 = 1 ";
		return findList(sql, first, count, filters, orders);
	}
	/**
	 * 查找实体对象分页
	 * 
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	public Page<M> findPage(Pageable pageable) {
		String sqlExceptSelect = "FROM `" + getTableName() + "` WHERE 1 = 1 ";
		return findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	public long count(Filter... filters) {
		String sql = "SELECT COUNT(*) FROM `" + getTableName() + "` WHERE 1 = 1 ";
		sql += getFilters(ArrayUtils.isNotEmpty(filters) ? Arrays.asList(filters) : null);
		return Db.queryLong(sql);
	}
	
	
	/**
	 * 查询实体对象数量
	 * 
	 * @param sqlExceptSelect   写法 : 从from 往后的语句
	 *            筛选
	 * @return 实体对象数量
	 */
	public Long count(String sqlExceptSelect) {
		String sql = "SELECT COUNT(1) " + sqlExceptSelect;
		return Db.queryLong(sql);
	}

	/**
	 * 持久化实体对象
	 * 
	 * @param model
	 *            实体对象
	 */
	public void save(M model) {
		Assert.notNull(model);
		model.set(CREATE_DATE, new Date());
		model.set(MODIFY_DATE, new Date());
		model.set(VERSION, 0);
		model.save();
	}

	/**
	 * 更新实体对象
	 * 
	 * @param model
	 *            实体对象
	 * @return 实体对象
	 */
	public M update(M model) {
		Assert.notNull(model);
		model.set(MODIFY_DATE, new Date());
		M pModel = find(model.getLong(ID));
		model.set(VERSION, pModel.getLong(VERSION) + 1);
		model.update();
		return model;
	}

	/**
	 * 移除实体对象
	 * 
	 * @param model
	 *            实体对象
	 */
	public void remove(M model) {
		if (model != null) {
			model.delete();
		}
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param sql
	 *            查询条件
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	protected List<M> findList(String sql, Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		Assert.notNull(sql);
		
		String sqlFilters = getFilters(filters);
		sql += sqlFilters;
		
		String sqlOrders = getOrders(orders);
		if (StrKit.isBlank(sqlOrders)) {
			if (compareClass()) {
				sqlOrders = " ORDER BY " + OrderEntity.ORDER_NAME + " ASC ";
			} else {
				sqlOrders = " ORDER BY " + CREATE_DATE + " DESC ";
			}
		}
		sql += sqlOrders;
		if (first != null && count != null) {
			sql += " LIMIT " + first + ", " + count;
		}
		return modelManager.find(sql);
	}


	/**
	 * 查找实体对象分页
	 * 
	 * @param
	 *
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	protected Page<M> findPage(String sqlExceptSelect, Pageable pageable) {
		Assert.notNull(sqlExceptSelect);
		if (pageable == null) {
			pageable = new Pageable();
		}
		//String modelName = StrKit.firstCharToLowerCase(getModelClass().getSimpleName());
		String select = "SELECT * ";
		
		// 过滤条件
		String filtersSQL = getFilters(pageable.getFilters());
		LogKit.info("filtersSQL:" + filtersSQL);
		
		// 搜索属性、搜索值
		String searchProperty = pageable.getSearchProperty();
		String searchValue = pageable.getSearchValue();
		if (StringUtils.isNotEmpty(searchProperty) && StringUtils.isNotEmpty(searchValue)) {
			filtersSQL += " AND " + searchProperty + " LIKE '%" + StringUtils.trim(searchValue) + "%' ";
		}
		sqlExceptSelect += filtersSQL;

		String ordersSQL = getOrders(pageable.getOrders());
		LogKit.info("ordersSQL:" + ordersSQL);
		
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
		} else if (StrKit.isBlank(ordersSQL)) {
			if (compareClass()) {
				ordersSQL = " ORDER BY " + OrderEntity.ORDER_NAME +" ASC ";
			} else {
				ordersSQL = " ORDER BY " + CREATE_DATE + " DESC ";
			}
		}
		sqlExceptSelect += ordersSQL;
		LogKit.info("sqlExceptSelect:" + sqlExceptSelect);
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
		
	}
	
	/**
	 * 转换为Predicate
	 * 
	 * @param
	 *
	 * @param filters
	 *            筛选
	 * @return Predicate
	 */
	private String getFilters(List<Filter> filters) {
		String sql = ""; 
		if (CollectionUtils.isEmpty(filters)) {
			return "";
		}
		for (Filter filter : filters) {
			if (filter == null) {
				continue;
			}
			String property = filter.getProperty();
			Filter.Operator operator = filter.getOperator();
			Object value = filter.getValue();
			Boolean ignoreCase = filter.getIgnoreCase();
			switch (operator) {
			case eq:
				if (value != null) {
					if (BooleanUtils.isTrue(ignoreCase) && value instanceof String) {
						sql +=" AND "+  property + " = " + ((String) value).toLowerCase();
					} else {
						sql +=" AND "+ property + " = " + value;
					}
				} else {
					sql +=" AND "+ property + " IS NULL ";
				}
				break;
			case ne:
				if (value != null) {
					if (BooleanUtils.isTrue(ignoreCase) && value instanceof String) {
						sql +=" AND "+ property + " != " + ((String) value).toLowerCase();
					} else {
						sql +=" AND "+ property + " != " + value;
					}
				} else {
					sql +=" AND "+ property + " IS NOT NULL ";
				}
				break;
			case gt:
				if (value instanceof Number) {
					sql +=" AND "+ property + " > " + (Number) value;
				}
				break;
			case lt:
				if (value instanceof Number) {
					sql +=" AND "+ property + " < " + (Number) value;
				}
				break;
			case ge:
				if (value instanceof Number) {
					sql +=" AND "+ property + " >= " + (Number) value;
				}
				break;
			case le:
				if (value instanceof Number) {
					sql +=" AND "+ property + " <= " + (Number) value;
				}
				break;
			case like:
				if (value instanceof String) {
					if (BooleanUtils.isTrue(ignoreCase)) {
						sql += " AND " + property + " LIKE '%" + ((String) value).toLowerCase() + "'";
					} else {
						sql += " AND " + property + " LIKE '%" + (String) value + "'";
					}
				}
				break;
			case in:
				sql +=" AND "+ property + " IN(" + value + ")";
				break;
			case isNull:
				sql +=" AND "+ property + " IS NULL";
				break;
			case isNotNull:
				sql +=" AND "+ property + " IS NOT NULL";
				break;
			}
		}
		return sql;
	}

	/**
	 * 转换为Order
	 * 
	 * @param
	 *
	 * @param orders
	 *            排序
	 * @return Order
	 */
	private String getOrders(List<Order> orders) {
		String orderSql = "";
		if (CollectionUtils.isNotEmpty(orders)) {
			orderSql = " ORDER BY ";
			for (Order order : orders) {
				String property = order.getProperty();
				Order.Direction direction = order.getDirection();
				switch (direction) {
				case asc:
					orderSql += property + " ASC, ";
					break;
				case desc:
					orderSql += property + " DESC,";
					break;
				}
			}
			orderSql = StringUtils.substring(orderSql, 0, orderSql.length() - 1);
		}
		return orderSql;
	}
	
	/**
	 * 判断一个类Class1和另一个类Class2是否相同或是另一个类的超类或接口。
	 * @return
	 * 
	 */
	private boolean compareClass() {
		try {
			Class<?> onwClass = Class.forName("com.jfinalshop.dao." + modelClass.getSimpleName() + "Dao");
			return OrderEntity.class.isAssignableFrom(onwClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}