package com.jfinalshop.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Tag;
import com.jfinalshop.util.DateUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Dao - 货品
 * 
 * 
 */
public class GoodsDao extends BaseDao<Goods> {
	
	/**
	 * 构造方法
	 */
	public GoodsDao() {
		super(Goods.class);
	}
	
	/**
	 * 查找用户经常买的商品
	 * 
	 * @param Goods
	 *            商品
	 */
	public List<Goods> findMemberBuyList(Member member, Integer count) {
		if (member == null) {
			return null;
		}
		String sql = ""
				+ "SELECT "
				+ "		i.product_id , "
				+ "		sum(i.`quantity`) AS sales , "
				+ "		g.* "
				+ "	FROM "
				+ "		`order_item` i , "
				+ "		`order` o , "
				+ "		`product` p , "
				+ "		`goods` g "
				+ "	WHERE "
				+ "		i.`order_id` = o.`id` "
				+ "	AND o.`member_id` = ? "
				+ "	AND p.`id` = i.`product_id` "
				+ "	AND g.`id` = p.`goods_id` "
				+ "	GROUP BY "
				+ "		i.`product_id` "
				+ "	ORDER BY "
				+ "		sales DESC ";
		if (count != null) {
			sql += " LIMIT 0 , " + count;
		}
		return modelManager.find(sql, member.getId());
	}
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM goods WHERE LOWER(sn) = LOWER(?)";
		Long count = Db.queryLong(sql, sn);
		return count > 0;
	}

	/**
	 * 根据编号查找货品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 货品，若不存在则返回null
	 */
	public Goods findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}

		String sql = "SELECT * FROM goods WHERE LOWER(sn) = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 查找货品
	 * 
	 * @param type
	 *            类型
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tag
	 *            标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 货品
	 */
	public List<Goods> findList(Goods.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, String channel, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock,
			Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		//orders = new ArrayList<Order>();
		String channel1 = "'"+channel+"'";
		String sql = "SELECT * FROM goods g WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND g.type = " + type.ordinal();
		}
		if (productCategory != null) {
			sql += " AND EXISTS (SELECT 1 FROM product_category p WHERE g.product_category_id = p.id AND (p.`tree_path` LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' OR g.product_category_id = "+ productCategory.getId() +")) ";
		}
		if (brand != null) {
			sql += " AND g.brand_id = " + brand.getId();
		}
		if (promotion != null) {
			sql += " AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		if (tag != null) {
			sql += " AND EXISTS (select 1 from goods_tag gt WHERE gt.`goods` = g.`id` AND gt.tags = " + tag.getId() + ") ";
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = "attribute_value" + entry.getKey().getPropertyIndex();
				sql += " AND " + propertyName + " = '" + entry.getValue()+"'";
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND g.price => " + startPrice;
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND g.price <= " + endPrice ;
		}
		if (channel != null) {
			sql += " AND g.channel = " + channel1;
		}
		if (isMarketable != null) {
			sql += " AND g.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sql += " AND g.is_list = " + isList;
		}
		if (isTop != null) {
			sql += " AND g.is_top = " + isTop;
		}
		if (isOutOfStock != null) {
			String subquery = "";
			if (isOutOfStock) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock`";
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock`";
			}
			sql += "AND EXISTS (" + subquery + ") ";
		}
		if (isStockAlert != null) {
			String subquery = "";
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock` + " + setting.getStockAlertCount();
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock` + " + setting.getStockAlertCount();
			}
			sql += "AND EXISTS (" + subquery + ") ";
		}
		if (hasPromotion != null) {
			sql += "AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
			if(orders == null){
			orders = new ArrayList<Order>();
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
				}
				break;
			case priceAsc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("price", Order.Direction.asc));
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case priceDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("price", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));;
					}
				break;
			case salesDesc:
				if(orders == null){
					orders = new ArrayList<Order>();
					orders.add(new Order("sales", Order.Direction.desc));
					orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case scoreDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case dateDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			}
		} else if (CollectionUtils.isEmpty(orders)) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
		}
		return super.findList(sql, 0, count, filters, orders);
	}
    
	/**
	 * 热搜专用
	 * 查找货品
	 * 
	 * @param type
	 *            类型
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tag
	 *            标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 货品
	 */
	public List<Goods> findHot(Goods.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock,
			Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		//orders = new ArrayList<Order>();
		String sql = "SELECT * FROM goods g WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND g.type = " + type.ordinal();
		}
		if (productCategory != null) {
			sql += " AND EXISTS (SELECT 1 FROM product_category p WHERE g.product_category_id = p.id AND (p.`tree_path` LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' OR g.product_category_id = "+ productCategory.getId() +")) ";
		}
		if (brand != null) {
			sql += " AND g.brand_id = " + brand.getId();
		}
		if (promotion != null) {
			sql += " AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		if (tag != null) {
			sql += " AND EXISTS (select 1 from goods_tag gt WHERE gt.`goods` = g.`id` AND gt.tags = " + tag.getId() + ") ";
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = "attribute_value" + entry.getKey().getPropertyIndex();
				sql += " AND " + propertyName + " = '" + entry.getValue()+"'";
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND g.price => " + startPrice;
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND g.price <= " + endPrice ;
		}
		if (isMarketable != null) {
			sql += " AND g.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sql += " AND g.is_list = " + isList;
		}
		if (isTop != null) {
			sql += " AND g.is_top = " + isTop;
		}
		if (isOutOfStock != null) {
			String subquery = "";
			if (isOutOfStock) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock`";
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock`";
			}
			sql += "AND EXISTS (" + subquery + ") ";
		}
		if (isStockAlert != null) {
			String subquery = "";
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock` + " + setting.getStockAlertCount();
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock` + " + setting.getStockAlertCount();
			}
			sql += "AND EXISTS (" + subquery + ") ";
		}
		if (hasPromotion != null) {
			sql += "AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
			if(orders == null){
			orders = new ArrayList<Order>();
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
				}
				break;
			case priceAsc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("price", Order.Direction.asc));
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case priceDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("price", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));;
					}
				break;
			case salesDesc:
				if(orders == null){
					orders = new ArrayList<Order>();
					orders.add(new Order("sales", Order.Direction.desc));
					orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case scoreDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			case dateDesc:
				if(orders == null){
				orders = new ArrayList<Order>();
				orders.add(new Order("create_date", Order.Direction.desc));
					}
				break;
			}
		} else if (CollectionUtils.isEmpty(orders)) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
		}
		return super.findList(sql, 0, count, filters, orders);
	}
	
	/**
	 * 查找货品    修改添加渠道条件
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param isMarketable
	 *            是否上架
	 * @param generateMethod
	 *            静态生成方式
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 货品
	 */
	public List<Goods> findList(ProductCategory productCategory, Boolean isMarketable,String channel, Goods.GenerateMethod generateMethod, Date beginDate, Date endDate, Integer first, Integer count) {
		String rchannel = "'"+channel+"'";
		String sql = "SELECT * FROM goods g WHERE 1 = 1 ";
		if (productCategory != null) {
			sql += " AND EXISTS (SELECT 1 FROM product_category p WHERE g.product_category_id = p.id AND (p.`tree_path` LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' OR g.product_category_id = "+ productCategory.getId() +")) ";
		}
		if (isMarketable != null) {
			sql += " AND is_marketable = " + isMarketable;
		}
		if (channel != null) {
			sql += " AND channel = " + rchannel;
		}
		if (generateMethod != null) {
			sql += " AND generate_method = " + generateMethod;
		}
		if (beginDate != null) {
			sql += " AND create_date >= '" + DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sql += " AND create_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		return super.findList(sql, first, count, null, null);
	}

	/**
	 * 查找货品分页
	 * 
	 * @param type
	 *            类型
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tag
	 *            标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 货品分页
	 */
	public Page<Goods> findPage(Goods.Type type, String channel, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock,
			Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Pageable pageable) {
		//String rchannel = "%"+channel+"%";
		String rchannel = "'"+channel+"'";
		String sqlExceptSelect = "FROM goods g WHERE 1 = 1 ";
		if (type != null) {
			sqlExceptSelect += " AND g.type = " + type.ordinal();
		}
		if (productCategory != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM product_category p WHERE g.product_category_id = p.id AND (p.`tree_path` LIKE '%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%' OR g.product_category_id = " + productCategory.getId() +" )) ";
		}
		if (brand != null) {
			sqlExceptSelect += " AND brand_id = " + brand.getId();
		}
		if (promotion != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		if (tag != null) {
			sqlExceptSelect += " AND EXISTS (select 1 from goods_tag gt WHERE gt.`goods` = g.`id` AND gt.tags = " + tag.getId() + ") ";
		}
		if (MapUtils.isNotEmpty(attributeValueMap)) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = "attribute_value" + entry.getKey().getPropertyIndex();
				sqlExceptSelect += "AND " + propertyName + " = '" + entry.getValue()+"'";
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sqlExceptSelect += " AND g.price >= " + startPrice;
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sqlExceptSelect += " AND g.price <= " + endPrice;
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND g.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sqlExceptSelect += " AND g.is_list = " + isList;
		}
		if (isTop != null) {
			sqlExceptSelect += " AND g.is_top = " + isTop;
		}
		if (channel != null) {
			sqlExceptSelect += " AND g.channel = " + rchannel;
		}
		if (isOutOfStock != null) {
			String subquery = "";
			if (isOutOfStock) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock`";
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock`";
			}
			sqlExceptSelect += " AND EXISTS (" + subquery + ") ";
		}
		if (isStockAlert != null) {
			String subquery = "";
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock` + " + setting.getStockAlertCount();
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock` + " + setting.getStockAlertCount();
			}
			sqlExceptSelect += " AND EXISTS (" + subquery + ") ";
		}
		if (hasPromotion != null) {
			sqlExceptSelect += "AND EXISTS (SELECT 1 from goods_promotion gp WHERE gp.`goods` = g.`id` AND gp.promotions = " + promotion.getId() + ") ";
		}
		List<Order> orders = new ArrayList<Order>();
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				orders.add(new Order("is_top", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			case priceAsc:
				orders.add(new Order("price", Order.Direction.asc));
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			case priceDesc:
				orders.add(new Order("price", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			case salesDesc:
				orders.add(new Order("sales", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			case scoreDesc:
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			case dateDesc:
				orders.add(new Order("create_date", Order.Direction.desc));
				break;
			}
		} else if (pageable == null || ((StringUtils.isEmpty(pageable.getOrderProperty()) || pageable.getOrderDirection() == null) && (CollectionUtils.isEmpty(pageable.getOrders())))) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
		}
		pageable.setOrders(orders);
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找货品分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 货品分页
	 */
	public Page<Goods> findPage(Goods.RankingType rankingType, Pageable pageable) {
		String sqlExceptSelect = "FROM goods WHERE 1 = 1 ";
		List<Order> orderList = new ArrayList<Order>();
		if (rankingType != null) {
			switch (rankingType) {
			case score:
				orderList.add(new Order("score", Order.Direction.desc));
				orderList.add(new Order("score_count", Order.Direction.desc));
				break;
			case scoreCount:
				orderList.add(new Order("score_count", Order.Direction.desc));
				orderList.add(new Order("score", Order.Direction.desc));
				break;
			case weekHits:
				orderList.add(new Order("week_hits", Order.Direction.desc));
				break;
			case monthHits:
				orderList.add(new Order("month_hits", Order.Direction.desc));
				break;
			case hits:
				orderList.add(new Order("hits", Order.Direction.desc));
				break;
			case weekSales:
				orderList.add(new Order("week_sales", Order.Direction.desc));
				break;
			case monthSales:
				orderList.add(new Order("month_sales", Order.Direction.desc));
				break;
			case sales:
				orderList.add(new Order("sales", Order.Direction.desc));
				break;
			}
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找收藏货品分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收藏货品分页
	 */
	public Page<Goods> findPage(Member member, Pageable pageable, Date beginDate, Date endDate) {
		if (member == null) {
			return null;
		}
		String sqlExceptSelect = "FROM member_favorite_goods fg LEFT join `goods` g ON fg.`favorite_goods` = g.`id` WHERE fg.`favorite_members` = " + member.getId();
		if (beginDate != null) {
			sqlExceptSelect += " AND g.create_date > '" + DateUtils.getDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sqlExceptSelect += " AND g.create_date < '" + DateUtils.getDateTime(endDate) + "' ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查询货品数量
	 * 
	 * @param type
	 *            类型
	 * @param favoriteMember
	 *            收藏会员
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @return 货品数量
	 */
	public Long count(Goods.Type type, Member favoriteMember, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert) {
		String sql = "FROM goods g WHERE 1 = 1 ";
		if (type != null) {
			sql += " AND g.type = " + type.ordinal();
		}
		if (favoriteMember != null) {
			sql += " AND EXISTS (SELECT 1 FROM member_favorite_goods mfg WHERE mfg.favorite_goods = g.id AND mfg.favorite_members = " + favoriteMember.getId() + ") ";
		}
		if (isMarketable != null) {
			sql += " AND g.is_marketable = " + isMarketable;
		}
		if (isList != null) {
			sql += " AND g.is_list = " + isList;
		}
		if (isTop != null) {
			sql += " AND g.is_top = " + isTop;
		}
		if (isOutOfStock != null) {
			String subquery = "";
			if (isOutOfStock) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock`";
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock`";
			}
			sql += "AND EXISTS (" + subquery + ") ";
		}
		if (isStockAlert != null) {
			String subquery = "";
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` <= p1.`allocated_stock` + " + setting.getStockAlertCount();
			} else {
				subquery += "SELECT 1 FROM product p1 WHERE p1.`goods_id` = g.id AND p1.`stock` > p1.`allocated_stock` + " + setting.getStockAlertCount();
			}
			sql += " AND EXISTS ( " + subquery + " ) ";
		}
		return super.count(sql);
	}

	/**
	 * 清空货品属性值
	 * 
	 * @param attribute
	 *            属性
	 */
	public void clearAttributeValue(Attribute attribute) {
		if (attribute == null || attribute.getPropertyIndex() == null || attribute.getProductCategory() == null) {
			return;
		}
		String sql = "UPDATE goods SET attribute_value" + attribute.getPropertyIndex() + " = null WHERE product_category_id = ?";
		Db.update(sql, attribute.getProductCategoryId());
	}

}