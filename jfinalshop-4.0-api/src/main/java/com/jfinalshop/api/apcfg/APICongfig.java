package com.jfinalshop.api.apcfg;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.collect.Lists;
import com.jfinal.config.*;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;

import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

import com.jfinalshop.api.handler.APINotFoundHandler;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model._MappingKit;

import com.jfinalshop.security.MyJdbcAuthzService;
import com.jfinalshop.shiro.core.ShiroPlugin;
import com.ld.zxw.config.LucenePlusConfig;
import com.ld.zxw.core.LucenePlugin;
import net.hasor.plugins.jfinal.HasorInterceptor;
import net.hasor.plugins.jfinal.HasorPlugin;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinalshop.Filter;
import com.jfinal.kit.StrKit;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.SearchService;
public class APICongfig extends JFinalConfig {
    private static Logger logger = LoggerFactory.getLogger(APICongfig.class);
    /**
	 * 生成类型
	 */
	public enum GenerateType {
		/**
		 * 文章
		 */
		article,

		/**
		 * 商品
		 */
		goods
	}
    @Override
    public void configConstant(Constants me) {
        PropKit.use("jfinalshop.properties");
       // me.setDevMode(false);
        me.setDevMode(PropKit.getBoolean("devMode", false));
        me.setEncoding("UTF-8");
        me.setViewType(ViewType.JFINAL_TEMPLATE);
        me.setI18nDefaultBaseName("i18n");
        me.setI18nDefaultLocale(StringUtils.trim(PropKit.get("locale")));
        //配置默认上传路径
        /*me.setBaseUploadPath("upload");*/
        me.setBaseUploadPath(PathKit.getWebRootPath() +"/upload/image/");
        me.setMaxPostSize(1*Const.DEFAULT_MAX_POST_SIZE);
    }

    @Override
    public void configRoute(Routes routes) {
        AutoBindRoutes abr = new AutoBindRoutes();
        List<Class<? extends Controller>> clazzes = new ArrayList<Class<? extends Controller>>();
        clazzes.add(com.jfinalshop.api.controller.BaseAPIController.class);
        abr.addExcludeClasses(clazzes);
        routes.add(abr);
        logger.info("configRoute加载完毕");
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        //配置druid连接池
        DruidPlugin druidDefault = new DruidPlugin(
                StringUtils.trim(PropKit.get("jdbc.url")),
                StringUtils.trim(PropKit.get("jdbc.username")),
                //EncriptionKit.passwordDecrypt(publicKey, password),
                StringUtils.trim(PropKit.get("jdbc.password")),
                StringUtils.trim(PropKit.get("jdbc.driver")));
        druidDefault.addFilter(new StatFilter());
        WallFilter wallDefault = new WallFilter();

        logger.info("jdbc.url--"+PropKit.get("jdbc.url"));
        wallDefault.setDbType("mysql");
        druidDefault.addFilter(wallDefault);
        druidDefault.setInitialSize(PropKit.getInt("db.default.poolInitialSize"));
        druidDefault.setMaxPoolPreparedStatementPerConnectionSize(PropKit.getInt("db.default.poolMaxSize"));
        druidDefault.setTimeBetweenConnectErrorMillis(PropKit.getInt("db.default.connectionTimeoutMillis"));
        me.add(druidDefault);
        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidDefault);
        me.add(arp);

        // 配置属性名(字段名)大小写不敏感容器工厂
        arp.setContainerFactory(new CaseInsensitiveContainerFactory());
        // 显示SQL
        arp.setShowSql(true);
        // 所有配置在 MappingKit
        _MappingKit.mapping(arp);
        //Ehcache缓存
        me.add(new EhCachePlugin());
        //（必选）Hasor 框架的启动和销毁
        me.add(new HasorPlugin(JFinal.me()));

// Lucene插件
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
        me.add(lucenePlugin);

    }

    @Override
    public void configInterceptor(Interceptors me) {
        //依赖注入
        me.add(new HasorInterceptor(JFinal.me()));
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new APINotFoundHandler());
       // me.add(new ContextPathHandler("base"));
    }
    //项目初始加载 分词检索goods源
    @Override
    public void afterJFinalStart(){
    	String channel = "IOS";
    	String channel1 = "'"+channel +"'";
    	SearchService searchService = new SearchService();
    	ArticleService articleService = new ArticleService();
    	GoodsService goodsService = new GoodsService();
    	String generateTypeName = "goods";
		GenerateType generateType = StrKit.notBlank(generateTypeName) ? GenerateType.valueOf(generateTypeName) : null;
		Boolean isPurge = false;
		Integer first = 0;
		Integer count = 100;
		
		long startTime = System.currentTimeMillis();
		if (first == null || first < 0) {
			first = 0;
		}
		if (count == null || count <= 0) {
			count = 100;
		}
		int generateCount = 0;
		boolean isCompleted = true;
		List<Filter> filters = new ArrayList<Filter>();
		switch (generateType) {
		case article:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delArticleAll();
			}
			filters.add(Filter.eq("is_publication", true));
			List<Article> articleList = articleService.findList(first, count, null, null);
			generateCount = searchService.indexArticle(articleList);
			first += articleList.size();
			if (articleList.size() == count) {
				isCompleted = false;
			}
			break;
		case goods:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delGoodsAll();
			}
			filters.add(Filter.eq("is_marketable", true));
			filters.add(Filter.eq("is_list", true));
			//filters.add(Filter.eq("channel", channel1));
			List<Goods> productList = goodsService.findList(first, count, filters, null);
			generateCount = searchService.indexGoods(productList);
			first += productList.size();
			if (productList.size() == count) {
				isCompleted = false;
			}
			break;
		}
		/*String generateTypeName1 = "article";
		GenerateType generateType1 = StrKit.notBlank(generateTypeName1) ? GenerateType.valueOf(generateTypeName1) : null;
		switch (generateType1) {
		case article:
			if (first == 0 && isPurge != null && isPurge) {
				.delArticleAll();
			}
			filters.add(Filter.eq("is_publication", true));
			List<Article> articleList = articleService.findList(first, count, null, null);
			generateCount = searchService.indexArticle(articleList);
			first += articleList.size();
			if (articleList.size() == count) {
				isCompleted = false;
			}
			break;
		case goods:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delGoodsAll();
			}
			filters.add(Filter.eq("is_marketable", true));
			filters.add(Filter.eq("is_list", true));
			List<Goods> productList = goodsService.findList(first, count, filters, null);
			generateCount = searchService.indexGoods(productList);
			first += productList.size();
			if (productList.size() == count) {
				isCompleted = false;
			}
			break;
		}*/
		long endTime = System.currentTimeMillis();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("first", first);
		data.put("generateCount", generateCount);
		data.put("generateTime", endTime - startTime);
		data.put("isCompleted", isCompleted);
		logger.info("加载索引数据1"+data);
    };
}
