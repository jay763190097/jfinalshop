package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
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
@ControllerBind(controllerKey = "/wap/member/receiver")
@Before(WapMemberInterceptor.class)
public class ReceiverController extends BaseController {
	
	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;
	
	@Inject
	private MemberService memberService;
	@Inject
	private ReceiverService receiverService;
	@Inject
	private AreaService areaService;
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pages", receiverService.findPage(member, pageable));
		setAttr("title" , "选择收货地址");
		render("/wap/member/receiver/list.ftl");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		String redirectUrl = getPara("url_forward", null);
		setSessionAttr("url_forward", redirectUrl);
		setAttr("title" , "添加收货地址 - 会员中心");
		render("/wap/member/receiver/add.ftl");
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Receiver receiver = getModel(Receiver.class);
		String redirectUrl = getSessionAttr("url_forward");
		
		Map<String, String> map = new HashMap<String, String>();
		Area area = areaService.find(receiver.getAreaId());
		if (area == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "地址不能为空!");
			renderJson(map);
			return;
		}
		receiver.setAreaId(area.getId());
		receiver.setAreaName(area.getFullName());
		
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "收货地址超过最大保存数【" + Receiver.MAX_RECEIVER_COUNT + "】");
			renderJson(map);
			return;
		}
		
		receiver.setMemberId(member.getId());
		receiver.setIsDefault(false);
		receiverService.save(receiver);
		map.put(STATUS, SUCCESS);
		if (StrKit.isBlank(redirectUrl)) {
			map.put("referer", "/wap/member/receiver/list.jhtml");
		} else {
			map.put("referer", redirectUrl);
		}
		renderJson(map);
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		String redirectUrl = getPara("url_forward", null);
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(receiver.getMember())) {
			return;
		}
		setSessionAttr("url_forward", redirectUrl);
		setAttr("receiver", receiver);
		setAttr("title" , "编辑收货地址 - 会员中心");
		render("/wap/member/receiver/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Receiver receiver = getModel(Receiver.class);
		String redirectUrl = getSessionAttr("url_forward");
		
		Area area = areaService.find(receiver.getAreaId());
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}
		
		Map<String, String> map = new HashMap<String, String>();
		Receiver pReceiver = receiverService.find(receiver.getId());
		if (pReceiver == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前地址为空!");
			renderJson(map);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(pReceiver.getMember())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "非当前用户!");
			renderJson(map);
			return;
		}
		receiver.setMemberId(pReceiver.getMemberId());
		receiver.setIsDefault(true);
		receiverService.update(receiver);
		map.put(STATUS, SUCCESS);
		if (StrKit.isBlank(redirectUrl)) {
			map.put("referer", "/wap/member/receiver/list.jhtml");
		} else {
			map.put("referer", redirectUrl);
		}
		renderJson(map);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);
		Map<String, String> map = new HashMap<String, String>();
		if (receiver == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前地址为空!");
			renderJson(map);
			return;
		}
		if (receiver.getIsDefault()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "默认地址不能删除!");
			renderJson(map);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(receiver.getMember())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "非当前用户!");
			renderJson(map);
			return;
		}
		String redirectUrl = getSessionAttr("url_forward");
		receiverService.delete(id);
		map.put(STATUS, SUCCESS);
		if (StrKit.isBlank(redirectUrl)) {
			map.put("referer", "/wap/member/receiver/list.jhtml");
		} else {
			map.put("referer", redirectUrl);
		}
		renderJson(map);
	}
	
	/**
	 * 地区
	 */
	@Before(CacheInterceptor.class) 
	@CacheName("wapArea")
	public void area() {
		String JSONDataArea = areaService.createJSONData();
		renderJson(JSONDataArea);
	}
	
}
