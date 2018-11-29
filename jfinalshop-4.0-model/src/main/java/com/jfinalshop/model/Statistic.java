package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.jfinalshop.model.base.BaseStatistic;

/**
 * Model - 统计
 * 
 * 
 */
public class Statistic extends BaseStatistic<Statistic> {
	private static final long serialVersionUID = -6997850608276216831L;
	public static final Statistic dao = new Statistic();
	
	/**
	 * 周期
	 */
	public enum Period {

		/** 年 */
		year,

		/** 月 */
		month,

		/** 日 */
		day
	}

	/**
	 * 构造方法
	 */
	public Statistic() {
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.setYear(year);
		this.setRegisterMemberCount(registerMemberCount);
		this.setCreateOrderCount(createOrderCount);
		this.setCompleteOrderCount(completeOrderCount);
		this.setCreateOrderAmount(createOrderAmount);
		this.setCompleteOrderAmount(completeOrderAmount); 
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Integer month, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.setYear(year);
		this.setMonth(month);
		this.setRegisterMemberCount(registerMemberCount);
		this.setCreateOrderCount(createOrderCount);
		this.setCompleteOrderCount(completeOrderCount);
		this.setCreateOrderAmount(createOrderAmount);
		this.setCompleteOrderAmount(completeOrderAmount);
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Integer month, Integer day, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.setYear(year);
		this.setMonth(month);
		this.setDay(day);
		this.setRegisterMemberCount(registerMemberCount);
		this.setCreateOrderCount(createOrderCount);
		this.setCompleteOrderCount(completeOrderCount);
		this.setCreateOrderAmount(createOrderAmount);
		this.setCompleteOrderAmount(completeOrderAmount);
	}
	
	
	/**
	 * 获取日期
	 * 
	 * @return 日期
	 */
	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		if (getYear() != null) {
			calendar.set(Calendar.YEAR, getYear());
		}
		if (getMonth() != null) {
			calendar.set(Calendar.MONTH, getMonth());
		}
		if (getDay() != null) {
			calendar.set(Calendar.DAY_OF_MONTH, getDay());
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

}
