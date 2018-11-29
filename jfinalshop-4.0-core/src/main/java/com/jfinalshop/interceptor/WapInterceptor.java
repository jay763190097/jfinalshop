package com.jfinalshop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinalshop.RequestContextHolder;

public class WapInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		RequestContextHolder.setRequestAttributes(controller.getRequest());
		inv.invoke();
	}

}
