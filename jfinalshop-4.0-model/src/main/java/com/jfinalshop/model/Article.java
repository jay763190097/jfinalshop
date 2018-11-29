package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.jfinalshop.Setting;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.model.base.BaseArticle;
import com.jfinalshop.util.SystemUtils;

/**
 * Model - 文章
 * 
 * 
 */
public class Article extends BaseArticle<Article> {
	private static final long serialVersionUID = 1622188336597868597L;
	public static final Article dao = new Article();
	
	/** 点击数缓存名称 */
	public static final String HITS_CACHE_NAME = "articleHits";

	/** 内容分页长度 */
	private static final int PAGE_CONTENT_LENGTH = 2000;

	/** 内容分页标签 */
	private static final String PAGE_BREAK_TAG = "jfinalshop_page_break_tag";

	/** 段落配比 */
	private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("[^,;\\.!?，；。！？]*([,;\\.!?，；。！？]+|$)");

	/**
	 * 静态生成方式
	 */
	public enum GenerateMethod {

		/** 无 */
		none,

		/** 即时 */
		eager,

		/** 延时 */
		lazy
	}
	
	/** 文章分类 */
	private ArticleCategory articleCategory;
	
	/** 标签 */
	private List<Tag> tags = new ArrayList<Tag>();
	
	/**
	 * 类型名称
	 */
	public GenerateMethod getGenerateMethodName() {
		return GenerateMethod.values()[getGenerateMethod()];
	}
	
	/**
	 * 获取文章分类
	 * 
	 * @return 文章分类
	 */
	public ArticleCategory getArticleCategory() {
		if (articleCategory == null) {
			articleCategory = ArticleCategory.dao.findById(getArticleCategoryId());
		}
		return articleCategory;
	}

	/**
	 * 设置文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 */
	public void setArticleCategory(ArticleCategory articleCategory) {
		this.articleCategory = articleCategory;
	}

	
	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	public List<Tag> getTags() {
		String sql = "SELECT t.* FROM `article_tag` a LEFT JOIN `tag` t ON a.`tags` = t.`id` WHERE a.`articles` = ?";
		if (CollectionUtils.isEmpty(tags)) {
			tags = Tag.dao.find(sql, getId());
		}
		return tags;
	}

	/**
	 * 设置标签
	 * 
	 * @param tags
	 *            标签
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * 设置页面关键词
	 * 
	 * @param seoKeywords
	 *            页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		set("seo_keywords", seoKeywords);
	}
	
	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getPath(1);
	}

	/**
	 * 获取路径
	 * 
	 * @param pageNumber
	 *            页码
	 * @return 路径
	 */
	public String getPath(Integer pageNumber) {
		if (pageNumber == null || pageNumber < 1) {
			return null;
		}
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("articleContent");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("article", this);
		model.put("pageNumber", pageNumber);
		return templateConfig.getRealStaticPath(model);
	}

	/**
	 * 获取URL
	 * 
	 * @return URL
	 */
	public String getUrl() {
		return getUrl(1);
	}

	/**
	 * 获取URL
	 * 
	 * @param pageNumber
	 *            页码
	 * @return URL
	 */
	public String getUrl(Integer pageNumber) {
		if (pageNumber == null || pageNumber < 1) {
			return null;
		}
		Setting setting = SystemUtils.getSetting();
		return setting.getSiteUrl() + getPath(pageNumber);
	}

	/**
	 * 获取文本内容
	 * 
	 * @return 文本内容
	 */
	public String getText() {
		if (StringUtils.isEmpty(getContent())) {
			return StringUtils.EMPTY;
		}
		return StringUtils.remove(Jsoup.parse(getContent()).text(), PAGE_BREAK_TAG);
	}

	/**
	 * 获取分页内容
	 * 
	 * @return 分页内容
	 */
	public String[] getPageContents() {
		if (StringUtils.isEmpty(getContent())) {
			return new String[] { StringUtils.EMPTY };
		}
		if (StringUtils.contains(getContent(), PAGE_BREAK_TAG)) {
			return StringUtils.splitByWholeSeparator(getContent(), PAGE_BREAK_TAG);
		}
		List<Node> childNodes = Jsoup.parse(getContent()).body().childNodes();
		if (CollectionUtils.isEmpty(childNodes)) {
			return new String[] { getContent() };
		}
		List<String> pageContents = new ArrayList<String>();
		int textLength = 0;
		StringBuilder paragraph = new StringBuilder();
		for (Node node : childNodes) {
			if (node instanceof Element) {
				Element element = (Element) node;
				paragraph.append(element.outerHtml());
				textLength += element.text().length();
				if (textLength >= PAGE_CONTENT_LENGTH) {
					pageContents.add(paragraph.toString());
					textLength = 0;
					paragraph.setLength(0);
				}
			} else if (node instanceof TextNode) {
				TextNode textNode = (TextNode) node;
				Matcher matcher = PARAGRAPH_PATTERN.matcher(textNode.text());
				while (matcher.find()) {
					String content = matcher.group();
					paragraph.append(content);
					textLength += content.length();
					if (textLength >= PAGE_CONTENT_LENGTH) {
						pageContents.add(paragraph.toString());
						textLength = 0;
						paragraph.setLength(0);
					}
				}
			}
		}
		String pageContent = paragraph.toString();
		if (StringUtils.isNotEmpty(pageContent)) {
			pageContents.add(pageContent);
		}
		return pageContents.toArray(new String[pageContents.size()]);
	}

	/**
	 * 获取分页内容
	 * 
	 * @param pageNumber
	 *            页码
	 * @return 分页内容
	 */
	public String getPageContent(Integer pageNumber) {
		if (pageNumber == null || pageNumber < 1) {
			return null;
		}
		String[] pageContents = getPageContents();
		if (pageContents.length < pageNumber) {
			return null;
		}
		return pageContents[pageNumber - 1];
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPages() {
		return getPageContents().length;
	}

}
