package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinalshop.model.base.BaseReview;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 评论
 * 
 * 
 */
public class Review extends BaseReview<Review> {
	private static final long serialVersionUID = -7786965922193880425L;
	public static final Review dao = new Review();
	
	/** 路径前缀 */
	private static final String PATH_PREFIX = "/review/content";

	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/**
	 * 类型
	 */
	public enum Type {

		/** 好评 */
		positive,

		/** 中评 */
		moderate,

		/** 差评 */
		negative
	}
	
	/** 会员 */
	private Member member;

	/** 产品 */
	private Product product;
	
	/** 货品 */
	private Goods goods;
	
	/** 回复 */
	private List<Review> replyReviews = new ArrayList<Review>();
	
	
	/**
	 * 获取回复
	 * 
	 * @return 回复
	 */
	public List<Review> getReplyReviews() {
		if (CollectionUtils.isEmpty(replyReviews)) {
			String sql = "SELECT * FROM `review` WHERE for_review_id = ?";
			replyReviews = Review.dao.find(sql, getId());
		}
		return replyReviews;
	}

	/**
	 * 设置回复
	 * 
	 * @param replyConsultations
	 *            回复
	 */
	public void setReplyReviews(List<Review> replyReviews) {
		this.replyReviews = replyReviews;
	}
	
	/**
	 * 获取评价图片
	 * 
	 * @return 评价图片
	 */
	public List<String> getImagesConverter() {
		List<String> images = new ArrayList<String>();
		JSONArray imagesArrays = JSONArray.parseArray(getImages());
		if (CollectionUtils.isNotEmpty(imagesArrays)) {
			for (int i = 0; i < imagesArrays.size(); i++) {
				images.add(imagesArrays.getString(i));
			}
		}
		return images;
	}
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (ObjectUtils.isEmpty(member)) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取产品
	 * 
	 * @return 产品
	 */
	public Product getProduct() {
		if (ObjectUtils.isEmpty(product)) {
			product = Product.dao.findById(getProductId());
		}
		return product;
	}

	/**
	 * 设置产品
	 * 
	 * @param product
	 *            产品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public Goods getGoods() {
		if (ObjectUtils.isEmpty(goods)) {
			goods = Goods.dao.findById(getGoodsId());
		}
		return goods;
	}

	/**
	 * 设置货品
	 * 
	 * @param goods
	 *            货品
	 */
	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getGoods() != null && getGoods().getId() != null ? PATH_PREFIX + "/" + getGoods().getId() + PATH_SUFFIX : null;
	}
	
}
