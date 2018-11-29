package com.jfinalshop.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.AttributeDao;
import com.jfinalshop.dao.BrandDao;
import com.jfinalshop.dao.GoodsDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.dao.PromotionDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.dao.TagDao;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.GoodsPromotion;
import com.jfinalshop.model.GoodsTag;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Tag;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 货品
 * 
 * 
 */
@Singleton
public class GoodsService extends BaseService<Goods> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1165391378249380043L;

	/**
	 * 构造方法
	 */
	public GoodsService() {
		super(Goods.class);
	}
	
	private CacheManager cacheManager =CacheKit.getCacheManager();
	@Inject
	private GoodsDao goodsDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private SnDao snDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private BrandDao brandDao;
	@Inject
	private PromotionDao promotionDao;
	@Inject
	private TagDao tagDao;
	@Inject
	private AttributeDao attributeDao;
	@Inject
	private StockLogDao stockLogDao;
	@Inject
	private ProductImageService productImageService;
	@Inject
	private StaticService staticService;
	
	/**
	 * 查找用户经常买的商品
	 * 
	 * @param Goods
	 *            商品
	 */
	public List<Goods> findMemberBuyList(Member member, Integer count) {
		return goodsDao.findMemberBuyList(member, count);
	}
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		return goodsDao.snExists(sn);
	}


	/**
	 * 根据编号查找货品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 货品，若不存在则返回null
	 */
	public Goods findBySn(String sn) {
		return goodsDao.findBySn(sn);
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
		return goodsDao.findList(type, productCategory, brand, promotion, tag, attributeValueMap, startPrice, endPrice, channel, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	/**
	 * 查找货品
	 * 
	 * @param type
	 *            类型
	 * @param productCategoryId
	 *            商品分类ID
	 * @param brandId
	 *            品牌ID
	 * @param promotionId
	 *            促销ID
	 * @param tagId
	 *            标签ID
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
	 * @param useCache
	 *            是否使用缓存
	 * @return 货品
	 */
	public List<Goods> findList(Goods.Type type, Long productCategoryId, Long brandId, Long promotionId, Long tagId, Map<Long, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, String channel, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert,
			Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		Brand brand = brandDao.find(brandId);
		if (brandId != null && brand == null) {
			return Collections.emptyList();
		}
		Promotion promotion = promotionDao.find(promotionId);
		if (promotionId != null && promotion == null) {
			return Collections.emptyList();
		}
		Tag tag = tagDao.find(tagId);
		if (tagId != null && tag == null) {
			return Collections.emptyList();
		}
		Map<Attribute, String> map = new HashMap<Attribute, String>();
		if (attributeValueMap != null) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Attribute attribute = attributeDao.find(entry.getKey());
				if (attribute != null) {
					map.put(attribute, entry.getValue());
				}
			}
		}
		if (MapUtils.isNotEmpty(attributeValueMap) && MapUtils.isEmpty(map)) {
			return Collections.emptyList();
		}
		return goodsDao.findList(type, productCategory, brand, promotion, tag, map, startPrice, endPrice, channel, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	/** 热搜专用
	 * 查找货品
	 * 
	 * @param type
	 *            类型
	 * @param productCategoryId
	 *            商品分类ID
	 * @param brandId
	 *            品牌ID
	 * @param promotionId
	 *            促销ID
	 * @param tagId
	 *            标签ID
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
	 * @param useCache
	 *            是否使用缓存
	 * @return 货品
	 */
	public List<Goods> findHot(Goods.Type type, Long productCategoryId, Long brandId, Long promotionId, Long tagId, Map<Long, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, String channel , Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert,
			Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		Brand brand = brandDao.find(brandId);
		if (brandId != null && brand == null) {
			return Collections.emptyList();
		}
		Promotion promotion = promotionDao.find(promotionId);
		if (promotionId != null && promotion == null) {
			return Collections.emptyList();
		}
		Tag tag = tagDao.find(tagId);
		if (tagId != null && tag == null) {
			return Collections.emptyList();
		}
		Map<Attribute, String> map = new HashMap<Attribute, String>();
		if (attributeValueMap != null) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Attribute attribute = attributeDao.find(entry.getKey());
				if (attribute != null) {
					map.put(attribute, entry.getValue());
				}
			}
		}
		if (MapUtils.isNotEmpty(attributeValueMap) && MapUtils.isEmpty(map)) {
			return Collections.emptyList();
		}
		return goodsDao.findList(type, productCategory, brand, promotion, tag, map, startPrice, endPrice, channel, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	
	/**
	 * 查找货品
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
	public List<Goods> findList(ProductCategory productCategory, Boolean isMarketable, String channel , Goods.GenerateMethod generateMethod, Date beginDate, Date endDate, Integer first, Integer count) {
		return goodsDao.findList(productCategory, isMarketable, channel, generateMethod, beginDate, endDate, first, count);
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
		return goodsDao.findPage(type, channel, productCategory, brand, promotion, tag, attributeValueMap, startPrice, endPrice, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, pageable);
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
		return goodsDao.findPage(rankingType, pageable);
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
		return goodsDao.findPage(member, pageable, beginDate, endDate);
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
		return goodsDao.count(type, favoriteMember, isMarketable, isList, isTop, isOutOfStock, isStockAlert);
	}

	/**
	 * 查看点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Assert.notNull(id);

		Ehcache cache = cacheManager.getEhcache(Goods.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue() + 1;
		} else {
			Goods goods = goodsDao.find(id);
			if (goods == null) {
				return 0L;
			}
			hits = goods.getHits() + 1;
		}
		cache.put(new Element(id, hits));
		return hits;
	}


	/**
	 * 增加点击数
	 * 
	 * @param goods
	 *            货品
	 * @param amount
	 *            值
	 */
	public void addHits(Goods goods, long amount) {
		Assert.notNull(goods);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekHitsCalendar = DateUtils.toCalendar(goods.getWeekHitsDate());
		Calendar monthHitsCalendar = DateUtils.toCalendar(goods.getMonthHitsDate());
		if (nowCalendar.get(Calendar.YEAR) > weekHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekHitsCalendar.get(Calendar.WEEK_OF_YEAR)) {
			goods.setWeekHits(amount);
		} else {
			goods.setWeekHits(goods.getWeekHits() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthHitsCalendar.get(Calendar.MONTH)) {
			goods.setMonthHits(amount);
		} else {
			goods.setMonthHits(goods.getMonthHits() + amount);
		}
		goods.setHits(goods.getHits() + amount);
		goods.setWeekHitsDate(new Date());
		goods.setMonthHitsDate(new Date());
		goodsDao.update(goods);
	}

	/**
	 * 增加销量
	 * 
	 * @param goods
	 *            货品
	 * @param amount
	 *            值
	 */
	public void addSales(Goods goods, long amount) {
		Assert.notNull(goods);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekSalesCalendar = DateUtils.toCalendar(goods.getWeekSalesDate());
		Calendar monthSalesCalendar = DateUtils.toCalendar(goods.getMonthSalesDate());
		if (nowCalendar.get(Calendar.YEAR) > weekSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekSalesCalendar.get(Calendar.WEEK_OF_YEAR)) {
			goods.setWeekSales(amount);
		} else {
			goods.setWeekSales(goods.getWeekSales() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthSalesCalendar.get(Calendar.MONTH)) {
			goods.setMonthSales(amount);
		} else {
			goods.setMonthSales(goods.getMonthSales() + amount);
		}
		goods.setSales(goods.getSales() + amount);
		goods.setWeekSalesDate(new Date());
		goods.setMonthSalesDate(new Date());
		goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
		goodsDao.update(goods);
	}

	/**
	 * 保存
	 * 
	 * @param goods
	 *            货品
	 * @param product
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	@Before(Tx.class)
	public Goods save(Goods goods, Product product, Admin operator) {
		Assert.notNull(goods);
		Assert.isTrue(goods.isNew());
		Assert.notNull(goods.getType());
		Assert.isTrue(!goods.hasSpecification());
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.state(!product.hasSpecification());

		switch (goods.getTypeName()) {
		case general:
			product.setExchangePoint(0L);
			break;
		case exchange:
			product.setPrice(BigDecimal.ZERO);
			product.setRewardPoint(0L);
			goods.setPromotions(null);
			break;
		case gift:
			product.setPrice(BigDecimal.ZERO);
			product.setRewardPoint(0L);
			product.setExchangePoint(0L);
			goods.setPromotions(null);
			break;
		}
		if (product.getMarketPrice() == null) {
			product.setMarketPrice(calculateDefaultMarketPrice(product.getPrice()));
		}
		if (product.getRewardPoint() == null) {
			product.setRewardPoint(calculateDefaultRewardPoint(product.getPrice()));
		}
		product.setAllocatedStock(0);
		product.setIsDefault(true);
		product.setGoods(goods);
		product.setSpecificationValues(null);
		product.setCartItems(null);
		product.setOrderItems(null);
		product.setShippingItems(null);
		product.setProductNotifies(null);
		product.setStockLogs(null);
		product.setGiftPromotions(null);

		goods.setPrice(product.getPrice());
		goods.setMarketPrice(product.getMarketPrice());
		goods.setScore(0F);
		goods.setTotalScore(0L);
		goods.setScoreCount(0L);
		goods.setHits(0L);
		goods.setWeekHits(0L);
		goods.setMonthHits(0L);
		goods.setSales(0L);
		goods.setWeekSales(0L);
		goods.setMonthSales(0L);
		goods.setWeekHitsDate(new Date());
		goods.setMonthHitsDate(new Date());
		goods.setWeekSalesDate(new Date());
		goods.setMonthSalesDate(new Date());
		goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		goods.setSpecificationItems(null);
		goods.setReviews(null);
		goods.setConsultations(null);
		goods.setFavoriteMembers(null);
		goods.setProducts(null);
		setValue(goods);
		goodsDao.save(goods);

		setValue(product);
		product.setGoodsId(goods.getId());
		
		productDao.save(product);
		stockIn(product, operator);
		
		List<Promotion> promotions = goods.getPromotions();
		if (CollectionUtils.isNotEmpty(promotions)) {
			for (Promotion promotion : promotions) {
				GoodsPromotion goodsPromotion = new GoodsPromotion();
				goodsPromotion.setGoods(goods.getId());
				goodsPromotion.setPromotions(promotion.getId());
				goodsPromotion.save();
			}
		}
		
		List<Tag> tags = goods.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			for (Tag tag : tags) {
				GoodsTag goodsTag = new GoodsTag();
				goodsTag.setGoods(goods.getId());
				goodsTag.setTags(tag.getId());
				goodsTag.save();
			}
		}

		return goods;
	}

	/**
	 * 保存
	 * 
	 * @param goods
	 *            货品
	 * @param products
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	@Before(Tx.class)
	public Goods save(Goods goods, List<Product> products, Admin operator) {
		Assert.notNull(goods);
		Assert.isTrue(goods.isNew());
		Assert.notNull(goods.getType());
		Assert.isTrue(goods.hasSpecification());
		Assert.notEmpty(products);

		//final List<SpecificationItem> specificationItems = goods.getSpecificationItemConverter();
		if (CollectionUtils.exists(products, new Predicate() {
			private Set<List<Integer>> set = new HashSet<List<Integer>>();

			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product == null || !product.isNew() || !product.hasSpecification() || !set.add(product.getSpecificationValueIds()/*) || !specificationValueService.isValid(specificationItems, product.getSpecificationValues()*/);
			}
		})) {
			throw new IllegalArgumentException();
		}

		Product defaultProduct = (Product) CollectionUtils.find(products, new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getIsDefault();
			}
		});
		if (defaultProduct == null) {
			defaultProduct = products.get(0);
			defaultProduct.setIsDefault(true);
		}

		for (Product product : products) {
			switch (goods.getTypeName()) {
			case general:
				product.setExchangePoint(0L);
				break;
			case exchange:
				product.setPrice(BigDecimal.ZERO);
				product.setRewardPoint(0L);
				goods.setPromotions(null);
				break;
			case gift:
				product.setPrice(BigDecimal.ZERO);
				product.setRewardPoint(0L);
				product.setExchangePoint(0L);
				goods.setPromotions(null);
				break;
			}
			if (product.getMarketPrice() == null) {
				product.setMarketPrice(calculateDefaultMarketPrice(product.getPrice()));
			}
			if (product.getRewardPoint() == null) {
				product.setRewardPoint(calculateDefaultRewardPoint(product.getPrice()));
			}
			if (product != defaultProduct) {
				product.setIsDefault(false);
			}
			product.setAllocatedStock(0);
			product.setGoods(goods);
			product.setCartItems(null);
			product.setOrderItems(null);
			product.setShippingItems(null);
			product.setProductNotifies(null);
			product.setStockLogs(null);
			product.setGiftPromotions(null);
		}

		goods.setPrice(defaultProduct.getPrice());
		goods.setMarketPrice(defaultProduct.getMarketPrice());
		goods.setScore(0F);
		goods.setTotalScore(0L);
		goods.setScoreCount(0L);
		goods.setHits(0L);
		goods.setWeekHits(0L);
		goods.setMonthHits(0L);
		goods.setSales(0L);
		goods.setWeekSales(0L);
		goods.setMonthSales(0L);
		goods.setWeekHitsDate(new Date());
		goods.setMonthHitsDate(new Date());
		goods.setWeekSalesDate(new Date());
		goods.setMonthSalesDate(new Date());
		goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		goods.setReviews(null);
		goods.setConsultations(null);
		goods.setFavoriteMembers(null);
		goods.setProducts(null);
		setValue(goods);
		goodsDao.save(goods);

		for (Product product : products) {
			setValue(product);
			product.setGoodsId(goods.getId());
			
			productDao.save(product);
			stockIn(product, operator);
		}
		
		List<Promotion> promotions = goods.getPromotions();
		if (CollectionUtils.isNotEmpty(promotions)) {
			for (Promotion promotion : promotions) {
				GoodsPromotion goodsPromotion = new GoodsPromotion();
				goodsPromotion.setGoods(goods.getId());
				goodsPromotion.setPromotions(promotion.getId());
				goodsPromotion.save();
			}
		}
		
		List<Tag> tags = goods.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			for (Tag tag : tags) {
				GoodsTag goodsTag = new GoodsTag();
				goodsTag.setGoods(goods.getId());
				goodsTag.setTags(tag.getId());
				goodsTag.save();
			}
		}
		
		return goods;
	}

	/**
	 * 更新
	 * 
	 * @param goods
	 *            货品
	 * @param product
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	@Before(Tx.class)
	public Goods update(Goods goods, Product product, Admin operator) {
		Assert.notNull(goods);
		Assert.isTrue(!goods.isNew());
		Assert.isTrue(!goods.hasSpecification());
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.state(!product.hasSpecification());

		Goods pGoods = goodsDao.find(goods.getId());
		switch (pGoods.getTypeName()) {
		case general:
			product.setExchangePoint(0L);
			break;
		case exchange:
			product.setPrice(BigDecimal.ZERO);
			product.setRewardPoint(0L);
			goods.setPromotions(null);
			break;
		case gift:
			product.setPrice(BigDecimal.ZERO);
			product.setRewardPoint(0L);
			product.setExchangePoint(0L);
			goods.setPromotions(null);
			break;
		}
		if (product.getMarketPrice() == null) {
			product.setMarketPrice(calculateDefaultMarketPrice(product.getPrice()));
		}
		if (product.getRewardPoint() == null) {
			product.setRewardPoint(calculateDefaultRewardPoint(product.getPrice()));
		}
		product.setAllocatedStock(0);
		product.setIsDefault(true);
		product.setGoodsId(pGoods.getId());
		product.setSpecificationValues(null);
		product.setCartItems(null);
		product.setOrderItems(null);
		product.setShippingItems(null);
		product.setProductNotifies(null);
		product.setStockLogs(null);
		product.setGiftPromotions(null);

		if (pGoods.hasSpecification()) {
			for (Product pProduct : pGoods.getProducts()) {
				productDao.remove(pProduct);
			}
			if (product.getStock() == null) {
				throw new IllegalArgumentException();
			}
			setValue(product);
			
			productDao.save(product);
			stockIn(product, operator);
		} else {
			Product defaultProduct = pGoods.getDefaultProduct();
			defaultProduct.setPrice(product.getPrice());
			defaultProduct.setCost(product.getCost());
			defaultProduct.setMarketPrice(product.getMarketPrice());
			defaultProduct.setRewardPoint(product.getRewardPoint());
			defaultProduct.setExchangePoint(product.getExchangePoint());
			setValue(defaultProduct);
			productDao.update(defaultProduct);
		}

		goods.setPrice(product.getPrice());
		goods.setMarketPrice(product.getMarketPrice());
		setValue(goods);
		copyProperties(goods, pGoods, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "generateMethod", "reviews", "consultations", "favoriteMembers",
				"products","createDate");
		pGoods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		goodsDao.update(pGoods);
		
		List<Promotion> promotions = goods.getPromotions();
		if (CollectionUtils.isNotEmpty(promotions)) {
			GoodsPromotion.dao.delete(goods.getId());
			for (Promotion promotion : promotions) {
				GoodsPromotion goodsPromotion = new GoodsPromotion();
				goodsPromotion.setGoods(goods.getId());
				goodsPromotion.setPromotions(promotion.getId());
				goodsPromotion.save();
			}
		}
		
		List<Tag> tags = goods.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			GoodsTag.dao.delete(goods.getId());
			for (Tag tag : tags) {
				GoodsTag goodsTag = new GoodsTag();
				goodsTag.setGoods(goods.getId());
				goodsTag.setTags(tag.getId());
				goodsTag.save();
			}
		}
		
		return pGoods;
	}
	
	

	/**
	 * 更新
	 * 
	 * @param goods
	 *            货品
	 * @param products
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	@Before(Tx.class)
	public Goods update(Goods goods, List<Product> products, Admin operator) {
		Assert.notNull(goods);
		Assert.isTrue(!goods.isNew());
		Assert.isTrue(goods.hasSpecification());
		Assert.notEmpty(products);

		//final List<SpecificationItem> specificationItems = goods.getSpecificationItemConverter();
		if (CollectionUtils.exists(products, new Predicate() {
			private Set<List<Integer>> set = new HashSet<List<Integer>>();

			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product == null || !product.isNew() || !product.hasSpecification() || !set.add(product.getSpecificationValueIds()); //) || !specificationValueService.isValid(specificationItems, product.getSpecificationValues()
			}
		})) {
			throw new IllegalArgumentException();
		}

		Product defaultProduct = (Product) CollectionUtils.find(products, new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getIsDefault();
			}
		});
		if (defaultProduct == null) {
			defaultProduct = products.get(0);
			defaultProduct.setIsDefault(true);
		}

		Goods pGoods = goodsDao.find(goods.getId());
		for (Product product : products) {
			switch (pGoods.getTypeName()) {
			case general:
				product.setExchangePoint(0L);
				break;
			case exchange:
				product.setPrice(BigDecimal.ZERO);
				product.setRewardPoint(0L);
				goods.setPromotions(null);
				break;
			case gift:
				product.setPrice(BigDecimal.ZERO);
				product.setRewardPoint(0L);
				product.setExchangePoint(0L);
				goods.setPromotions(null);
				break;
			}
			if (product.getMarketPrice() == null) {
				product.setMarketPrice(calculateDefaultMarketPrice(product.getPrice()));
			}
			if (product.getRewardPoint() == null) {
				product.setRewardPoint(calculateDefaultRewardPoint(product.getPrice()));
			}
			if (product != defaultProduct) {
				product.setIsDefault(false);
			}
			product.setAllocatedStock(0);
			product.setGoodsId(pGoods.getId());
			product.setCartItems(null);
			product.setOrderItems(null);
			product.setShippingItems(null);
			product.setProductNotifies(null);
			product.setStockLogs(null);
			product.setGiftPromotions(null);
		}

		if (pGoods.hasSpecification()) {
			for (Product pProduct : pGoods.getProducts()) {
				if (!exists(products, pProduct.getSpecificationValueIds())) {
					productDao.remove(pProduct);
				}
			}
			for (Product product : products) {
				Product pProduct = find(pGoods.getProducts(), product.getSpecificationValueIds());
				if (pProduct != null) {
					pProduct.setPrice(product.getPrice());
					pProduct.setCost(product.getCost());
					pProduct.setMarketPrice(product.getMarketPrice());
					pProduct.setRewardPoint(product.getRewardPoint());
					pProduct.setExchangePoint(product.getExchangePoint());
					pProduct.setIsDefault(product.getIsDefault());
					pProduct.setSpecificationValues(product.getSpecificationValues());
					setValue(pProduct);
					productDao.update(pProduct);
				} else {
					if (product.getStock() == null) {
						throw new IllegalArgumentException();
					}
					setValue(product);
					productDao.save(product);
					stockIn(product, operator);
				}
			}
		} else {
			productDao.remove(pGoods.getDefaultProduct());
			for (Product product : products) {
				if (product.getStock() == null) {
					throw new IllegalArgumentException();
				}
				setValue(product);
				productDao.save(product);
				stockIn(product, operator);
			}
		}

		goods.setPrice(defaultProduct.getPrice());
		goods.setMarketPrice(defaultProduct.getMarketPrice());
		setValue(goods);
		copyProperties(goods, pGoods, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "generateMethod", "reviews", "consultations", "favoriteMembers",
				"products","createDate");
		pGoods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		goodsDao.update(pGoods);
		
		List<Promotion> promotions = goods.getPromotions();
		if (CollectionUtils.isNotEmpty(promotions)) {
			GoodsPromotion.dao.delete(goods.getId());
			for (Promotion promotion : promotions) {
				GoodsPromotion goodsPromotion = new GoodsPromotion();
				goodsPromotion.setGoods(goods.getId());
				goodsPromotion.setPromotions(promotion.getId());
				goodsPromotion.save();
			}
		}
		
		List<Tag> tags = goods.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			GoodsTag.dao.delete(goods.getId());
			for (Tag tag : tags) {
				GoodsTag goodsTag = new GoodsTag();
				goodsTag.setGoods(goods.getId());
				goodsTag.setTags(tag.getId());
				goodsTag.save();
			}
		}
		return pGoods;
	}

	public Goods save(Goods goods) {
		Assert.notNull(goods);

		goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		setValue(goods);
		return super.save(goods);
	}

	public Goods update(Goods goods) {
		Assert.notNull(goods);

		goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
		setValue(goods);
		return super.update(goods);
	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		if (ids != null) {
			for (Long id : ids) {
				delete(goodsDao.find(id));
			}
		}
	}

	public void delete(Goods goods) {
		GoodsPromotion.dao.delete(goods.getId());
		GoodsTag.dao.delete(goods.getId());
		productDao.delete(goods.getId());
		staticService.delete(goods);
		super.delete(goods);
	}

	/**
	 * 设置货品值
	 * 
	 * @param goods
	 *            货品
	 */
	private void setValue(Goods goods) {
		if (goods == null) {
			return;
		}

		productImageService.generate(goods.getProductImagesConverter());
		List<ProductImage> productImagesConverter = goods.getProductImagesConverter();
		if (CollectionUtils.isNotEmpty(productImagesConverter)) {
			List<ProductImage> productImages = new ArrayList<ProductImage>();
			for (ProductImage productImage : productImagesConverter) {
				productImages.add(productImage);
			}
			goods.setProductImages(JSONArray.toJSONString(productImages));
		}
		
		if (StringUtils.isEmpty(goods.getImage()) && StringUtils.isNotEmpty(goods.getThumbnail())) {
			goods.setImage(goods.getThumbnail());
		}
		if (goods.isNew()) {
			if (StringUtils.isEmpty(goods.getSn())) {
				String sn;
				do {
					sn = snDao.generate(Sn.Type.goods);
				} while (snExists(sn));
				goods.setSn(sn);
			}
		}
	}

	/**
	 * 设置商品值
	 * 
	 * @param product
	 *            商品
	 */
	private void setValue(Product product) {
		if (product == null) {
			return;
		}

		if (product.isNew()) {
			Goods goods = product.getGoods();
			if (goods != null && StringUtils.isNotEmpty(goods.getSn())) {
				String sn;
				int i = product.hasSpecification() ? 1 : 0;
				do {
					sn = goods.getSn() + (i == 0 ? "" : "_" + i);
					i++;
				} while (productDao.snExists(sn));
				product.setSn(sn);
			}
		}
	}

	/**
	 * 计算默认市场价
	 * 
	 * @param price
	 *            价格
	 * @return 默认市场价
	 */
	private BigDecimal calculateDefaultMarketPrice(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return defaultMarketPriceScale != null ? setting.setScale(price.multiply(new BigDecimal(String.valueOf(defaultMarketPriceScale)))) : BigDecimal.ZERO;
	}

	/**
	 * 计算默认赠送积分
	 * 
	 * @param price
	 *            价格
	 * @return 默认赠送积分
	 */
	private long calculateDefaultRewardPoint(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultPointScale = setting.getDefaultPointScale();
		return defaultPointScale != null ? price.multiply(new BigDecimal(String.valueOf(defaultPointScale))).longValue() : 0L;
	}

	/**
	 * 根据规格值ID查找商品
	 * 
	 * @param products
	 *            商品
	 * @param specificationValueIds
	 *            规格值ID
	 * @return 商品
	 */
	private Product find(Collection<Product> products, final List<Integer> specificationValueIds) {
		if (CollectionUtils.isEmpty(products) || CollectionUtils.isEmpty(specificationValueIds)) {
			return null;
		}

		return (Product) CollectionUtils.find(products, new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getSpecificationValueIds() != null && product.getSpecificationValueIds().equals(specificationValueIds);
			}
		});
	}

	/**
	 * 根据规格值ID判断商品是否存在
	 * 
	 * @param products
	 *            商品
	 * @param specificationValueIds
	 *            规格值ID
	 * @return 商品是否存在
	 */
	private boolean exists(Collection<Product> products, final List<Integer> specificationValueIds) {
		return find(products, specificationValueIds) != null;
	}

	/**
	 * 入库
	 * 
	 * @param product
	 *            商品
	 * @param operator
	 *            操作员
	 */
	private void stockIn(Product product, Admin operator) {
		if (product == null || product.getStock() == null || product.getStock() <= 0) {
			return;
		}

		StockLog stockLog = new StockLog();
		stockLog.setType(StockLog.Type.stockIn.ordinal());
		stockLog.setInQuantity(product.getStock());
		stockLog.setOutQuantity(0);
		stockLog.setStock(product.getStock());
		stockLog.setOperator(operator);
		stockLog.setMemo(null);
		stockLog.setProductId(product.getId());
		stockLogDao.save(stockLog);
	}

}