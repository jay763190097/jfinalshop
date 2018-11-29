package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.GoodsDao;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.ReviewDao;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Review;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 评论
 * 
 * 
 */
@Singleton
public class ReviewService extends BaseService<Review> {

	/**
	 * 构造方法
	 */
	public ReviewService() {
		super(Review.class);
	}
	
	@Inject
	private ReviewDao reviewDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private GoodsDao goodsDao;
	@Inject
	private OrderDao orderDao;
	@Inject
	private OrderItemDao orderItemDao;
	
	/**
	 * 查找评论
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 评论
	 */
	public List<Review> findList(Member member, Goods goods, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		return reviewDao.findList(member, goods, type, isShow, count, filters, orders);
	}

	/**
	 * 查找评论
	 * 
	 * @param memberId
	 *            会员ID
	 * @param goodsId
	 *            货品ID
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 评论
	 */
	public List<Review> findList(Long memberId, Long goodsId, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		Member member = memberDao.find(memberId);
		if (memberId != null && member == null) {
			return Collections.emptyList();
		}
		Goods goods = goodsDao.find(goodsId);
		if (goodsId != null && goods == null) {
			return Collections.emptyList();
		}
		return reviewDao.findList(member, goods, type, isShow, count, filters, orders);
	}

	/**
	 * 查找评论分页
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 评论分页
	 */
	public Page<Review> findPage(Member member, Goods goods, Review.Type type, Boolean isShow, Pageable pageable) {
		return reviewDao.findPage(member, goods, type, isShow, pageable);
	}


	/**
	 * 查找评论数量
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Goods goods, Review.Type type, Boolean isShow) {
		return reviewDao.count(member, goods, type, isShow);
	}
	
	/**
	 * 查找待评论的订单行
	 * 
	 * @param member
	 * 			会员
	 * @param isReview
	 * 			是否评价
	 * @param pageable
	 *            分页信息
	 * @return
	 * 		订单项分页
	 */
	public Page<OrderItem> findPendingOrderItems(Member member, Boolean isReview, Pageable pageable) {
		return orderItemDao.findPendingOrderItems(member, com.jfinalshop.model.Order.Status.completed, isReview, pageable);
	}
	
	/**
	 * 查找待评论数量
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Boolean isReview) {
		return orderItemDao.count(member, com.jfinalshop.model.Order.Status.completed, isReview);
	}
	
	/**
	 * 咨询回复
	 * 
	 * @param consultation
	 *            咨询
	 * @param replyConsultation
	 *            回复咨询
	 */
	public void reply(Review review, Review replyReview) {
		if (review == null || replyReview == null) {
			return;
		}
		review.setIsShow(true);
		review.update();

		replyReview.setIsShow(true);
		replyReview.setProductId(review.getProductId());
		replyReview.setForReviewId(review.getId());
		reviewDao.save(replyReview);

		Goods goods = review.getProduct().getGoods();
		if (goods != null) {
			goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			goods.update();
		}
	}

	/**
	 * 判断是否拥有评论权限
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @return 是否拥有评论权限
	 */
	public boolean hasPermission(Member member, Goods goods) {
		Assert.notNull(member);
		Assert.notNull(goods);

		Setting setting = SystemUtils.getSetting();
		if (Setting.ReviewAuthority.purchased.equals(setting.getReviewAuthority())) {
			long reviewCount = reviewDao.count(member, goods, null, null);
			long orderCount = orderDao.count(null, com.jfinalshop.model.Order.Status.completed, member, goods, null, null, null, null, null, null);
			return orderCount > reviewCount;
		}
		return true;
	}

}