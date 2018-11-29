package com.jfinalshop.api.controller;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jfinalshop.service.MemberService;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.core.Controller;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.Require;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.CartService;
import com.jfinalshop.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAPIController extends Controller {
	private static Logger logger = LoggerFactory.getLogger(BaseAPIController.class);
	/** 返回图片地址 */
	protected Setting setting = SystemUtils.getSetting();
	protected CartService cartService = enhance(CartService.class);

	@Inject
	private MemberService memberService_;
	
    /**
     * 获取当前用户对象
     * @return
     */
    protected Member getMember() {
    	String token = getPara("token");
		logger.info("token:"+token);
        if (StringUtils.isNotEmpty(token)) {
            return TokenManager.getMe().validate(token);
        }
        return null;
    }

    /**
     * 响应接口不存在*
     */
    public void render404() {
        renderJson(new BaseResponse(Code.NOT_FOUND));      
    }

    /**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    public void renderArgumentError(String message) {
        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));
    }

    /**
     * 响应数组类型*
     * @param list 结果集合
     */
    public void renderDataResponse(List<?> list) {
        DataResponse resp = new DataResponse();
        resp.setData(list);
        if (CollectionUtils.isEmpty(list)) {
            resp.setMessage("未查询到数据");
        } else {
            resp.setMessage("success");
        }
        renderJson(resp);
    }

    /**
     * 响应操作成功*
     * @param message 响应信息
     */
    public void renderSuccess(String message) {
        renderJson(new BaseResponse().setMessage(message));        
    }

    /**
     * 响应操作失败*
     * @param message 响应信息
     */
    public void renderFailed(String message) {
        renderJson(new BaseResponse(Code.FAIL, message));    
    }
    
    /**
     * 判断请求类型是否相同*
     * @param name
     * @return
     */
    protected boolean methodType(String name) {
        return getRequest().getMethod().equalsIgnoreCase(name);
    }
    
    /**
     * 判断参数值是否为空
     * @param rules
     * @return
     */
    public boolean notNull(Require rules) {
        if (rules == null || rules.getLength() < 1) {
            return true;
        }

        for (int i = 0, total = rules.getLength(); i < total; i++) {
            Object key = rules.get(i);
            String message = rules.getMessage(i);
            BaseResponse response = new BaseResponse(Code.ARGUMENT_ERROR);
            
            if (key == null) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof String && StringUtils.isEmpty((String) key)) {
                renderJson(response.setMessage(message));
                return false;
            }

            if (key instanceof Array) {
                Object[] arr = (Object[]) key;

                if (arr.length < 1) {
                    renderJson(response.setMessage(message));
                    return false;
                }
            }
        }
        return true;
    }    
    
    /**
	 * 货币格式化
	 * 
	 * @param amount
	 *            金额
	 * @param showSign
	 *            显示标志
	 * @param showUnit
	 *            显示单位
	 * @return 货币格式化
	 */
	protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
		Setting setting = SystemUtils.getSetting();
		String price = setting.setScale(amount).toString();
		if (showSign) {
			price = setting.getCurrencySign() + price;
		}
		if (showUnit) {
			price += setting.getCurrencyUnit();
		}
		return price;
	}
	
	/**
	 * 转换成前端所需的商品
	 * @param goodsList
	 * @return
	 */
	protected List<Goods> convertGoods(List<Goods> goodsList) {
		if (CollectionUtils.isNotEmpty(goodsList)) {
			for(Goods goods : goodsList) {
				Goods pGoods = new Goods();
				pGoods.setId(goods.getDefaultProduct().getId());
				pGoods.setName(goods.getName());
				pGoods.setImage(goods.getImage());
				pGoods.setPrice(new BigDecimal(currency(goods.getPrice(), false, false)));
				pGoods.setUnit(goods.getUnit());
				pGoods.setWeight(goods.getWeight());
				
				String brand = goods.getBrand() != null ? goods.getBrand().getName() : "";
				Set<Promotion> promotions = goods.getValidPromotions() != null ? goods.getValidPromotions() : null;
				
				goods.clear();
				goods._setAttrs(pGoods);
				goods.put("brand", brand);
				goods.put("promotions", promotions);
			}
		}
		return goodsList;
	}
	
	/**
	 * 转换订单详情
	 * @param orderItems
	 * @return
	 */
	protected List<OrderItem> convertOrderItem(List<OrderItem> orderItems) {
		if (CollectionUtils.isNotEmpty(orderItems)) {
			for (OrderItem orderItem : orderItems) {
				Goods goods = orderItem.getProduct().getGoods();
				orderItem.put("unit", goods.getUnit());
				orderItem.put("brand", goods.getBrand() != null ? goods.getBrand().getName() : null);
			}
		}
		return orderItems;
	}
	
	/**
	 * 将String数组转换为Long类型数组
	 * @param strs
	 * @return
	 */
	protected static Long[] convertToLong(String[] strs) {
		Long[] longs = new Long[strs.length];
		for (int i = 0; i < strs.length; i++) {
			try {
				String str = strs[i];
				Long thelong = Long.parseLong(str);
				longs[i] = thelong;
			} catch (NumberFormatException e) {
			}
		}
		return longs;
	}
	
	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车，若不存在则返回null
	 */
	protected Cart getCurrent(String cartKey) {
		Cart cart = cartService.getCartByKey(cartKey);
		if (cart != null) {
			Date expire = DateUtils.addSeconds(new Date(), Cart.TIMEOUT);
			if (!DateUtils.isSameDay(cart.getExpire(), expire)) {
				cart.setExpire(expire);
			}
		}
		return cart;
	}
}
