package com.jfinalshop.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Entity - 规格项
 * 
 * 
 */
public class SpecificationItem implements Serializable {

	private static final long serialVersionUID = 2991055013548605308L;

	/** 名称 */
	@JSONField(ordinal = 1)
	private String name;

	/** 条目 */
	@JSONField(ordinal = 2)
	private List<SpecificationItem.Entry> entries = new ArrayList<SpecificationItem.Entry>();

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取条目
	 * 
	 * @return 条目
	 */
	public List<SpecificationItem.Entry> getEntries() {
		return entries;
	}

	/**
	 * 设置条目
	 * 
	 * @param entries
	 *            条目
	 */
	public void setEntries(List<SpecificationItem.Entry> entries) {
		this.entries = entries;
	}

	/**
	 * 判断是否已选
	 * 
	 * @return 是否已选
	 */
	@JSONField(serialize = false)
	public boolean isSelected() {
		if (CollectionUtils.isNotEmpty(getEntries())) {
			for (SpecificationItem.Entry entry : getEntries()) {
				if (entry.getIsSelected()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断规格值是否有效
	 * 
	 * @param specificationValue
	 *            规格值
	 * @return 规格值是否有效
	 */
	@JSONField(serialize = false)
	public boolean isValid(SpecificationValue specificationValue) {
		if (specificationValue != null && specificationValue.getId() != null && StringUtils.isNotEmpty(specificationValue.getValue()) && CollectionUtils.isNotEmpty(getEntries())) {
			for (SpecificationItem.Entry entry : getEntries()) {
				if (entry != null && entry.getIsSelected() && specificationValue.getId().equals(entry.getId()) && StringUtils.equals(entry.getValue(), specificationValue.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Entity - 条目
	 * 
	 * 
	 */
	public static class Entry implements Serializable {

		private static final long serialVersionUID = 3324219650905893437L;

		/** ID */
		@JSONField(ordinal = 1)
		private Integer id;

		/** 值 */
		@JSONField(ordinal = 2)
		private String value;

		/** 是否已选 */
		@JSONField(ordinal = 3)
		private Boolean isSelected = false;

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

		/**
		 * 获取是否已选
		 * 
		 * @return 是否已选
		 */
		public Boolean getIsSelected() {
			return isSelected;
		}

		/**
		 * 设置是否已选
		 * 
		 * @param isSelected
		 *            是否已选
		 */
		public void setIsSelected(Boolean isSelected) {
			this.isSelected = isSelected;
		}

	}

}
