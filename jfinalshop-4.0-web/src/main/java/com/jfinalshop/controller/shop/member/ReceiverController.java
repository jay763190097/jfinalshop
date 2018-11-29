package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReceiverService;

/**
 * Controller - 会员中心 - 收货地址
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/receiver")
@Before(MemberInterceptor.class)
public class ReceiverController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private AreaService areaService;
	@Inject
	private ReceiverService receiverService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", receiverService.findPage(member, pageable));
		setAttr("member", member);
		render("/shop/${theme}/member/receiver/list.ftl");
	}

	/**
	 * 添加
	 */
	public void add() {
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			addFlashMessage(Message.warn("shop.member.receiver.addCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			redirect("/shop/${theme}/member/receiver/list.jhtml");
		}
		render("/shop/${theme}/member/receiver/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		
		Area area = areaService.find(areaId);
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}

		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			redirect(ERROR_VIEW);
			return;
		}
		
		receiver.setMemberId(member.getId());
		receiverService.save(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/shop/${theme}/member/receiver/list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(receiver.getMember())) {
			redirect(ERROR_VIEW);
			return;
		}
		setAttr("receiver", receiver);
		render("/shop/${theme}/member/receiver/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Receiver receiver = getModel(Receiver.class);
		//Long id = getParaToLong("id");
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		
		Area area = areaService.find(areaId);
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}
		
		Receiver pReceiver = receiverService.find(receiver.getId());
		if (pReceiver == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(pReceiver.getMember())) {
			redirect(ERROR_VIEW);
			return;
		}
		receiver.setMemberId(pReceiver.getMemberId());
		receiverService.update(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/shop/${theme}/member/receiver/list.jhtml");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(receiver.getMember())) {
			redirect(ERROR_VIEW);
			return;
		}
		receiverService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}