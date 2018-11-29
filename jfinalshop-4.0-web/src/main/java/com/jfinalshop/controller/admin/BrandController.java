package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Brand.Type;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.GoodsService;

/**
 * Controller - 品牌
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/brand")
public class BrandController extends BaseController {

	@Inject
	private BrandService brandService;
	@Inject
	private GoodsService goodsService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Brand.Type.values());
		render("/admin/brand/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Brand brand = getModel(Brand.class);
		String typeName = getPara("type");
		Brand.Type type = StrKit.notBlank(typeName) ? Type.valueOf(typeName) : null;
		if (type != null) {
			brand.setType(type.ordinal());
		}
		if (Brand.Type.text.ordinal() == brand.getType()) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			redirect(ERROR_VIEW);
			return;
		}
		brand.setGoods(null);
		brand.setProductCategories(null);
		brandService.save(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/brand/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Brand.Type.values());
		setAttr("brand", brandService.find(id));
		render("/admin/brand/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Brand brand = getModel(Brand.class);
		String typeName = getPara("type");
		Brand.Type type = StrKit.notBlank(typeName) ? Type.valueOf(typeName) : null;
		if (type != null) {
			brand.setType(type.ordinal());
		}
		if (Brand.Type.text.equals(brand.getType())) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			redirect(ERROR_VIEW);
			return;
		}
		brandService.update(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/brand/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", brandService.findPage(pageable));
		LogKit.info(">" + pageable.getPageNumber());
		setAttr("pageable", pageable);
		render("/admin/brand/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Goods goods = goodsService.find(id);
				if (goods != null) {
					renderJson(Message.error("admin.brand.deleteExistNotAllowed", goods.getName()));
					return;
				}
			}
			brandService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}