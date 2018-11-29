package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PointLogService;

/**
 * Controller - 积分
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/point")
public class PointController extends BaseController {

	@Inject
	private PointLogService pointLogService;
	@Inject
	private MemberService memberService;
	@Inject
	private AdminService adminService;

	/**
	 * 检查会员
	 */
	public void checkMember() {
		String username = getPara("username");
		Map<String, Object> data = new HashMap<String, Object>();
		Member member = memberService.findByUsername(username);
		if (member == null) {
			data.put("message", Message.warn("admin.point.memberNotExist"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("point", member.getPoint());
		renderJson(data);
	}

	/**
	 * 调整
	 */
	public void adjust() {
		render("/admin/point/adjust.ftl");
	}

	/**
	 * 调整
	 */
	public void adjustSubmit() {
		String username = getPara("username");
		Long amount = getParaToLong("amount");
		String memo = getPara("memo");
		
		Member member = memberService.findByUsername(username);
		if (member == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (amount == 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (member.getPoint() == null || member.getPoint() + amount < 0) {
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		memberService.addPoint(member, amount, PointLog.Type.adjustment, admin, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/point/log.jhtml");
	}

	/**
	 * 记录
	 */
	public void log() {
		Long memberId = getParaToLong("memberId");
		Pageable pageable = getBean(Pageable.class);
		
		Member member = memberService.find(memberId);
		if (member != null) {
			setAttr("member", member);
			setAttr("page", pointLogService.findPage(member, pageable));
		} else {
			setAttr("page", pointLogService.findPage(pageable));
		}
		render("/admin/point/log.ftl");
	}

}