package com.jfinalshop.dao;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Review;
import com.jfinalshop.util.Assert;

/**
 * Dao - 评论
 * 
 * 
 */
public class ReviewDao extends BaseDao<Review> {
	
	/**
	 * 构造方法
	 */
	public ReviewDao() {
		super(Review.class);
	}
	
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
		String sql = "SELECT * FROM review WHERE 1 = 1 ";
		if (member != null) {
			sql += " AND member_id = " + member.getId();
		}
		if (goods != null) {
			sql += " AND goods_id = " + goods.getId();
		}
		if (type != null) {
			switch (type) {
			case positive:
				sql += " AND score = 0";
				break;
			case moderate:
				sql += " AND score = 1";
				break;
			case negative:
				sql += " AND score = 2";
				break;
			}
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		return super.findList(sql, null, count, filters, orders);
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
		String sqlExceptSelect = "FROM review WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (goods != null) {
			sqlExceptSelect += " AND goods_id = " + goods.getId();
		}
		if (type != null) {
			switch (type) {
			case positive:
				sqlExceptSelect += " AND score = 0";
				break;
			case moderate:
				sqlExceptSelect += " AND score = 1";
				break;
			case negative:
				sqlExceptSelect += " AND score = 2";
				break;
			}
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		return super.findPage(sqlExceptSelect, pageable);
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
		String sqlExceptSelect = "FROM review WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (goods != null) {
			sqlExceptSelect += " AND goods_id = " + goods.getId();
		}
		if (type != null) {
			switch (type) {
			case positive:
				sqlExceptSelect += " AND score = 0";
				break;
			case moderate:
				sqlExceptSelect += " AND score = 1";
				break;
			case negative:
				sqlExceptSelect += " AND score = 2";
				break;
			}
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		return super.count(sqlExceptSelect);
	}


	/**
	 * 计算货品总评分
	 * 
	 * @param goods
	 *            货品
	 * @return 货品总评分，仅计算显示评论
	 */
	public long calculateTotalScore(Goods goods) {
		Assert.notNull(goods);

		String sql = "SELECT SUM(score) FROM review WHERE goods_id = ? AND is_show = ?";
		Long totalScore = Db.queryLong(sql, goods.getId(), true);
		return totalScore != null ? totalScore : 0L;
	}

	/**
	 * 计算货品评分次数
	 * 
	 * @param goods
	 *            货品
	 * @return 货品评分次数，仅计算显示评论
	 */
	public long calculateScoreCount(Goods goods) {
		Assert.notNull(goods);

		String sql = "SELECT COUNT(*) FROM review WHERE goods_id = ? AND is_show = ?";
		return Db.queryInt(sql, goods.getId(), true);
	}

}