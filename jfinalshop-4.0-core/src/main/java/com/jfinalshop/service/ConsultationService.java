package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ConsultationDao;
import com.jfinalshop.dao.GoodsDao;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;

/**
 * Service - 咨询
 * 
 * 
 */
@Singleton
public class ConsultationService extends BaseService<Consultation> {

	/**
	 * 构造方法
	 */
	public ConsultationService() {
		super(Consultation.class);
	}
	
	@Inject
	private ConsultationDao consultationDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private GoodsDao goodsDao;
	
	/**
	 * 查找咨询
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 咨询，不包含咨询回复
	 */
	public List<Consultation> findList(Member member, Goods goods, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		return consultationDao.findList(member, goods, isShow, count, filters, orders);
	}

	/**
	 * 查找咨询
	 * 
	 * @param memberId
	 *            会员ID
	 * @param goodsId
	 *            货品ID
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
	 * @return 咨询，不包含咨询回复
	 */
	public List<Consultation> findList(Long memberId, Long goodsId, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		Member member = memberDao.find(memberId);
		if (memberId != null && member == null) {
			return Collections.emptyList();
		}
		Goods goods = goodsDao.find(goodsId);
		if (goodsId != null && goods == null) {
			return Collections.emptyList();
		}
		return consultationDao.findList(member, goods, isShow, count, filters, orders);
	}


	/**
	 * 查找咨询分页
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页，不包含咨询回复
	 */
	public Page<Consultation> findPage(Member member, Goods goods, Boolean isShow, Pageable pageable) {
		return consultationDao.findPage(member, goods, isShow, pageable);
	}

	/**
	 * 查找咨询数量
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @return 咨询数量，不包含咨询回复
	 */
	public Long count(Member member, Goods goods, Boolean isShow) {
		return consultationDao.count(member, goods, isShow);
	}

	/**
	 * 咨询回复
	 * 
	 * @param consultation
	 *            咨询
	 * @param replyConsultation
	 *            回复咨询
	 */
	public void reply(Consultation consultation, Consultation replyConsultation) {
		if (consultation == null || replyConsultation == null) {
			return;
		}
		consultation.setIsShow(true);
		consultation.update();

		replyConsultation.setIsShow(true);
		replyConsultation.setGoodsId(consultation.getGoods().getId());
		replyConsultation.setForConsultationId(consultation.getId());
		consultationDao.save(replyConsultation);

		Goods goods = consultation.getGoods();
		if (goods != null) {
			goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			goods.update();
		}
	}
	
	public Consultation save(Consultation consultation) {
		Goods goods = consultation.getGoods();
		if (goods != null) {
			goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			goods.update();
		}
		return super.save(consultation);
	}

	public Consultation update(Consultation consultation) {
		Goods goods = consultation.getGoods();
		if (goods != null) {
			goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			goods.update();
		}
		return super.update(consultation);
	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(Consultation consultation) {
		Goods goods = consultation.getGoods();
		if (goods != null) {
			goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			goods.update();
		}
		super.delete(consultation);
	}


}