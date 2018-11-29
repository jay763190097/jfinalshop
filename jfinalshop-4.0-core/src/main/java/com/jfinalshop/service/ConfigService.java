package com.jfinalshop.service;

import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;

import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.Setting;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * Service - 配置
 * 
 * 
 */
@Singleton
public class ConfigService {

	@InjectSettings("${template.update_delay}")
	private String templateUpdateDelay;
	
	/**
	 * 初始化
	 */
	public void init() {
		try {
			Setting setting = SystemUtils.getSetting();
			setting.setSmtpPassword(null);
			setting.setKuaidi100Key(null);
			setting.setCnzzPassword(null);
			setting.setSmsKey(null);
			Configuration configuration = FreeMarkerRender.getConfiguration();
			configuration.setSharedVariable("locale", setting.getLocale());
			configuration.setSharedVariable("theme", setting.getTheme());
			if (setting.getIsDevelopmentEnabled()) {
				configuration.setSetting("template_update_delay", "0");
			} else {
				configuration.setSetting("template_update_delay", templateUpdateDelay);
			}
		} catch (TemplateModelException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}