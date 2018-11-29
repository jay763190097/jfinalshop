package com.jfinalshop.validator;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;

public class ValidatorInterceptor implements Interceptor {
	
	public void intercept(Invocation inv) {
		String methodName = inv.getMethodName();
		Controller controller = inv.getController();
		Method[] methods = controller.getClass().getDeclaredMethods();
		boolean matchResult = true;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				boolean hasAnnotation = method.isAnnotationPresent(Validation.class);
				if (hasAnnotation) {
					Validation anno = method.getAnnotation(Validation.class);
					Map<String, String> ruleMap = dealRule(anno.rules());
					for (String key : ruleMap.keySet()) {
						String value = controller.getPara(key);
						String rule = ruleMap.get(key);
						matchResult = ValidationRules.isMatch(rule, value);
						Message message = new Message();
						message.setType(Message.Type.error);
						if (!matchResult) {
							message.setContent(key + ValidationRules.getErrorMessage(rule));
							controller.renderJson(message);
							break;
					   }
					}
				}
			}
		}
		if (matchResult)
			inv.invoke();
	}

	public Map<String, String> dealRule(String rules) {
		Map<String, String> map = new IdentityHashMap<String, String>();
		if (StrKit.notBlank(rules)) {
			String[] paraRules = rules.split(";");
			for (String paraRule : paraRules) {
				String[] para = paraRule.split("=");
				map.put(para[0], para[1]);
			}
		}
		return map;
	}

}