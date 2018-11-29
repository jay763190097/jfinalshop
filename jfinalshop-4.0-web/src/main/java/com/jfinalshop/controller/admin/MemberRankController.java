package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.service.MemberRankService;

/**
 * Controller - 会员等级
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/member_rank")
public class MemberRankController extends BaseController {

	@Inject
	private MemberRankService memberRankService;

	/**
	 * 检查名称是否唯一
	 */
	public void checkName() {
		String previousName = getPara("previousName");
		String name = getPara("memberRank.name");
		if (StringUtils.isEmpty(name)) {
			renderJson(false);
			return;
		}
		renderJson(memberRankService.nameUnique(previousName, name));
	}

	/**
	 * 检查消费金额是否唯一
	 */
	public void checkAmount() {
		BigDecimal previousAmount = new BigDecimal(getPara("previousAmount", "0"));  
		BigDecimal amount = new BigDecimal(getPara("memberRank.amount", "0"));    
		if (amount.compareTo(BigDecimal.ZERO) == -1) {
			renderJson(false);
			return;
		}
		renderJson(memberRankService.amountUnique(previousAmount, amount));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/member_rank/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		MemberRank memberRank = getModel(MemberRank.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		Boolean isSpecial = getParaToBoolean("isSpecial", false);
		memberRank.setIsDefault(isDefault);
		memberRank.setIsSpecial(isSpecial);
		if (memberRankService.nameExists(memberRank.getName())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || memberRankService.amountExists(memberRank.getAmount())) {
			redirect(ERROR_VIEW);
			return;
		}
		memberRank.setMembers(null);
		memberRank.setPromotions(null);
		memberRankService.save(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member_rank/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberRank", memberRankService.find(id));
		render("/admin/member_rank/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		MemberRank memberRank = getModel(MemberRank.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		Boolean isSpecial = getParaToBoolean("isSpecial", false);
		memberRank.setIsDefault(isDefault);
		memberRank.setIsSpecial(isSpecial);
		MemberRank pMemberRank = memberRankService.find(memberRank.getId());
		if (pMemberRank == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!memberRankService.nameUnique(pMemberRank.getName(), memberRank.getName())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (pMemberRank.getIsDefault()) {
			memberRank.setIsDefault(true);
		}
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || !memberRankService.amountUnique(pMemberRank.getAmount(), memberRank.getAmount())) {
			redirect(ERROR_VIEW);
			return;
		}
		memberRank.remove("members", "promotions");
		memberRankService.update(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member_rank/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", memberRankService.findPage(pageable));
		render("/admin/member_rank/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				MemberRank memberRank = memberRankService.find(id);
				if (memberRank != null && memberRank.getMembers() != null && !memberRank.getMembers().isEmpty()) {
					renderJson(Message.error("admin.memberRank.deleteExistNotAllowed", memberRank.getName()));
					return;
				}
			}
			long totalCount = memberRankService.count();
			if (ids.length >= totalCount) {
				renderJson(Message.error("admin.common.deleteAllNotAllowed"));
				return;
			}
			memberRankService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}