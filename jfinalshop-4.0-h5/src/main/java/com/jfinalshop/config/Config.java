package com.jfinalshop.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.render.RenderManager;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinalshop.model._MappingKit;
import com.jfinalshop.validator.ValidationPlugin;
import com.jfinalshop.validator.ValidatorInterceptor;
import net.hasor.plugins.jfinal.HasorInterceptor;
import net.hasor.plugins.jfinal.HasorPlugin;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Config extends JFinalConfig {
    private static Logger logger = LoggerFactory.getLogger(Config.class);

    @Override
    public void configConstant(Constants me) {
        PropKit.use("shop_h5.properties");
        me.setI18nDefaultBaseName("i18n");
        me.setI18nDefaultLocale(StringUtils.trim(PropKit.get("locale")));
        me.setEncoding("UTF-8");
        me.setViewType(ViewType.JFINAL_TEMPLATE);

    }

    @Override
    public void configRoute(Routes me) {
        AutoBindRoutes abr = new AutoBindRoutes();
        List<Class<? extends Controller>> clazzes = new ArrayList<Class<? extends Controller>>();
        clazzes.add(com.jfinalshop.controller.BaseController.class);
        abr.addExcludeClasses(clazzes);
        me.add(abr);
    }

    @Override
    public void configEngine(Engine me) {
        me.setDevMode(true);
    }

    @Override
    public void configPlugin(Plugins me) {
        String cacheName = PropKit.get("redis.cacheName");
        String host=PropKit.get("redis.host");
        int port=PropKit.getInt("redis.port");
        int timeOut=PropKit.getInt("redis.timeOut");
        String password=PropKit.get("redis.password");
        int database=PropKit.getInt("redis.database");
        String clientName=PropKit.get("redis.clientName");
        //正式启用
        //RedisPlugin redis = new RedisPlugin(cacheName,host,port,timeOut);
        RedisPlugin redis = new RedisPlugin(cacheName,host,port,timeOut,password,database,clientName);
        redis.setSerializer(JdkSerializer.me);
        me.add(redis);
        logger.info("================================redis加载完成=========================================");
        //（必选）Hasor 框架的启动和销毁
        me.add(new HasorPlugin(JFinal.me()));
    }

    @Override
    public void configInterceptor(Interceptors me) {
        // 依赖注入
        me.add(new HasorInterceptor(JFinal.me()));
    }

    @Override
    public void configHandler(Handlers me) {

    }
    @Override
    public void afterJFinalStart(){
        RenderManager.me().getEngine().addSharedObject("contextPath", JFinal.me().getContextPath());
    }
}
