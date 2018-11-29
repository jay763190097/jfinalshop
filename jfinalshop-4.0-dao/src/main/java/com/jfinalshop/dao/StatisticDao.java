package com.jfinalshop.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Statistic;
import com.jfinalshop.util.Assert;

/**
 * Dao - 统计
 * 
 * 
 */
public class StatisticDao extends BaseDao<Statistic> {
	
	/**
	 * 构造方法
	 */
	public StatisticDao() {
		super(Statistic.class);
	}
	
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
		String sql = "SELECT COUNT(*) FROM statistic WHERE year = ? AND month = ? AND day = ?";
		Long count = Db.queryLong(sql, year, month, day);
		return count > 0;
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
		Assert.notNull(period);

		String sql = "";
		String groupBy = "";
		switch (period) {
		case year:
			sql = ""
				+ "SELECT year, "
				+ "       SUM(register_member_count) AS registerMemberCount, "
				+ "       SUM(create_order_count) AS createOrderCount, "
				+ "       SUM(complete_order_count) AS completeOrderCount, "
				+ "       SUM(create_order_amount) AS createOrderAmount, "
				+ "       SUM(complete_order_amount) AS completeOrderAmount "
				+ "FROM statistic WHERE 1 = 1 ";
			groupBy = " GROUP BY year ";
			break;
		case month:
			sql = ""
				+ "SELECT year, "
				+ "       month, "
				+ "       SUM(register_member_count) AS registerMemberCount, "
				+ "       SUM(create_order_count) AS createOrderCount, "
				+ "       SUM(complete_order_count) AS completeOrderCount, "
				+ "       SUM(create_order_amount) AS createOrderAmount, "
				+ "       SUM(complete_order_amount) AS completeOrderAmount "
				+ "FROM statistic WHERE 1 = 1 ";
			groupBy = " GROUP BY year, month ";
			break;
		case day:
			sql = ""
				+ "SELECT year, "
				+ "       month, "
				+ "       day, "
				+ "       SUM(register_member_count) AS registerMemberCount, "
				+ "       SUM(create_order_count) AS createOrderCount, "
				+ "       SUM(complete_order_count) AS completeOrderCount, "
				+ "       SUM(create_order_amount) AS createOrderAmount, "
				+ "       SUM(complete_order_amount) AS completeOrderAmount "
				+ "FROM statistic WHERE 1 = 1 ";
			groupBy = " GROUP BY year, month, day ";
			break;
		}
		if (beginDate != null) {
			Calendar calendar = DateUtils.toCalendar(beginDate);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			sql += " AND ((year > " + year + ") OR (year = " + year + " AND month > " + month + ") OR (year = " + year + " AND month = " + month + " AND day >= " + day + ")) ";
		}
		if (endDate != null) {
			Calendar calendar = DateUtils.toCalendar(endDate);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			sql += " AND((year < " + year + ") OR (year = " + year + " AND month < " + month + ") OR (year = " + year + " AND month = " + month + " AND day <= " + day + ")) ";
		}
		sql += groupBy;
		return modelManager.find(sql);
	}

}