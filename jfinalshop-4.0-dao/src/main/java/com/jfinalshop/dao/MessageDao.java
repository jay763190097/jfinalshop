package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;

/**
 * Dao - 消息
 * 
 * 
 */
public class MessageDao extends BaseDao<Message> {
	
	/**
	 * 构造方法
	 */
	public MessageDao() {
		super(Message.class);
	}
	
	/**
	 * 查找消息分页
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 消息分页
	 */
	public Page<Message> findPage(Member member, Pageable pageable, Boolean read) {
		String sqlExceptSelect = "FROM message WHERE for_message_id IS NULL AND is_draft = false ";
		if (member != null) {
			sqlExceptSelect += "AND ((sender_id = " + member.getId() + " AND sender_delete = false ) OR (receiver_id = " + member.getId() + " AND receiver_delete = false)) ";
		} else {
			sqlExceptSelect += "AND ((sender_id IS NULL OR sender_delete = false) OR (receiver_id IS NULL OR receiver_delete = false)) ";
		}
		if (read != null) {
			sqlExceptSelect += "AND receiver_read = " + read + " ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找草稿分页
	 * 
	 * @param sender
	 *            发件人，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 草稿分页
	 */
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		String sqlExceptSelect = "FROM message WHERE for_message_id IS NULL AND is_draft = true ";
		if (sender != null) {
			sqlExceptSelect += " AND sender_id = " + sender.getId();
		} else {
			sqlExceptSelect += " AND sender_id IS NULL ";
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找消息数量
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param read
	 *            是否已读
	 * @return 消息数量，不包含草稿
	 */
	public Long count(Member member, Boolean read) {
		String sqlExceptSelect = "FROM message WHERE for_message_id IS NULL AND is_draft = false ";
		if (member != null) {
			if (read != null) {
				sqlExceptSelect += "AND ((sender_id = " + member.getId() + " AND sender_delete = false AND sender_read = " + read + ") OR (receiver_id = " + member.getId() + " AND receiver_delete = false AND receiver_read = " + read + ")) ";
			} else {
				sqlExceptSelect += "AND ((sender_id = " + member.getId() + " AND sender_delete = false) OR (receiver_id = " + member.getId() + " AND receiver_delete = false))";
			}
		} else {
			if (read != null) {
				sqlExceptSelect += "AND ((sender_id IS NULL OR sender_delete = false OR sender_read = " + read + ") OR (receiver_id IS NULL OR receiver_delete = false OR receiver_read = " + read + ")) ";
			} else {
				sqlExceptSelect += "AND ((sender_id IS NULL OR sender_delete = false) OR (receiver_id IS NULL OR receiver_delete = false))";
			}
		}
		return super.count(sqlExceptSelect);
	}


	/**
	 * 删除消息
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人，null表示管理员
	 */
	public void remove(Long id, Member member){
		Message message = super.find(id);
		if (message == null || message.getForMessage() != null) {
			return;
		}
		if ((member != null && member.equals(message.getReceiver())) || (member == null && message.getReceiver() == null)) {
			if (!message.getIsDraft()) {
				if (message.getSenderDelete()) {
					super.remove(message);
				} else {
					message.setReceiverDelete(true);
					super.update(message);
				}
			}
		} else if ((member != null && member.equals(message.getSender())) || (member == null && message.getSender() == null)) {
			if (message.getIsDraft()) {
				super.remove(message);
			} else {
				if (message.getReceiverDelete()) {
					super.remove(message);
				} else {
					message.setSenderDelete(true);
					super.update(message);
				}
			}
		}
	}

	/**
	 * wap设置消息已读
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人，null表示管理员
	 */
	public void read(Long id, Member member) {
		Message message = super.find(id);
		if (message == null || message.getForMessage() != null) {
			return;
		}
		
		if (member != null && member.equals(message.getReceiver())) {
			if (!message.getIsDraft()) {
				message.setReceiverRead(true);
				super.update(message);
			}
		}
	}
	
}