package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.util.DateUtils;

/**
 * Dao - 会员
 * 
 */
public class MemberDao extends BaseDao<Member> {
	
	/**
	 * 构造方法
	 */
	public MemberDao() {
		super(Member.class);
	}
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member WHERE username = LOWER(?)";
		Long count = Db.queryLong(sql, username);
		return count > 0;
	}

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member WHERE email = LOWER(?)";
		Long count = Db.queryLong(sql, email);
		return count > 0;
	}
    
	/**
	 * 判断手机号是否存在
	 * 
	 * @param phone
	 *            手机号
	 * @return 手机号是否存在
	 */
	public boolean phoneExists(String phone) {
		if (StringUtils.isEmpty(phone)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM member WHERE phone = ?";
		Long count = Db.queryLong(sql, phone);
		return count > 0;
	}
	
	/**
	 * 查找会员
	 * 
	 * @param loginPluginId
	 *            登录插件ID
	 * @param openId
	 *            openID
	 * @return 会员，若不存在则返回null
	 */
	public Member find(String loginPluginId, String openId) {
		if (StringUtils.isEmpty(loginPluginId) || StringUtils.isEmpty(openId)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM member WHERE login_plugin_id = ? AND open_id = ?";
			return modelManager.findFirst(sql, loginPluginId, openId);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据用户名查找会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM member WHERE username = LOWER(?)";
			return modelManager.findFirst(sql, username);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据openId查找会员
	 * 
	 * @param open_id
	 *            
	 * @return 会员，若不存在则返回null
	 */
	public Member findByOpenId(String openId) {
		if (StringUtils.isEmpty(openId)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM member WHERE open_id = ?";
			return modelManager.findFirst(sql, openId);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据E-mail查找会员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public List<Member> findListByEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return Collections.emptyList();
		}
		String sql = "SELECT * FROM member WHERE email = LOWER(?)";
		return modelManager.find(sql, email);
	}

	/**
	 * 查找会员分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 会员分页
	 */
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		String sqlExceptSelect = "FROM member WHERE 1 = 1 ";
		List<Order> orders = new ArrayList<Order>();
		if (rankingType != null) {
			switch (rankingType) {
			case point:
				orders.add(new Order("point", Order.Direction.desc));
				break;
			case balance:
				orders.add(new Order("balance", Order.Direction.desc));
				break;
			case amount:
				orders.add(new Order("amount", Order.Direction.desc));
				break;
			}
		}
		pageable.setOrders(orders);
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查询会员注册数
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 会员注册数
	 */
	public Long registerMemberCount(Date beginDate, Date endDate) {
		String sqlExceptSelect = "FROM member WHERE 1 = 1 ";
		if (beginDate != null) {
			sqlExceptSelect += " AND create_date >= '" + DateUtils.getDateTime(beginDate) +"' ";
		}
		if (endDate != null) {
			sqlExceptSelect += " AND create_date <= '" + DateUtils.getDateTime(endDate) +"' ";
		}
		return super.count(sqlExceptSelect);
	}

	/**
	 * 清空会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 */
	public void clearAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute == null || memberAttribute.getType() == null || memberAttribute.getPropertyIndex() == null) {
			return;
		}

		String propertyName;
		switch (memberAttribute.getTypeName()) {
		case text:
		case select:
		case checkbox:
			propertyName = "attribute_value" + memberAttribute.getPropertyIndex();
			break;
		default:
			propertyName = String.valueOf(memberAttribute.getType());
			break;
		}
		String sql = "UPDATE member SET " + propertyName + " = null";
		Db.update(sql);
	}

}