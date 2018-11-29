package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.ShippingService;

/**
 * Controller - 发货单
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/shipping")
public class ShippingController extends BaseController {

	@Inject
	private ShippingService shippingService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("shipping", shippingService.find(id));
		render("/admin/shipping/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", shippingService.findPage(pageable));
		render("/admin/shipping/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		shippingService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}