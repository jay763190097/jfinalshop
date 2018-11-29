package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ReturnsItemDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.ReturnsService;
import com.jfinalshop.util.DateUtils;

/**
 * Controller - 退货单
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/returns")
public class ReturnsController extends BaseController {

	@Inject
	private ReturnsService returnsService;
	@Inject
	private AdminService adminService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("returns", returnsService.find(id));
		render("/admin/returns/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", returnsService.findPage(pageable));
		render("/admin/returns/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		returnsService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 审核
	 */
	public void review() {
		Long id = getParaToLong("id");
		ReturnsItemDao returnsItemDao = new ReturnsItemDao();
		ReturnsItem returnsItem = returnsItemDao.find(id);
		returnsItem.setStatus(ReturnsItem.Status.completed.ordinal());
		returnsItem.update();
		Returns returns = returnsService.find(returnsItem.getReturnId());
		Admin admin = adminService.getCurrent();
		returns.setOperator(admin);
		returns.setModifyDate(DateUtils.getSysDate());
		returns.update();
		setAttr("returns", returns);
		render("/admin/returns/view.ftl");
	}
	
}