package com.jfinalshop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.entity.ArticleVO;
import com.jfinalshop.entity.GoodsVO;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.ld.zxw.page.Page;
import com.ld.zxw.service.LuceneService;
import com.ld.zxw.service.LuceneServiceImpl;
import com.ld.zxw.util.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 搜索
 * 
 * 
 */
@Singleton
public class SearchService {
	private LuceneService luceneProductService = new LuceneServiceImpl("goods");
	private LuceneService luceneArticleService = new LuceneServiceImpl("article");
	/**
	 * 删除索引
	 * 
	 */
	public void delGoodsAll() {
		try {
			luceneProductService.delAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除索引
	 * 
	 */
	public void delArticleAll() {
		try {
			luceneArticleService.delAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 创建索引
	 * 
	 */
	public int indexArticle(List<Article> articleList) {
		int generateCount = 0;
		long time = DateUtil.getTime();
		try {
			if (CollectionUtil.isNotEmpty(articleList)) {
				List<ArticleVO> pArticleList = new ArrayList<>();
				for (Article article : articleList) {
					ArticleVO articleVO = copyProperty(article);
					pArticleList.add(articleVO);
					generateCount++;
				}
				luceneArticleService.saveObj(pArticleList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DateUtil.timeConsuming("添加索引时间", time);
		return generateCount;
	}
	
	/**
	 * 创建索引
	 * 
	 */
	public int indexGoods(List<Goods> goodsList) {
		int generateCount = 0;
		long time = DateUtil.getTime();
		try {
			if (CollectionUtil.isNotEmpty(goodsList)) {
				List<GoodsVO> pProductList = new ArrayList<>();
				for (Goods goods : goodsList) {
					GoodsVO goodsVO = copyProperty(goods);
					pProductList.add(goodsVO);
					generateCount++;
				}
				luceneProductService.saveObj(pProductList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DateUtil.timeConsuming("添加索引时间", time);
		return generateCount;
	}
	
	/**
	 * 搜索文章分页
	 * 
	 * @param keyword
	 *            关键词
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	/**
	 * 搜索文章分页
	 * 
	 * @param keyword
	 *            关键词
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<ArticleVO> search(String keyword, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page<>();
		}
		if (pageable == null) {
			pageable = new Pageable();
		}
		// 在多个字段中查询相同的内容
		Page<ArticleVO> articles = null;
		try {
			String[] fields = {"title", "content"};
			Map<String, Float> boosts = new HashMap<>();
			boosts.put("title", 1.5F); //权重
			Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(keyword);
			Sort sort = new Sort(new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true));
			articles = luceneArticleService.findList(keywordQuery, pageable.getPageNumber(), pageable.getPageSize(), ArticleVO.class, sort);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return articles;
	}

	/**
	 * 搜索货品分页
	 * 添加渠道条件
	 * 
	 * @param keyword
	 *            关键词
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 货品分页
	 */
	public Page<GoodsVO> search(String keyword, String channel, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page<>();
		}

		if (pageable == null) {
			pageable = new Pageable();
		}

		// 在多个字段中查询相同的内容
		Page<GoodsVO> productDtos = null;
		try {
			Builder builder = new BooleanQuery.Builder();
			Map<String, Float> boosts = new HashMap<>();

			String[] fields = {"sn", "name", "caption", "keyword", "store", "productCategory", "brand", "channel"};
			boosts.put("keyword", 1.5F); //权重
			boosts.put("channel", 1.4F); //权重
			BooleanClause keywordClause = null;
			BooleanClause channelClause = null;
			//BooleanClause plusClause = null;
			//String keyword1 = keyword;
			if(StrKit.notBlank(keyword)) {
				Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(keyword);
				keywordClause = new BooleanClause(keywordQuery, Occur.MUST);
				builder.add(keywordClause);
			}
			if(StrKit.notBlank(channel)) {
				Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(channel);
				channelClause = new BooleanClause(keywordQuery, Occur.MUST);
				builder.add(channelClause);
			}
			BooleanClause priceClause = null;
			//BooleanClause channelClause = null;
			if (startPrice != null && endPrice != null) {
				Query priceQuery = DoublePoint.newRangeQuery("price", startPrice.doubleValue(), endPrice.doubleValue());
				priceClause = new BooleanClause(priceQuery, Occur.SHOULD);
				builder.add(priceClause);
			} else if (startPrice != null) {
				Query priceQuery = DoublePoint.newRangeQuery("price", startPrice.doubleValue(), Double.MAX_VALUE);
				priceClause = new BooleanClause(priceQuery, Occur.SHOULD);
				builder.add(priceClause);
			} else if (endPrice != null) {
				Query priceQuery = DoublePoint.newRangeQuery("price", Double.MIN_VALUE, endPrice.doubleValue());
				priceClause = new BooleanClause(priceQuery, Occur.SHOULD);
				builder.add(priceClause);
			} else {
				Query priceQuery = DoublePoint.newRangeQuery("price", Double.MIN_VALUE, Double.MAX_VALUE);
				priceClause = new BooleanClause(priceQuery, Occur.SHOULD);
				builder.add(priceClause);
			}
			//添加分渠道查询和关键字的联合查询
			/*if (StrKit.notBlank(channel)) {
				Query channelQuery = new FuzzyQuery(new Term("channel",channel), 2);
				channelClause = new BooleanClause(channelQuery, Occur.MUST);
				builder.add(channelClause);
			}*/
			/*//添加分渠道查询
			if (StrKit.notBlank(channel)) {
				Query plusQuery = new TermQuery(new Term("keyword",keyword));
				plusClause = new BooleanClause(plusQuery, Occur.MUST);
				builder.add(plusClause);
			}*/
			BooleanQuery query = builder.build();

			// 排序
			SortField[] sortFields = null;
			if (orderType != null) {
				switch (orderType) {
					case topDesc:
						sortFields = new SortField[] { new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					case priceAsc:
						sortFields = new SortField[] { new SortField("price", SortField.Type.DOUBLE, false), new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					case priceDesc:
						sortFields = new SortField[] { new SortField("price", SortField.Type.DOUBLE, true), new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					case salesDesc:
						sortFields = new SortField[] { new SortField("sales", SortField.Type.LONG, true), new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					case scoreDesc:
						sortFields = new SortField[] { new SortField("score", SortField.Type.FLOAT, true), new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					case dateDesc:
						sortFields = new SortField[] { new SortField("createdDate", SortField.Type.LONG, true) };
						break;
					default:
						break;
				}
			} else {
				sortFields = new SortField[] { new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true) };
			}
			productDtos = luceneProductService.findList(query, pageable.getPageNumber(), pageable.getPageSize(), GoodsVO.class, new Sort(sortFields));

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return productDtos;
	}
	
	/**
	 * 商品属性复制
	 * 
	 * @return 商品
	 */
	private GoodsVO copyProperty(Goods goods) {
		GoodsVO goodsVO = new GoodsVO();
		goodsVO.setId(goods.getId());
		goodsVO.setCreatedDate(goods.getCreateDate());
		goodsVO.setSn(goods.getSn());
		goodsVO.setName(goods.getName());
		goodsVO.setCaption(goods.getCaption());
		goodsVO.setType(goods.getType());
		goodsVO.setPrice(goods.getPrice());
		goodsVO.setMarketPrice(goods.getMarketPrice());
		goodsVO.setImage(goods.getImage());
		goodsVO.setUnit(goods.getUnit());
		goodsVO.setWeight(goods.getWeight());
		goodsVO.setIsMarketable(goods.getIsMarketable());
		goodsVO.setIsList(goods.getIsList());
		goodsVO.setIsTop(goods.getIsTop());
		goodsVO.setIsDelivery(goods.getIsDelivery());
		goodsVO.setIntroduction(goods.getIntroduction());
		goodsVO.setChannel(goods.getChannel());
		goodsVO.setKeyword(goods.getKeyword());
		goodsVO.setScore(goods.getScore());
		goodsVO.setTotalScore(goods.getTotalScore());
		goodsVO.setScoreCount(goods.getScoreCount());
		goodsVO.setWeekHits(goods.getWeekHits());
		goodsVO.setMonthHits(goods.getMonthHits());
		goodsVO.setHits(goods.getHits());
		goodsVO.setWeekSales(goods.getWeekSales());
		goodsVO.setMonthSales(goods.getMonthSales());
		goodsVO.setSales(goods.getSales());
		goodsVO.setWeekHitsDate(goods.getWeekHitsDate());
		goodsVO.setMonthHitsDate(goods.getMonthHitsDate());
		goodsVO.setWeekSalesDate(goods.getWeekSalesDate());
		goodsVO.setProductImages(goods.getProductImages());
		return goodsVO;
	}
	
	/**
	 * 文章属性复制
	 * 
	 * @return 文章
	 */
	private ArticleVO copyProperty(Article article) {
		ArticleVO articleVO = new ArticleVO();
		articleVO.setId(article.getId());
		articleVO.setCreateDate(article.getCreateDate());
		articleVO.setTitle(article.getTitle());
		articleVO.setAuthor(article.getAuthor());
		articleVO.setContent(article.getContent());
		articleVO.setSeoTitle(article.getSeoTitle());
		articleVO.setSeoKeywords(article.getSeoKeywords());
		articleVO.setSeoDescription(article.getSeoDescription());
		articleVO.setIsPublication(article.getIsPublication());
		articleVO.setIsTop(article.getIsTop());
		articleVO.setHits(article.getHits());
		return articleVO;
	}
}