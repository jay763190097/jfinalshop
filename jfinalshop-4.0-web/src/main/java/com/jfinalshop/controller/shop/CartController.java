package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.CartItemService;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 购物车
 * 
 * 
 */
@ControllerBind(controllerKey = "/cart")
@Before(ThemeInterceptor.class)
public class CartController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private ProductService productService;
	@Inject
	private CartService cartService;
	@Inject
	private CartItemService cartItemService;

	/**
	 * 数量
	 */
	public void quantity() {
		String channel= PropKit.get("channelcode");
		Map<String, Integer> data = new HashMap<String, Integer>();
		Cart cart = cartService.getCurrent(channel);
		data.put("quantity", cart != null ? cart.getProductQuantity() : 0);
		renderJson(data);
	}

	/**
	 * 添加
	 */
	public void add() {
		String channel= PropKit.get("channelcode");
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		if (quantity == null || quantity < 1) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Product product = productService.find(productId);
		if (product == null) {
			renderJson(Message.warn("shop.cart.productNotExist"));
			return;
		}
		if (!Goods.Type.general.equals(product.getType())) {
			renderJson(Message.warn("shop.cart.productNotForSale"));
			return;
		}
		if (!product.getIsMarketable()) {
			renderJson(Message.warn("shop.cart.productNotMarketable"));
			return;
		}
		Cart cart = cartService.getCurrent(channel);
		if (cart != null) {
			if (cart.contains(product)) {
				CartItem cartItem = cart.getCartItem(product);
				if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
					renderJson(Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
					return;
				}
				if (cartItem.getQuantity() + quantity > product.getAvailableStock()) {
					renderJson(Message.warn("shop.cart.productLowStock"));
					return;
				}
			} else {
				if (Cart.MAX_CART_ITEM_COUNT != null && cart.getCartItems().size() >= Cart.MAX_CART_ITEM_COUNT) {
					renderJson(Message.warn("shop.cart.addCartItemCountNotAllowed", Cart.MAX_CART_ITEM_COUNT));
					return;
				}
				if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
					renderJson(Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
					return;
				}
				if (quantity > product.getAvailableStock()) {
					renderJson(Message.warn("shop.cart.productLowStock"));
					return;
				}
			}
		} else {
			if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
				renderJson(Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
				return;
			}
			if (quantity > product.getAvailableStock()) {
				renderJson(Message.warn("shop.cart.productLowStock"));
				return;
			}
		}
		cart = cartService.add(product, quantity, channel,false);

		Member member = memberService.getCurrent();
		if (member == null) {
			WebUtils.addCookie(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME, cart.getCartKey(), Cart.TIMEOUT);
		}
		renderJson(Message.success("shop.cart.addSuccess", cart.getProductQuantity(), currency(cart.getEffectivePrice(), true, false)));
	}

	/**
	 * 列表
	 */
	public void list() {
		String channel= PropKit.get("channelcode");
		setAttr("cart", cartService.getCurrent(channel));
		render("/shop/${theme}/cart/list.ftl");
	}
	/**
	 * 编辑
	 */
	public void edit() {
		String channel= PropKit.get("channelcode");
		Long id = getParaToLong("id");
		Integer quantity = getParaToInt("quantity");
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			renderJson(data);
			return;
		}
		CartItem cartItem = cartItemService.find(id);
		if (!cart.contains(cartItem)) {
			data.put("message", Message.error("shop.cart.cartItemNotExist"));
			renderJson(data);
			return;
		}
		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			data.put("message", Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
			renderJson(data);
			return;
		}
		Product product = cartItem.getProduct();
		if (quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.cart.productLowStock"));
			renderJson(data);
			return;
		}
		cartItem.setQuantity(quantity);
		cartItemService.update(cartItem);
		cart = cartService.getCurrent(channel);

		data.put("message", SUCCESS_MESSAGE);
		data.put("subtotal", cartItem.getSubtotal());
		data.put("isLowStock", cartItem.getIsLowStock());
		data.put("quantity", cart.getProductQuantity());
		data.put("effectiveRewardPoint", cart.getEffectiveRewardPoint());
		data.put("effectivePrice", cart.getEffectivePrice());
		data.put("giftNames", cart.getGiftNames());
		data.put("promotionNames", cart.getPromotionNames());
		renderJson(data);
	}

	/**
	 * 删除
	 */
	public void delete() {
		String channel= PropKit.get("channelcode");
		Long id = getParaToLong("id");
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent(channel);
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			renderJson(data);
			return;
		}
		CartItem cartItem = cartItemService.find(id);
		if (!cart.contains(cartItem)) {
			data.put("message", Message.error("shop.cart.cartItemNotExist"));
			renderJson(data);
			return;
		}
		cartItemService.delete(cartItem);
		cart.getCartItems().remove(cartItem);

		data.put("message", SUCCESS_MESSAGE);
		data.put("isLowStock", cart.getIsLowStock());
		data.put("quantity", cart.getProductQuantity());
		data.put("effectiveRewardPoint", cart.getEffectiveRewardPoint());
		data.put("effectivePrice", cart.getEffectivePrice());
		data.put("giftNames", cart.getGiftNames());
		data.put("promotionNames", cart.getPromotionNames());
		renderJson(data);
	}

	/**
	 * 清空
	 */
	public void clear() {
		String channel= PropKit.get("channelcode");
		Cart cart = cartService.getCurrent(channel);
		cartService.delete(cart.getId());
		renderJson(SUCCESS_MESSAGE);
	}

}