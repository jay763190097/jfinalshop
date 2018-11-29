package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.UUID;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 会员
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/member")
public class MemberController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	private PluginService pluginService;

	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername() {
		String username = getPara("member.username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		renderJson(!memberService.usernameDisabled(username) && !memberService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否唯一
	 */
	public void checkEmail() {
		String previousEmail = getPara("previousEmail");
		String email = getPara("member.email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
			return;
		}
		renderJson(memberService.emailUnique(previousEmail, email));
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Member member = memberService.find(id);
		setAttr("genders", Member.Gender.values());
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		setAttr("member", member);
		setAttr("loginPlugin", pluginService.getLoginPlugin(member.getLoginPluginId()));
		render("/admin/member/view.ftl");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("genders", Member.Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		render("/admin/member/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Member member = getModel(Member.class);
		Long memberRankId = getParaToLong("memberRankId");
		member.setMemberRankId(memberRankService.find(memberRankId).getId());
		
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		member.setIsEnabled(isEnabled);
		
		Setting setting = SystemUtils.getSetting();
		if (member.getUsername().length() < setting.getUsernameMinLength() || member.getUsername().length() > setting.getUsernameMaxLength()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (member.getPassword().length() < setting.getPasswordMinLength() || member.getPassword().length() > setting.getPasswordMaxLength()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (memberService.usernameDisabled(member.getUsername()) || memberService.usernameExists(member.getUsername())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!setting.getIsDuplicateEmail() && memberService.emailExists(member.getEmail())) {
			redirect(ERROR_VIEW);
			return;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getRequest().getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				redirect(ERROR_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			if (StrKit.notNull(memberAttributeValue)) {
				member.setAttributeValue(memberAttribute, memberAttributeValue);
			}
		}
		member.setPassword(DigestUtils.md5Hex(member.getPassword()));
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(getRequest().getRemoteAddr());
		member.setLoginIp(null);
		member.setLoginDate(null);
		member.setLoginPluginId(null);
		member.setOpenId(null);
		member.setLockKey(null);
		member.setCart(null);
		member.setOrders(null);
		member.setPaymentLogs(null);
		member.setDepositLogs(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setFavoriteGoods(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		member.setPointLogs(null);
		member.setUsername(StringUtils.lowerCase(member.getUsername()));
		member.setEmail(StringUtils.lowerCase(member.getEmail()));
		member.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		memberService.save(member);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Member member = memberService.find(id);
		setAttr("genders", Member.Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		setAttr("member", member);
		setAttr("loginPlugin", pluginService.getLoginPlugin(member.getLoginPluginId()));
		render("/admin/member/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Member member = getModel(Member.class);
		Long memberRankId = getParaToLong("memberRankId");

		member.setMemberRank(memberRankService.find(memberRankId));
		
		Setting setting = SystemUtils.getSetting();
		if (member.getPassword() != null && (member.getPassword().length() < setting.getPasswordMinLength() || member.getPassword().length() > setting.getPasswordMaxLength())) {
			redirect(ERROR_VIEW);
			return;
		}
		Member pMember = memberService.find(member.getId());
		if (pMember == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!setting.getIsDuplicateEmail() && !memberService.emailUnique(pMember.getEmail(), member.getEmail())) {
			redirect(ERROR_VIEW);
			return;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getRequest().getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				redirect(ERROR_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			if (StrKit.notNull(memberAttributeValue)) {
				member.setAttributeValue(memberAttribute, memberAttributeValue);
			}
		}
		if (StringUtils.isEmpty(member.getPassword())) {
			member.setPassword(pMember.getPassword());
		} else {
			member.setPassword(DigestUtils.md5Hex(member.getPassword()));
		}
		if (pMember.getIsLocked() && !member.getIsLocked()) {
			member.setLoginFailureCount(0);
			member.setLockedDate(null);
		} else {
			member.setIsLocked(pMember.getIsLocked());
			member.setLoginFailureCount(pMember.getLoginFailureCount());
			member.setLockedDate(pMember.getLockedDate());
		}
		member.setEmail(StringUtils.lowerCase(member.getEmail()));
		member.remove("username");
		memberService.update(member);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/member/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findAll());
		setAttr("pageable", pageable);
		setAttr("page", memberService.findPage(pageable));
		render("/admin/member/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Member member = memberService.find(id);
				if (member != null && member.getBalance().compareTo(BigDecimal.ZERO) > 0) {
					renderJson(Message.error("admin.member.deleteExistDepositNotAllowed", member.getUsername()));
					return;
				}
			}
			memberService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}