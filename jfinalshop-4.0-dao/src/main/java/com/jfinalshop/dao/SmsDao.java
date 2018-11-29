package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Sms;

public class SmsDao extends BaseDao<Sms>{

	/**
	 * 构造方法
	 */
	public SmsDao() {
		super(Sms.class);
	}

	
	/**
	 * 判断验证码是否存在
	 * 
	 * @param mobile
	 * @param smsCode
	 * @param smsType
	 *            
	 * @return 验证码是否存在
	 */
	public boolean smsExists(String mobile, String smsCode, Setting.SmsType smsType) {
		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(smsCode) || smsType == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM sms WHERE mobile = ? AND sms_code = ? AND sms_type = ? ";
		Long count = Db.queryLong(sql, mobile, smsCode, smsType.ordinal());
		return count > 0;
	}
	
	/**
	 * 查询短信
	 * @param mobile
	 * @param smsCode
	 * @param smsType
	 * @return
	 */
	public Sms find(String mobile, String smsCode, Integer smsType) {
		String sql = "SELECT * FROM sms WHERE 1 = 1 ";
		if (StringUtils.isNotEmpty(mobile) || StringUtils.isEmpty(smsCode) || smsType == null) {
			sql += " AND mobile = " + mobile;
		}
		if (StringUtils.isNotEmpty(smsCode)) {
			sql += " AND sms_code = " + smsCode;
		}
		if (smsType != null) {
			sql += " AND sms_type = " + smsType;
		}
		return modelManager.findFirst(sql);
		
	}
	
	
	/**
	 * 删除
	 * @param mobile
	 * @param smsCode
	 * @return
	 */
	public boolean delete(String mobile, String smsCode) {
		String sql = "DELETE FROM sms WHERE mobile = ? AND sms_code = ? ";
		return Db.update(sql, mobile, smsCode) > 0;
	}
}
