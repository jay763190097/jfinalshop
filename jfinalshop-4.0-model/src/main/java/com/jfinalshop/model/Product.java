package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.SpecificationValue;
import com.jfinalshop.model.base.BaseProduct;
import com.jfinalshop.util.ObjectUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Model - 商品
 * 
 * 
 */
public class Product extends BaseProduct<Product> {
	private static final long serialVersionUID = -428218912308567347L;
	public static final Product dao = new Product();
	
	/** 货品 */
	private Goods goods;
	
	/** 规格值 */
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

	/** 购物车项 */
	private List<CartItem> cartItems = new ArrayList<CartItem>();

	/** 订单项 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 发货项 */
	private List<ShippingItem> shippingItems = new ArrayList<ShippingItem>();

	/** 到货通知 */
	private List<ProductNotify> productNotifies = new ArrayList<ProductNotify>();

	/** 库存记录 */
	private List<StockLog> stockLogs = new ArrayList<StockLog>();

	/** 赠品促销 */
	private List<Promotion> giftPromotions = new ArrayList<Promotion>();
	
	
	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public Goods getGoods() {
		if (ObjectUtils.isEmpty(goods)) {
			goods = Goods.dao.findById(getGoodsId());
		}
		return goods;
	}

	/**
	 * 设置货品
	 * 
	 * @param goods
	 *            货品
	 */
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	
	
	/**
	 * 获取规格值
	 * 
	 * @return 规格值
	 */
	public List<SpecificationValue> getSpecificationValuesConverter() {
		if (CollectionUtils.isEmpty(specificationValues)) {
			JSONArray specificationValueArrays = JSONArray.parseArray(getSpecificationValues());
			if (CollectionUtils.isNotEmpty(specificationValueArrays)) {
				for(int i = 0; i < specificationValueArrays.size(); i++) {
					specificationValues.add(JSONObject.parseObject(specificationValueArrays.getString(i), SpecificationValue.class));
				}
			}
		}
		return specificationValues;
	}

	/**
	 * 设置规格值
	 * 
	 * @param specificationValues
	 *            规格值
	 */
	public void setSpecificationValuesConverter(List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}


	/**
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	public List<CartItem> getCartItems() {
		if (CollectionUtils.isEmpty(cartItems)) {
			String sql = "SELECT * FROM `cart_item` WHERE product_id = ?";
			cartItems = CartItem.dao.find(sql, getId());
		}
		return cartItems;
	}

	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		if (CollectionUtils.isEmpty(orderItems)) {
			String sql = "SELECT * FROM `order_item` WHERE product_id = ?";
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
	 * 获取发货项
	 * 
	 * @return 发货项
	 */
	public List<ShippingItem> getShippingItems() {
		if (CollectionUtils.isEmpty(shippingItems)) {
			String sql = "SELECT * FROM `shipping_item` WHERE product_id = ?";
			shippingItems = ShippingItem.dao.find(sql, getId());
		}
		return shippingItems;
	}

	/**
	 * 设置发货项
	 * 
	 * @param shippingItems
	 *            发货项
	 */
	public void setShippingItems(List<ShippingItem> shippingItems) {
		this.shippingItems = shippingItems;
	}

	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `product_notify` WHERE product_id = ?";
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
	 * 获取库存记录
	 * 
	 * @return 库存记录
	 */
	public List<StockLog> getStockLogs() {
		if (CollectionUtils.isEmpty(stockLogs)) {
			String sql = "SELECT * FROM `stock_log` WHERE product_id = ?";
			stockLogs = StockLog.dao.find(sql, getId());
		}
		return stockLogs;
	}

	/**
	 * 设置库存记录
	 * 
	 * @param stockLogs
	 *            库存记录
	 */
	public void setStockLogs(List<StockLog> stockLogs) {
		this.stockLogs = stockLogs;
	}

	/**
	 * 获取赠品促销
	 * 
	 * @return 赠品促销
	 */
	public List<Promotion> getGiftPromotions() {
		if (CollectionUtils.isEmpty(giftPromotions)) {
			String sql = "SELECT p.* FROM promotion_gift pg LEFT JOIN promotion p ON pg.gift_promotions = p.id WHERE pg.gifts = ?";
			giftPromotions = Promotion.dao.find(sql, getId());
		}
		return giftPromotions;
	}

	/**
	 * 设置赠品促销
	 * 
	 * @param giftPromotions
	 *            赠品促销
	 */
	public void setGiftPromotions(List<Promotion> giftPromotions) {
		this.giftPromotions = giftPromotions;
	}

	
	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return getGoods() != null ? getGoods().getName() : null;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Goods.Type getType() {
		return getGoods() != null ? getGoods().getTypeName() : null;
	}

	/**
	 * 获取展示图片
	 * 
	 * @return 展示图片
	 */
	public String getImage() {
		return getGoods() != null ? getGoods().getImage() : null;
	}

	/**
	 * 获取单位
	 * 
	 * @return 单位
	 */
	public String getUnit() {
		return getGoods() != null ? getGoods().getUnit() : null;
	}

	/**
	 * 获取重量
	 * 
	 * @return 重量
	 */
	public Integer getWeight() {
		return getGoods() != null ? getGoods().getWeight() : null;
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public boolean getIsMarketable() {
		return getGoods() != null && BooleanUtils.isTrue(getGoods().getIsMarketable());
	}

	/**
	 * 获取是否列出
	 * 
	 * @return 是否列出
	 */
	public boolean getIsList() {
		return getGoods() != null && BooleanUtils.isTrue(getGoods().getIsList());
	}

	/**
	 * 获取是否置顶
	 * 
	 * @return 是否置顶
	 */
	public boolean getIsTop() {
		return getGoods() != null && BooleanUtils.isTrue(getGoods().getIsTop());
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return getGoods() != null && BooleanUtils.isTrue(getGoods().getIsDelivery());
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getGoods() != null ? getGoods().getPath() : null;
	}

	/**
	 * 获取URL
	 * 
	 * @return URL
	 */
	public String getUrl() {
		return getGoods() != null ? getGoods().getUrl() : null;
	}

	/**
	 * 获取缩略图
	 * 
	 * @return 缩略图
	 */
	public String getThumbnail() {
		return getGoods() != null ? getGoods().getThumbnail() : null;
	}

	/**
	 * 获取可用库存
	 * 
	 * @return 可用库存
	 */
	public int getAvailableStock() {
		int availableStock = getStock() - getAllocatedStock();
		return availableStock >= 0 ? availableStock : 0;
	}

	/**
	 * 获取是否库存警告
	 * 
	 * @return 是否库存警告
	 */
	public boolean getIsStockAlert() {
		Setting setting = SystemUtils.getSetting();
		return setting.getStockAlertCount() != null && getAvailableStock() <= setting.getStockAlertCount();
	}

	/**
	 * 获取是否缺货
	 * 
	 * @return 是否缺货
	 */
	public boolean getIsOutOfStock() {
		return getAvailableStock() <= 0;
	}

	/**
	 * 获取规格值ID
	 * 
	 * @return 规格值ID
	 */
	public List<Integer> getSpecificationValueIds() {
		List<Integer> specificationValueIds = new ArrayList<Integer>();
		if (CollectionUtils.isNotEmpty(getSpecificationValuesConverter())) {
			for (SpecificationValue specificationValue : getSpecificationValuesConverter()) {
				specificationValueIds.add(specificationValue.getId());
			}
		}
		return specificationValueIds;
	}

	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<String> getSpecifications() {
		List<String> specifications = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(getSpecificationValuesConverter())) {
			for (SpecificationValue specificationValue : getSpecificationValuesConverter()) {
				specifications.add(specificationValue.getValue());
			}
		}
		return specifications;
	}

	/**
	 * 获取有效促销
	 * 
	 * @return 有效促销
	 */
	public Set<Promotion> getValidPromotions() {
		return getGoods() != null ? getGoods().getValidPromotions() : Collections.<Promotion> emptySet();
	}

	/**
	 * 是否存在规格
	 * 
	 * @return 是否存在规格
	 */
	public boolean hasSpecification() {
		return CollectionUtils.isNotEmpty(getSpecificationValuesConverter());
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	public boolean isValid(Promotion promotion) {
		return getGoods() != null ? getGoods().isValid(promotion) : false;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<OrderItem> orderItems = getOrderItems();
		if (orderItems != null) {
			for (OrderItem orderItem : orderItems) {
				orderItem.setProduct(null);
			}
		}
		List<ShippingItem> shippingItems = getShippingItems();
		if (shippingItems != null) {
			for (ShippingItem shippingItem : getShippingItems()) {
				shippingItem.setProduct(null);
			}
		}
		List<Promotion> giftPromotions = getGiftPromotions();
		if (giftPromotions != null) {
			for (Promotion giftPromotion : giftPromotions) {
				giftPromotion.getGifts().remove(this);
			}
		}
	}

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
