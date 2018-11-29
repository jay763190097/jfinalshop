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
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Specification;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.SpecificationService;

/**
 * Controller - 规格
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/specification")
public class SpecificationController extends BaseController {

	@Inject
	private SpecificationService specificationService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		Long sampleId = getParaToLong("sampleId");
		setAttr("sample", specificationService.find(sampleId));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/specification/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Specification specification = getModel(Specification.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		List<String> options = Arrays.asList(getParaValues("options"));
		
		specification.setOptionsConverter(options);
		CollectionUtils.filter(specification.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		specification.setProductCategoryId(productCategoryService.find(productCategoryId).getId());
		
		specification.setOptions(JSONArray.toJSONString(options));
		specificationService.save(specification);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/specification/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("specification", specificationService.find(id));
		render("/admin/specification/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Specification specification = getModel(Specification.class);
		List<String> options = Arrays.asList(getParaValues("options"));
		
		specification.setOptionsConverter(options);
		CollectionUtils.filter(specification.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		
		specification.setOptions(JSONArray.toJSONString(options));
		specification.remove("product_category_id");
		specificationService.update(specification);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/specification/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", specificationService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/specification/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		specificationService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}