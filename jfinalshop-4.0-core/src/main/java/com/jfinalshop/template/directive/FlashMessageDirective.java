package com.jfinalshop.template.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinalshop.Message;
import com.jfinalshop.RequestContextHolder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 瞬时消息
 * 
 * 
 */
public class FlashMessageDirective extends BaseDirective {

	/** "瞬时消息"属性名称 */
	public static final String FLASH_MESSAGE_ATTRIBUTE_NAME = FlashMessageDirective.class.getName() + ".FLASH_MESSAGE";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "flashMessage";

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
		HttpServletRequest request = RequestContextHolder.currentRequestAttributes();
		if (request != null) {
			Message message = (Message) request.getSession().getAttribute(FLASH_MESSAGE_ATTRIBUTE_NAME);
			if (body != null) {
				setLocalVariable(VARIABLE_NAME, message, env, body);
			} else {
				if (message != null) {
					Writer out = env.getOut();
					request.getSession().removeAttribute(FLASH_MESSAGE_ATTRIBUTE_NAME);
					out.write("$.message(\"" + message.getType() + "\", \"" + message.getContent() + "\");");
				}
			}
		}
	}

}