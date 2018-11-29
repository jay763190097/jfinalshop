package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Tag;
import com.jfinalshop.service.TagService;

/**
 * Controller - 标签
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/tag")
public class TagController extends BaseController {

	@Inject
	private TagService tagService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Tag.Type.values());
		render("/admin/tag/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Tag tag = getModel(Tag.class);
		
		String typeName = getPara("type", null);
		Tag.Type type = StrKit.notBlank(typeName) ? Tag.Type.valueOf(typeName) : null;
		tag.setType(type.ordinal());
		
		tag.setArticles(null);
		tag.setGoods(null);
		tagService.save(tag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/tag/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Tag.Type.values());
		setAttr("tag", tagService.find(id));
		render("/admin/tag/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Tag tag = getModel(Tag.class);
		
		tag.remove("type");
		tagService.update(tag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/tag/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", tagService.findPage(pageable));
		render("/admin/tag/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		tagService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}