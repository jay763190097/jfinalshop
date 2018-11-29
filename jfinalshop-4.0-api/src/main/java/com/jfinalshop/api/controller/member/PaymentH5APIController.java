package com.jfinalshop.api.controller.member;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.hasor.core.Inject;
import net.sf.json.JSONString;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.common.sms.SMSManager;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.api.config.URL;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.utils.AARProtocol;
import com.jfinalshop.api.utils.HttpUtils;
import com.jfinalshop.api.utils.Md5Util;
import com.jfinalshop.api.utils.RsaKit;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.entity.AjaxResult;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.model.Sn;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentLogService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.shiro.session.RedisManager;
import com.jfinalshop.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * H5支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/payment")
public class PaymentH5APIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(PaymentH5APIController.class);

	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentLogService paymentLogService;
	@Inject
	private SnDao snDao;
	/** redis管理 */
	private RedisManager redisManager = new RedisManager();
	public static final String WEIXIN_PAYMENT_PLUGIN = "weixinPaymentPlugin";
	
	Prop prop = PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
	private Boolean isTestURL = prop.getBoolean("accessToken.isTestURL", false);
	
	private Res res = I18n.use();
	private AjaxResult ajax = new AjaxResult();
	
/*	@Override
	public ApiConfig getApiConfig() {
		ApiConfig apiConfig = new ApiConfig();
		// 配置微信 API 相关常量
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		apiConfig.setToken(pluginConfig.getAttribute("mch_id"));
		apiConfig.setAppId(pluginConfig.getAttribute("appid"));
		apiConfig.setAppSecret(pluginConfig.getAttribute("appSecret"));
		apiConfig.setEncryptMessage(false);
		return apiConfig;
	}*/
        //获取回调
	    public void getNotify (){
	        try {
	            System.out.println("调用回调方法了没");
	            // 总金额
	            String amount = getRequest().getParameter("total_fee");
	            //String amount = "1000";
	            // 返回状态码
	            String result_code = getPara("result_code");
	            //String result_code = "0000";
	            // 商户订单号
	            String orderNo = getPara("sn");
	            // 交易状态
	            String status = getRequest().getParameter("status");
	            //结果描述descdes
	            String  desc= getRequest().getParameter("desc");
	            //银行名称
	            String  bankName= getRequest().getParameter("bankName");
	            //用户名称
	            String  userid= getRequest().getParameter("username");
	            //String userid = "15264259878";
	            //交易类型
	            String trade_type = "银生宝";
	            // 交易完成回调时间，格式为yyyyMMddHHmmss
	            Date date = new Date();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    		String time_end = sdf.format(date);;
	    		//String sn = getPara("sn");
	    		Member member = null;
	    		/*Member member = memberService.findByUsername(userid);
	    		Long memberId = member.getId();
	    		logger.info("当前用户"+member.getId().equals(userid));*/
	    		//String token = getSessionAttr("username_token");
	    		//String token = getPara("token");
	    		String ID = orderNo+"code";
	    		logger.info("key值"+ID);
	    		String token = redisManager.get(ID);
	    		logger.info("token有没有"+token);
	    		if (StrKit.notBlank(token)) {
	            	member = TokenManager.getMe().validate(token);
	            	if (member != null && StrKit.isBlank(member.getOpenId())) {
	        			member = memberService.find(member.getId());
	        		}
	            }
	    		PaymentLog paymentLog = paymentLogService.findBySn(orderNo);
	    		if(paymentLog!=null){
	    		paymentLog.setMemberId(member.getId());
	    		paymentLog.setMemo(" total_fee = " + amount  + ",trade_type = " + trade_type + ",time_end=" + time_end);
	    		//paymentLog.put("transaction_id", transaction_id);
	    		// 执行更新订单
	    		if("0000".equals(result_code)){
	    		paymentLogService.handleH5(paymentLog);
	    		logger.info("回调成功");
	    		renderJson(new BaseResponse(Code.SUCCESS,"SUCCESS"));
	    		}else{
	    			logger.info("操作不成功");
	    			renderJson(new BaseResponse(Code.FAIL,"FAIL"));
	    		}
	    		}else{
	    			renderArgumentError("是一笔单号吗？");
	    		}
	        } catch (Exception e) {
	            logger.warn("",e);
	        }
	    }
	  //获取回调    移动端专用
	    public void getNotifyAPP(){
	        try {
	            System.out.println("调用回调方法了没");
	            // 总金额
	            String amount = getRequest().getParameter("total_fee");
	            //String amount = "1000";
	            // 返回状态码
	            String result_code = getPara("result_code");
	            //String result_code = "0000";
	            // 商户订单号
	             String orderNo = getPara("orderNo");
	             logger.info("外面到底是什么"+orderNo);
	             if(orderNo==null){
	            	 orderNo = getPara("orderId");
	            	  logger.info("里面到底是什么"+orderNo);
	             }
	          
	            // 交易状态
	            String status = getRequest().getParameter("status");
	            //结果描述descdes
	            String  desc= getRequest().getParameter("desc");
	            //银行名称
	            String  bankName= getRequest().getParameter("bankName");
	            //用户名称
	            String  userid= getRequest().getParameter("username");
	            //String userid = "15264259878";
	            //交易类型
	            String trade_type = "银生宝";
	            // 交易完成回调时间，格式为yyyyMMddHHmmss
	            Date date = new Date();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    		String time_end = sdf.format(date);;
	    		//String sn = getPara("sn");
	    		Member member = null;
	    		/*Member member = memberService.findByUsername(userid);
	    		Long memberId = member.getId();
	    		logger.info("当前用户"+member.getId().equals(userid));*/
	    		//String token = getSessionAttr("username_token");
	    		//String token = getPara("token");
	    		String ID = orderNo+"code";
	    		String token = redisManager.get(ID);
	    		if (StrKit.notBlank(token)) {
	            	member = TokenManager.getMe().validate(token);
	            	if (member != null && StrKit.isBlank(member.getOpenId())) {
	        			member = memberService.find(member.getId());
	        		}
	            }
	    		PaymentLog paymentLog = paymentLogService.findBySn(orderNo);
	    		if(paymentLog!=null){
	    		paymentLog.setMemberId(member.getId());
	    		paymentLog.setMemo(" total_fee = " + amount  + ",trade_type = " + trade_type + ",time_end=" + time_end);
	    		//paymentLog.put("transaction_id", transaction_id);
	    		// 执行更新订单
	    		if("0000".equals(result_code)){
	    		paymentLogService.handleH5(paymentLog);
	    		logger.info("回调成功");
	    		renderJson(new BaseResponse(Code.SUCCESS,"SUCCESS"));
	    		}else{
	    			logger.info("操作不成功");
	    			renderJson(new BaseResponse(Code.FAIL,"FAIL"));
	    		}
	    		}else{
	    			renderArgumentError("是一笔单号吗？");
	    		}
	        } catch (Exception e) {
	            logger.warn("",e);
	        }
	    }
	/**
	 * H5专用
	 * 用于将支付信息入库 回调之前
	 */ 
	public void savePaymentLog(){
		//getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String typeName = getPara("type", "payment");
		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		//String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		String paymentMothod = getPara("paymentMothod");
		String amountStr = getPara("amount");
		BigDecimal amount = StrKit.notBlank(amountStr) ? new BigDecimal(amountStr) : null;
		
		if (type == null) {
			renderArgumentError("支付类型不能为空! ");
			return;
		}
		
		Member member = null;
		String token = getPara("token");
		logger.info("传递的token值"+token);
		String generate = SMSManager.getMe().generateToken(sn,token);
		logger.info("存入的token"+generate);
        if (StrKit.notBlank(token)) {
        	member = TokenManager.getMe().validate(token);
        	if (member != null && StrKit.isBlank(member.getOpenId())) {
    			member = memberService.find(member.getId());
    		}
        }
        Order order = orderService.findBySn(sn);
		if (order == null || !member.getId().equals(order.getMemberId()) || orderService.isLocked(order, member, true)) {
			renderArgumentError("订单不能为空或订单不属于当前用户! ");
			return;
		}
		if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
			renderArgumentError("所选方式不是在线支付! ");
			return;
		}
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			renderArgumentError("应付金额小于等于0! ");
			return;
		}
		
		PaymentLog paymentLog = new PaymentLog();
		paymentLog.setSn(sn);
		paymentLog.setType(type.ordinal());
		paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
		paymentLog.setFee(order.getFee());
		paymentLog.setAmount(amount); // 改成取【应付金额】
		//paymentLog.setPaymentPluginId(paymentPluginId);
		paymentLog.setPaymentPluginName(paymentMothod);
		paymentLog.setOrderId(order.getId());
		paymentLog.setMember(member);
		PaymentLog log = paymentLogService.saveH5(paymentLog);
        if(log!=null){
        	renderJson(new BaseResponse(Code.SUCCESS,"保存成功"));
        }else{
        	renderArgumentError("保存失败 ");
        }
	}
	
	/**
	 * 插件通知H5 银生宝
	 */ 
/*	public void getNotify(){
		//String token = getPara("token");
		String username = getPara("username");
		String total_fee = getPara("total_fee");
		String result_code = getPara("result_code");
		String trade_type = getPara("trade_type");
		
		//String transaction_id = getPara("transaction_id");
		//String time_end = getPara("time_end");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String time_end = sdf.format(date);;
		String sn = getPara("sn");
		Member member = memberService.findByUsername(username);
		PaymentLog paymentLog = paymentLogService.findBySn(sn);
		paymentLog.setMemberId(member.getId());
		paymentLog.setMemo(" total_fee = " + total_fee  + ",trade_type = " + trade_type + ",time_end=" + time_end);
		//paymentLog.put("transaction_id", transaction_id);
		
		// 执行更新订单
		if("0000".equals(result_code)){
		paymentLogService.handle(paymentLog);
		Message message = new Message();
		message.set("message", "SUCCESS");
		renderJson(new DatumResponse(message));
		}else{
			renderArgumentError("操作不成功");
		}
	}*/
	/**
	 * 插件提交
	 */ 
	public void submit() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String typeName = getPara("type", "");
		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		String paymentPluginId = getPara("paymentPluginId");
		
		String sn = getPara("sn");
		String amountStr = getPara("amount");
		BigDecimal amount = StrKit.notBlank(amountStr) ? new BigDecimal(amountStr) : null;
		
		if (type == null) {
			renderArgumentError("支付类型不能为空! ");
			return;
		}
		
		Member member = null;
		String token = getPara("token");
        if (StrKit.notBlank(token)) {
        	member = TokenManager.getMe().validate(token);
        	if (member != null && StrKit.isBlank(member.getOpenId())) {
    			member = memberService.find(member.getId());
    		}
        }
		
		if (member == null || StrKit.isBlank(member.getOpenId())) {
			renderArgumentError("当前用户,openid不能为空! ");
			return;
		}
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			renderArgumentError("支付为空或未启用! ");
			return;
		}
		
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		switch (type) {
			case recharge: {
				if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.precision() > 15 || amount.scale() > setting.getPriceScale()) {
					renderArgumentError("充值金额异常! ");
					return;
				}
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(amount));
				paymentLog.setAmount(paymentPlugin.calculateAmount(amount));
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setMemberId(member.getId());
				paymentLog.setOrder(null);
				paymentLogService.save(paymentLog);
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), res.format("shop.payment.rechargeDescription"), getRequest());
				break;
			}
			case payment: {
				Order order = orderService.findBySn(sn);
				if (order == null || !member.getId().equals(order.getMemberId()) || orderService.isLocked(order, member, true)) {
					renderArgumentError("订单不能为空或订单不属于当前用户! ");
					return;
				}
				if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					renderArgumentError("所选方式不是在线支付! ");
					return;
				}
				if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					renderArgumentError("应付金额小于等于0! ");
					return;
				}
				
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(order.getAmount()));
				paymentLog.setAmount(paymentPlugin.calculateAmount(order.getAmountPayable())); // 改成取【应付金额】
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setOrderId(order.getId());
				paymentLog.setMember(null);
				paymentLogService.save(paymentLog);
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), res.format("shop.payment.paymentDescription", order.getSn()), getRequest());
				break;
			}
		}
		Map<String, String> params = convertAttributes(parameterMap);
		params.put("openid", member.getOpenId());
		params.put("sign",  PaymentKit.createSign(params, pluginConfig.getAttribute("paternerKey"))); // 签名
		
		// 统一下单
		
		String xmlResult = PaymentApi.pushOrder(params);
		
		LogKit.info("xml >>> " + xmlResult);

		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		String result_code = result.get("result_code");
		String err_code_des = result.get("err_code_des");
		if (StrKit.isBlank(result_code) || StringUtils.equals("FAIL", result_code)) {
			renderArgumentError(err_code_des);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");

		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appId", pluginConfig.getAttribute("appid"));
		packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		packageParams.put("nonceStr", System.currentTimeMillis() + "");
		packageParams.put("package", "prepay_id=" + prepay_id);
		packageParams.put("signType", "MD5");
		String packageSign = PaymentKit.createSign(packageParams, pluginConfig.getAttribute("paternerKey"));
		packageParams.put("paySign", packageSign);

		String jsonStr = JsonUtils.toJson(packageParams);
		ajax.success(jsonStr);
		LogKit.info("返回ajax: " + ajax);
		renderJson(ajax);
	}
	
	/**
	 * 插件通知
	 * 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
	 */
	public void paymentNotify() {
		String xmlMsg = HttpKit.readData(getRequest());
		LogKit.info("支付通知=" + xmlMsg);
		
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
		// 总金额
		String total_fee = params.get("total_fee"); 
		// 微信支付订单号
		String transaction_id = params.get("transaction_id"); 
		// 商户订单号
		String out_trade_no = params.get("out_trade_no"); 
		// 交易类型
		String trade_type = params.get("trade_type");
		// 支付完成时间，格式为yyyyMMddHHmmss
		String time_end = params.get("time_end");
		// 以下是附加参数
		String openId = params.get("openid");
		
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态 避免已经成功、关闭、退款的订单被再次更新
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		if (paymentPlugin != null && PaymentKit.verifyNotify(params, pluginConfig.getAttribute("paternerKey"))) {
			PaymentLog paymentLog = paymentLogService.findBySn(out_trade_no);
			if (paymentLog != null) {
				Member member = memberService.findByOpenId(openId);
				paymentLog.setMemberId(member.getId());
				paymentLog.setMemo(" total_fee = " + total_fee  + ",trade_type = " + trade_type  + ",transaction_id = " + transaction_id + ",time_end=" + time_end);
				paymentLog.put("transaction_id", transaction_id);
				
				// 执行更新订单
				paymentLogService.handle(paymentLog);
				
				// 发送通知等
				Map<String, String> xml = new HashMap<String, String>();
				xml.put("return_code", "SUCCESS");
				xml.put("return_msg", "OK");
				renderText(PaymentKit.toXml(xml));
				return;
			}
		}
		renderText("");
	}
	
	/**
	 * 支付成功
	 * 
	 */
	public void payResult() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String sn = getPara("sn");
		if (StrKit.isBlank(sn)) {
			renderArgumentError("订单编号不能为空!");
			return;
		}
		
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单编号没有找到!");
			return;
		}
		
		// 返回支付方式
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sn", order.getSn());
		map.put("status", order.getStatus());
		map.put("status_name", order.getStatusName());
		map.put("amountPaid", order.getAmountPaid());
		map.put("paymentMethodName", order.getPaymentMethodName());
		map.put("shippingMethodName", order.getShippingMethodName());
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 订单支付接口
	 * 
	 */
	public void orderPay(){
	        String paymentMothod = getPara("PaymentMothod");
	        System.out.println(paymentMothod);
	        String orderNo = getPara("orderNo");
	        String userid = getPara("accountId");
	        String token = getPara("username_token");
	        String typeName = getPara("type", "payment");
	        String amount = getPara("amount");
	        //交易信息日志
	        BigDecimal amount1 = StrKit.notBlank(amount) ? new BigDecimal(amount) : null;
	        PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
			if (typeName == null) {
				renderArgumentError("支付类型不能为空! ");
				return;
			}
			
			Member member = null;
	       if (StrKit.notBlank(token)) {
	        	member = TokenManager.getMe().validate(token);
	        	if (member != null && StrKit.isBlank(member.getOpenId())) {
	    			member = memberService.find(member.getId());
	    		}
	        }
			//member = memberService.findByUsername(userid);
	        Order order = orderService.findBySn(orderNo);
			if (order == null || !member.getId().equals(order.getMemberId()) || orderService.isLocked(order, member, true)) {
				renderArgumentError("订单不能为空或订单不属于当前用户! ");
				return;
			}
			if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
				renderArgumentError("所选方式不是在线支付! ");
				return;
			}
			if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
				renderArgumentError("应付金额小于等于0! ");
				return;
			}
			logger.info("传递的token值"+token);
			String generate = SMSManager.getMe().generateToken(orderNo,token);
			logger.info("存入的token"+generate);
			PaymentLog paymentLog = new PaymentLog();
			paymentLog.setSn(orderNo);
			paymentLog.setType(type.ordinal());
			paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
			paymentLog.setFee(order.getFee());
			paymentLog.setAmount(amount1); // 改成取【应付金额】
			//paymentLog.setPaymentPluginId(paymentPluginId);
			paymentLog.setPaymentPluginName(paymentMothod);
			paymentLog.setOrderId(order.getId());
			paymentLog.setMember(member);
			PaymentLog log = paymentLogService.saveH5(paymentLog);
			Boolean bll = log!=null;
	        logger.info("是否生成交易记录"+bll);
	       /* String res=null;
	        try {
	                res = HttpUtils.POSTMethod(PropKit.get("savepayment"),mapp);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        JSONObject result = JSONObject.parseObject(res);
	        logger.info(result.getString("message"));*/
	        switch(paymentMothod){
	            case "银行卡支付": {
	                Map<String, String> map = new LinkedHashMap<String, String>(10);
	                //map.put("accountId", "2120180312153713001");
	                map.put("accountId", "1120180126093952001");
	                map.put("customerId", userid);
	                map.put("orderNo", orderNo);
	                map.put("commodityName", "watch");
	                map.put("amount", amount);
	                map.put("responseUrl", "http://114.55.93.111:8080/jfinalshop-4.0-api/api/payment/getNotifyAPP");
	                map.put("pageResponseUrl", "http://114.55.93.111:8080/jfinalshop-4.0-h5/pages/pay-success.html");
	                map.put("key", "123456");
	                map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
	                map.remove("key");
	                System.out.println(Md5Util.buildWithQueryString(map));
	                try {
	                    String post = HttpKit.post(URL.WAP_URL, map, null);
	                    logger.info("进来了吗？？？？");
	                    if("".equals(post)){
	                    	renderJson(new BaseResponse(Code.FAIL,"交易失败"));
	                    }
	                    logger.info("成功进来了吗？？？？");
	                    /*JSONObject result = JSONObject.parseObject(post);
	                    String json = result.toJSONString();*/
	                    //logger.info("报文格式"+json);
	                    logger.info(post);
	                    renderJson(new BaseResponse(Code.SUCCESS,post));
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                break;
	                }

	            case "H5支付": {
	                JSONObject json = new JSONObject();
	                JSONObject obj = new JSONObject();
	                obj.put("accountId", "2120180312153713001");
	                //obj.put("accountId", "1120180126093952001");
	                obj.put("payType", "1");
	                obj.put("orderId", orderNo);
	                obj.put("commodity", "watch");
	                obj.put("amount", amount);
	                obj.put("responseUrl", "http://114.55.93.111:8080/jfinalshop-4.0-api/api/payment/getNotifyAPP");
	                obj.put("ext", "");
	                String msgText = obj.toJSONString();
                    //此处的私钥是商户私钥
	                json.put("accountId", "2120180312153713001");
	                //json.put("accountId", "1120180126093952001");
	                json.put("msg", msgText);
	                /*json.put("sign", RsaKit.sign(msgText, URL.pricateKey));
	                String data = AARProtocol.encrypt(json.toJSONString(), URL.publicKey);*/
	                json.put("sign", RsaKit.sign(msgText, URL.pricateKey));
	                String data = AARProtocol.encrypt(json.toJSONString(), URL.publicKey);
	                try {
	                    String sop = HttpKit.post(URL.H5_URL, data);
	                    if (sop != null) {
	                        //解密返回信息 此处是商户私钥
	                        //String ss = AARProtocol.decrypt(sop, URL.pricateKey);
	                        String ss = AARProtocol.decrypt(sop, URL.pricateKey);
	                        JSONObject jsonObject = JSONObject.parseObject(ss);
	                        String response = jsonObject.getString("msg");
	                        JSONObject results = JSONObject.parseObject(response);
	                        String resultCode = results.getString("result_code");
	                        logger.info(resultCode);
	                        String qrcode = results.getString("qrcode");
	                        String message = results.getString("result_msg");
	                        logger.info(message);
	                        if ("0000".equals(resultCode)) {
	                            logger.info("创建结果"+message);
	                            renderJson(new BaseResponse(Code.SUCCESS,qrcode));
	                        }else if("1111".equals(resultCode)){
	                            renderJson(new BaseResponse(Code.QUOTA,message));
	                        } else {
	                            logger.info("操作失败：返回码为" + resultCode);
	                            renderJson(new BaseResponse(Code.FAIL,message));
	                        }
	                    } else {
	                        renderJson(new BaseResponse(Code.ERROR,"网关错误"));
	                        break;
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                break;
	            }
	            case "二维码支付": {
	                JSONObject json = new JSONObject();
	                JSONObject obj = new JSONObject();
	                obj.put("accountId", "2120180312153713001");
	                obj.put("payType", "1");
	                obj.put("orderId", orderNo);
	                obj.put("commodity", "watch");
	                obj.put("amount", amount);
	                obj.put("responseUrl", "http://114.55.93.111:8080/jfinalshop-4.0-api/api/payment/getNotifyAPP");
	                obj.put("ext", "");
	                String msgText = obj.toJSONString();
	                System.out.println(msgText);
	                //此处的私钥是商户私钥
	                json.put("accountId", "2120180312153713001");
	                json.put("msg", msgText);
	                json.put("sign", RsaKit.sign(msgText, URL.pricateKey));
	                String data = AARProtocol.encrypt(json.toJSONString(), URL.publicKey);
	                try{
	                    String sop = HttpKit.post(URL.Code_URL, data);
	                    if (sop != null) {
	                        //解密返回信息 此处是商户私钥
	                        String ss = AARProtocol.decrypt(sop, URL.pricateKey);
	                        JSONObject jsonObject = JSONObject.parseObject(ss);
	                        String response = jsonObject.getString("msg");
	                        JSONObject results = JSONObject.parseObject(response);
	                        String resultCode = results.getString("result_code");
	                        String qrcode = results.getString("qrcode");
	                        String message = results.getString("result_msg");
	                        if ("0000".equals(resultCode)) {
	                            logger.info(message);
	                            renderJson(new BaseResponse(Code.SUCCESS,qrcode));
	                        }else if("1111".equals(resultCode)){
	                            renderJson(new BaseResponse(Code.QUOTA,message));
	                        } else {
	                            logger.info("操作失败：返回码为" + resultCode);
	                            renderJson(new BaseResponse(Code.FAIL,message));
	                        }
	                    }else {
	                        renderJson(new BaseResponse(Code.ERROR,"网关错误"));
	                    }
	                }catch(Exception e){
	                    e.printStackTrace();
	                }
	                break;
	            }
	        }
	    }
        
		/**
		 * H5支付
		 * 
		 */
	    public void queryOrder(){
	    	String accountId = getPara("accountId","1120180126093952001");
	    	String orderId = getPara("orderId");
	    	if(StringUtils.isEmpty(accountId)){
	    		renderJson(new BaseResponse(Code.ARGUMENT_ERROR,"用户名不能为空"));
	    	}else if(StringUtils.isEmpty(orderId)){
	    		renderJson(new BaseResponse(Code.ARGUMENT_ERROR,"订单号不能为空"));
	    	}
	    	JSONObject msg = new JSONObject();
	        msg.put("accountId",accountId);
	        msg.put("orderNo",orderId);
	        String msgText = msg.toJSONString();
	        JSONObject result = new JSONObject();
	        result.put("msg",msgText);
	        result.put("sign", RsaKit.sign(msgText,URL.pricateKeys));
	        System.out.println("快捷订单查询："+result.toJSONString());
	        result.put("accountId",accountId);
	        String result_msg = AARProtocol.encrypt(result.toJSONString(), URL.publicKeys);
	        String post = HttpKit.post(URL.WAPORDER_URL, result_msg);
	        String s = null;
             try {
                 s = new String(AARProtocol.decrypt(post, URL.pricateKeys).getBytes(),"UTF-8");
                 JSONObject s1 = JSONObject.parseObject(s);
                 String ress = s1.getString("msg");
                 String status=JSONObject.parseObject(ress).getString("status");
                 System.out.println("交易的状态"+status);
                 logger.info("再次确认"+status);
                	 if("00".equals(status)){
                		 renderJson(new BaseResponse(Code.TRADE_SUCCESS,"交易成功"));
                	 }else if("10".equals(status)){ 
                		 renderJson(new BaseResponse(Code.PROCESSING,"交易处理中"));
                	 }else{
                		 renderJson(new BaseResponse(Code.TRADE_FAILED,"交易失败"));
                	 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
             }
	    
	/**
	 * H5支付
	 * 
	 *//*
	public void H5Pay(){
		String orderNo = getPara("sn");
		String amount = getPara("amount");
        JSONObject json = new JSONObject();
        JSONObject obj = new JSONObject();
        obj.put("accountId", "2120180312153713001");
        obj.put("payType", "1");
        obj.put("orderId", orderNo);
        obj.put("commodity", "watch");
        obj.put("amount", amount);
        obj.put("responseUrl", "http://114.55.93.111:8080/jfinalshop-4.0-api/api/payment/getNotify");
        obj.put("ext", "");
        String msgText = obj.toJSONString();
        System.out.println(msgText);
//此处的私钥是商户私钥
        json.put("accountId", "2120180312153713001");
        json.put("msg", msgText);
        json.put("sign", RsaKit.sign(msgText, URL.pricateKey));
        String data = AARProtocol.encrypt(json.toJSONString(), URL.publicKey);
        try {
            String sop = HttpKit.post(URL.Code_URL, data);
            if (sop != null) {
                //解密返回信息 此处是商户私钥
                String ss = AARProtocol.decrypt(sop, URL.pricateKey);
                JSONObject jsonObject = JSONObject.parseObject(ss);
                String response = jsonObject.getString("msg");
                JSONObject results = JSONObject.parseObject(response);
                String resultCode = results.getString("result_code");
                String qrcode = results.getString("qrcode");
                String message = results.getString("result_msg");
                if ("0000".equals(resultCode)) {
                    logger.info(message);
                    renderJson(new BaseResponse(Code.SUCCESS,qrcode));
                }else if("1111".equals(resultCode)){
                	renderJson(new BaseResponse(Code.QUOTA,"超出限额"));
				}else{
					renderJson(new BaseResponse(Code.FAIL,"交易失败"));            
				}
			            }
				}catch(Exception e){
					System.out.println(e);
					}
				}
	*//**
	 * 银生宝支付跳转接口
	 * 
	 *//*
	public void unspay() {
	    //String orderNo = getPara("orderNo");//sn
	    String userid = getPara("accountId");//账户号码
	    Map<String, String> mapp = new HashMap<String, String>();
	    mapp.put("token",token);
	    mapp.put("type",typeName);
	    mapp.put("amount",amount);
	    mapp.put("sn",orderNo);
	    
		String typeName = getPara("type", "payment");//支付类型
		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		//String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		String amountStr = getPara("amount");
		BigDecimal amount = StrKit.notBlank(amountStr) ? new BigDecimal(amountStr) : null;
		
		if (type == null) {
			renderArgumentError("支付类型不能为空! ");
			return;
		}
		
		Member member = null;
		//String token = getPara("username_token");
		member = memberService.findByUsername(userid);
        if (StrKit.notBlank(token)) {
        	member = TokenManager.getMe().validate(token);
        	if (member != null && StrKit.isBlank(member.getOpenId())) {
    			member = memberService.find(member.getId());
    		}
        }
        Order order = orderService.findBySn(sn);
		if (order == null || !member.getId().equals(order.getMemberId()) || orderService.isLocked(order, member, true)) {
			renderArgumentError("订单不能为空或订单不属于当前用户! ");
			return;
		}
		if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
			renderArgumentError("所选方式不是在线支付! ");
			return;
		}
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			renderArgumentError("应付金额小于等于0! ");
			return;
		}
		
		PaymentLog paymentLog = new PaymentLog();
		paymentLog.setSn(null);
		paymentLog.setType(type.ordinal());
		paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
		paymentLog.setFee(order.getFee());
		paymentLog.setAmount(amount); // 改成取【应付金额】
		//paymentLog.setPaymentPluginId(paymentPluginId);
		paymentLog.setPaymentPluginName("银生宝");
		paymentLog.setOrderId(order.getId());
		paymentLog.setMember(null);
		paymentLogService.save(paymentLog);
	    Map<String,String> map = new LinkedHashMap<String,String>(10);
	    map.put("accountId","1120180126093952001");
	    map.put("customerId",userid);
	    map.put("orderNo",sn);
	    map.put("commodityName","watch");
	    map.put("amount","0.01");
	    map.put("responseUrl","http://114.55.93.111:8080/jfinalshop-4.0-api/api/payment/getNotify");
	    //map.put("responseUrl","http://cmtqug.natappfree.cc/jfinalshop-4.0-h5/callback/update");
	    //map.put("pageResponseUrl","http://cmtqug.natappfree.cc/jfinalshop-4.0-h5/pages/pay-success.html");
	    map.put("pageResponseUrl","http://114.55.93.111:8080/jfinalshop-4.0-h5/pages/pay-success.html");
	    map.put("key","hangyipai1");
	    map.put("key","123456");
	    map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
	    map.remove("key");
	    System.out.println(Md5Util.buildWithQueryString(map));
	    String post = HttpKit.post(URL.WAP_URL, map, null);
	    if("".equals(post)){
	        renderJson(new BaseResponse(Code.FAIL,"交易失败"));
	        return;
	    }
	    JSONObject jsonObject = JSONObject.parseObject(post);
        JSONObject jsonDatum =JSONObject.parseObject( jsonObject.getString("datum"));
        String s = jsonDatum.toJSONString();
        logger.info("报文格式"+s);
        System.out.println("报文格式"+s);
	    renderHtml(post);
	}
	*/
	/**
	 * 获取微信token
	 * 
	 */
	public void getAccessToken() {
		Long id = getParaToLong("id");
		String urlForward = getPara("url_forward");
		
		String url;
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Setting setting = SystemUtils.getSetting();
		String redirectUri = setting.getSiteUrl() + "/api/tokenApi/codeNotify.jhtml";
		String state = urlForward + "," + id;
		
		if(isTestURL) {
			url = "http://www.omengo.com/get-weixin-code.html?appid=wx7838f486e14b1a24&scope=snsapi_base&state=" + urlForward + ","+ id + "&redirect_uri=http%3A%2F%2Ftest.omengo.com%2Fapi%2FtokenApi%2FcodeNotify.jhtml";
		} else {
			url =  SnsAccessTokenApi.getAuthorizeURL(pluginConfig.getAttribute("appid"), redirectUri, state, true);
		}
		
		try {
			getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	
	/**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    public void renderArgumentError(String message) {
        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));
    }
		
	/**
	 * Object convert String
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	protected Map<String, String> convertAttributes(Map<String, Object> parameterMap) {
		Map<String, String> parameterNewMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				parameterNewMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return parameterNewMap;
	}
    
}
