package com.jfinalshop.service;

import com.jfinalshop.dao.PluginConfigDao;
import com.jfinalshop.model.PluginConfig;

/**
 * Service - 插件配置
 * 
 * 
 */
public class PluginConfigService extends BaseService<PluginConfig> {
	
	public PluginConfigService() {
		super(PluginConfig.class);
	}

	private PluginConfigDao pluginConfigDao = new PluginConfigDao();
	
	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	public boolean pluginIdExists(String pluginId) {
		return pluginConfigDao.pluginIdExists(pluginId);
	}

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	public PluginConfig findByPluginId(String pluginId) {
		return pluginConfigDao.findByPluginId(pluginId);
	}

	/**
	 * 根据插件ID删除插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 */
	public void deleteByPluginId(String pluginId) {
		PluginConfig pluginConfig = pluginConfigDao.findByPluginId(pluginId);
		pluginConfigDao.remove(pluginConfig);
	}

	public PluginConfig save(PluginConfig pluginConfig) {
		return super.save(pluginConfig);
	}

	public PluginConfig update(PluginConfig pluginConfig) {
		return super.update(pluginConfig);
	}

//	public PluginConfig update(PluginConfig pluginConfig, String... ignoreProperties) {
//		return super.update(pluginConfig, ignoreProperties);
//	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(PluginConfig pluginConfig) {
		super.delete(pluginConfig);
	}

}