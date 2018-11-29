package com.jfinalshop.controller.admin;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.service.ProductNotifyService;

/**
 * Controller - 到货通知
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/product_notify")
public class ProductNotifyController extends BaseController {

	@Inject
	private ProductNotifyService productNotifyService;

	/**
	 * 发送到货通知
	 */
	public void send() {
		Long[] ids = getParaValuesToLong("ids");
		List<ProductNotify> productNotifies = productNotifyService.findList(ids);
		int count = productNotifyService.send(productNotifies);
		renderJson(Message.success("admin.productNotify.sentSuccess", count));
	}

	/**
	 * 列表
	 */
	public void list() {
		Boolean isMarketable = getParaToBoolean("isMarketable", false);
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock", false);
		Boolean hasSent = getParaToBoolean("hasSent", false);
		Pageable pageable = getBean(Pageable.class);
		setAttr("isMarketable", isMarketable);
		setAttr("isOutOfStock", isOutOfStock);
		setAttr("hasSent", hasSent);
		setAttr("pageable", pageable);
		setAttr("page", productNotifyService.findPage(null, isMarketable, isOutOfStock, hasSent, pageable));
		render("/admin/product_notify/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete(Long[] ids) {
		productNotifyService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}