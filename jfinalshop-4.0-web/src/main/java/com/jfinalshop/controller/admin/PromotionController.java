package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 促销
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/promotion")
public class PromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private ProductService productService;
	@Inject
	private CouponService couponService;

	/**
	 * 检查价格运算表达式是否正确
	 */
	public void checkPriceExpression() {
		String priceExpression = getPara("promotion.price_expression");
		if (StringUtils.isEmpty(priceExpression)) {
			renderJson(false);
			return;
		}
		renderJson(promotionService.isValidPriceExpression(priceExpression));
	}

	/**
	 * 检查积分运算表达式是否正确
	 */
	public void checkPointExpression() {
		String pointExpression = getPara("promotion.point_expression");
		if (StringUtils.isEmpty(pointExpression)) {
			renderJson(false);
			return;
		}
		renderJson(promotionService.isValidPointExpression(pointExpression));
	}

	/**
	 * 赠品选择
	 */
	public void giftSelect() {
		String keyword = getPara("q");
		Long[] excludeIds = getParaValuesToLong("excludeIds");
		Integer count = getParaToInt("limit");
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(keyword)) {
			renderJson(data);
			return;
		}
		Set<Product> excludes = new HashSet<Product>(productService.findList(excludeIds));
		List<Product> products = productService.search(Goods.Type.gift, keyword, excludes, count);
		for (Product product : products) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", product.getId());
			item.put("sn", product.getSn());
			item.put("name", product.getName());
			item.put("specifications", product.getSpecifications());
			item.put("url", product.getUrl());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("coupons", couponService.findAll());
		render("/admin/promotion/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Promotion promotion = getModel(Promotion.class);
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] giftIds = getParaValuesToLong("giftIds");
		Boolean isFreeShipping = getParaToBoolean("isFreeShipping", false);
		Boolean isCouponAllowed = getParaToBoolean("isCouponAllowed", false);
		promotion.setIsFreeShipping(isFreeShipping);
		promotion.setIsCouponAllowed(isCouponAllowed);

		promotion.setMemberRanks(new ArrayList<MemberRank>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new ArrayList<Coupon>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Product> gifts = productService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Product gift = (Product) object;
					return gift != null && Goods.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new ArrayList<Product>(gifts));
		} else {
			promotion.setGifts(null);
		}
		
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression()) && !promotionService.isValidPriceExpression(promotion.getPriceExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression()) && !promotionService.isValidPointExpression(promotion.getPointExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		promotion.setGoods(null);
		promotion.setProductCategories(null);
		promotionService.save(promotion);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/promotion/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("promotion", promotionService.find(id));
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("coupons", couponService.findAll());
		render("/admin/promotion/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Promotion promotion = getModel(Promotion.class);
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] giftIds = getParaValuesToLong("giftIds");
		Boolean isFreeShipping = getParaToBoolean("isFreeShipping", false);
		Boolean isCouponAllowed = getParaToBoolean("isCouponAllowed", false);
		
		promotion.setIsFreeShipping(isFreeShipping);
		promotion.setIsCouponAllowed(isCouponAllowed);
		promotion.setMemberRanks(new ArrayList<MemberRank>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new ArrayList<Coupon>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Product> gifts = productService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Product gift = (Product) object;
					return gift != null && Goods.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new ArrayList<Product>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression()) && !promotionService.isValidPriceExpression(promotion.getPriceExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression()) && !promotionService.isValidPointExpression(promotion.getPointExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		
		//promotion.remove("goods", "productCategories");
		promotionService.update(promotion);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/promotion/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", promotionService.findPage(pageable));
		render("/admin/promotion/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		promotionService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}