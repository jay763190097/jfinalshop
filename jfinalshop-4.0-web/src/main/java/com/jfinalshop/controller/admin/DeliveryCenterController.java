package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.DeliveryCenterService;

/**
 * Controller - 发货点
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/delivery_center")
public class DeliveryCenterController extends BaseController {

	@Inject
	private DeliveryCenterService deliveryCenterService;
	@Inject
	private AreaService areaService;

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/delivery_center/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		DeliveryCenter deliveryCenter = getModel(DeliveryCenter.class);
		Long areaId = getParaToLong("areaId");
		deliveryCenter.setArea(areaService.find(areaId));
		
		Boolean isDefault = StringUtils.equals(getPara("isDefault", ""), "on") ? true : false;
		deliveryCenter.setIsDefault(isDefault);
		
		deliveryCenter.setAreaName(null);
		deliveryCenterService.save(deliveryCenter);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCenter", deliveryCenterService.find(id));
		render("/admin/delivery_center/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		DeliveryCenter deliveryCenter = getModel(DeliveryCenter.class);
		Long areaId = getParaToLong("areaId");
		deliveryCenter.setArea(areaService.find(areaId));
		
		Boolean isDefault = StringUtils.equals(getPara("isDefault", ""), "on") ? true : false;
		deliveryCenter.setIsDefault(isDefault);
		
		deliveryCenter.remove("area_name");
		deliveryCenterService.update(deliveryCenter);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", deliveryCenterService.findPage(pageable));
		render("/admin/delivery_center/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		deliveryCenterService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}