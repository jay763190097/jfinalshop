package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseReceiver;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 收货地址
 * 
 * 
 */
public class Receiver extends BaseReceiver<Receiver> {
	private static final long serialVersionUID = -7437844240386808113L;
	public static final Receiver dao = new Receiver();
	
	/** 收货地址最大保存数 */
	public static final Integer MAX_RECEIVER_COUNT = 10;
	
	/** 地区 */
	private Area area;
	
	/** 会员 */
	private Member member;
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (ObjectUtils.isEmpty(area)) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
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
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
