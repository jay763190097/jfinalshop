package com.jfinalshop.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.Setting;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Product;
import com.jfinalshop.util.SystemUtils;

/**
 * Entity - 货品
 * 
 */
public class GoodsVO {

	/** 点击数缓存名称 */
	public static final String HITS_CACHE_NAME = "goodsHits";

	/** 属性值属性个数 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 20;

	/**
	 * 类型
	 */
	public enum Type {

		/** 普通商品 */
		general,

		/** 兑换商品 */
		exchange,

		/** 赠品 */
		gift
	}

	/**
	 * 静态生成方式
	 */
	public enum GenerateMethod {

		/** 无 */
		none,

		/** 即时 */
		eager,

		/** 延时 */
		lazy
	}

	/**
	 * 排序类型
	 */
	public enum OrderType {

		/** 置顶降序 */
		topDesc,

		/** 价格升序 */
		priceAsc,

		/** 价格降序 */
		priceDesc,

		/** 销量降序 */
		salesDesc,

		/** 评分降序 */
		scoreDesc,

		/** 日期降序 */
		dateDesc
	}

	/**
	 * 排名类型
	 */
	public enum RankingType {

		/** 评分 */
		score,

		/** 评分数 */
		scoreCount,

		/** 周点击数 */
		weekHits,

		/** 月点击数 */
		monthHits,

		/** 点击数 */
		hits,

		/** 周销量 */
		weekSales,

		/** 月销量 */
		monthSales,

		/** 销量 */
		sales
	}
	
	/**
	 * ID
	 */
	private Long id;
	
	/**
	 * 创建日期
	 */
	private Date createdDate;

	/** 编号 */
	private String sn;

	/** 名称 */
	private String name;

	/** 副标题 */
	private String caption;

	/** 类型 */
	private Integer type;

	/** 销售价 */
	private BigDecimal price;

	/** 市场价 */
	private BigDecimal marketPrice;

	/** 展示图片 */
	private String image;

	/** 单位 */
	private String unit;

	/** 重量 */
	private Integer weight;

	/** 是否上架 */
	private Boolean isMarketable;

	/** 是否列出 */
	private Boolean isList;

	/** 是否置顶 */
	private Boolean isTop;

	/** 是否需要物流 */
	private Boolean isDelivery;

	/** 介绍 */
	private String introduction;

	/** 渠道 */
	private String channel;

	/** 搜索关键词 */
	private String keyword;

	/** 页面标题 */
	private String seoTitle;

	/** 页面关键词 */
	private String seoKeywords;

	/** 页面描述 */
	private String seoDescription;

	/** 评分 */
	private Float score;

	/** 总评分 */
	private Long totalScore;

	/** 评分数 */
	private Long scoreCount;

	/** 周点击数 */
	private Long weekHits;

	/** 月点击数 */
	private Long monthHits;

	/** 点击数 */
	private Long hits;

	/** 周销量 */
	private Long weekSales;

	/** 月销量 */
	private Long monthSales;

	/** 销量 */
	private Long sales;

	/** 周点击数更新日期 */
	private Date weekHitsDate;

	/** 月点击数更新日期 */
	private Date monthHitsDate;

	/** 周销量更新日期 */
	private Date weekSalesDate;

	/** 月销量更新日期 */
	private Date monthSalesDate;
	
	/**
	 * 商品图片
	 */
	private String productImages;
	
	/** 商品图片 */
	private List<ProductImage> pProductImages = new ArrayList<ProductImage>();
	
	/** 商品 */
	private List<Product> products = new ArrayList<Product>();

	/**
	 * 获取ID
	 * 
	 * @return ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id
	 *            ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取创建日期
	 * 
	 * @return 创建日期
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param createdDate
	 *            创建日期
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * 获取编号
	 * 
	 * @return 编号
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * 设置编号
	 * 
	 * @param sn
	 *            编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取副标题
	 * 
	 * @return 副标题
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * 设置副标题
	 * 
	 * @param caption
	 *            副标题
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 获取销售价
	 * 
	 * @return 销售价
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置销售价
	 * 
	 * @param price
	 *            销售价
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取市场价
	 * 
	 * @return 市场价
	 */
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	/**
	 * 设置市场价
	 * 
	 * @param marketPrice
	 *            市场价
	 */
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	/**
	 * 获取展示图片
	 * 
	 * @return 展示图片
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 设置展示图片
	 * 
	 * @param image
	 *            展示图片
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 获取单位
	 * 
	 * @return 单位
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * 设置单位
	 * 
	 * @param unit
	 *            单位
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 获取重量
	 * 
	 * @return 重量
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * 设置重量
	 * 
	 * @param weight
	 *            重量
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public Boolean getIsMarketable() {
		return isMarketable;
	}

	/**
	 * 设置是否上架
	 * 
	 * @param isMarketable
	 *            是否上架
	 */
	public void setIsMarketable(Boolean isMarketable) {
		this.isMarketable = isMarketable;
	}

	/**
	 * 获取是否列出
	 * 
	 * @return 是否列出
	 */
	public Boolean getIsList() {
		return isList;
	}

	/**
	 * 设置是否列出
	 * 
	 * @param isList
	 *            是否列出
	 */
	public void setIsList(Boolean isList) {
		this.isList = isList;
	}

	/**
	 * 获取是否置顶
	 * 
	 * @return 是否置顶
	 */
	public Boolean getIsTop() {
		return isTop;
	}

	/**
	 * 设置是否置顶
	 * 
	 * @param isTop
	 *            是否置顶
	 */
	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public Boolean getIsDelivery() {
		return isDelivery;
	}

	/**
	 * 设置是否需要物流
	 * 
	 * @param isDelivery
	 *            是否需要物流
	 */
	public void setIsDelivery(Boolean isDelivery) {
		this.isDelivery = isDelivery;
	}

	/**
	 * 获取介绍
	 * 
	 * @return 介绍
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * 设置介绍
	 * 
	 * @param introduction
	 *            介绍
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * 获取渠道
	 * 
	 * @return 渠道
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * 设置渠道
	 * 
	 * @param channel
	 *            渠道
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 获取搜索关键词
	 * 
	 * @return 搜索关键词
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置搜索关键词
	 * 
	 * @param keyword
	 *            搜索关键词
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.keyword = keyword;
	}

	/**
	 * 获取页面标题
	 * 
	 * @return 页面标题
	 */
	public String getSeoTitle() {
		return seoTitle;
	}

	/**
	 * 设置页面标题
	 * 
	 * @param seoTitle
	 *            页面标题
	 */
	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	/**
	 * 获取页面关键词
	 * 
	 * @return 页面关键词
	 */
	public String getSeoKeywords() {
		return seoKeywords;
	}

	/**
	 * 设置页面关键词
	 * 
	 * @param seoKeywords
	 *            页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.seoKeywords = seoKeywords;
	}

	/**
	 * 获取页面描述
	 * 
	 * @return 页面描述
	 */
	public String getSeoDescription() {
		return seoDescription;
	}

	/**
	 * 设置页面描述
	 * 
	 * @param seoDescription
	 *            页面描述
	 */
	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	/**
	 * 获取评分
	 * 
	 * @return 评分
	 */
	public Float getScore() {
		return score;
	}

	/**
	 * 设置评分
	 * 
	 * @param score
	 *            评分
	 */
	public void setScore(Float score) {
		this.score = score;
	}

	/**
	 * 获取总评分
	 * 
	 * @return 总评分
	 */
	public Long getTotalScore() {
		return totalScore;
	}

	/**
	 * 设置总评分
	 * 
	 * @param totalScore
	 *            总评分
	 */
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * 获取评分数
	 * 
	 * @return 评分数
	 */
	public Long getScoreCount() {
		return scoreCount;
	}

	/**
	 * 设置评分数
	 * 
	 * @param scoreCount
	 *            评分数
	 */
	public void setScoreCount(Long scoreCount) {
		this.scoreCount = scoreCount;
	}

	/**
	 * 获取周点击数
	 * 
	 * @return 周点击数
	 */
	public Long getWeekHits() {
		return weekHits;
	}

	/**
	 * 设置周点击数
	 * 
	 * @param weekHits
	 *            周点击数
	 */
	public void setWeekHits(Long weekHits) {
		this.weekHits = weekHits;
	}

	/**
	 * 获取月点击数
	 * 
	 * @return 月点击数
	 */
	public Long getMonthHits() {
		return monthHits;
	}

	/**
	 * 设置月点击数
	 * 
	 * @param monthHits
	 *            月点击数
	 */
	public void setMonthHits(Long monthHits) {
		this.monthHits = monthHits;
	}

	/**
	 * 获取点击数
	 * 
	 * @return 点击数
	 */
	public Long getHits() {
		return hits;
	}

	/**
	 * 设置点击数
	 * 
	 * @param hits
	 *            点击数
	 */
	public void setHits(Long hits) {
		this.hits = hits;
	}

	/**
	 * 获取周销量
	 * 
	 * @return 周销量
	 */
	public Long getWeekSales() {
		return weekSales;
	}

	/**
	 * 设置周销量
	 * 
	 * @param weekSales
	 *            周销量
	 */
	public void setWeekSales(Long weekSales) {
		this.weekSales = weekSales;
	}

	/**
	 * 获取月销量
	 * 
	 * @return 月销量
	 */
	public Long getMonthSales() {
		return monthSales;
	}

	/**
	 * 设置月销量
	 * 
	 * @param monthSales
	 *            月销量
	 */
	public void setMonthSales(Long monthSales) {
		this.monthSales = monthSales;
	}

	/**
	 * 获取销量
	 * 
	 * @return 销量
	 */
	public Long getSales() {
		return sales;
	}

	/**
	 * 设置销量
	 * 
	 * @param sales
	 *            销量
	 */
	public void setSales(Long sales) {
		this.sales = sales;
	}

	/**
	 * 获取周点击数更新日期
	 * 
	 * @return 周点击数更新日期
	 */
	public Date getWeekHitsDate() {
		return weekHitsDate;
	}

	/**
	 * 设置周点击数更新日期
	 * 
	 * @param weekHitsDate
	 *            周点击数更新日期
	 */
	public void setWeekHitsDate(Date weekHitsDate) {
		this.weekHitsDate = weekHitsDate;
	}

	/**
	 * 获取月点击数更新日期
	 * 
	 * @return 月点击数更新日期
	 */
	public Date getMonthHitsDate() {
		return monthHitsDate;
	}

	/**
	 * 设置月点击数更新日期
	 * 
	 * @param monthHitsDate
	 *            月点击数更新日期
	 */
	public void setMonthHitsDate(Date monthHitsDate) {
		this.monthHitsDate = monthHitsDate;
	}

	/**
	 * 获取周销量更新日期
	 * 
	 * @return 周销量更新日期
	 */
	public Date getWeekSalesDate() {
		return weekSalesDate;
	}

	/**
	 * 设置周销量更新日期
	 * 
	 * @param weekSalesDate
	 *            周销量更新日期
	 */
	public void setWeekSalesDate(Date weekSalesDate) {
		this.weekSalesDate = weekSalesDate;
	}

	/**
	 * 获取月销量更新日期
	 * 
	 * @return 月销量更新日期
	 */
	public Date getMonthSalesDate() {
		return monthSalesDate;
	}

	/**
	 * 设置月销量更新日期
	 * 
	 * @param monthSalesDate
	 *            月销量更新日期
	 */
	public void setMonthSalesDate(Date monthSalesDate) {
		this.monthSalesDate = monthSalesDate;
	}

	
	/**
	 * 获取缩略图
	 * 
	 * @return 缩略图
	 */
	public String getThumbnail() {
		if (CollectionUtils.isEmpty(getProductImagesConverter())) {
			return null;
		}
		return getProductImagesConverter().get(0).getThumbnail();
	}
	
	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public List<ProductImage> getProductImagesConverter() {
		if (CollectionUtils.isEmpty(pProductImages)) {
			JSONArray productImageArrays = JSONArray.parseArray(getProductImages());
			if (CollectionUtils.isNotEmpty(productImageArrays)) {
				for (int i = 0; i < productImageArrays.size(); i++) {
					pProductImages.add(JSONObject.parseObject(productImageArrays.getString(i), ProductImage.class));
				}
			}
		}
		return pProductImages;
	}
	
	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public String getProductImages() {
		return productImages;
	}

	/**
	 * 设置商品图片
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void setProductImages(String productImages) {
		this.productImages = productImages;
	}
	
	/**
	 * 获取默认商品
	 * 
	 * @return 默认商品
	 */
	public Product getDefaultProduct() {
		return (Product) CollectionUtils.find(getProducts(), new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getIsDefault();
			}
		});
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT * FROM product WHERE goods_id = ?";
			products = Product.dao.find(sql, getId());
		}
		return products;
	}
	
	/**
	 * 获取URL
	 * 
	 * @return URL
	 */
	public String getUrl() {
		Setting setting = SystemUtils.getSetting();
		return setting.getSiteUrl() + getPath();
	}
	
	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("goodsContent");
		Map<String, Object> model = new HashMap<String, Object>();
		Goods goods = Goods.dao.findById(getId());
		model.put("goods", goods);
		return templateConfig.getRealStaticPath(model);
	}
}
