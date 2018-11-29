package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 咨询
 * 
 * 
 */
public class ConsultationListDirective extends BaseDirective {

	/** "会员ID"参数名称 */
	private static final String MEMBER_ID_PARAMETER_NAME = "member_id";

	/** "货品ID"参数名称 */
	private static final String GOODS_ID_PARAMETER_NAME = "goods_id";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "consultations";

	private ConsultationService consultationService = appContext.getInstance(ConsultationService.class);

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
		Long memberId = FreeMarkerUtils.getParameter(MEMBER_ID_PARAMETER_NAME, Long.class, params);
		Long goodsId = FreeMarkerUtils.getParameter(GOODS_ID_PARAMETER_NAME, Long.class, params);
		Integer count = getCount(params);
		List<Filter> filters = getFilters(params, Consultation.class);
		List<Order> orders = getOrders(params);
		boolean useCache = useCache(env, params);
		List<Consultation> consultations = consultationService.findList(memberId, goodsId, true, count, filters, orders, useCache);
		setLocalVariable(VARIABLE_NAME, consultations, env, body);
	}

}