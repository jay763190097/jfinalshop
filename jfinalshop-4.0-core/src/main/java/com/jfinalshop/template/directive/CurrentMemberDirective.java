package com.jfinalshop.template.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 当前会员
 * 
 * 
 */
public class CurrentMemberDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "currentMember";

	private MemberService memberService = appContext.getInstance(MemberService.class);

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
	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Member currentMember = memberService.getCurrent();
		if (body != null) {
			setLocalVariable(VARIABLE_NAME, currentMember, env, body);
		} else {
			if (currentMember != null) {
				Writer out = env.getOut();
				out.write(currentMember.getUsername());
			}
		}
	}

}