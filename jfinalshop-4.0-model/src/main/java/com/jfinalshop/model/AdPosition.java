package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseAdPosition;

/**
 * Model - 广告位
 * 
 * 
 */
public class AdPosition extends BaseAdPosition<AdPosition> {
	private static final long serialVersionUID = 6838748335797699769L;
	public static final AdPosition dao = new AdPosition();
	
	/** 广告 */
	private List<Ad> ads = new ArrayList<Ad>();
	
	/**
	 * 获取广告    API专用
	 * 
	 * @return 广告
	 */
	public List<Ad> getAds1(String channel) {
		String channel1 = "'"+channel+"'";
		if (CollectionUtils.isEmpty(ads)) {
			String sql = "SELECT * FROM ad WHERE `ad_position_id` = ?";
			if(StringUtils.isNotEmpty(channel)){
			sql+=" AND CHANNEL= "+channel1;
			}
			ads = Ad.dao.find(sql, getId());
		}
		return ads;
	}


	/**
	 * 设置广告  API专用
	 * 
	 * @param ads
	 *            广告
	 */
	public void setAds1(List<Ad> ads,String channel) {
		this.ads = ads;
	}
	
	/**
	 * 获取广告
	 * 
	 * @return 广告
	 */
	public List<Ad> getAds() {
		   if (CollectionUtils.isEmpty(ads)) {
		      String sql = "SELECT * FROM ad WHERE `ad_position_id` = ?";
		      ads = Ad.dao.find(sql, getId());
		   }
		   return ads;
		}
	/**
	 * 设置广告
	 * 
	 * @param ads
	 *            广告
	 */
	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}
}
