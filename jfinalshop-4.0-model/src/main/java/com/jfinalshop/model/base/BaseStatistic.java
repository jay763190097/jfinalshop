package com.jfinalshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStatistic<M extends BaseStatistic<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return get("id");
	}

	public void setCreateDate(java.util.Date createDate) {
		set("create_date", createDate);
	}

	public java.util.Date getCreateDate() {
		return get("create_date");
	}

	public void setModifyDate(java.util.Date modifyDate) {
		set("modify_date", modifyDate);
	}

	public java.util.Date getModifyDate() {
		return get("modify_date");
	}

	public void setVersion(java.lang.Long version) {
		set("version", version);
	}

	public java.lang.Long getVersion() {
		return get("version");
	}

	public void setCompleteOrderAmount(java.math.BigDecimal completeOrderAmount) {
		set("complete_order_amount", completeOrderAmount);
	}

	public java.math.BigDecimal getCompleteOrderAmount() {
		return get("complete_order_amount");
	}

	public void setCompleteOrderCount(java.lang.Long completeOrderCount) {
		set("complete_order_count", completeOrderCount);
	}

	public java.lang.Long getCompleteOrderCount() {
		return get("complete_order_count");
	}

	public void setCreateOrderAmount(java.math.BigDecimal createOrderAmount) {
		set("create_order_amount", createOrderAmount);
	}

	public java.math.BigDecimal getCreateOrderAmount() {
		return get("create_order_amount");
	}

	public void setCreateOrderCount(java.lang.Long createOrderCount) {
		set("create_order_count", createOrderCount);
	}

	public java.lang.Long getCreateOrderCount() {
		return get("create_order_count");
	}

	public void setDay(java.lang.Integer day) {
		set("day", day);
	}

	public java.lang.Integer getDay() {
		return get("day");
	}

	public void setMonth(java.lang.Integer month) {
		set("month", month);
	}

	public java.lang.Integer getMonth() {
		return get("month");
	}

	public void setRegisterMemberCount(java.lang.Long registerMemberCount) {
		set("register_member_count", registerMemberCount);
	}

	public java.lang.Long getRegisterMemberCount() {
		return get("register_member_count");
	}

	public void setYear(java.lang.Integer year) {
		set("year", year);
	}

	public java.lang.Integer getYear() {
		return get("year");
	}

}
