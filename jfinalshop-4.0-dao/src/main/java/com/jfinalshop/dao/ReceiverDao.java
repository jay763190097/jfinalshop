package com.jfinalshop.dao;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.util.Assert;

/**
 * Dao - 收货地址
 * 
 * 
 */
public class ReceiverDao extends BaseDao<Receiver> {
	
	/**
	 * 构造方法
	 */
	public ReceiverDao() {
		super(Receiver.class);
	}

	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		if (member == null) {
			return null;
		}
		Receiver receiver;
		String sql = "SELECT * FROM receiver WHERE member_id = ? AND is_default = true";
		receiver = modelManager.findFirst(sql, member.getId());
		if (receiver == null) {
			sql = "SELECT * FROM receiver WHERE member_id = ? ORDER BY modify_date DESC";
			return modelManager.findFirst(sql, member.getId());
		}
		return receiver;
	}

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM receiver WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += "AND member_id = " + member.getId();
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 设置默认收货地址
	 * 
	 * @param receiver
	 *            收货地址
	 */
	public void setDefault(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());

		receiver.setIsDefault(true);
		if (receiver.isNew()) {
			String sql = "UPDATE receiver SET is_default = false WHERE member_id = ? AND is_default = true";
			Db.update(sql, receiver.getMember().getId());
		} else {
			String sql = "UPDATE receiver SET is_default = false WHERE member_id = ? AND is_default = true AND id != ?";
			Db.update(sql, receiver.getMember().getId(), receiver.getId());
		}
	}

}