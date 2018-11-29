package com.jfinalshop.controller.admin;

import java.util.Arrays;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.AttributeService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 属性
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/attribute")
public class AttributeController extends BaseController {

	@Inject
	private AttributeService attributeService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		Long sampleId = getParaToLong("sampleId");
		setAttr("sample", attributeService.find(sampleId));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/attribute/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Attribute attribute = getModel(Attribute.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		List<String> options = Arrays.asList(getParaValues("options"));
		
		attribute.setOptionsConverter(options);
		CollectionUtils.filter(attribute.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		attribute.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		
		attribute.setOptions(JSONArray.toJSONString(options));
		Integer propertyIndex = attributeService.findUnusedPropertyIndex(attribute.getProductCategory());
		if (propertyIndex == null) {
			addFlashMessage(Message.error("admin.attribute.addCountNotAllowed", Goods.ATTRIBUTE_VALUE_PROPERTY_COUNT));
		} else {
			attribute.setPropertyIndex(null);
			attributeService.save(attribute);
			addFlashMessage(SUCCESS_MESSAGE);
		}
		redirect("list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("attribute", attributeService.find(id));
		render("/admin/attribute/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Attribute attribute = getModel(Attribute.class);
		List<String> options = Arrays.asList(getParaValues("options"));
		
		attribute.setOptionsConverter(options);
		CollectionUtils.filter(attribute.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		
		attribute.setOptions(JSONArray.toJSONString(options));
		attribute.remove("property_index", "product_category_id");
		attributeService.update(attribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", attributeService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/attribute/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		attributeService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}