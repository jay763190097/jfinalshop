package com.jfinalshop.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Entity - 规格值
 * 
 * 
 */
public class SpecificationValue implements Serializable {

	private static final long serialVersionUID = 2727026870667528070L;

	/** ID */
	@JSONField(ordinal = 1)
	private Integer id;

	/** 值 */
	@JSONField(ordinal = 2)
	private String value;

	/**
	 * 获取ID
	 * 
	 * @return ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id
	 *            ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取值
	 * 
	 * @return 值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置值
	 * 
	 * @param value
	 *            值
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
