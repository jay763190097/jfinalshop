package com.jfinalshop.controller.admin;

import java.util.ArrayList;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ShippingMethodService;

/**
 * Controller - 配送方式
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/shipping_method")
public class ShippingMethodController extends BaseController {

	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private DeliveryCorpService deliveryCorpService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("paymentMethods", paymentMethodService.findAll());
		render("/admin/shipping_method/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		ShippingMethod shippingMethod = getModel(ShippingMethod.class);
		Long defaultDeliveryCorpId = getParaToLong("defaultDeliveryCorpId");
		Long[] paymentMethodIds = getParaValuesToLong("paymentMethodIds");
		
		shippingMethod.setDefaultDeliveryCorpId(deliveryCorpService.find(defaultDeliveryCorpId).getId());
		shippingMethod.setPaymentMethods(new ArrayList<PaymentMethod>(paymentMethodService.findList(paymentMethodIds)));
		
		shippingMethod.setFreightConfigs(null);
		shippingMethod.setOrder(null);
		shippingMethodService.save(shippingMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/shipping_method/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethod", shippingMethodService.find(id));
		render("/admin/shipping_method/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		ShippingMethod shippingMethod = getModel(ShippingMethod.class);
		Long defaultDeliveryCorpId = getParaToLong("defaultDeliveryCorpId");
		Long[] paymentMethodIds = getParaValuesToLong("paymentMethodIds");
		
		shippingMethod.setDefaultDeliveryCorpId(deliveryCorpService.find(defaultDeliveryCorpId).getId());
		shippingMethod.setPaymentMethods(new ArrayList<PaymentMethod>(paymentMethodService.findList(paymentMethodIds)));
		
		shippingMethodService.update(shippingMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/shipping_method/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", shippingMethodService.findPage(pageable));
		render("/admin/shipping_method/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length >= shippingMethodService.count()) {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
			return;
		}
		shippingMethodService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}