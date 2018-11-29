package com.jfinalshop.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.Setting;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.dao.SmsDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MessageConfig;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Sms;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service - 短信
 * 
 * 
 */
@Singleton
public class SmsService extends BaseService<Sms> {
	
	/**
	 * 构造方法
	 */
	public SmsService() {
		super(Sms.class);
	}

	
	private Configuration configuration = FreeMarkerRender.getConfiguration();
	@Inject
	private MessageConfigService messageConfigService;
	@Inject
	private SmsDao smsDao;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(4);  
	
	private void addSendTask(final String[] mobiles, final String content) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				send(mobiles, content);
			}
		});
		executorService.shutdown();
	}
	
	/**
	 * 发送短信
	 * 
	 * @param mobiles
	 *            手机号码
	 * @param content
	 *            内容
	 * @param
	 */
	private void send(String[] mobiles, String content) {
		Assert.notEmpty(mobiles);
		Assert.hasText(content);

		Setting setting = SystemUtils.getSetting();
		String smsSn = setting.getSmsSn();
		String smsKey = setting.getSmsKey();
		if (StringUtils.isEmpty(smsSn) || StringUtils.isEmpty(smsKey)) {
			return;
		}
		try {
			// 根据不同短信供应商实现发短信
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public JSONObject sendSmsVerifyCode(String mobiles, String content) {
		Assert.hasText(content);

		Setting setting = SystemUtils.getSetting();
		String smsSn = setting.getSmsSn();
		String smsKey = setting.getSmsKey();
		
		if (StringUtils.isEmpty(smsSn) || StringUtils.isEmpty(smsKey) || StringUtils.isEmpty(mobiles)) {
			return null;
		}
		// 根据不同短信供应商实现发短信
		return null;
		
	}
	
	
	/**
	 * 发送短信
	 * 
	 * @param mobiles
	 *            手机号码
	 * @param content
	 *            内容
	 * @param
	 * @param async
	 *            是否异步
	 * @return 
	 */
	public void send(String[] mobiles, String content, boolean async) {
		Assert.notEmpty(mobiles);
		Assert.hasText(content);

		if (async) {
			addSendTask(mobiles, content);
		} else {
			send(mobiles, content);
		}
	}
	


	/**
	 * 发送短信
	 * 
	 * @param mobiles
	 *            手机号码
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param sendTime
	 *            发送时间
	 * @param async
	 *            是否异步
	 */
	public void send(String[] mobiles, String templatePath, Map<String, Object> model, Date sendTime, boolean async) {
		Assert.notEmpty(mobiles);
		Assert.hasText(templatePath);

		try {
			Template template = configuration.getTemplate(templatePath);
			String content = FreeMarkerUtils.processTemplateIntoString(template, model);
			send(mobiles, content, async);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	/**
	 * 发送短信(异步)
	 * 
	 * @param mobile
	 *            手机号码
	 * @param content
	 *            内容
	 */
	public void send(String mobile, String content) {
		Assert.hasText(mobile);
		Assert.hasText(content);

		send(new String[] { mobile }, content, true);
	}

	/**
	 * 发送短信(异步)
	 * 
	 * @param mobile
	 *            手机号码
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 */
	public void send(String mobile, String templatePath, Map<String, Object> model) {
		Assert.hasText(mobile);
		Assert.hasText(templatePath);

		send(new String[] { mobile }, templatePath, model, null, true);
	}

	/**
	 * 发送会员注册短信(异步)
	 * 
	 * @param member
	 *            会员
	 */
	public void sendRegisterMemberSms(Member member) {
		if (member == null || StringUtils.isEmpty(member.getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.registerMember);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("member", member);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("registerMemberSms");
		send(member.getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单创建短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCreateOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.createOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("createOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单更新短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendUpdateOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.updateOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("updateOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单取消短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCancelOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.cancelOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("cancelOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单审核短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReviewOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.reviewOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("reviewOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单收款短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendPaymentOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.paymentOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("paymentOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单退款短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendRefundsOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.refundsOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("refundsOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单发货短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendShippingOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.shippingOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("shippingOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单退货短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReturnsOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.returnsOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("returnsOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单收货短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReceiveOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.receiveOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("receiveOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单完成短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCompleteOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.completeOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("completeOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 发送订单失败短信(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendFailOrderSms(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getMobile())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.failOrder);
		if (messageConfig == null || !messageConfig.getIsSmsEnabled()) {
			return;
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("order", order);
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("failOrderSms");
		send(order.getMember().getMobile(), templateConfig.getRealTemplatePath(), model);
	}

	/**
	 * 获取短信余额
	 * 
	 * @return 短信余额，查询失败则返回-1
	 */
	public long getBalance() {
		Setting setting = SystemUtils.getSetting();
		String smsSn = setting.getSmsSn();
		String smsKey = setting.getSmsKey();
		if (StringUtils.isEmpty(smsSn) || StringUtils.isEmpty(smsKey)) {
			return -1L;
		}
		try {
			// 根据不同短信供应商实现发短信
			LogKit.info("请添加短信供应商...");
//			Client client = new Client(smsSn, smsKey);
//			double result = client.getBalance();
//			if (result >= 0) {
//				return (long) (result * 10);
//			}
		} catch (Exception e) {
		}
		return -1L;
	}

	/**
	 * 判断验证码是否存在
	 * 
	 * @param mobile
	 * @param smsCode
	 * @param smsType
	 *            
	 * @return 验证码是否存在
	 */
	public boolean smsExists(String mobile, String smsCode, Setting.SmsType smsType) {
		return smsDao.smsExists(mobile, smsCode, smsType);
	}
	
	/**
	 * 保存或更新
	 * @param
	 */
	public void saveOrUpdate(String mobile, String vcode, Setting.SmsType smsType) {
		Sms pSms = smsDao.find(mobile, null, smsType.ordinal());
		Sms sms = new Sms();
		sms.setMobile(mobile);
		sms.setSmsCode(vcode);
		sms.setSmsType(smsType.ordinal());
		if(pSms == null) {
			super.save(sms);
		} else {
			pSms.setSmsCode(vcode);
			super.update(pSms);
		}
	}

	/**
	 * 删除
	 * @param mobile
	 * @param smsCode
	 * @return
	 */
	public boolean delete(String mobile, String smsCode) {
		return smsDao.delete(mobile, smsCode);
	}
	
}