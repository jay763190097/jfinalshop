package com.jfinalshop.config;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class LoginInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller c = inv.getController();
        if (c.getSession().getAttribute("username") ==null){
            c.render("/pages/login-phone.html");
        }else {
            inv.invoke();
        }
    }
}
