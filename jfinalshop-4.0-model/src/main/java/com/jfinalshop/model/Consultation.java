package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseConsultation;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 咨询
 * 
 * 
 */
public class Consultation extends BaseConsultation<Consultation> {
	private static final long serialVersionUID = -5464078454735650427L;
	public static final Consultation dao = new Consultation();
	
	/** 路径前缀 */
	private static final String PATH_PREFIX = "/consultation/content";

	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";
	
	/** 会员 */
	private Member member;

	/** 货品 */
	private Goods goods;

	/** 咨询 */
	private Consultation forConsultation;
	
	/** 回复 */
	private List<Consultation> replyConsultations = new ArrayList<Consultation>();
	
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
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public Consultation getForConsultation() {
		if (ObjectUtils.isEmpty(forConsultation)) {
			forConsultation = findById(getForConsultationId());
		}
		return forConsultation;
	}

	/**
	 * 设置咨询
	 * 
	 * @param forConsultation
	 *            咨询
	 */
	public void setForConsultation(Consultation forConsultation) {
		this.forConsultation = forConsultation;
	}
	
	/**
	 * 获取回复
	 * 
	 * @return 回复
	 */
	public List<Consultation> getReplyConsultations() {
		if (CollectionUtils.isEmpty(replyConsultations)) {
			String sql = "SELECT * FROM `consultation` WHERE for_consultation_id = ?";
			replyConsultations = Consultation.dao.find(sql, getId());
		}
		return replyConsultations;
	}

	/**
	 * 设置回复
	 * 
	 * @param replyConsultations
	 *            回复
	 */
	public void setReplyConsultations(List<Consultation> replyConsultations) {
		this.replyConsultations = replyConsultations;
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
