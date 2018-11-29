package com.jfinalshop.model;

import java.util.Date;

import com.jfinalshop.model.base.BaseAd;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 广告
 * 
 * 
 */
public class Ad extends BaseAd<Ad> {
	private static final long serialVersionUID = 5279484023423432835L;
	public static final Ad dao = new Ad();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	}
	
	/** 广告位 */
	private AdPosition adPosition;
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取广告位
	 * 
	 * @return 广告位
	 */
	public AdPosition getAdPosition() {
		if (ObjectUtils.isEmpty(adPosition)) {
			adPosition = AdPosition.dao.findById(getAdPositionId());
		}
		return adPosition;
	}

	/**
	 * 设置广告位
	 * 
	 * @param adPosition
	 *            广告位
	 */
	public void setAdPosition(AdPosition adPosition) {
		this.adPosition = adPosition;
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
}
