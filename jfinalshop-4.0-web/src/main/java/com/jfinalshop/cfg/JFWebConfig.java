package com.jfinalshop.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.hasor.plugins.jfinal.HasorInterceptor;
import net.hasor.plugins.jfinal.HasorPlugin;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.collect.Lists;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.FakeStaticHandler;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.FreeMarkerExceptionHandler;
//import com.jfinalshop.api.interceptor.ErrorInterceptor;
import com.jfinalshop.model._MappingKit;
import com.jfinalshop.security.MyJdbcAuthzService;
import com.jfinalshop.shiro.core.ShiroInterceptor;
import com.jfinalshop.shiro.core.ShiroPlugin;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.freemarker.ShiroTags;
import com.jfinalshop.template.directive.AdPositionDirective;
import com.jfinalshop.template.directive.ArticleCategoryChildrenListDirective;
import com.jfinalshop.template.directive.ArticleCategoryParentListDirective;
import com.jfinalshop.template.directive.ArticleCategoryRootListDirective;
import com.jfinalshop.template.directive.ArticleListDirective;
import com.jfinalshop.template.directive.AttributeListDirective;
import com.jfinalshop.template.directive.BrandListDirective;
import com.jfinalshop.template.directive.ConsultationListDirective;
import com.jfinalshop.template.directive.CurrentMemberDirective;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.template.directive.FriendLinkListDirective;
import com.jfinalshop.template.directive.GoodsListDirective;
import com.jfinalshop.template.directive.MemberAttributeListDirective;
import com.jfinalshop.template.directive.NavigationListDirective;
import com.jfinalshop.template.directive.PaginationDirective;
import com.jfinalshop.template.directive.ProductCategoryChildrenListDirective;
import com.jfinalshop.template.directive.ProductCategoryParentListDirective;
import com.jfinalshop.template.directive.ProductCategoryRootListDirective;
import com.jfinalshop.template.directive.PromotionListDirective;
import com.jfinalshop.template.directive.ReviewListDirective;
import com.jfinalshop.template.directive.SeoDirective;
import com.jfinalshop.template.directive.TagListDirective;
import com.jfinalshop.template.method.AbbreviateMethod;
import com.jfinalshop.template.method.CurrencyMethod;
import com.jfinalshop.template.method.MessageMethod;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.validator.ValidationPlugin;
import com.jfinalshop.validator.ValidatorInterceptor;
import com.ld.zxw.config.LucenePlusConfig;
import com.ld.zxw.core.LucenePlugin;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StandardCompress;

public class JFWebConfig extends JFinalConfig {

	/**
	 * 供Shiro插件使用。
	 */
	Routes routes;
	
	@Override
	public void configConstant(Constants constants) {
		PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
		constants.setDevMode(PropKit.getBoolean("devMode", false));
		//constants.setEncoding("UTF-8");
		constants.setI18nDefaultBaseName("i18n");
		constants.setViewType(ViewType.FREE_MARKER);
		constants.setViewExtension(".ftl");
		constants.setI18nDefaultLocale(StringUtils.trim(PropKit.get("locale")));
		constants.setError401View("/admin/common/error.ftl");
		constants.setError403View("/admin/common/unauthorized.ftl");
		constants.setError404View("/admin/common/error.ftl");
		constants.setError500View("/admin/common/error.ftl");
		
	}

	@Override
	public void configRoute(Routes routes) {
		this.routes = routes;
		AutoBindRoutes abr = new AutoBindRoutes();
		// api 模块
		abr.addJars("jfinalshop-4.0-api-1.0-SNAPSHOT.jar");
		// 忽略不自动扫描的Controller
		List<Class<? extends Controller>> clazzes = new ArrayList<Class<? extends Controller>>();
		clazzes.add(com.jfinalshop.controller.shop.BaseController.class);
		clazzes.add(com.jfinalshop.controller.admin.BaseController.class);
//		clazzes.add(com.jfinalshop.controller.wap.BaseController.class);
		abr.addExcludeClasses(clazzes);
		routes.add(abr);
	}

	@Override
	public void configPlugin(Plugins plugins) {
		//String publicKey = getProperty("jdbc.publicKey");
		//String password = getProperty("jdbc.password");
		//配置druid连接池
		DruidPlugin druidDefault = new DruidPlugin(
		StringUtils.trim(PropKit.get("jdbc.url")), 
		StringUtils.trim(PropKit.get("jdbc.username")),
		//EncriptionKit.passwordDecrypt(publicKey, password),
		StringUtils.trim(PropKit.get("jdbc.password")),
		StringUtils.trim(PropKit.get("jdbc.driver")));
		// StatFilter提供JDBC层的统计信息
		druidDefault.addFilter(new StatFilter());

		// WallFilter的功能是防御SQL注入攻击
		WallFilter wallDefault = new WallFilter();

		wallDefault.setDbType("mysql");
		druidDefault.addFilter(wallDefault);
		druidDefault.setInitialSize(PropKit.getInt("db.default.poolInitialSize"));
		druidDefault.setMaxPoolPreparedStatementPerConnectionSize(PropKit.getInt("db.default.poolMaxSize"));
		druidDefault.setTimeBetweenConnectErrorMillis(PropKit.getInt("db.default.connectionTimeoutMillis"));
		plugins.add(druidDefault);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidDefault);
		plugins.add(arp);
		
		// 配置属性名(字段名)大小写不敏感容器工厂
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		
		// 显示SQL
		arp.setShowSql(true);
		
		// 所有配置在 MappingKit
		_MappingKit.mapping(arp);
		
		// 缓存
		plugins.add(new EhCachePlugin());
		
		//shiro权限框架，添加到plugin
		plugins.add(new ShiroPlugin(routes, new MyJdbcAuthzService()));
		
		// 参数校验插件
		plugins.add(new ValidationPlugin("validation.properties"));
		
		//（必选）Hasor 框架的启动和销毁
		plugins.add(new HasorPlugin(JFinal.me()));
		
		//quarts插件配置
		/*QuartzPlugin quartzPlugin = new QuartzPlugin();
		quartzPlugin.setJobs("system-quartz.properties");
		plugins.add(quartzPlugin);*/
		// Lucene插件
		LucenePlugin lucenePlugin = new LucenePlugin(); 
		LucenePlusConfig luceneConfig = new LucenePlusConfig(); 
		// 默认开发模式 false  生产模式 为 true
		luceneConfig.setDevMode(false);
		// 词库目录 启用词
		//luceneConfig.setExtWordPath("");
		// 开启高亮 如果开启 高亮  必须设置高亮字段
		luceneConfig.setHighlight(true);
		luceneConfig.setHighlightFields(Lists.newArrayList("keyword"));
		// 分词字段 设置后模糊查询
		luceneConfig.setParticipleField(Lists.newArrayList("keyword","name","caption","brand","seoTitle","seoKeywords","channel"));
		// 索引目录
		luceneConfig.setLucenePath(PathKit.getWebRootPath() + "/lucene/"); 
		//添加 article 源
		lucenePlugin.putDataSource(luceneConfig, "article"); 
		//添加 goods 源
		lucenePlugin.putDataSource(luceneConfig, "goods");
		System.out.println("添加索引成功");
		plugins.add(lucenePlugin);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// 添加shiro的过滤器到interceptor
		me.add(new ShiroInterceptor());
		// 依赖注入
        me.add(new HasorInterceptor(JFinal.me()));
		// 参数校全局拦截
        me.add(new ValidatorInterceptor());
		// 捕获所有异常
        //me.add(new ErrorInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		handlers.add(new FakeStaticHandler(".jhtml"));
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid", new IDruidStatViewAuth() {
			public boolean isPermitted(HttpServletRequest request) {
				if (SubjectKit.hasRoleAdmin()) {
					return true;
				} else {
					return false;
				}
			}
		});
		handlers.add(dvh);
		handlers.add(new ContextPathHandler("base"));
	}

	@Override
	public void afterJFinalStart() {
		try {
			Configuration cfg = FreeMarkerRender.getConfiguration();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("base", JFinal.me().getContextPath());
			map.put("showPowered", StringUtils.trim(PropKit.get("show_powered")));
			map.put("setting", SystemUtils.getSetting());
			map.put("message", new MessageMethod());
			map.put("abbreviate", new AbbreviateMethod());
			map.put("currency", new CurrencyMethod());
			map.put("flash_message", new FlashMessageDirective());
			map.put("current_member", new CurrentMemberDirective());
			map.put("pagination", new PaginationDirective());
			map.put("seo", new SeoDirective());
			map.put("ad_position", new AdPositionDirective());
			map.put("member_attribute_list", new MemberAttributeListDirective());
			map.put("navigation_list", new NavigationListDirective());
			map.put("tag_list", new TagListDirective());
			map.put("friend_link_list", new FriendLinkListDirective());
			map.put("brand_list", new BrandListDirective());
			map.put("attribute_list", new AttributeListDirective());
			map.put("article_list", new ArticleListDirective());
			map.put("article_category_root_list", new ArticleCategoryRootListDirective());
			map.put("article_category_parent_list", new ArticleCategoryParentListDirective());
			map.put("article_category_children_list", new ArticleCategoryChildrenListDirective());
			map.put("goods_list", new GoodsListDirective());
			map.put("product_category_root_list", new ProductCategoryRootListDirective());
			map.put("product_category_parent_list", new ProductCategoryParentListDirective());
			map.put("product_category_children_list", new ProductCategoryChildrenListDirective());
			map.put("review_list", new ReviewListDirective());
			map.put("consultation_list", new ConsultationListDirective());
			map.put("promotion_list", new PromotionListDirective());
			map.put("compress", StandardCompress.INSTANCE);
			cfg.setSharedVaribles(map);

			cfg.setDefaultEncoding(StringUtils.trim(PropKit.get(("template.encoding"))));
			cfg.setURLEscapingCharset(StringUtils.trim(PropKit.get(("url_escaping_charset"))));
			cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
			cfg.setWhitespaceStripping(true);
			cfg.setClassicCompatible(true);
			cfg.setNumberFormat(StringUtils.trim(PropKit.get("template.number_format")));
			cfg.setBooleanFormat(StringUtils.trim(PropKit.get("template.boolean_format")));
			cfg.setDateTimeFormat(StringUtils.trim(PropKit.get(("template.datetime_format"))));
			cfg.setDateFormat(StringUtils.trim(PropKit.get("template.date_format")));
			cfg.setTimeFormat(StringUtils.trim(PropKit.get("template.time_format")));
			cfg.setObjectWrapper(new BeansWrapper(Configuration.VERSION_2_3_23));
			cfg.setTemplateUpdateDelayMilliseconds(PropKit.getInt(("template.update_delay")));
			cfg.setTemplateExceptionHandler(new FreeMarkerExceptionHandler());
			cfg.setSharedVariable("shiro", new ShiroTags());
			
			cfg.setServletContextForTemplateLoading(JFinal.me().getServletContext(), StringUtils.trim(PropKit.get("template.loader_path")));
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		super.afterJFinalStart();
	}

	@Override
	public void configEngine(Engine me) {
		
	}
}
