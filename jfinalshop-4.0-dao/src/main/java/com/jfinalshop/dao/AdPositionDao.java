package com.jfinalshop.dao;

import com.jfinalshop.model.AdPosition;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Dao - 广告位
 * 
 * 
 */
public class AdPositionDao extends BaseDao<AdPosition> {
	
	/**
	 * 构造方法
	 */
	public AdPositionDao() {
		super(AdPosition.class);
	}

	/** 广告 */
	private List<AdPosition> ads = new ArrayList<AdPosition>();
	/**
	 * 获取广告    API专用
	 *
	 * @return 广告
	 */
//	public List<AdPosition> getAds1(Long id,String channel) {
//		String channel1 = "'"+channel+"'";
//		if (CollectionUtils.isEmpty(ads)) {
//			String sql = "SELECT * FROM ad WHERE `ad_position_id` = "+id;
//			if(StringUtils.isNotEmpty(channel)){
//				sql+=" AND CHANNEL= "+channel1;
//			}
//			ads = AdPosition.dao.find(sql);
//		}
//		return ads;
//	}


}