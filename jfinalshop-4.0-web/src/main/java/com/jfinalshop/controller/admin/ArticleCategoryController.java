package com.jfinalshop.controller.admin;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;

/**
 * Controller - 文章分类
 * 
 *
 */
@ControllerBind(controllerKey = "/admin/article_category")
public class ArticleCategoryController extends BaseController {

	@Inject
	private ArticleCategoryService articleCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		ArticleCategory articleCategory = getModel(ArticleCategory.class);
		Long parentId = getParaToLong("parentId");
		if (parentId != null) {
			articleCategory.setParentId(articleCategoryService.find(parentId).getId());
		}
		articleCategory.setTreePath(null);
		articleCategory.setGrade(null);
		articleCategory.setChildren(null);
		articleCategory.setArticles(null);
		articleCategoryService.save(articleCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/article_category/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("articleCategory", articleCategory);
		setAttr("children", articleCategoryService.findChildren(articleCategory, true, null));
		render("/admin/article_category/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		ArticleCategory articleCategory = getModel(ArticleCategory.class);
		Long parentId = getParaToLong("parentId");
		if (parentId != null) {
			articleCategory.setParentId(articleCategoryService.find(parentId).getId());
		}
		if (articleCategory.getParent() != null) {
			ArticleCategory parent = articleCategory.getParent();
			if (parent.equals(articleCategory)) {
				redirect(ERROR_VIEW);
				return;
			}
			List<ArticleCategory> children = articleCategoryService.findChildren(parent, true, null);
			if (children != null && children.contains(parent)) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		articleCategory.remove("tree_path", "grade");
		articleCategoryService.update(articleCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/article_category/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		if (articleCategory == null) {
			redirect(ERROR_VIEW);
			return;
		}
		List<ArticleCategory> children = articleCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			renderJson(Message.error("admin.articleCategory.deleteExistChildrenNotAllowed"));
			return;
		}
		List<Article> articles = articleCategory.getArticles();
		if (articles != null && !articles.isEmpty()) {
			renderJson(Message.error("admin.articleCategory.deleteExistArticleNotAllowed"));
			return;
		}
		articleCategoryService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}