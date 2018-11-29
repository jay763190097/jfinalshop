package com.jfinalshop.template.directive;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.model.Ad;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 广告位
 * 
 * 
 */
public class AdPositionDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "adPosition";

	private Configuration cfg = FreeMarkerRender.getConfiguration();
	private AdPositionService adPositionService = appContext.getInstance(AdPositionService.class);

	/**
	 * 执行
	 * 
	 * @param env
	 *            环境变量
	 * @param params
	 *            参数
	 * @param loopVars
	 *            循环变量
	 * @param body
	 *            模板内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		String channel = PropKit.get("channelcode");
		Long id = getId(params);
		String a = String.valueOf(id);
		boolean useCache = useCache(env, params);
		AdPosition adPosition = null;
		List<AdPosition> adPositions =null;
		if("1".equals(a)){
			adPosition = adPositionService.find(id, useCache);
		}else {
			adPositions = adPositionService.finds(id, useCache,channel);
		}
		if (body != null) {
			setLocalVariable(VARIABLE_NAME, adPositions, env, body);
		} else {
			if (adPosition != null && adPosition.getTemplate() != null) {
				try {
					Map<String, Object> model = new HashMap<String, Object>();
					model.put(VARIABLE_NAME, adPosition);
					Writer out = env.getOut();
					new Template("adTemplate", new StringReader(adPosition.getTemplate()), cfg).process(model, out);
				} catch (TemplateException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

}