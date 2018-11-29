package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Navigation;
import com.jfinalshop.service.NavigationService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 导航列表
 * 
 * 
 */
public class NavigationListDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "navigations";

	private NavigationService navigationService = appContext.getInstance(NavigationService.class);

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
		Integer count = getCount(params);
		List<Filter> filters = getFilters(params, Navigation.class);
		List<Order> orders = getOrders(params);
		boolean useCache = useCache(env, params);
		List<Navigation> navigations = navigationService.findList(count, filters, orders, useCache,channel);
		setLocalVariable(VARIABLE_NAME, navigations, env, body);
	}

}