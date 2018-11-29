package com.jfinalshop.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.StatisticDao;
import com.jfinalshop.model.Statistic;
import com.jfinalshop.util.Assert;

/**
 * Service - 统计
 * 
 * 
 */
@Singleton
public class StatisticService extends BaseService<Statistic> {

	/**
	 * 构造方法
	 */
	public StatisticService() {
		super(Statistic.class);
	}
	
	@Inject
	private StatisticDao statisticDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private OrderDao orderDao;
	
	/**
	 * 判断统计是否存在
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 统计是否存在
	 */
	public boolean exists(int year, int month, int day) {
		return statisticDao.exists(year, month, day);
	}

	/**
	 * 收集
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 统计
	 */
	public Statistic collect(int year, int month, int day) {
		Assert.state(month >= 0);
		Assert.state(day >= 0);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(year, month, day);
		beginCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginCalendar.set(Calendar.MINUTE, beginCalendar.getActualMinimum(Calendar.MINUTE));
		beginCalendar.set(Calendar.SECOND, beginCalendar.getActualMinimum(Calendar.SECOND));
		Date beginDate = beginCalendar.getTime();

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(year, month, day);
		endCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE, beginCalendar.getActualMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND, beginCalendar.getActualMaximum(Calendar.SECOND));
		Date endDate = endCalendar.getTime();

		Statistic statistics = new Statistic();
		statistics.setYear(year);
		statistics.setMonth(month);
		statistics.setDay(day);
		statistics.setRegisterMemberCount(memberDao.registerMemberCount(beginDate, endDate));
		statistics.setCreateOrderCount(orderDao.createOrderCount(beginDate, endDate));
		statistics.setCompleteOrderCount(orderDao.completeOrderCount(beginDate, endDate));
		statistics.setCreateOrderAmount(orderDao.createOrderAmount(beginDate, endDate));
		statistics.setCompleteOrderAmount(orderDao.completeOrderAmount(beginDate, endDate));

		return statistics;
	}

	/**
	 * 分析
	 * 
	 * @param period
	 *            周期
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 统计
	 */
	public List<Statistic> analyze(Statistic.Period period, Date beginDate, Date endDate) {
		return statisticDao.analyze(period, beginDate, endDate);
	}

}