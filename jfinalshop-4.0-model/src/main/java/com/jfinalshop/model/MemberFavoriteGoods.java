package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseMemberFavoriteGoods;

/**
 * Model - 会员收藏
 * 
 * 
 */
public class MemberFavoriteGoods extends BaseMemberFavoriteGoods<MemberFavoriteGoods> {
	private static final long serialVersionUID = -4075465081773405290L;
	public static final MemberFavoriteGoods dao = new MemberFavoriteGoods();
	
	
	/**
	 * 周期
	 */
	public enum Period {

		/** 历史 */
		lastyear,
		
		/** 年 */
		year,

		/** 月 */
		month,

		/** 周 */
		week,
		
		/** 日 */
		day
	}
	
	
}
