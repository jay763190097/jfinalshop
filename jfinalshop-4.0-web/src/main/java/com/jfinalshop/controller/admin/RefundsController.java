package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.RefundsService;

/**
 * Controller - 退款单
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/refunds")
public class RefundsController extends BaseController {

	@Inject
	private RefundsService refundsService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("refunds", refundsService.find(id));
		render("/admin/refunds/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", refundsService.findPage(pageable));
		render("/admin/refunds/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		refundsService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}