package com.jfinalshop.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.kit.StrKit;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.dao.CartDao;
import com.jfinalshop.dao.CartItemDao;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.WebUtils;

/**
 * Service - 购物车
 * 
 * 
 */
@Singleton
public class CartService extends BaseService<Cart> {

	/**
	 * 构造方法
	 */
	public CartService() {
		super(Cart.class);
	}
	
	@Inject
	private CartDao cartDao;
	@Inject
	private CartItemDao cartItemDao;
	@Inject
	private MemberService memberService;
	
	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车，若不存在则返回null
	 */
	public Cart
	getCurrent(String channel) {
		Cart cart;
		Member member = memberService.getCurrent(true);
		if (member != null) {
			Cart pCart = getRequestCart(channel);
			cart = member.getCart(channel);
			if (pCart != null && cart == null) {
				cart = pCart;
				cart.setMemberId(member.getId());
				cartDao.update(cart);
			}
		} else {
			cart = getRequestCart(channel);
		}
		if (cart != null) {
			Date expire = DateUtils.addSeconds(new Date(), Cart.TIMEOUT);
			if (!DateUtils.isSameDay(cart.getExpire(), expire)) {
				cart.setExpire(expire);
			}
		}
		return cart;
	}
	/**
	 * 分配对象
	 * 获取临时产生的购物车
	 * 
	 */
	public Cart getRequestCart(String channel) {
		CartDao cartDao = new CartDao();
		HttpServletRequest request = RequestContextHolder.currentRequestAttributes();
		if (request == null) {
			return null;
		}
		String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);
		return cartDao.findByKey(key,channel);
	}
	
	/**
	 * 改动 分配对象
	 * key获取临时产生的购物车
	 * 
	 */
	public Cart getCartByKey(String cartKey,String channel) {
		CartDao cartDao = new CartDao();
		if (StrKit.notBlank(cartKey)) {
			return cartDao.findByKey(cartKey,channel);
		}
		return null;
	}
	
	/**
	 * 新添的根据商户id查询cartKey
	 * 
	 * 
	 */
	public Cart getCartKey(String member_id) {
		CartDao cartDao = new CartDao();
		if (StrKit.notBlank(member_id)) {
			return cartDao.findByMerId(member_id);
		}
		return null;
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
	public Cart add(Product product, int quantity, String channel,Boolean buyNow) {
		Assert.notNull(product);
		Assert.state(quantity > 0);
		Cart cart = getCurrent(channel);
		if (cart == null) {
			cart = new Cart();
			Member member = memberService.getCurrent();
			if (member != null && member.getCart(channel) == null) {
				cart.setMemberId(member.getId());
			}
			cart.setChannel(channel);
			cart.setCartKey((DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30))));
			cart.setExpire(DateUtils.addSeconds(new Date(), Cart.TIMEOUT));
			cartDao.save(cart);
		}
		
		if (cart.contains(product)) {
			if (buyNow) {
				CartItem cartItem = cart.getCartItem(product);
				cartItem.setQuantity(quantity);
				cartItemDao.update(cartItem);
			} else {
				CartItem cartItem = cart.getCartItem(product);
				cartItem.add(quantity);
				cartItemDao.update(cartItem);
			}
		} else {
			CartItem cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setProductId(product.getId());
			cartItem.setCartId(cart.getId());
			cartItemDao.save(cartItem);
		}
		return cart;
	}

	/**
	 * 合并临时购物车至会员
	 * 
	 * @param member
	 *            会员
	 * @param cart
	 *            临时购物车
	 */
	public void merge(Member member, Cart cart,String channel) {

		if (member == null || cart == null || cart.getMember() != null) {
			return;
		}
		Cart memberCart = member.getCart(channel);
		if (memberCart != null) {
			if (cart.getCartItems() != null) {
				for (CartItem cartItem : cart.getCartItems()) {
					Product product = cartItem.getProduct();
					if (memberCart.contains(product)) {
						CartItem memberCartItem = memberCart.getCartItem(product);
						if (CartItem.MAX_QUANTITY != null && memberCartItem.getQuantity() + cartItem.getQuantity() > CartItem.MAX_QUANTITY) {
							continue;
						}
						memberCartItem.add(cartItem.getQuantity());
						memberCartItem.update();
					} else {
						if (Cart.MAX_CART_ITEM_COUNT != null && memberCart.getCartItems().size() >= Cart.MAX_CART_ITEM_COUNT) {
							continue;
						}
						if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() > CartItem.MAX_QUANTITY) {
							continue;
						}
						CartItem item = new CartItem();
						item.setQuantity(cartItem.getQuantity());
						item.setProductId(cartItem.getProduct().getId());
						item.setCartId(memberCart.getId());
						cartItemDao.save(item);
						memberCart.getCartItems().add(cartItem);
					}
				}
			}
			delete(cart.getId());
		} else {
			cart.setMemberId(member.getId());
			cart.update();
			member.setCart(cart);
		}
	}

	/**
	 * 清除过期购物车
	 */
	public void evictExpired() {
		while (true) {
			List<Cart> carts = cartDao.findList(true, 100);
			if (CollectionUtils.isNotEmpty(carts)) {
				for (Cart cart : carts) {
					//cartDao.remove(cart);
					delete(cart.getId());
				}
			}
			if (carts.size() < 100) {
				break;
			}
		}
	}

	public void delete(Long id) {
		cartItemDao.delete(id);
		super.delete(id);
	}
	
}