package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.service.PaymentMethodService;

/**
 * Controller - 支付方式
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/payment_method")
public class PaymentMethodController extends BaseController {

	@Inject
	private PaymentMethodService paymentMethodService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", PaymentMethod.Type.values());
		setAttr("methods", PaymentMethod.Method.values());
		render("/admin/payment_method/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		PaymentMethod paymentMethod = getModel(PaymentMethod.class);
		
		String methodName = getPara("method", null);
		PaymentMethod.Method method = StrKit.notBlank(methodName) ? PaymentMethod.Method.valueOf(methodName) : null;
		
		String typeName = getPara("type", null);
		PaymentMethod.Type type = StrKit.notBlank(typeName) ? PaymentMethod.Type.valueOf(typeName) : null;
		
		paymentMethod.setMethod(method.ordinal());
		paymentMethod.setType(type.ordinal());
		paymentMethod.setShippingMethods(null);
		paymentMethod.setOrders(null);
		paymentMethodService.save(paymentMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/payment_method/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", PaymentMethod.Type.values());
		setAttr("methods", PaymentMethod.Method.values());
		setAttr("paymentMethod", paymentMethodService.find(id));
		render("/admin/payment_method/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		PaymentMethod paymentMethod = getModel(PaymentMethod.class);
		
		String methodName = getPara("method", null);
		PaymentMethod.Method method = StrKit.notBlank(methodName) ? PaymentMethod.Method.valueOf(methodName) : null;
		
		String typeName = getPara("type", null);
		PaymentMethod.Type type = StrKit.notBlank(typeName) ? PaymentMethod.Type.valueOf(typeName) : null;
		
		paymentMethod.setMethod(method.ordinal());
		paymentMethod.setType(type.ordinal());
		paymentMethodService.update(paymentMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/payment_method/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", paymentMethodService.findPage(pageable));
		render("/admin/payment_method/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length >= paymentMethodService.count()) {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
			return;
		}
		paymentMethodService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}