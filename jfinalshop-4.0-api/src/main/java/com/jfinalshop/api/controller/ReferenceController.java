package com.jfinalshop.api.controller;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Filter;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.SearchService;
import net.hasor.core.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerBind(controllerKey = "/api/reference")
public class ReferenceController extends BaseAPIController{

    @Inject
    private ArticleService articleService;
    @Inject
    private GoodsService goodsService;
    @Inject
    private SearchService searchService;

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

    /**
     * 生成索引
     */
    public void generateSubmit() {
        String generateTypeName = getPara("generateType");//goods
        GenerateType generateType = StrKit.notBlank(generateTypeName) ? GenerateType.valueOf(generateTypeName) : null;
        Boolean isPurge = getParaToBoolean("isPurge");//false 是否清楚元索引
        Integer first = getParaToInt("first");//0
        Integer count = getParaToInt("count");//100

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
                List<Goods> productList = goodsService.findList(first, count, filters, null);
                generateCount = searchService.indexGoods(productList);
                first += productList.size();
                if (productList.size() == count) {
                    isCompleted = false;
                }
                break;
        }
        long endTime = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("first", first);
        data.put("generateCount", generateCount);
        data.put("generateTime", endTime - startTime);
        data.put("isCompleted", isCompleted);
        renderJson(data);
    }


    public void index(){
        render("/pages/index.html");
    }

}
