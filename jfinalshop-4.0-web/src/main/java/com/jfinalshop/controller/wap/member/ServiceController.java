package com.jfinalshop.controller.wap.member;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderItemService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ReturnsItemService;
import com.jfinalshop.service.ReturnsService;
import com.jfinalshop.util.DateUtils;

/**
 * Controller - 会员中心 - 售后服务
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/service")
@Before(WapMemberInterceptor.class)
public class ServiceController extends BaseController {
	
	@Inject
	private ReturnsService returnsService;
	@Inject
	private ReturnsItemService returnsItemService;
	@Inject
	private OrderItemService orderItemService;
	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private DeliveryCorpService deliveryCorpService;
	
	
	/**
	 * 列表
	 */
	public void list() {
		Integer status = getParaToInt("status", 0);
		setAttr("status", status);
		setAttr("returnsItems", returnsItemService.findReturnsItems(memberService.getCurrent(), status));
		setAttr("title" , "会员中心 - 售后服务");
		render("/wap/member/service/list.ftl");
	}
	
	/**
	 * 申请售后
	 */
	public void alert_refund() {
		Long orderItemId = getParaToLong("orderItemId");
		setAttr("orderItem", orderItemService.find(orderItemId));
		setAttr("title" , "会员中心 - 申请售后");
		render("/wap/member/service/alert_refund.ftl");
	}
	
	/**
	 * 保存(售后申请)
	 */
	public void save() {
		Long id = getParaToLong("id");
		String amount = getPara("amount", "0");
		ReturnsItem returnsItem = getModel(ReturnsItem.class);
		
		OrderItem orderItem = orderItemService.find(id);
		Map<String, String> map = new HashMap<String, String>();
		if (orderItem == null || orderItem.getReturnableQuantity() <= 0) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "可退货数有误!");
			renderJson(map);
			return;
		}
		
		Member member = memberService.getCurrent();
		if (member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前用户不能是空!");
			renderJson(map);
			return;
		}
		returnsItem.setName(orderItem.getName());
		returnsItem.setSn(orderItem.getSn());
		returnsItem.setSpecifications(orderItem.getSpecifications());
		returnsItem.setAmount(new BigDecimal(amount));
		returnsItem.setQuantity(1);
		returnsItem.setMemberId(member.getId());
		returnsItem.setProductId(orderItem.getProductId());
		returnsItem.setStatus(ReturnsItem.Status.pendingReview.ordinal());
		
		List<ReturnsItem> returnsItems = new ArrayList<ReturnsItem>();
		returnsItems.add(returnsItem);
		Returns returns = new Returns();
		returns.setReturnsItems(returnsItems);
		
		orderService.returns(orderItem.getOrder(), returns, null);
		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, "申请成功!");
		map.put("referer", "/wap/member/service/view_detail.jhtml");
		renderJson(map);
	}
	
	/**
	 * 等待商家处理退货申请
	 */
	public void view_detail() {
		Long id = getParaToLong("id");
		
		setAttr("returnsItem", returnsItemService.find(id));
		setAttr("type", "return");
		setAttr("title" , "会员中心 - 提交成功");
		render("/wap/member/service/view_detail.ftl");
	}
	
	/**
	 * 取消售后申请
	 */
	public void cancel() {
		Long id = getParaToLong("id");
		
		Map<String, String> map = new HashMap<String, String>();
		ReturnsItem returnsItem = returnsItemService.find(id);
		if (returnsItem == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "退货项不存在!");
			renderJson(map);
			return;
		}
		
		if (returnsItem.getStatus() != ReturnsItem.Status.pendingReview.ordinal()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "只有【待审核】才能取消!");
			renderJson(map);
			return;
		}
		
		Member member = memberService.getCurrent();
		if (member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前用户不能是空!");
			renderJson(map);
			return;
		}
		returnsItem.setStatus(ReturnsItem.Status.canceled.ordinal());
		returnsItem.setMemberId(member.getId());
		returnsItem.setModifyDate(DateUtils.getSysDate());
		returnsItem.update();
		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, "取消成功!");
		renderJson(map);
	}
	
	
	/**
	 * 退货并填写退货信息
	 */ 
	public void return_info() {
		Long id = getParaToLong("id");
		Returns returns = returnsService.find(id);
		setAttr("title" , "会员中心 - 填写退货信息");
		setAttr("returns", returns);
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		render("/wap/member/service/return_info.ftl");
	}
	
	/**
	 * 退货物流信息保存
	 */
	public void save_info() {
		Returns returns = getModel(Returns.class);
		
		Map<String, String> map = new HashMap<String, String>();
		Returns pReturns = returnsService.find(returns.getId());
		if (pReturns == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "退货单不存在!");
			renderJson(map);
			return;
		}
		
		Member member = memberService.getCurrent();
		if (member == null) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "当前用户不能是空!");
			renderJson(map);
			return;
		}
		
		pReturns.setTrackingNo(returns.getTrackingNo());
		pReturns.setDeliveryCorp(returns.getDeliveryCorp());
		pReturns.setMemo(returns.getMemo());
		pReturns.setShipper(member.getUsername());
		pReturns.update();
		
		List<ReturnsItem> returnsItems = pReturns.getReturnsItems();
		if (CollectionUtils.isNotEmpty(returnsItems)) {
			for (ReturnsItem returnsItem : returnsItems) {
				returnsItem.setStatus(ReturnsItem.Status.returned.ordinal());
				returnsItem.update();
			}
		}
		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, "申请成功!");
		map.put("referer", "/wap/member/service/result.jhtml");
		renderJson(map);
	}
	
	/**
	 * 售后结果
	 */
	public void result() {
		setAttr("title" , "会员中心 - 退货申请提交成功");
		render("/wap/member/service/result.ftl");
	}
	
}
