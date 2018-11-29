package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 到货通知
 * 
 * 
 */
@ControllerBind(controllerKey = "/product_notify")
public class ProductNotifyController extends BaseController {

	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private MemberService memberService;
	@Inject
	private ProductService productService;

	/**
	 * 获取当前会员E-mail
	 */
	public void email() {
		Member member = memberService.getCurrent();
		String email = member != null ? member.getEmail() : null;
		Map<String, String> data = new HashMap<String, String>();
		data.put("email", email);
		renderJson(data);
	}

	/**
	 * 保存
	 */
	public void save() {
		String email = getPara("email");
		Long productId = getParaToLong("productId");
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", Message.warn("shop.productNotify.productNotExist"));
			renderJson(data);
			return;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("shop.productNotify.productNotMarketable"));
			renderJson(data);
			return;
		}
		if (!product.getIsOutOfStock()) {
			data.put("message", Message.warn("shop.productNotify.productInStock"));
			renderJson(data);
			return;
		}
		if (productNotifyService.exists(product, email)) {
			data.put("message", Message.warn("shop.productNotify.exist"));
			renderJson(data);
			return;
		}
		Member member = memberService.getCurrent();
		ProductNotify productNotify = new ProductNotify();
		productNotify.setEmail(email);
		productNotify.setHasSent(false);
		productNotify.setMemberId(member != null ? member.getId() : null);
		productNotify.setProductId(product.getId());
		productNotifyService.save(productNotify);
		data.put("message", SUCCESS_MESSAGE);
		renderJson(data);
	}

}