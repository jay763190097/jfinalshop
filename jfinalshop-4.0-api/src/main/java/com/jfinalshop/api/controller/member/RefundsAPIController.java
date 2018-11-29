package com.jfinalshop.api.controller.member;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.service.RefundsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 退款
 * 
 */
@ControllerBind(controllerKey = "/api/member/refunds")
@Before(TokenInterceptor.class)
public class RefundsAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(RefundsAPIController.class);

	@Inject
	private RefundsService refundsService;
	private Res res = I18n.use();
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Member member = getMember();
		if (member == null) {
			renderArgumentError("当前用户不能为空!");
			return;
		}
		Page<Refunds> pages = refundsService.findPage(member, pageable);
		List<Refunds> refunds = pages.getList();
		for (Refunds refund : refunds) {
			Order order = refund.getOrder();
			order.put("order_items", convertOrderItem(order.getOrderItems()));
			order.put("status_name", res.format("Order.Status." + order.getStatusName()));
			refund.put("order", order);
		}
		renderJson(new DatumResponse(pages));
	}
	
	/**
	 * 详情
	 */
	public void view() {
		Long id = getParaToLong("id");
		if (id == null) {
			renderArgumentError("退款单Id不能为空!");
			return;
		}
		Refunds refunds = refundsService.find(id);
		if (refunds == null) {
			renderArgumentError("退款单不存在!");
			return;
		}
		Order order = refunds.getOrder();
		refunds.put("status_name", res.format("Order.Status." + order.getStatusName()));
		renderJson(new DatumResponse(refunds));
	}
	
}
