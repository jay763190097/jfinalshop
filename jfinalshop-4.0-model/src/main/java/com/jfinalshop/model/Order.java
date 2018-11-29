package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.base.BaseOrder;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 订单
 * 
 * 
 */
public class Order extends BaseOrder<Order> {
	private static final long serialVersionUID = 3491084708343393819L;
	public static final Order dao = new Order();
	
	/** 锁定过期时间 */
	public static final int LOCK_EXPIRE = 60;

	/**
	 * 类型
	 */
	public enum Type {

		/** 普通订单 */
		general,

		/** 兑换订单 */
		exchange
	}

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待付款 */
		pendingPayment,

		/** 等待审核 */
		pendingReview,

		/** 等待发货 */
		pendingShipment,

		/** 已发货 */
		shipped,

		/** 已收货 */
		received,

		/** 已完成 */
		completed,

		/** 已失败 */
		failed,

		/** 已取消 */
		canceled,

		/** 已拒绝 */
		denied,
		
		/** 未完成 */
		unfinished,
		
		/** 退款中 */
		refunding,
		
		/** 退款完成 */
		refunded,
		
		/** 所有已取消 */
		allCanceled,
		
		/** 已评价 */
		reviewed
	}
	
	/**
	 * 来源
	 */
	public enum Source {

		/** PC */
		PC,

		/** H5 */
		H5,
		
		/** IOS */
		IOS,
		
		/** android */
		ANDROID,
		
		/** 线下 */
		OFFLINE
	}
	
	/** 地区 */
	private Area area;

	/** 支付方式 */
	private PaymentMethod paymentMethod;

	/** 配送方式 */
	private ShippingMethod shippingMethod;

	/** 会员 */
	private Member member;

	/** 优惠码 */
	private CouponCode couponCode;
	
	/** 发票 */
	private Invoice invoice;
	
	/** 促销名称 */
	private List<String> promotionNames = new ArrayList<String>();

	/** 赠送优惠券 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/** 订单项 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 支付记录 */
	private List<PaymentLog> paymentLogs = new ArrayList<PaymentLog>();

	/** 收款单 */
	private List<Payment> payments = new ArrayList<Payment>();

	/** 退款单 */
	private List<Refunds> refunds = new ArrayList<Refunds>();

	/** 发货单 */
	private List<Shipping> shippings = new ArrayList<Shipping>();

	/** 退货单 */
	private List<Returns> returns = new ArrayList<Returns>();

	/** 订单记录 */
	private List<OrderLog> orderLogs = new ArrayList<OrderLog>();
	
	/**
	 * 类型
	 */
	public Order.Type getTypeName() {
		return getType() != null ? Order.Type.values()[getType()] : null;
	}
	
	/**
	 * 状态
	 */
	public Order.Status getStatusName() {
		return getStatus() != null ? Order.Status.values()[getStatus()] : null;
	}
	
	/**
	 * 获取发票
	 * 
	 * @return 发票
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * 设置发票
	 * 
	 * @param invoice
	 *            发票
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (ObjectUtils.isEmpty(area)) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 是否是新增 没有保存的
	 * @return
	 */
	public Boolean isNew(){
		if(getId()!=null){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取支付方式
	 * 
	 * @return 支付方式
	 */
	public PaymentMethod getPaymentMethod() {
		if (ObjectUtils.isEmpty(paymentMethod)) {
			paymentMethod = PaymentMethod.dao.findById(getPaymentMethodId());
		}
		return paymentMethod;
	}

	/**
	 * 设置支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public ShippingMethod getShippingMethod() {
		if (ObjectUtils.isEmpty(shippingMethod)) {
			shippingMethod = ShippingMethod.dao.findById(getShippingMethodId());
		}
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (ObjectUtils.isEmpty(member)) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public CouponCode getCouponCode() {
		if (ObjectUtils.isEmpty(member)) {
			couponCode = CouponCode.dao.findById(getCouponCodeId());
		}
		return couponCode;
	}

	/**
	 * 设置优惠码
	 * 
	 * @param couponCode
	 *            优惠码
	 */
	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}
	
	/**
	 * 获取促销名称
	 * 
	 * @return 促销名称
	 */
	public List<String> getPromotionNamesConverter() {
		if (CollectionUtils.isEmpty(promotionNames)) {
			promotionNames = JsonUtils.convertJsonStrToList(getPromotionNames());
		}
		return promotionNames;
	}

	/**
	 * 设置促销名称
	 * 
	 * @param promotionNames
	 *            促销名称
	 */
	public void setPromotionNamesConverter(List<String> promotionNames) {
		this.promotionNames = promotionNames;
	}


	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	public List<Coupon> getCoupons() {
		if (CollectionUtils.isEmpty(coupons)) {
			String sql = "SELECT c.* FROM `order_coupon` oc LEFT JOIN `coupon` c ON oc.`coupons` = c.`id` WHERE oc.`orders` = ?";
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}

	/**
	 * 设置赠送优惠券
	 * 
	 * @param coupons
	 *            赠送优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		if (CollectionUtils.isEmpty(orderItems)) {
			String sql = "SELECT * FROM `order_item` WHERE order_id = ? ORDER BY `type` ASC";
			orderItems = OrderItem.dao.find(sql, getId());
		}
		return orderItems;
	}

	/**
	 * 设置订单项
	 * 
	 * @param orderItems
	 *            订单项
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * 获取支付记录
	 * 
	 * @return 支付记录
	 */
	public List<PaymentLog> getPaymentLogs() {
		if (CollectionUtils.isEmpty(paymentLogs)) {
			String sql = "SELECT * FROM `payment_log` WHERE order_id = ? ORDER BY `create_date` ASC";
			paymentLogs = PaymentLog.dao.find(sql, getId());
		}
		return paymentLogs;
	}

	/**
	 * 设置支付记录
	 * 
	 * @param paymentLogs
	 *            支付记录
	 */
	public void setPaymentLogs(List<PaymentLog> paymentLogs) {
		this.paymentLogs = paymentLogs;
	}

	/**
	 * 获取收款单
	 * 
	 * @return 收款单
	 */
	public List<Payment> getPayments() {
		if (CollectionUtils.isEmpty(payments)) {
			String sql = "SELECT * FROM `payment` WHERE order_id = ? ORDER BY `create_date` ASC";
			payments = Payment.dao.find(sql, getId());
		}
		return payments;
	}

	/**
	 * 设置收款单
	 * 
	 * @param payments
	 *            收款单
	 */
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	/**
	 * 获取退款单
	 * 
	 * @return 退款单
	 */
	public List<Refunds> getRefunds() {
		if (CollectionUtils.isEmpty(refunds)) {
			String sql = "SELECT * FROM `refunds` WHERE order_id = ? order by `create_date` asc";
			refunds = Refunds.dao.find(sql, getId());
		}
		return refunds;
	}

	/**
	 * 设置退款单
	 * 
	 * @param refunds
	 *            退款单
	 */
	public void setRefunds(List<Refunds> refunds) {
		this.refunds = refunds;
	}

	/**
	 * 获取发货单
	 * 
	 * @return 发货单
	 */
	public List<Shipping> getShippings() {
		if (CollectionUtils.isEmpty(shippings)) {
			String sql = "SELECT * FROM `shipping` WHERE order_id = ? ORDER BY `create_date` ASC";
			shippings = Shipping.dao.find(sql, getId());
		}
		return shippings;
	}

	/**
	 * 设置发货单
	 * 
	 * @param shippings
	 *            发货单
	 */
	public void setShippings(List<Shipping> shippings) {
		this.shippings = shippings;
	}

	/**
	 * 获取退货单
	 * 
	 * @return 退货单
	 */
	public List<Returns> getReturns() {
		if (CollectionUtils.isEmpty(refunds)) {
			String sql = "SELECT * FROM `returns` WHERE order_id = ? ORDER BY `create_date` ASC";
			returns = Returns.dao.find(sql, getId());
		}
		return returns;
	}

	/**
	 * 设置退货单
	 * 
	 * @param returns
	 *            退货单
	 */
	public void setReturns(List<Returns> returns) {
		this.returns = returns;
	}

	/**
	 * 获取订单记录
	 * 
	 * @return 订单记录
	 */
	public List<OrderLog> getOrderLogs() {
		if (CollectionUtils.isEmpty(orderLogs)) {
			String sql = "SELECT * FROM `order_log` WHERE order_id = ? ORDER BY `create_date` ASC";
			orderLogs = OrderLog.dao.find(sql, getId());
		}
		return orderLogs;
	}

	/**
	 * 设置订单记录
	 * 
	 * @param orderLogs
	 *            订单记录
	 */
	public void setOrderLogs(List<OrderLog> orderLogs) {
		this.orderLogs = orderLogs;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return CollectionUtils.exists(getOrderItems(), new Predicate() {
			public boolean evaluate(Object object) {
				OrderItem orderItem = (OrderItem) object;
				return orderItem != null && BooleanUtils.isTrue(orderItem.getIsDelivery());
			}
		});
	}

	/**
	 * 获取应付金额
	 * 
	 * @return 应付金额
	 */
	public BigDecimal getAmountPayable() {
		if (!hasExpired() && !Order.Status.completed.equals(getStatusName()) && !Order.Status.failed.equals(getStatusName()) && !Order.Status.canceled.equals(getStatusName()) && !Order.Status.denied.equals(getStatusName())) {
			BigDecimal amountPayable = getAmount().subtract(getAmountPaid());
			return amountPayable.compareTo(BigDecimal.ZERO) >= 0 ? amountPayable : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取应收金额
	 * 
	 * @return 应收金额
	 */
	public BigDecimal getAmountReceivable() {
		if (!hasExpired() && PaymentMethod.Type.cashOnDelivery.equals(getPaymentMethodType()) && !Order.Status.completed.equals(getStatusName()) && !Order.Status.failed.equals(getStatusName()) && !Order.Status.canceled.equals(getStatusName()) && !Order.Status.denied.equals(getStatusName())) {
			BigDecimal amountReceivable = getAmount().subtract(getAmountPaid());
			return amountReceivable.compareTo(BigDecimal.ZERO) >= 0 ? amountReceivable : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取应退金额
	 * 
	 * @return 应退金额
	 */
	public BigDecimal getRefundableAmount() {
		if (hasExpired() || Order.Status.failed.equals(getStatusName()) || Order.Status.canceled.equals(getStatusName()) || Order.Status.denied.equals(getStatusName())) {
			BigDecimal refundableAmount = getAmountPaid();
			return refundableAmount.compareTo(BigDecimal.ZERO) >= 0 ? refundableAmount : BigDecimal.ZERO;
		}
		if (Order.Status.completed.equals(getStatusName())) {
			BigDecimal refundableAmount = getAmountPaid().subtract(getAmount());
			return refundableAmount.compareTo(BigDecimal.ZERO) >= 0 ? refundableAmount : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取可发货数
	 * 
	 * @return 可发货数
	 */
	public int getShippableQuantity() {
		if (!hasExpired() && Order.Status.pendingShipment.equals(getStatusName())) {
			int shippableQuantity = getQuantity() - getShippedQuantity();
			return shippableQuantity >= 0 ? shippableQuantity : 0;
		}
		return 0;
	}

	/**
	 * 获取可退货数
	 * 
	 * @return 可退货数
	 */
	public int getReturnableQuantity() {
		//if (!hasExpired() && Order.Status.failed.equals(getStatusName())) {
			int returnableQuantity = getShippedQuantity() - getReturnedQuantity();
			return returnableQuantity >= 0 ? returnableQuantity : 0;
		//}
		//return 0;
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getExpire() != null && !getExpire().after(new Date());
	}

	/**
	 * 获取订单项
	 * 
	 * @param sn
	 *            商品编号
	 * @return 订单项
	 */
	public OrderItem getOrderItem(String sn) {
		if (StringUtils.isEmpty(sn) || CollectionUtils.isEmpty(getOrderItems())) {
			return null;
		}
		for (OrderItem orderItem : getOrderItems()) {
			if (orderItem != null && StringUtils.equalsIgnoreCase(orderItem.getSn(), sn)) {
				return orderItem;
			}
		}
		return null;
	}

	/**
	 * 获取商品数量
	 * 
	 * @return 商品数量
	 */
	public int getProductQuantity() {
		int productQuantity = 0;
		List<OrderItem> orderItems = getOrderItems();
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				if (orderItem.getQuantity() != null) {
					productQuantity += orderItem.getQuantity();
				}
			}
		}
		return productQuantity;
	}


	/**
	 * 获取商品金额
	 * 
	 * @return 商品金额
	 */
	public BigDecimal getOrderItemTotal() {
		BigDecimal productQuantity = BigDecimal.ZERO;
		List<OrderItem> orderItems = getOrderItems();
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				if (orderItem.getSubtotal() != null) {
					productQuantity = productQuantity.add(orderItem.getSubtotal());
				}
			}
		}
		return productQuantity;
	}
}
