package com.jfinalshop.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.service.MemberAttributeService;

/**
 * Controller - 会员注册项
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/member_attribute")
public class MemberAttributeController extends BaseController {

	@Inject
	private MemberAttributeService memberAttributeService;

	/**
	 * 检查配比语法是否正确
	 */
	public void checkPattern() {
		String pattern = getPara("pattern");
		if (StringUtils.isEmpty(pattern)) {
			renderJson(false);
			return;
		}
		try {
			Pattern.compile(pattern);
		} catch (PatternSyntaxException e) {
			renderJson(false);
			return;
		}
		renderJson(true);
	}

	/**
	 * 添加
	 */
	public void add() {
		if (memberAttributeService.findUnusedPropertyIndex() == null) {
			addFlashMessage(Message.warn("admin.memberAttribute.addCountNotAllowed", Member.ATTRIBUTE_VALUE_PROPERTY_COUNT));
			redirect("list.jhtml");
		}
		render("/admin/member_attribute/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		MemberAttribute memberAttribute = getModel(MemberAttribute.class);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		
		String typeName = getPara("type", "");
		MemberAttribute.Type type = StrKit.notBlank(typeName) ? MemberAttribute.Type.valueOf(typeName) : null;
		memberAttribute.setType(type.ordinal());
		
		if (MemberAttribute.Type.select.ordinal() == memberAttribute.getType() || MemberAttribute.Type.checkbox.ordinal() == memberAttribute.getType()) {
			//List<String> options = memberAttribute.getOptionsConverter();
			List<String> options = Arrays.asList(getParaValues("options"));
			memberAttribute.setOptionsConverter(options);
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				redirect(ERROR_VIEW);
				return;
			}
			memberAttribute.setOptions(JSONArray.toJSONString(options));
			memberAttribute.setPattern(null);
		} else if (MemberAttribute.Type.text.ordinal() == memberAttribute.getType()) {
			memberAttribute.setOptions(null);
		} else {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(memberAttribute.getPattern())) {
			try {
				Pattern.compile(memberAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		Integer propertyIndex = memberAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			redirect(ERROR_VIEW);
			return;
		}
		memberAttribute.setPropertyIndex(null);
		memberAttributeService.save(memberAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member_attribute/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberAttribute", memberAttributeService.find(id));
		render("/admin/member_attribute/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		MemberAttribute memberAttribute = getModel(MemberAttribute.class);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		
		MemberAttribute pMemberAttribute = memberAttributeService.find(memberAttribute.getId());
		if (pMemberAttribute == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (MemberAttribute.Type.select.ordinal() == pMemberAttribute.getType() || MemberAttribute.Type.checkbox.ordinal() == pMemberAttribute.getType()) {
			//List<String> options = memberAttribute.getOptionConverter();
			List<String> options = Arrays.asList(getParaValues("options"));
			memberAttribute.setOptionsConverter(options);
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				redirect(ERROR_VIEW);
				return;
			}
			memberAttribute.setOptions(JSONArray.toJSONString(options));
			memberAttribute.setPattern(null);
		} else {
			memberAttribute.setOptions(null);
		}
		if (StringUtils.isNotEmpty(memberAttribute.getPattern())) {
			try {
				Pattern.compile(memberAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		memberAttribute.remove("type", "property_index");
		memberAttributeService.update(memberAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member_attribute/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", memberAttributeService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/member_attribute/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		memberAttributeService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}