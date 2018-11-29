package com.jfinalshop.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.PromotionDao;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.PromotionCoupon;
import com.jfinalshop.model.PromotionGift;
import com.jfinalshop.model.PromotionMemberRank;
import com.jfinalshop.util.Assert;

/**
 * Service - 促销
 * 
 * 
 */
@Singleton
public class PromotionService extends BaseService<Promotion> {

	/**
	 * 构造方法
	 */
	public PromotionService() {
		super(Promotion.class);
	}
	
	/** 价格表达式变量 */
	private static final List<Map<String, Object>> PRICE_EXPRESSION_VARIABLES = new ArrayList<Map<String, Object>>();

	/** 积分表达式变量 */
	private static final List<Map<String, Object>> POINT_EXPRESSION_VARIABLES = new ArrayList<Map<String, Object>>();

	@Inject
	private PromotionDao promotionDao;
	@Inject
	private MemberRankDao memberRankDao;
	@Inject
	private ProductCategoryDao productCategoryDao;

	static {
		Map<String, Object> variable0 = new HashMap<String, Object>();
		Map<String, Object> variable1 = new HashMap<String, Object>();
		Map<String, Object> variable2 = new HashMap<String, Object>();
		Map<String, Object> variable3 = new HashMap<String, Object>();
		variable0.put("quantity", 99);
		variable0.put("price", new BigDecimal("99"));
		variable1.put("quantity", 99);
		variable1.put("price", new BigDecimal("9.9"));
		variable2.put("quantity", 99);
		variable2.put("price", new BigDecimal("0.99"));
		variable3.put("quantity", 99);
		variable3.put("point", 99L);
		PRICE_EXPRESSION_VARIABLES.add(variable0);
		PRICE_EXPRESSION_VARIABLES.add(variable1);
		PRICE_EXPRESSION_VARIABLES.add(variable2);
		POINT_EXPRESSION_VARIABLES.add(variable3);
	}

	/**
	 * 验证价格运算表达式
	 * 
	 * @param priceExpression
	 *            价格运算表达式
	 * @return 验证结果
	 */
	public boolean isValidPriceExpression(String priceExpression) {
		Assert.hasText(priceExpression);

		for (Map<String, Object> variable : PRICE_EXPRESSION_VARIABLES) {
			try {
				Binding binding = new Binding();
				for (Map.Entry<String, Object> entry : variable.entrySet()) {
					binding.setVariable(entry.getKey(), entry.getValue());
				}
				GroovyShell groovyShell = new GroovyShell(binding);
				Object result = groovyShell.evaluate(priceExpression);
				new BigDecimal(result.toString());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证积分运算表达式
	 * 
	 * @param pointExpression
	 *            积分运算表达式
	 * @return 验证结果
	 */
	public boolean isValidPointExpression(String pointExpression) {
		Assert.hasText(pointExpression);

		for (Map<String, Object> variable : POINT_EXPRESSION_VARIABLES) {
			try {
				Binding binding = new Binding();
				for (Map.Entry<String, Object> entry : variable.entrySet()) {
					binding.setVariable(entry.getKey(), entry.getValue());
				}
				GroovyShell groovyShell = new GroovyShell(binding);
				Object result = groovyShell.evaluate(pointExpression);
				Long.valueOf(result.toString());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找促销
	 * 
	 * @param memberRank
	 *            会员等级
	 * @param productCategory
	 *            商品分类
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(MemberRank memberRank, ProductCategory productCategory, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		return promotionDao.findList(memberRank, productCategory, hasBegun, hasEnded, count, filters, orders);
	}

	/**
	 * 查找促销
	 * 
	 * @param memberRankId
	 *            会员等级ID
	 * @param productCategoryId
	 *            商品分类ID
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 促销
	 */
	public List<Promotion> findList(Long memberRankId, Long productCategoryId, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		MemberRank memberRank = memberRankDao.find(memberRankId);
		if (memberRankId != null && memberRank == null) {
			return Collections.emptyList();
		}
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return promotionDao.findList(memberRank, productCategory, hasBegun, hasEnded, count, filters, orders);
	}
	
	/**
	 * 保存
	 * 
	 */
	public Promotion save(Promotion promotion) {
		super.save(promotion);
		// 允许参加会员等级
		List<MemberRank> memberRanks = promotion.getMemberRanks();
		if (CollectionUtils.isNotEmpty(memberRanks)) {
			for (MemberRank memberRank : memberRanks) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanks(memberRank.getId());
				promotionMemberRank.setPromotions(promotion.getId());
				promotionMemberRank.save();
			}
		}
		// 赠送优惠券
		List<Coupon> coupons = promotion.getCoupons();
		if (CollectionUtils.isNotEmpty(coupons)) {
			for (Coupon coupon : coupons) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCoupons(coupon.getId());
				promotionCoupon.setPromotions(promotion.getId());
				promotionCoupon.save();
			}
		}
		// 赠品
		List<Product> gifts = promotion.getGifts();
		if (CollectionUtils.isNotEmpty(gifts)) {
			for (Product gift : gifts) {
				PromotionGift promotionGift = new PromotionGift();
				promotionGift.setGiftPromotions(promotion.getId());
				promotionGift.setGifts(gift.getId());
				promotionGift.save();
			}
		}
		return promotion;
	}

	/**
	 * 更新
	 * 
	 */
	public Promotion update(Promotion promotion) {
		super.update(promotion);
		// 允许参加会员等级
		List<MemberRank> memberRanks = promotion.getMemberRanks();
		if (CollectionUtils.isNotEmpty(memberRanks)) {
			PromotionMemberRank.dao.delete(promotion.getId());
			for (MemberRank memberRank : memberRanks) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanks(memberRank.getId());
				promotionMemberRank.setPromotions(promotion.getId());
				promotionMemberRank.save();
			}
		}
		// 赠送优惠券
		List<Coupon> coupons = promotion.getCoupons();
		if (CollectionUtils.isNotEmpty(coupons)) {
			PromotionCoupon.dao.delete(promotion.getId());
			for (Coupon coupon : coupons) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCoupons(coupon.getId());
				promotionCoupon.setPromotions(promotion.getId());
				promotionCoupon.save();
			}
		}
		// 赠品
		List<Product> gifts = promotion.getGifts();
		if (CollectionUtils.isNotEmpty(gifts)) {
			PromotionGift.dao.delete(promotion.getId());
			for (Product gift : gifts) {
				PromotionGift promotionGift = new PromotionGift();
				promotionGift.setGiftPromotions(promotion.getId());
				promotionGift.setGifts(gift.getId());
				promotionGift.save();
			}
		}
		return promotion;
	}

	public void delete(Long id) {
		PromotionMemberRank.dao.delete(id);
		PromotionCoupon.dao.delete(id);
		PromotionGift.dao.delete(id);
		super.delete(id);
	}

	public void delete(Long... ids) {
		if (ids != null) {
			for (Long id : ids) {
				PromotionMemberRank.dao.delete(id);
				PromotionCoupon.dao.delete(id);
				PromotionGift.dao.delete(id);
			}
		}
		super.delete(ids);
	}

	public void delete(Promotion promotion) {
		super.delete(promotion);
	}
}