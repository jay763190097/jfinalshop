package com.jfinalshop.controller.admin.plugin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.weixinPayment.WeixinPaymentPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 微信支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/payment_plugin/weixin_payment")
public class WeixinPaymentController extends BaseController {

	private WeixinPaymentPlugin weixinPaymentPlugin = new WeixinPaymentPlugin();
	private PluginConfigService pluginConfigService = enhance(PluginConfigService.class);
	
	/**
	 * 安装
	 */
	public void install() {
		if (!weixinPaymentPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(weixinPaymentPlugin.getId());
			pluginConfig.setIsEnabled(false);
			//pluginConfig.setAttributes(null);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}
	
	/**
	 * 卸载
	 */
	public void uninstall() {
		if (weixinPaymentPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(weixinPaymentPlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = weixinPaymentPlugin.getPluginConfig();
		setAttr("feeTypes", PaymentPlugin.FeeType.values());
		setAttr("pluginConfig", pluginConfig);
		render("/admin/plugin/weixinPaymentPlugin/setting.ftl");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String paymentName = getPara("paymentName");
		String appid = getPara("appid");
		String mch_id = getPara("mch_id");
		String paternerKey = getPara("paternerKey");
		String appSecret = getPara("appSecret");
		String feeTypeName = getPara("feeType", null);
		PaymentPlugin.FeeType feeType = StrKit.notBlank(feeTypeName) ? PaymentPlugin.FeeType.valueOf(feeTypeName) : null;
		BigDecimal fee = new BigDecimal(getPara("fee", "0"));
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("orders");
		
		PluginConfig pluginConfig = weixinPaymentPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(PaymentPlugin.PAYMENT_NAME_ATTRIBUTE_NAME, paymentName);
		attributes.put("appid", appid);
		attributes.put("mch_id", mch_id);
		attributes.put("paternerKey", paternerKey);
		attributes.put("appSecret", appSecret);
		attributes.put(PaymentPlugin.FEE_TYPE_ATTRIBUTE_NAME, feeType.toString());
		attributes.put(PaymentPlugin.FEE_ATTRIBUTE_NAME, fee.toString());
		attributes.put(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		attributes.put(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setAttributes(attributes);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/payment_plugin/list.jhtml");
	}
}
