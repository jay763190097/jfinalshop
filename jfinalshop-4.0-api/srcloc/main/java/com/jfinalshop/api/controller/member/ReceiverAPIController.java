package com.jfinalshop.api.controller.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.ReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 会员中心 - 收货地址
 *
 */
@ControllerBind(controllerKey = "/api/receiver")
@Before(TokenInterceptor.class)
public class ReceiverAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(ReceiverAPIController.class);

	@Inject
	private ReceiverService receiverService;
	@Inject
	private AreaService areaService;
	
	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;
	
	/**
	 * 查找默认收货地址
	 */
	public void findDefault() {
		Member member = getMember();
		Receiver receiver = receiverService.findDefault(member);
		renderJson(new DatumResponse(receiver));
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = getMember();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Receiver> pages = receiverService.findPage(member, pageable);
		DataResponse dataResponse = null;
		if (pages != null) {
			dataResponse = new DataResponse(pages.getList());
		}
		renderJson(dataResponse);
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Receiver receiver = getModel(Receiver.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		if (receiver == null) {
			renderArgumentError("地址不能为空哟!");
			return;
		}
		receiver.setIsDefault(isDefault);
	/*	Area project = areaService.find(receiver.getAreaId());
		if (project == null) {
			renderArgumentError("小区不能为空哟!");
			return;
		}
		receiver.setAreaId(project.getId());
		receiver.setAreaName(project.getName());*/
		
		Member member = getMember();
		//Receiver receiver1 = receiverService.findDefault(member);
		member.setReceivers(null);
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			renderArgumentError("收货地址超过最大保存数【" + Receiver.MAX_RECEIVER_COUNT + "】");
			return;
		}
		
		receiver.setMemberId(member.getId());
		//receiver.setIsDefault(false);
		receiverService.save(receiver);
		renderJson(new BaseResponse(Code.SUCCESS, "保存成功!"));
	}
	
	
	/**
	 * 更新
	 */
	public void update() {
		Receiver receiver = getModel(Receiver.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		receiver.setIsDefault(isDefault);
		/*Area project = areaService.find(receiver.getAreaId());
		if (project != null) {
			receiver.setAreaId(project.getId());
			receiver.setAreaName(project.getName());
		}*/
		
		Receiver pReceiver = receiverService.find(receiver.getId());
		if (pReceiver == null) {
			renderArgumentError("地址没有找到哟!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(pReceiver.getMemberId())) {
			renderArgumentError("地址可能不是我哟!");
			return;
		}
		receiver.setMemberId(pReceiver.getMemberId());
		receiverService.update(receiver);
		renderJson(new BaseResponse(Code.SUCCESS, "更新成功!"));
	}
	
	
	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		
		if (receiver == null) {
			renderArgumentError("地址没有找到哟!");
			return;
		}
		if (receiver.getIsDefault()) {
			renderArgumentError("默认地址不能删除!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(receiver.getMemberId())) {
			renderArgumentError("地址可能不是我哟!");
			return;
		}
		receiverService.delete(id);
		renderJson(new BaseResponse(Code.SUCCESS, "删除成功!"));
	}
	
	
}
