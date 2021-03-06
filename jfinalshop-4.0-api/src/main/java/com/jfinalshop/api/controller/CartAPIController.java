package com.jfinalshop.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.CartItemService;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 购物车
 *
 */
@ControllerBind(controllerKey = "/api/cart")
@Before(AccessInterceptor.class)
public class CartAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(CartAPIController.class);
	String channel = null;
	@Inject
	private ProductService productService;
	@Inject
	private CartService cartService;
	@Inject
	private CartItemService cartItemService;
	private Res res = I18n.use();
	
	/**
	 * 获取购物车总数
	 * 
	 */
	public void count() {
		channel = getPara("channel");
		String cartKey = getPara("cartKey");
		Cart cart = getCurrent(cartKey,channel);
		int quantity = cart != null ? cart.getQuantity() : 0;
		renderJson(new DatumResponse().setDatum(quantity));
	}
	
	/**
	 * 更新购物车数量
	 * 改变数量 加减按钮
	 * 
	 */
	public void setNums() {
		channel = getPara("channel");
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		String cartKey = getPara("cartKey");
		
		if (quantity == null || quantity < 1) {
			renderArgumentError("数量不能为空!");
			return;
		}
		
		Product product = productService.find(productId);
		if (product == null) {
			renderArgumentError(res.format("shop.cart.productNotExist"));
			return;
		}
		
		Cart cart = getCurrent(cartKey,channel);
		if (cart == null) {
			renderArgumentError(res.format("shop.cart.notEmpty"));
			return;
		}
		CartItem cartItem = cart.getCartItem(product);
		if (cartItem == null) {
			renderArgumentError(res.format("shop.cart.cartItemNotExist"));
			return;
		}
		cartItem.setQuantity(quantity);
		cartItem.update();
		renderJson(new BaseResponse(Code.SUCCESS, "修改成功!"));
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		channel = getPara("channel");
		String cartKey = getPara("cartKey");
		Long[] ids = convertToLong(StringUtils.split(getPara("ids"), ","));
		
		Cart cart = getCurrent(cartKey,channel);
		if (cart == null || cart.isEmpty()) {
			renderArgumentError(res.format("shop.cart.notEmpty"));
			return;
		}
		List<CartItem> cartItems = cartItemService.findList(ids);
		for (CartItem cartItem : cartItems) {
			if (!cart.contains(cartItem)) {
				renderArgumentError(res.format("shop.cart.cartItemNotExist"));
				return;
			}
			cartItemService.delete(cartItem);
			cart.getCartItems().remove(cartItem);
		}
		renderJson(new BaseResponse(Code.SUCCESS, "删除成功!"));
	}
	
	/**
	 * 列表
	 */
	public void list() {
		channel = getPara("channel");
		String cartKey = getPara("cartKey");
		Cart cart = getCurrent(cartKey,channel);
		DatumResponse datumResponse = new DatumResponse();
		if (cart != null ) {
			List<CartItem> cartItems = cart.getCartItems();
			if (CollectionUtils.isNotEmpty(cartItems)) {
				List<CartItem> pCartItems = new ArrayList<CartItem>();
				for (CartItem cartItem : cartItems) {
					Goods goods = cartItem.getProduct().getGoods();
					Product product = cartItem.getProduct();
					cartItem.put("name", goods.getName());
					cartItem.put("image", goods.getImage());
					cartItem.put("price", new BigDecimal(currency(cartItem.getProduct().getPrice(), false, false)));
					cartItem.put("unit", goods.getUnit());
					cartItem.put("weight", goods.getWeight());
					cartItem.put("brand", goods.getBrand() != null ? goods.getBrand().getName() : null);
					cartItem.put("product",product);
					pCartItems.add(cartItem);
				}
				cart.put("cartItems", pCartItems);
				datumResponse.setDatum(cart);
			}
		}
		renderJson(datumResponse);
	}
	
	/**
	 * 添加
	 */
	public void add() {
		channel = getPara("channel");
		Long productId = getParaToLong("productId");
		Integer quantity = getParaToInt("quantity");
		String cartKey = getPara("cartKey");
		channel = getPara("channel");
		if (quantity == null || quantity < 1) {
			renderArgumentError("数量不能为空哟!");
			return;
		}
		Product product = productService.find(productId);
		if (product == null) {
			renderArgumentError(res.format("shop.cart.productNotExist"));
			return;
		}
		if ((Goods.Type.exchange.equals(product.getType())) || (Goods.Type.gift.equals(product.getType()))) {
			renderArgumentError(res.format("shop.cart.productNotForSale"));
			return;
		}
		if (!product.getIsMarketable()) {
			renderArgumentError(res.format("shop.cart.productNotMarketable"));
			return;
		}

		Cart cart = getCurrent(cartKey,channel);
		if (cart != null) {
			if (cart.contains(product)) {
				CartItem cartItem = cart.getCartItem(product);
				if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
					renderArgumentError(res.format("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
					return;
				}
				if (cartItem.getQuantity() + quantity > product.getAvailableStock()) {
					renderArgumentError(res.format("shop.cart.productLowStock"));
					return;
				}
			} else {
				if (Cart.MAX_CART_ITEM_COUNT != null && cart.getCartItems().size() >= Cart.MAX_CART_ITEM_COUNT) {
					renderArgumentError(res.format("shop.cart.addCartItemCountNotAllowed", Cart.MAX_CART_ITEM_COUNT));
					return;
				}
				if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
					renderArgumentError(res.format("shop.cart.addCartItemCountNotAllowed", CartItem.MAX_QUANTITY));
					return;
				}
				if (quantity > product.getAvailableStock()) {
					renderArgumentError(res.format("shop.cart.productLowStock"));
					return;
				}
			}
		} else {
			if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
				renderArgumentError(res.format("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
				return;
			}
			if (quantity > product.getAvailableStock()) {
				renderArgumentError(res.format("shop.cart.productLowStock"));
				return;
			}
		}
		cart = add(product, quantity, cartKey,channel);
		renderJson(new DatumResponse(cart));
	}
	
	
	/**
	 * 添加商品至当前购物车
	 * 
	 * @param product
	 *            商品
	 * @param quantity
	 *            数量
	 * @return 当前购物车
	 */
	private Cart add(Product product, int quantity, String cartKey,String channel) {
		Assert.notNull(product);
		Assert.state(quantity > 0);
		Cart cart = getCurrent(cartKey,channel);
		if (cart == null) {
			cart = new Cart();
			Member member = getMember();
			if (member != null && member.getCart(channel) == null) {
				cart.setMemberId(member.getId());
			}
			cart.setChannel(channel);
			cart.setCartKey((DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30))));
			cart.setExpire(DateUtils.addSeconds(new Date(), Cart.TIMEOUT));
			cartService.save(cart);
		}
		
		if (cart.contains(product)) {
			CartItem cartItem = cart.getCartItem(product);
			cartItem.add(quantity);
			cartItemService.update(cartItem);
		} else {
			CartItem cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setProductId(product.getId());
			cartItem.setCartId(cart.getId());
			cartItemService.save(cartItem);
		}
		return cart;
	}
	//通过cartKey获取购物车
	public void getCartByKey() {
		String cartKey = getPara("cartKey");
		Cart cart = cartService.getCartByKey(cartKey,channel);
		renderJson(new DatumResponse(cart));
	}
	//通过商户id获取cartKey
    public void getCartKey() {
		String member_id = getPara("member_id");
		Cart cart = cartService.getCartKey(member_id);
		if(cart!=null){
			Cart cart1 = new Cart();
			cart1.setCartKey(cart.getCartKey());
		//String cartKey = cart.getCartKey();
		renderJson(new DatumResponse(cart1));
		}else{
			renderArgumentError("该商户购物车为空");
		}
		
	}
   //获取当前购物车
  	public void getCurrent() {
		channel = getPara("channel");
  		//String cartKey = getPara("cartKey");
  		Cart cart = cartService.getCurrent(channel);
  		renderJson(new DatumResponse(cart));
  	}
  	//合并用户购物车
  	public void userCart(){
		channel = getPara("channel");
  		String cartKey = getPara("cartKey");
  		Cart cart = getCurrent(cartKey,channel);
  		Member merber = getMember();
  		cartService.merge(merber,cart,channel);
  		String member_id = merber.getId().toString();
  		Cart mercart = cartService.getCartKey(member_id);
  		renderJson(new DatumResponse(mercart));
  	}
}
