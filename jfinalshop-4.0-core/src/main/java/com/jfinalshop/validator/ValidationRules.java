package com.jfinalshop.validator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.jfinal.kit.StrKit;
public class ValidationRules {
	static Map<String, String> ruleMap = new HashMap<String,String>();
	static Map<String, String> ruleErrorMessageMap = new HashMap<String,String>();

	public static boolean isMatch(String rule, String value) {
		boolean matchResult;
		if(rule.equals("required")){
			matchResult = StrKit.notBlank(value);
		}else {
			if(StrKit.notBlank(value))matchResult = Pattern.compile(ruleMap.get(rule)).matcher(value).matches();
			else matchResult = true;
		}
		return matchResult;
	}
	
	public static String getErrorMessage(String rule){
		return ruleErrorMessageMap.get(rule);
	}
}