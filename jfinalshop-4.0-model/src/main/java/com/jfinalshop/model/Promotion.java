package com.jfinalshop.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BasePromotion;

/**
 * Model - 促销
 * 
 * 
 */
public class Promotion extends BasePromotion<Promotion> {
	private static final long serialVersionUID = -7841815369133578029L;
	public static final Promotion dao = new Promotion();
	
	/** 路径前缀 */
	private static final String PATH_PREFIX = "/promotion/content";

	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";
	
	/** 允许参加会员等级 */
	private List<MemberRank> memberRanks = new ArrayList<MemberRank>();

	/** 赠送优惠券 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/** 赠品 */
	private List<Product> gifts = new ArrayList<Product>();

	/** 货品 */
	private List<Goods> goods = new ArrayList<Goods>();

	/** 商品分类 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();
	
	/**
	 * 获取允许参加会员等级
	 * 
	 * @return 允许参加会员等级
	 */
	public List<MemberRank> getMemberRanks() {
		if (CollectionUtils.isEmpty(memberRanks)) {
			String sql = "SELECT mr.* FROM `promotion_member_rank` pmr LEFT JOIN `member_rank` mr ON pmr.`member_ranks` = mr.`id` WHERE pmr.`promotions` = ?";
			memberRanks = MemberRank.dao.find(sql, getId());
		}
		return memberRanks;
	}

	/**
	 * 设置允许参加会员等级
	 * 
	 * @param memberRanks
	 *            允许参加会员等级
	 */
	public void setMemberRanks(List<MemberRank> memberRanks) {
		this.memberRanks = memberRanks;
	}

	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	public List<Coupon> getCoupons() {
		if (CollectionUtils.isEmpty(coupons)) {
			String sql = "SELECT c.* FROM `promotion_coupon` pc LEFT JOIN `coupon` c ON pc.`coupons` = c.`id` WHERE pc.`promotions` = ?";
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}

	/**
	 * 设置赠送优惠券
	 * 
	 * @param coupons
	 *            赠送优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取赠品
	 * 
	 * @return 赠品
	 */
	public List<Product> getGifts() {
		if (CollectionUtils.isEmpty(gifts)) {
			String sql = "SELECT p.* FROM `promotion_gift` pg LEFT JOIN `product` p ON pg.`gifts` = p.`id` WHERE pg.`gift_promotions` = ?";
			gifts = Product.dao.find(sql, getId());
		}
		return gifts;
	}

	/**
	 * 设置赠品
	 * 
	 * @param gifts
	 *            赠品
	 */
	public void setGifts(List<Product> gifts) {
		this.gifts = gifts;
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public List<Goods> getGoods() {
		if (CollectionUtils.isEmpty(goods)) {
			String sql = "SELECT g.* FROM `goods_promotion` gp LEFT JOIN `goods` g ON gp.`goods` = g.`id` WHERE gp.`promotions` = ?";
			goods = Goods.dao.find(sql, getId());
		}
		return goods;
	}

	/**
	 * 设置货品
	 * 
	 * @param goods
	 *            货品
	 */
	public void setGoods(List<Goods> goods) {
		this.goods = goods;
	}

	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		if (CollectionUtils.isEmpty(productCategories)) {
			String sql = "SELECT pc.* FROM `product_category_promotion` pcp LEFT JOIN `product_category` pc ON pcp.`product_categories` = pc.`id` WHERE pcp.`promotions` = 1 ORDER BY `orders` ASC";
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getId() != null ? PATH_PREFIX + "/" + getId() + PATH_SUFFIX : null;
	}

	/**
	 * 判断是否已开始
	 * 
	 * @return 是否已开始
	 */
	public boolean hasBegun() {
		return getBeginDate() == null || !getBeginDate().after(new Date());
	}

	/**
	 * 判断是否已结束
	 * 
	 * @return 是否已结束
	 */
	public boolean hasEnded() {
		return getEndDate() != null && !getEndDate().after(new Date());
	}

	/**
	 * 计算促销价格
	 * 
	 * @param price
	 *            商品价格
	 * @param quantity
	 *            商品数量
	 * @return 促销价格
	 */
	public BigDecimal calculatePrice(BigDecimal price, Integer quantity) {
		if (price == null || quantity == null || StringUtils.isEmpty(getPriceExpression())) {
			return price;
		}
		BigDecimal result = BigDecimal.ZERO;
		try {
			Binding binding = new Binding();
			binding.setVariable("quantity", quantity);
			binding.setVariable("price", price);
			GroovyShell groovyShell = new GroovyShell(binding);
			result = new BigDecimal(groovyShell.evaluate(getPriceExpression()).toString());
		} catch (Exception e) {
			return price;
		}
		if (result.compareTo(price) > 0) {
			return price;
		}
		return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
	}

	/**
	 * 计算促销赠送积分
	 * 
	 * @param point
	 *            赠送积分
	 * @param quantity
	 *            商品数量
	 * @return 促销赠送积分
	 */
	public Long calculatePoint(Long point, Integer quantity) {
		if (point == null || quantity == null || StringUtils.isEmpty(getPointExpression())) {
			return point;
		}
		Long result = 0L;
		try {
			Binding binding = new Binding();
			binding.setVariable("quantity", quantity);
			binding.setVariable("point", point);
			GroovyShell groovyShell = new GroovyShell(binding);
			result = Long.valueOf(groovyShell.evaluate(getPointExpression()).toString());
		} catch (Exception e) {
			return point;
		}
		if (result < point) {
			return point;
		}
		return result > 0L ? result : 0L;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Goods> goodsList = getGoods();
		if (goodsList != null) {
			for (Goods goods : goodsList) {
				goods.getPromotions().remove(this);
			}
		}
		List<ProductCategory> productCategories = getProductCategories();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				productCategory.getPromotions().remove(this);
			}
		}
	}
}
