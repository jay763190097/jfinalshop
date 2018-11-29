package com.jfinalshop.validator;
import java.util.Properties;

import com.jfinal.core.Const;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;

public class ValidationPlugin implements IPlugin{
	protected Prop prop = null;

    public ValidationPlugin(String fileName) {
        prop = PropKit.use(fileName, Const.DEFAULT_ENCODING);
    }

    public ValidationPlugin(String fileName, String encoding) {
        prop = PropKit.use(fileName, encoding);
    }
    /**
     * 初始化校验规则
     */
	public boolean start() {
		Properties properties = prop.getProperties();
		for (Object object : properties.keySet()) {
			String key = (String) object;
			String rule[] = properties.getProperty(key).split(",");
			ValidationRules.ruleMap.put(key, rule[0]);
			ValidationRules.ruleErrorMessageMap.put(key,rule[1]);
		}
		return true;
	}

	public boolean stop() {
		return true;
	}
}