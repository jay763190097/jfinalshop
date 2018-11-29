package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Filter;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.CartItemDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.dao.PaymentDao;
import com.jfinalshop.dao.PaymentLogDao;
import com.jfinalshop.dao.RefundsDao;
import com.jfinalshop.dao.ReturnsDao;
import com.jfinalshop.dao.ReturnsItemDao;
import com.jfinalshop.dao.ShippingDao;
import com.jfinalshop.dao.ShippingItemDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.OrderLog;
import com.jfinalshop.model.Payment;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.Refunds;
import com.jfinalshop.model.Returns;
import com.jfinalshop.model.ReturnsItem;
import com.jfinalshop.model.Shipping;
import com.jfinalshop.model.ShippingItem;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 订单
 * 
 * 
 */
@Singleton
public class OrderService extends BaseService<Order> {

	/**
	 * 构造方法
	 */
	public OrderService() {
		super(Order.class);
	}
	
	@Inject
	private OrderDao orderDao;
	@Inject
	private OrderItemDao orderItemDao;
	@Inject
	private OrderLogDao orderLogDao;
	@Inject
	private CartItemDao cartItemDao;
	@Inject
	private SnDao snDao;
	@Inject
	private PaymentDao paymentDao;
	@Inject
	private PaymentLogDao paymentLogDao;
	@Inject
	private RefundsDao refundsDao;
	@Inject
	private ShippingDao shippingDao;
	@Inject
	private ShippingItemDao shippingItemDao;
	@Inject
	private ReturnsDao returnsDao;
	@Inject
	private ReturnsItemDao returnsItemDao;
	@Inject
	private MemberService memberService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private ProductService productService;
	@Inject
	private ShippingMethodService shippingMethodService;
	
	/**
	 * 根据编号查找订单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 订单，若不存在则返回null
	 */
	public Order findBySn(String sn) {
		return orderDao.findBySn(sn);
	}

	/**
	 * 查找订单
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<Order> findList(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
			List<com.jfinalshop.Order> orders) {
		return orderDao.findList(type, status, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, count, filters, orders);
	}


	public List<Order> findListbyChannel(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
								List<com.jfinalshop.Order> orders,String channel) {
		return orderDao.findListbyChannel(type, status, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, count, filters, orders,channel);
	}
	/**
	 * 查找订单分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Order.Type type, Order.Status status, Order.Source source, Goods.Type goodsType, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, Boolean deleteFlag) {
		return orderDao.findPage(type, status, source, goodsType, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable, deleteFlag);
	}
	public Page<Order> findPagebyChannel(Order.Type type, Order.Status status, Order.Source source, Goods.Type goodsType, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, Boolean deleteFlag,String channel) {
		return orderDao.findPage(type, status, source, goodsType, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable, deleteFlag,channel);
	}
	/**
	 * 查找订单分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPageH5(Order.Type type, Order.Status status, Order.Source source, Goods.Type goodsType, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, Boolean deleteFlag, String channel) {
		return orderDao.findPage(type, status, source, goodsType, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable, deleteFlag, channel);
	}
	
	/**
	 * 查询订单数量
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @return 订单数量
	 */
	public Long count(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		return orderDao.count(type, status, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired);
	}

	/**
	 * 计算税金
	 * 
	 * @param price
	 *            商品价格
	 * @param promotionDiscount
	 *            促销折扣
	 * @param couponDiscount
	 *            优惠券折扣
	 * @param offsetAmount
	 *            调整金额
	 * @return 税金
	 */
	public BigDecimal calculateTax(BigDecimal price, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsTaxPriceEnabled()) {
			return BigDecimal.ZERO;
		}
		BigDecimal amount = price;
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		BigDecimal tax = amount.multiply(new BigDecimal(String.valueOf(setting.getTaxRate())));
		return tax.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(tax) : BigDecimal.ZERO;
	}

	/**
	 * 计算税金
	 * 
	 * @param order
	 *            订单
	 * @return 税金
	 */
	public BigDecimal calculateTax(Order order) {
		Assert.notNull(order);

		if (order.getInvoice() == null) {
			return BigDecimal.ZERO;
		}
		return calculateTax(order.getPrice(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	/**
	 * 计算订单金额
	 * 
	 * @param price
	 *            商品价格
	 * @param fee
	 *            支付手续费
	 * @param freight
	 *            运费
	 * @param tax
	 *            税金
	 * @param promotionDiscount
	 *            促销折扣
	 * @param couponDiscount
	 *            优惠券折扣
	 * @param offsetAmount
	 *            调整金额
	 * @return 订单金额
	 */
	public BigDecimal calculateAmount(BigDecimal price, BigDecimal fee, BigDecimal freight, BigDecimal tax, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(fee == null || fee.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(freight == null || freight.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(tax == null || tax.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		BigDecimal amount = price;
		if (fee != null) {
			amount = amount.add(fee);
		}
		if (freight != null) {
			amount = amount.add(freight);
		}
		if (tax != null) {
			amount = amount.add(tax);
		}
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		return amount.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(amount) : BigDecimal.ZERO;
	}

	/**
	 * 计算订单金额
	 * 
	 * @param order
	 *            订单
	 * @return 订单金额
	 */
	public BigDecimal calculateAmount(Order order) {
		Assert.notNull(order);

		return calculateAmount(order.getPrice(), order.getFee(), order.getFreight(), order.getTax(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	/**
	 * 判断订单是否锁定
	 * 
	 * @param order
	 *            订单
	 * @param admin
	 *            管理员
	 * @param autoLock
	 *            是否自动加锁
	 * @return 订单是否锁定
	 */
	public boolean isLocked(Order order, Admin admin, boolean autoLock) {
		Assert.notNull(order);
		Assert.notNull(admin);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), admin.getLockKey());
		if (autoLock && !isLocked && StringUtils.isNotEmpty(admin.getLockKey())) {
			order.setLockKey(admin.getLockKey());
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
		return isLocked;
	}

	/**
	 * 判断订单是否锁定
	 * 
	 * @param order
	 *            订单
	 * @param member
	 *            会员
	 * @param autoLock
	 *            是否自动加锁
	 * @return 订单是否锁定
	 */
	public boolean isLocked(Order order, Member member, boolean autoLock) {
		Assert.notNull(order);
		Assert.notNull(member);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), member.getLockKey());
		if (autoLock && !isLocked && StringUtils.isNotEmpty(member.getLockKey())) {
			order.setLockKey(member.getLockKey());
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
		return isLocked;
	}

	/**
	 * 订单锁定
	 * 
	 * @param order
	 *            订单
	 * @param admin
	 *            管理员
	 */
	public void lock(Order order, Admin admin) {
		Assert.notNull(order);
		Assert.notNull(admin);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), admin.getLockKey());
		if (!isLocked && StringUtils.isNotEmpty(admin.getLockKey())) {
			order.setLockKey(admin.getLockKey());
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
	}

	/**
	 * 订单锁定
	 * 
	 * @param order
	 *            订单
	 * @param member
	 *            会员
	 */
	public void lock(Order order, Member member) {
		Assert.notNull(order);
		Assert.notNull(member);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), member.getLockKey());
		if (!isLocked && StringUtils.isNotEmpty(member.getLockKey())) {
			order.setLockKey(member.getLockKey());
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
	}

	/**
	 * 过期订单优惠码使用撤销
	 */
	public void undoExpiredUseCouponCode() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, true, null, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoUseCouponCode(order);
				}
//				orderDao.flush();
//				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 过期订单积分兑换撤销
	 */
	public void undoExpiredExchangePoint() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, true, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoExchangePoint(order);
				}
//				orderDao.flush();
//				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 释放过期订单已分配库存
	 */
	public void releaseExpiredAllocatedStock() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, true, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					releaseAllocatedStock(order);
				}
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 订单生成
	 * 
	 * @param type
	 *            类型
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param invoice
	 *            发票
	 * @param balance
	 *            使用余额
	 * @param
	 * @return 订单
	 */
	public Order generate(Order.Type type, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String channel) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(channel);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();

		Order order = new Order();
		order.setType(type.ordinal());
		order.setPrice(cart.getPrice());
		order.setFee(BigDecimal.ZERO);
		order.setPromotionDiscount(cart.getDiscount());
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(cart.getEffectiveRewardPoint());
		order.setExchangePoint(cart.getExchangePoint());
		order.setWeight(cart.getWeight());
		order.setQuantity(cart.getQuantity());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
		order.setChannel(channel);
		order.setIsUseCouponCode(false);
		order.setIsExchangePoint(false);
		order.setIsAllocatedStock(false);
		order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
		order.setPaymentMethodId(paymentMethod != null ? paymentMethod.getId() : null);
		order.setMember(member);
		order.setPromotionNames(JSON.toJSONString(cart.getPromotionNames()));
		order.setCoupons(new ArrayList<Coupon>(cart.getCoupons()));

		if (shippingMethod != null && shippingMethod.isSupported(paymentMethod) && cart.getIsDelivery()) {
			order.setFreight(!cart.isFreeShipping() ? shippingMethodService.calculateFreight(shippingMethod, receiver, cart.getWeight()) : BigDecimal.ZERO);
			order.setShippingMethodId(shippingMethod.getId());
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setShippingMethod(null);
		}

		if (couponCode != null && cart.isCouponAllowed() && cart.isValid(couponCode)) {
			BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(), cart.getProductQuantity()));
			order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
			order.setCouponCodeId(couponCode.getId());
		} else {
			order.setCouponDiscount(BigDecimal.ZERO);
			order.setCouponCode(null);
		}

		order.setTax(calculateTax(order));
		order.setAmount(calculateAmount(order));

		if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(member.getBalance()) <= 0 && balance.compareTo(order.getAmount()) <= 0) {
			order.setAmountPaid(balance);
		} else {
			order.setAmountPaid(BigDecimal.ZERO);
		}

		if (cart.getIsDelivery() && receiver != null) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setArea(receiver.getArea());
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();
			if (product != null) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setType(product.getType().ordinal());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(product.getWeight());
				orderItem.setIsDelivery(product.getIsDelivery());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setProduct(cartItem.getProduct());
				orderItem.setOrder(order);
				orderItem.setSpecifications(JSON.toJSONString(product.getSpecifications()));
				orderItems.add(orderItem);
			}
		}

		for (Product gift : cart.getGifts()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(gift.getSn());
			orderItem.setName(gift.getName());
			orderItem.setType(gift.getType().ordinal());
			orderItem.setPrice(BigDecimal.ZERO);
			orderItem.setWeight(gift.getWeight());
			orderItem.setIsDelivery(gift.getIsDelivery());
			orderItem.setThumbnail(gift.getThumbnail());
			orderItem.setQuantity(1);
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setProduct(gift);
			orderItem.setOrder(order);
			orderItem.setSpecifications(JSON.toJSONString(gift.getSpecifications()));
			orderItems.add(orderItem);
		}

		return order;
	}

	/**
	 * 订单创建
	 * 
	 * @param type
	 *            类型
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param invoice
	 *            发票
	 * @param balance
	 *            使用余额
	 * @return 订单
	 */
	@Before(Tx.class)
	public Order create(Order.Type type, Order.Source source, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String channel, Date shippingDate) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());
		if (cart.getIsDelivery()) {
			Assert.notNull(receiver);
			Assert.notNull(shippingMethod);
			Assert.state(shippingMethod.isSupported(paymentMethod));
		} else {
			Assert.isNull(receiver);
			Assert.isNull(shippingMethod);
		}

		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();
			if (product == null || !product.getIsMarketable() || cartItem.getQuantity() > product.getAvailableStock()) {
				throw new IllegalArgumentException();
			}
		}

		for (Product gift : cart.getGifts()) {
			if (!gift.getIsMarketable() || gift.getIsOutOfStock()) {
				throw new IllegalArgumentException();
			}
		}

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();

		Order order = new Order();

		order.setSn(snDao.generate(Sn.Type.order));
		order.setType(type.ordinal());
		order.setSource(source.ordinal());
		order.setPrice(cart.getPrice());
		order.setFee(BigDecimal.ZERO);
		order.setFreight(cart.getIsDelivery() && !cart.isFreeShipping() ? shippingMethodService.calculateFreight(shippingMethod, receiver, cart.getWeight()) : BigDecimal.ZERO);
		order.setPromotionDiscount(cart.getDiscount());
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setAmountPaid(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(cart.getEffectiveRewardPoint());
		order.setExchangePoint(cart.getExchangePoint());
		order.setWeight(cart.getWeight());
		order.setQuantity(cart.getQuantity());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
		order.setDeleteFlag(false);
		if (cart.getIsDelivery()) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setAreaId(receiver.getAreaId());
			//order.setAreaName(receiver.getArea().getName());
		}
		order.setChannel(channel);
		order.setIsUseCouponCode(false);
		order.setIsExchangePoint(false);
		order.setIsAllocatedStock(false);
		order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
		order.setShippingMethodId(shippingMethod != null ? shippingMethod.getId() : null);
		order.setMemberId(member.getId());
		order.setPromotionNames(JSON.toJSONString(cart.getPromotionNames()));
		order.setCoupons(new ArrayList<Coupon>(cart.getCoupons()));
		order.setShippingDate(shippingDate);

		if (couponCode != null) {
			if (!cart.isCouponAllowed() || !cart.isValid(couponCode)) {
				throw new IllegalArgumentException();
			}
			BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(), cart.getProductQuantity()));
			order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
			order.setCouponCodeId(couponCode.getId());
			useCouponCode(order);
		} else {
			order.setCouponDiscount(BigDecimal.ZERO);
		}

		order.setTax(calculateTax(order));
		order.setAmount(calculateAmount(order));

		//-1表示小于，0是等于，1是大于
		if (balance != null && (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(member.getBalance()) > 0 /*|| balance.compareTo(order.getAmount()) > 0*/)) {
			throw new IllegalArgumentException();
		}
		BigDecimal amountPayable = balance != null ? order.getAmount().subtract(balance) : order.getAmount();
		if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
			if (paymentMethod == null) {
				throw new IllegalArgumentException();
			}
			order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getTypeName())  ? Order.Status.pendingPayment.ordinal() : Order.Status.pendingReview.ordinal());
			order.setPaymentMethodId(paymentMethod.getId());
			if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatusName())) {
				// 设置订单过期日期
				order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
			}
			if (PaymentMethod.Method.online.ordinal() == paymentMethod.getMethod()) {
				lock(order, member);
			}
		} else {
			order.setStatus(Order.Status.pendingReview.ordinal());
			if (paymentMethod == null) {
				order.setPaymentMethod(null);
			} else {
				order.setPaymentMethodId(paymentMethod.getId());
			}
		}
		
//		if (order.getArea() != null) {
//			order.setAreaName(order.getArea().getFullName());
//		}
		if (order.getPaymentMethod() != null) {
			order.setPaymentMethodName(order.getPaymentMethod().getName());
			order.setPaymentMethodType(order.getPaymentMethod().getType());
		}
		if (order.getShippingMethod() != null) {
			order.setShippingMethodName(order.getShippingMethod().getName());
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(product.getSn());
			orderItem.setName(product.getName());
			orderItem.setType(product.getType().ordinal());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setWeight(product.getWeight());
			orderItem.setIsDelivery(product.getIsDelivery());
			orderItem.setThumbnail(product.getThumbnail());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setProductId(cartItem.getProduct().getId());
			//orderItem.setOrderId(order.getId());
			orderItem.setSpecifications(JSON.toJSONString(product.getSpecifications()));
			orderItems.add(orderItem);
		}

		for (Product gift : cart.getGifts()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(gift.getSn());
			orderItem.setName(gift.getName());
			orderItem.setType(gift.getType().ordinal());
			orderItem.setPrice(BigDecimal.ZERO);
			orderItem.setWeight(gift.getWeight());
			orderItem.setIsDelivery(gift.getIsDelivery());
			orderItem.setThumbnail(gift.getThumbnail());
			orderItem.setQuantity(1);
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setProduct(gift);
			//orderItem.setOrder(order);
			orderItem.setSpecifications(JSON.toJSONString(gift.getSpecifications()));
			orderItems.add(orderItem);
		}
		
		exchangePoint(order);
		if (Setting.StockAllocationTime.order.equals(setting.getStockAllocationTime()) || (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime()) && (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 || order.getExchangePoint() > 0 || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0))) {
			allocateStock(order);
		}
		
		// 保存订单头
		orderDao.save(order);
		
		// 保存订单行
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				orderItem.setOrderId(order.getId());
				orderItemDao.save(orderItem);
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.create.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
			Payment payment = new Payment();
			payment.setMethod(Payment.Method.deposit.ordinal());
			payment.setFee(BigDecimal.ZERO);
			if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
				payment.setAmount(balance);
			} else {
				payment.setAmount(order.getAmount());
			}
			payment.setOrderId(order.getId());
			payment(order, payment, null);
		}

		if (!cart.isNew()) {
			cartItemDao.delete(cart.getId());
			//cartDao.remove(cart);
		}

		return order;
	}

	/**
	 * 订单更新
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void update(Order order, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state((Order.Status.pendingPayment.equals(order.getStatusName()) || Order.Status.pendingReview.equals(order.getStatusName())));

		order.setAmount(calculateAmount(order));
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview.ordinal());
			order.setExpire(null);
		} else {
			if (order.getPaymentMethod() != null && PaymentMethod.Type.deliveryAgainstPayment.equals(order.getPaymentMethod().getTypeName())) {
				order.setStatus(Order.Status.pendingPayment.ordinal());
			} else {
				order.setStatus(Order.Status.pendingReview.ordinal());
				order.setExpire(null);
			}
		}
		
		if (order.getArea() != null) {
			order.setAreaName(order.getArea().getFullName());
		}
		if (order.getPaymentMethod() != null) {
			order.setPaymentMethodName(order.getPaymentMethod().getName());
			order.setPaymentMethodType(order.getPaymentMethod().getType());
		}
		if (order.getShippingMethod() != null) {
			order.setShippingMethodName(order.getShippingMethod().getName());
		}
		
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.update.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

//		mailService.sendUpdateOrderMail(order);
//		smsService.sendUpdateOrderSms(order);
	}


	/**
	 * 订单取消
	 * 
	 * @param order
	 *            订单
	 */
	public void cancel(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(Order.Status.pendingPayment.equals(order.getStatusName()) || Order.Status.pendingReview.equals(order.getStatusName()));

		order.setStatus(Order.Status.canceled.ordinal());
		order.setExpire(null);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.cancel.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

	}

	/**
	 * 订单审核
	 * 
	 * @param order
	 *            订单
	 * @param passed
	 *            是否审核通过
	 * @param operator
	 *            操作员
	 */
	public void review(Order order, boolean passed, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(Order.Status.pendingReview.equals(order.getStatusName()));

		if (passed) {
			order.setStatus(Order.Status.pendingShipment.ordinal());
		} else {
			order.setStatus(Order.Status.denied.ordinal());

			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.review.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

//		mailService.sendReviewOrderMail(order);
//		smsService.sendReviewOrderSms(order);
	}

	/**
	 * 订单收款
	 * 
	 * @param order
	 *            订单
	 * @param payment
	 *            收款单
	 * @param operator
	 *            操作员
	 */
	public void payment(Order order, Payment payment, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(payment);
		Assert.isTrue(payment.isNew());
		Assert.notNull(payment.getAmount());
		Assert.state(payment.getAmount().compareTo(BigDecimal.ZERO) > 0);

		
		
		payment.setSn(snDao.generate(Sn.Type.payment));
		payment.setSn(order.getSn());
		payment.setOrderId(order.getId());
		paymentDao.save(payment);

		if (order.getMember() != null && Payment.Method.deposit.equals(payment.getMethodName())) {
			memberService.addBalance(order.getMember(), payment.getEffectiveAmount().negate(), DepositLog.Type.payment, operator, null);
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		order.setAmountPaid(order.getAmountPaid().add(payment.getEffectiveAmount()));
		order.setFee(order.getFee().add(payment.getFee()));
		if (Order.Status.pendingPayment.equals(order.getStatusName()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview.ordinal());
			order.setExpire(null);
		}
		super.update(order);
		LogKit.info("订单收款成功更新订单：" + order.getSn());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.payment.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

	}
	
	/**
	 * 订单收款
	 * 
	 * @param order
	 *            订单
	 * @param payment
	 *            收款单
	 * @param operator
	 *            操作员
	 */
	public void paymentH5(Order order, Payment payment, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(payment);
		Assert.isTrue(payment.isNew());
		Assert.notNull(payment.getAmount());
		Assert.state(payment.getAmount().compareTo(BigDecimal.ZERO) > 0);

		
		
		//payment.setSn(snDao.generate(Sn.Type.payment));
		payment.setSn(order.getSn());
		payment.setOrderId(order.getId());
		paymentDao.save(payment);

		if (order.getMember() != null && Payment.Method.deposit.equals(payment.getMethodName())) {
			memberService.addBalance(order.getMember(), payment.getEffectiveAmount().negate(), DepositLog.Type.payment, operator, null);
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		order.setAmountPaid(order.getAmountPaid().add(payment.getEffectiveAmount()));
		order.setFee(order.getFee().add(payment.getFee()));
		if (Order.Status.pendingPayment.equals(order.getStatusName()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview.ordinal());
			order.setExpire(null);
		}
		super.update(order);
		LogKit.info("订单收款成功更新订单：" + order.getSn());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.payment.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

	}
	

	/**
	 * 订单退款
	 * 
	 * @param order
	 *            订单
	 * @param refunds
	 *            退款单
	 * @param operator
	 *            操作员
	 */
	@Before(Tx.class)
	public void refunds(Order order, Refunds refunds, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0);
		Assert.notNull(refunds);
		Assert.isTrue(refunds.isNew());
		Assert.notNull(refunds.getAmount());
		Assert.state(refunds.getAmount().compareTo(BigDecimal.ZERO) > 0 && refunds.getAmount().compareTo(order.getRefundableAmount()) <= 0);

		refunds.setSn(snDao.generate(Sn.Type.refunds));
		refunds.setOrderId(order.getId());
		refunds.setOperator(operator);
		refundsDao.save(refunds);

		if (Refunds.Method.deposit.equals(refunds.getMethodName())) {
			memberService.addBalance(order.getMember(), refunds.getAmount(), DepositLog.Type.refunds, operator, null);
		}
		
		// 释放已分配库存
		releaseAllocatedStock(order);
		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setRefundAmount(order.getRefundAmount().add(refunds.getAmount()));
		order.setStatus(Order.Status.refunding.ordinal());
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.refunds.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

	}

	/**
	 * 订单发货
	 * 
	 * @param order
	 *            订单
	 * @param shipping
	 *            发货单
	 * @param operator
	 *            操作员
	 */
	@Before(Tx.class)
	public void shipping(Order order, Shipping shipping, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getShippableQuantity() > 0);
		Assert.notNull(shipping);
		Assert.isTrue(shipping.isNew());
		Assert.notEmpty(shipping.getShippingItems());

		shipping.setSn(snDao.generate(Sn.Type.shipping));
		shipping.setOrderId(order.getId());
		shippingDao.save(shipping);
		
		List<ShippingItem> shippingItems = shipping.getShippingItems();
		if (CollectionUtils.isNotEmpty(shippingItems)) {
			for (ShippingItem shippingItem : shippingItems) {
				shippingItem.setShippingId(shipping.getId());
				shippingItemDao.save(shippingItem);
			}
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.ship.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		for (ShippingItem shippingItem : shipping.getShippingItems()) {
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null || shippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + shippingItem.getQuantity());
			orderItem.update();
			Product product = shippingItem.getProduct();
			if (product != null) {
				if (shippingItem.getQuantity() > product.getStock()) {
					throw new IllegalArgumentException();
				}
				productService.addStock(product, -shippingItem.getQuantity(), StockLog.Type.stockOut, operator, null);
				if (BooleanUtils.isTrue(order.getIsAllocatedStock())) {
					productService.addAllocatedStock(product, -shippingItem.getQuantity());
				}
			}
		}

		order.setShippedQuantity(order.getShippedQuantity() + shipping.getQuantity());
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setStatus(Order.Status.shipped.ordinal());
			order.setIsAllocatedStock(false);
		}
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.shipping.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

	}

	/**
	 * 订单退货
	 * 
	 * @param order
	 *            订单
	 * @param returns
	 *            退货单
	 * @param operator
	 *            操作员
	 */
	public void returns(Order order, Returns returns, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getReturnableQuantity() > 0);
		Assert.notNull(returns);
		Assert.isTrue(returns.isNew());
		Assert.notEmpty(returns.getReturnsItems());

		returns.setSn(snDao.generate(Sn.Type.returns));
		returns.setOrderId(order.getId());
		returnsDao.save(returns);

		for (ReturnsItem returnsItem : returns.getReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem == null || returnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				throw new IllegalArgumentException();
			}
			returnsItem.setReturnId(returns.getId());
			returnsItemDao.save(returnsItem);
			orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + returnsItem.getQuantity());
			orderItem.update();
		}

		order.setReturnedQuantity(order.getReturnedQuantity() + returns.getQuantity());
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.returns.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

//		mailService.sendReturnsOrderMail(order);
//		smsService.sendReturnsOrderSms(order);
	}

	/**
	 * 订单收货
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void receive(Order order, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(Order.Status.shipped.equals(order.getStatusName()));

		order.setStatus(Order.Status.received.ordinal());
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.receive.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendReceiveOrderMail(order);
		//smsService.sendReceiveOrderSms(order);
	}


	/**
	 * 订单完成
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void complete(Order order, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(Order.Status.received.equals(order.getStatusName()));

		Member member = order.getMember();
		if (order.getRewardPoint() > 0) {
			memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, operator, null);
		}
		if (CollectionUtils.isNotEmpty(order.getCoupons())) {
			for (Coupon coupon : order.getCoupons()) {
				couponCodeService.generate(coupon, member);
			}
		}
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			memberService.addAmount(member, order.getAmountPaid());
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			Product product = orderItem.getProduct();
			if (product != null && product.getGoods() != null) {
				LogKit.info(">>>" + orderItem.getQuantity());
				goodsService.addSales(product.getGoods(), orderItem.getQuantity());
			}
		}

		order.setStatus(Order.Status.completed.ordinal());
		order.setCompleteDate(new Date());
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.complete.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

//		mailService.sendCompleteOrderMail(order);
//		smsService.sendCompleteOrderSms(order);
	}

	/**
	 * 订单失败
	 * 
	 * @param order
	 *            订单
	 * @param operator
	 *            操作员
	 */
	public void fail(Order order, Admin operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state((Order.Status.pendingShipment.equals(order.getStatusName()) || Order.Status.shipped.equals(order.getStatusName()) || Order.Status.received.equals(order.getStatusName())));

		order.setStatus(Order.Status.failed.ordinal());

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);
		super.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.fail.ordinal());
		orderLog.setOperator(operator);
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

//		mailService.sendFailOrderMail(order);
//		smsService.sendFailOrderSms(order);
	}

	@Before(Tx.class)
	public void delete(Order order) {
		if (order != null && !Order.Status.completed.equals(order.getStatusName())) {
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
			super.update(order);
		}
		
		orderItemDao.deleteByOrderId(order.getId());
		orderLogDao.deleteByOrderId(order.getId());
		paymentLogDao.deleteByOrderId(order.getId());
		super.delete(order);
	}

	
	/**
	 * 优惠码使用
	 * 
	 * @param order
	 *            订单
	 */
	private void useCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsUseCouponCode()) || order.getCouponCodeId() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(true);
		couponCode.setUsedDate(new Date());
		order.setIsUseCouponCode(true);
		couponCode.update();
	}

	/**
	 * 优惠码使用撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoUseCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(false);
		couponCode.setUsedDate(null);
		order.setIsUseCouponCode(false);
		order.setCouponCode(null);
		couponCode.update();
	}

	/**
	 * 积分兑换
	 * 
	 * @param order
	 *            订单
	 */
	private void exchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), -order.getExchangePoint(), PointLog.Type.exchange, null, null);
		order.setIsExchangePoint(true);
	}

	/**
	 * 积分兑换撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoExchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), order.getExchangePoint(), PointLog.Type.undoExchange, null, null);
		order.setIsExchangePoint(false);
	}
	
	/**
	 * 分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void allocateStock(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Product product = orderItem.getProduct();
				if (product != null) {
					productService.addAllocatedStock(product, orderItem.getQuantity() - orderItem.getShippedQuantity());
				}
			}
		}
		order.setIsAllocatedStock(true);
	}

	/**
	 * 释放已分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void releaseAllocatedStock(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Product product = orderItem.getProduct();
				if (product != null) {
					productService.addAllocatedStock(product, -(orderItem.getQuantity() - orderItem.getShippedQuantity()));
				}
			}
		}
		order.setIsAllocatedStock(false);
	}
}