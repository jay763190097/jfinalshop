package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.DepositLogService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 预存款
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/deposit")
public class DepositController extends BaseController {

	@Inject
	private DepositLogService depositLogService;
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
			data.put("message", Message.warn("admin.deposit.memberNotExist"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("balance", member.getBalance());
		renderJson(data);
	}

	/**
	 * 调整
	 */
	public void adjust() {
		render("/admin/deposit/adjust.ftl");
	}

	/**
	 * 调整
	 */
	public void adjustSubmit() {
		String username = getPara("username");
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		String memo = getPara("memo");
		Member member = memberService.findByUsername(username);
		if (member == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (member.getBalance() == null || member.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
			redirect(ERROR_VIEW);
			return;
		}
		Admin admin = adminService.getCurrent();
		memberService.addBalance(member, amount, DepositLog.Type.adjustment, admin, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("log.jhtml");
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
			setAttr("page", depositLogService.findPage(member, pageable));
		} else {
			setAttr("page", depositLogService.findPage(pageable));
		}
		setAttr("pageable", pageable);
		render("/admin/deposit/log.ftl");
	}

}