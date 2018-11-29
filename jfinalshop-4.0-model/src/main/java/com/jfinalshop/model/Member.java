package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.PropKit;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.entity.SafeKey;
import com.jfinalshop.model.base.BaseMember;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 会员
 * 
 * 
 */
public class Member extends BaseMember<Member> {
	private static final long serialVersionUID = -2107766706595334754L;
	public static final Member dao = new Member();
	
	/**
	 * 性别
	 */
	public enum Gender {

		/** 男 */
		male,

		/** 女 */
		female
	}

	/**
	 * 排名类型
	 */
	public enum RankingType {

		/** 积分 */
		point,

		/** 余额 */
		balance,

		/** 消费金额 */
		amount
	}
	

	/**
	 * 性别
	 * 
	 * @return 性别
	 */
	public Gender getGenderName() {
		return Member.Gender.values()[getGender()];
	}
	
	
	/** "身份信息"属性名称 */
	public static final String PRINCIPAL_ATTRIBUTE_NAME = Member.class.getName() + ".PRINCIPAL";
	
	/** "身份信息"属性名称 */
	public static final String OPEN_ID = "openId";

	/** "用户名"Cookie名称 */
	public static final String USERNAME_COOKIE_NAME = "username";

	/** "昵称"Cookie名称 */
	public static final String NICKNAME_COOKIE_NAME = "nickname";

	/** 会员注册项值属性个数 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 10;

	/** 会员注册项值属性名称前缀 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";

	/** 最大收藏商品数 */
	public static final Integer MAX_FAVORITE_COUNT = 10;
	
	/** 订单 */
	private List<Order> orders = new ArrayList<Order>();

	/** 支付记录 */
	private List<PaymentLog> paymentLogs = new ArrayList<PaymentLog>();

	/** 预存款记录 */
	private List<DepositLog> depositLogs = new ArrayList<DepositLog>();

	/** 优惠码 */
	private List<CouponCode> couponCodes = new ArrayList<CouponCode>();

	/** 收货地址 */
	private List<Receiver> receivers = new ArrayList<Receiver>();

	/** 评论 */
	private List<Review> reviews = new ArrayList<Review>();

	/** 咨询 */
	private List<Consultation> consultations = new ArrayList<Consultation>();

	/** 收藏货品 */
	private List<Goods> favoriteGoods = new ArrayList<Goods>();

	/** 到货通知 */
	private List<ProductNotify> productNotifies = new ArrayList<ProductNotify>();

	/** 接收的消息 */
	private List<Message> inMessages = new ArrayList<Message>();

	/** 发送的消息 */
	private List<Message> outMessages = new ArrayList<Message>();

	/** 积分记录 */
	private List<PointLog> pointLogs = new ArrayList<PointLog>();
	
	/** 地区 */
	private Area area;

	/** 会员等级 */
	private MemberRank memberRank;

	/** 购物车 */
	private Cart cart;
	
	/** 安全密匙 */
	private SafeKey safeKey;
	
	/**
	 * 获取安全密匙
	 * 
	 * @return 安全密匙
	 */
	public SafeKey getSafeKey() {
		return safeKey;
	}

	/**
	 * 设置安全密匙
	 * 
	 * @param safeKey
	 *            安全密匙
	 */
	public void setSafeKey(SafeKey safeKey) {
		this.safeKey = safeKey;
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
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取会员等级
	 * 
	 * @return 会员等级
	 */
	public MemberRank getMemberRank() {
		if (ObjectUtils.isEmpty(memberRank)) {
			memberRank = MemberRank.dao.findById(getMemberRankId());
		}
		return memberRank;
	}

	/**
	 * 设置会员等级
	 * 
	 * @param memberRank
	 *            会员等级
	 */
	public void setMemberRank(MemberRank memberRank) {
		this.memberRank = memberRank;
	}

	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart(String channel) {
		if (ObjectUtils.isEmpty(cart)) {
			String sql = "SELECT * FROM cart WHERE member_id = ? AND channel= "+"'"+channel+"'";
			cart = Cart.dao.findFirst(sql, getId());
		}
		return cart;
	}

	/**
	 * 设置购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrders() {
		if (CollectionUtils.isEmpty(orders)) {
			String sql = "SELECT * FROM `order` WHERE member_id = ?";
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取支付记录
	 * 
	 * @return 支付记录
	 */
	public List<PaymentLog> getPaymentLogs() {
		if (CollectionUtils.isEmpty(paymentLogs)) {
			String sql = "SELECT * FROM `payment_log` WHERE member_id = ?";
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
	 * 获取预存款记录
	 * 
	 * @return 预存款记录
	 */
	public List<DepositLog> getDepositLogs() {
		if (CollectionUtils.isEmpty(depositLogs)) {
			String sql = "SELECT * FROM `deposit_log` WHERE member_id = ?";
			depositLogs = DepositLog.dao.find(sql, getId());
		}
		return depositLogs;
	}

	/**
	 * 设置预存款记录
	 * 
	 * @param depositLogs
	 *            预存款记录
	 */
	public void setDepositLogs(List<DepositLog> depositLogs) {
		this.depositLogs = depositLogs;
	}

	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public List<CouponCode> getCouponCodes() {
		return couponCodes;
	}

	/**
	 * 设置优惠码
	 * 
	 * @param couponCodes
	 *            优惠码
	 */
	public void setCouponCodes(List<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}

	/**
	 * 获取收货地址
	 * 
	 * @return 收货地址
	 */
	public List<Receiver> getReceivers() {
		if (CollectionUtils.isEmpty(receivers)) {
			String sql = "SELECT * FROM `receiver` WHERE member_id = ? ORDER BY is_default DESC, create_date DESC";
			receivers = Receiver.dao.find(sql, getId());
		}
		return receivers;
	}

	/**
	 * 设置收货地址
	 * 
	 * @param receivers
	 *            收货地址
	 */
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		if (CollectionUtils.isEmpty(reviews)) {
			String sql = "SELECT * FROM `review` WHERE member_id = ? ORDER BY create_date DESC";
			reviews = Review.dao.find(sql, getId());
		}
		return reviews;
	}

	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		if (CollectionUtils.isEmpty(consultations)) {
			String sql = "SELECT * FROM `consultation` WHERE member_id = ? ORDER BY create_date DESC";
			consultations = Consultation.dao.find(sql, getId());
		}
		return consultations;
	}

	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}

	/**
	 * 获取收藏货品
	 * 
	 * @return 收藏货品
	 */
	public List<Goods> getFavoriteGoods() {
		if (CollectionUtils.isEmpty(favoriteGoods)) {
			String sql = "SELECT g.* FROM `member_favorite_goods` mfg LEFT JOIN `goods` g ON mfg.`favorite_goods` = g.`id` WHERE mfg.`favorite_members` = ? ORDER BY create_date DESC";
			favoriteGoods = Goods.dao.find(sql, getId());
		}
		return favoriteGoods;
	}

	/**
	 * 设置收藏货品
	 * 
	 * @param favoriteGoods
	 *            收藏货品
	 */
	public void setFavoriteGoods(List<Goods> favoriteGoods) {
		this.favoriteGoods = favoriteGoods;
	}

	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `product_notify` WHERE member_id = ?";
			productNotifies = ProductNotify.dao.find(sql, getId());
		}
		return productNotifies;
	}

	/**
	 * 设置到货通知
	 * 
	 * @param productNotifies
	 *            到货通知
	 */
	public void setProductNotifies(List<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}

	/**
	 * 获取接收的消息
	 * 
	 * @return 接收的消息
	 */
	public List<Message> getInMessages() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `message` WHERE receiver_id = ?";
			inMessages = Message.dao.find(sql, getId());
		}
		return inMessages;
	}

	/**
	 * 设置接收的消息
	 * 
	 * @param inMessages
	 *            接收的消息
	 */
	public void setInMessages(List<Message> inMessages) {
		this.inMessages = inMessages;
	}

	/**
	 * 获取发送的消息
	 * 
	 * @return 发送的消息
	 */
	public List<Message> getOutMessages() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `message` WHERE sender_id = ?";
			outMessages = Message.dao.find(sql, getId());
		}
		return outMessages;
	}

	/**
	 * 设置发送的消息
	 * 
	 * @param outMessages
	 *            发送的消息
	 */
	public void setOutMessages(List<Message> outMessages) {
		this.outMessages = outMessages;
	}

	/**
	 * 获取积分记录
	 * 
	 * @return 积分记录
	 */
	public List<PointLog> getPointLogs() {
		if (CollectionUtils.isEmpty(pointLogs)) {
			String sql = "SELECT * FROM `point_log` WHERE member_id = ?";
			pointLogs = PointLog.dao.find(sql, getId());
		}
		return pointLogs;
	}

	/**
	 * 设置积分记录
	 * 
	 * @param pointLogs
	 *            积分记录
	 */
	public void setPointLogs(List<PointLog> pointLogs) {
		this.pointLogs = pointLogs;
	}
	
	/**
	 * 获取会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @return 会员注册项值
	 */
	public Object getAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute == null || memberAttribute.getType() == null) {
			return null;
		}

		switch (memberAttribute.getTypeName()) {
		case name:
			return getName();
		case gender:
			return getGender();
		case birth:
			return getBirth();
		case area:
			return getArea();
		case address:
			return getAddress();
		case zipCode:
			return getZipCode();
		case phone:
			return getPhone();
		case mobile:
			return getMobile();
		case text:
		case select:
			if (memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					return PropertyUtils.getProperty(this, propertyName);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case checkbox:
			if (memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					String propertyValue = (String) PropertyUtils.getProperty(this, propertyName);
					if (StringUtils.isNotEmpty(propertyValue)) {
						return JsonUtils.toObject(propertyValue, List.class);
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		}
		return null;
	}

	/**
	 * 设置会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @param memberAttributeValue
	 *            会员注册项值
	 */
	public void setAttributeValue(MemberAttribute memberAttribute, Object memberAttributeValue) {
		if (memberAttribute == null || memberAttribute.getType() == null) {
			return;
		}

		switch (memberAttribute.getTypeName()) {
		case name:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setName((String) memberAttributeValue);
			}
			break;
		case gender:
			if (memberAttributeValue instanceof Member.Gender || memberAttributeValue == null) {
				Member.Gender gender = (Member.Gender) memberAttributeValue;
				setGender(gender.ordinal());
			}
			break;
		case birth:
			if (memberAttributeValue instanceof Date || memberAttributeValue == null) {
				setBirth((Date) memberAttributeValue);
			}
			break;
		case area:
			if (memberAttributeValue instanceof Area || memberAttributeValue == null) {
				Area area = (Area) memberAttributeValue;
				setArea(area);
			}
			break;
		case address:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setAddress((String) memberAttributeValue);
			}
			break;
		case zipCode:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setZipCode((String) memberAttributeValue);
			}
			break;
		case phone:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setPhone((String) memberAttributeValue);
			}
			break;
		case mobile:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setMobile((String) memberAttributeValue);
			}
			break;
		case text:
		case select:
			if ((memberAttributeValue instanceof String || memberAttributeValue == null) && memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, memberAttributeValue);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case checkbox:
			if ((memberAttributeValue instanceof Collection || memberAttributeValue == null) && memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, memberAttributeValue != null ? JsonUtils.toJson(memberAttributeValue) : null);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		}
	}

	/**
	 * 移除所有会员注册项值
	 */
	public void removeAttributeValue() {
		setName(null);
		setGender(null);
		setBirth(null);
		setArea(null);
		setAddress(null);
		setZipCode(null);
		setPhone(null);
		setMobile(null);
		for (int i = 0; i < ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
			try {
				PropertyUtils.setProperty(this, propertyName, null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	
}
