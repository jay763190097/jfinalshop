package com.jfinalshop.dao;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.PluginConfig;

/**
 * Dao - 插件配置
 * 
 * 
 */
public class PluginConfigDao extends OrderEntity<PluginConfig> {
	
	/**
	 * 构造方法
	 */
	public PluginConfigDao() {
		super(PluginConfig.class);
	}
	
	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	public boolean pluginIdExists(String pluginId) {
		if (StringUtils.isEmpty(pluginId)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM plugin_config WHERE plugin_id = ?";
		Long count = Db.queryLong(sql, pluginId);
		return count > 0;
	}

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	public PluginConfig findByPluginId(String pluginId) {
		if (StringUtils.isEmpty(pluginId)) {
			return null;
		}
		try {
			String sql = "SELECT * FROM plugin_config WHERE plugin_id = ?";
			return modelManager.findFirst(sql, pluginId);
		} catch (Exception e) {
			return null;
		}
	}

}