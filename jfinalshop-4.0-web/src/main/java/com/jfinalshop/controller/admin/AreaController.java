package com.jfinalshop.controller.admin;

import java.util.ArrayList;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinalshop.Message;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;

/**
 * Controller - 地区
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/area")
public class AreaController extends BaseController {

	@Inject
	private AreaService areaService;

	/**
	 * 添加
	 */
	public void add() {
		Long parentId = getParaToLong("parentId");
		setAttr("parent", areaService.find(parentId));
		render("/admin/area/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(EvictInterceptor.class) 
	@CacheName("wapArea")
	public void save() {
		Area area = getModel(Area.class);
		Long parentId = getParaToLong("parentId");
		Area pArea = areaService.find(parentId);
		if (pArea != null) {
			area.setParentId(pArea.getId());
		} else {
			area.setParentId(null);
		}
		
		area.setFullName(null);
		area.setTreePath(null);
		area.setGrade(null);
		area.setChildren(null);
		area.setMembers(null);
		area.setReceivers(null);
		area.setOrder(null);
		area.setDeliveryCenters(null);
		area.setFreightConfigs(null);
		areaService.save(area);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("area", areaService.find(id));
		render("/admin/area/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(EvictInterceptor.class) 
	@CacheName("wapArea")
	public void update() {
		Area area = getModel(Area.class);
		area.remove("full_name", "tree_path", "grade", "parent_id");
		areaService.update(area);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Long parentId = getParaToLong("parentId");
		Area parent = areaService.find(parentId);
		if (parent != null) {
			setAttr("parent", parent);
			setAttr("areas", new ArrayList<Area>(parent.getChildren()));
		} else {
			setAttr("areas", areaService.findRoots());
		}
		render("/admin/area/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Area area = areaService.find(id);
		if (CollectionUtils.isNotEmpty(area.getChildren())) {
			renderJson(new Message(Message.Type.error, "存在下级地区"));
			return;
		}
		areaService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}