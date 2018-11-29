package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinalshop.plugin.alipayBankPaymentShaXiang.AlipayBankPaymentPluginShaxiang;
import com.jfinalshop.plugin.yinshenpaymentplugin.FastPaymentPlugin;
import com.jfinalshop.plugin.yinshenpaymentplugin.H5PaymentPlugin;
import com.jfinalshop.plugin.yinshenpaymentplugin.NetPaymentPlugin;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.plugin.alipayBankPayment.AlipayBankPaymentPlugin;
import com.jfinalshop.plugin.alipayDirectPayment.AlipayDirectPaymentPlugin;
import com.jfinalshop.plugin.alipayDualPayment.AlipayDualPaymentPlugin;
import com.jfinalshop.plugin.alipayEscowPayment.AlipayEscowPaymentPlugin;
import com.jfinalshop.plugin.alipayLogin.AlipayLoginPlugin;
import com.jfinalshop.plugin.ccbPayment.CcbPaymentPlugin;
import com.jfinalshop.plugin.ftpStorage.FtpStoragePlugin;
import com.jfinalshop.plugin.localStorage.LocalStoragePlugin;
import com.jfinalshop.plugin.ossStorage.OssStoragePlugin;
import com.jfinalshop.plugin.pay99billBankPayment.Pay99billBankPaymentPlugin;
import com.jfinalshop.plugin.pay99billPayment.Pay99billPaymentPlugin;
import com.jfinalshop.plugin.paypalPayment.PaypalPaymentPlugin;
import com.jfinalshop.plugin.qqLogin.QqLoginPlugin;
import com.jfinalshop.plugin.tenpayBankPayment.TenpayBankPaymentPlugin;
import com.jfinalshop.plugin.tenpayDirectPayment.TenpayDirectPaymentPlugin;
import com.jfinalshop.plugin.tenpayEscowPayment.TenpayEscowPaymentPlugin;
import com.jfinalshop.plugin.unionpayPayment.UnionpayPaymentPlugin;
import com.jfinalshop.plugin.unionpayPaymentTest.UnionpayPaymentTestPlugin;
import com.jfinalshop.plugin.weiboLogin.WeiboLoginPlugin;
import com.jfinalshop.plugin.weixinPayment.WeixinPaymentPlugin;
import com.jfinalshop.plugin.yeepayPayment.YeepayPaymentPlugin;

/**
 * Service - 插件
 * 
 * 
 */
@Singleton
public class PluginService {

	private static final List<PaymentPlugin>		paymentPlugins		= new ArrayList<PaymentPlugin>();
	private static final List<StoragePlugin>		storagePlugins		= new ArrayList<StoragePlugin>();
	private static final List<LoginPlugin>			loginPlugins		= new ArrayList<LoginPlugin>();
	private static final Map<String, PaymentPlugin>	paymentPluginMap	= new HashMap<String, PaymentPlugin>();
	private static final Map<String, StoragePlugin>	storagePluginMap	= new HashMap<String, StoragePlugin>();
	private static final Map<String, LoginPlugin>	loginPluginMap		= new HashMap<String, LoginPlugin>();
	
	/**
	 * 构造方法
	 */
	static {
		AlipayBankPaymentPlugin		alipayBankPaymentPlugin		= new AlipayBankPaymentPlugin();
		AlipayBankPaymentPluginShaxiang alipayBankPaymentPluginShaxiang = new AlipayBankPaymentPluginShaxiang();
		AlipayDirectPaymentPlugin	alipayDirectPaymentPlugin	= new AlipayDirectPaymentPlugin();
		AlipayDualPaymentPlugin		alipayDualPaymentPlugin		= new AlipayDualPaymentPlugin();
		AlipayEscowPaymentPlugin	alipayEscowPaymentPlugin	= new AlipayEscowPaymentPlugin();
		CcbPaymentPlugin			ccbPaymentPlugin			= new CcbPaymentPlugin();
		Pay99billBankPaymentPlugin	pay99billBankPaymentPlugin	= new Pay99billBankPaymentPlugin();
		Pay99billPaymentPlugin		pay99billPaymentPlugin		= new Pay99billPaymentPlugin();
		PaypalPaymentPlugin			paypalPaymentPlugin			= new PaypalPaymentPlugin();
		TenpayBankPaymentPlugin		tenpayBankPaymentPlugin		= new TenpayBankPaymentPlugin();
		TenpayDirectPaymentPlugin	tenpayDirectPaymentPlugin	= new TenpayDirectPaymentPlugin();
		TenpayEscowPaymentPlugin	tenpayEscowPaymentPlugin	= new TenpayEscowPaymentPlugin();
		UnionpayPaymentPlugin		unionpayPaymentPlugin		= new UnionpayPaymentPlugin();
		UnionpayPaymentTestPlugin   unionpayPaymentTestPlugin   = new UnionpayPaymentTestPlugin();
		YeepayPaymentPlugin			yeepayPaymentPlugin			= new YeepayPaymentPlugin();
		WeixinPaymentPlugin			weixinPaymentPlugin			= new WeixinPaymentPlugin();
		FastPaymentPlugin  fastPaymentPlugin = new FastPaymentPlugin();
		NetPaymentPlugin netPaymentPlugin = new NetPaymentPlugin();
		H5PaymentPlugin h5PaymentPlugin = new H5PaymentPlugin();

		QqLoginPlugin				qqLoginPlugin				= new QqLoginPlugin();
		WeiboLoginPlugin			weiboLoginPlugin			= new WeiboLoginPlugin();
		AlipayLoginPlugin			alipayLoginPlugin			= new AlipayLoginPlugin();

		LocalStoragePlugin			filePlugin					= new LocalStoragePlugin();
		FtpStoragePlugin			ftpPlugin					= new FtpStoragePlugin();
		OssStoragePlugin			ossPlugin					= new OssStoragePlugin();

		paymentPlugins.add(alipayBankPaymentPlugin);
		paymentPlugins.add(alipayBankPaymentPluginShaxiang);
		paymentPlugins.add(alipayDirectPaymentPlugin);
		paymentPlugins.add(alipayDualPaymentPlugin);
		paymentPlugins.add(alipayEscowPaymentPlugin);
		paymentPlugins.add(ccbPaymentPlugin);
		paymentPlugins.add(pay99billBankPaymentPlugin);
		paymentPlugins.add(pay99billPaymentPlugin);
		paymentPlugins.add(paypalPaymentPlugin);
		paymentPlugins.add(tenpayBankPaymentPlugin);
		paymentPlugins.add(tenpayDirectPaymentPlugin);
		paymentPlugins.add(tenpayEscowPaymentPlugin);
		paymentPlugins.add(unionpayPaymentPlugin);
		paymentPlugins.add(unionpayPaymentTestPlugin);
		paymentPlugins.add(yeepayPaymentPlugin);
		paymentPlugins.add(weixinPaymentPlugin);
		paymentPlugins.add(fastPaymentPlugin);
		paymentPlugins.add(netPaymentPlugin);
		paymentPlugins.add(h5PaymentPlugin);
		
		paymentPluginMap.put(alipayBankPaymentPlugin.getId(), alipayBankPaymentPlugin);
		paymentPluginMap.put(alipayBankPaymentPluginShaxiang.getId(), alipayBankPaymentPluginShaxiang);
		paymentPluginMap.put(alipayDirectPaymentPlugin.getId(), alipayDirectPaymentPlugin);
		paymentPluginMap.put(alipayDualPaymentPlugin.getId(), alipayDualPaymentPlugin);
		paymentPluginMap.put(alipayEscowPaymentPlugin.getId(), alipayEscowPaymentPlugin);
		paymentPluginMap.put(ccbPaymentPlugin.getId(), ccbPaymentPlugin);
		paymentPluginMap.put(pay99billBankPaymentPlugin.getId(), pay99billBankPaymentPlugin);
		paymentPluginMap.put(pay99billPaymentPlugin.getId(),pay99billPaymentPlugin);
		paymentPluginMap.put(paypalPaymentPlugin.getId(), paypalPaymentPlugin);
		paymentPluginMap.put(tenpayBankPaymentPlugin.getId(), tenpayBankPaymentPlugin);
		paymentPluginMap.put(tenpayDirectPaymentPlugin.getId(), tenpayDirectPaymentPlugin);
		paymentPluginMap.put(tenpayEscowPaymentPlugin.getId(), tenpayEscowPaymentPlugin);
		paymentPluginMap.put(unionpayPaymentPlugin.getId(), unionpayPaymentPlugin);
		paymentPluginMap.put(unionpayPaymentTestPlugin.getId(), unionpayPaymentTestPlugin);
		paymentPluginMap.put(yeepayPaymentPlugin.getId(), yeepayPaymentPlugin);
		paymentPluginMap.put(weixinPaymentPlugin.getId(), weixinPaymentPlugin);
		paymentPluginMap.put(fastPaymentPlugin.getId(),fastPaymentPlugin);
		paymentPluginMap.put(netPaymentPlugin.getId(),netPaymentPlugin);
		paymentPluginMap.put(h5PaymentPlugin.getId(),h5PaymentPlugin);


		
		loginPlugins.add(weiboLoginPlugin);
		loginPlugins.add(qqLoginPlugin);
		loginPlugins.add(alipayLoginPlugin);
		
		loginPluginMap.put(weiboLoginPlugin.getId(), weiboLoginPlugin);
		loginPluginMap.put(qqLoginPlugin.getId(), qqLoginPlugin);
		loginPluginMap.put(alipayLoginPlugin.getId(), alipayLoginPlugin);
		
		storagePlugins.add(filePlugin);
		storagePlugins.add(ftpPlugin);
		storagePlugins.add(ossPlugin);
		
		storagePluginMap.put(filePlugin.getId(), filePlugin);
		storagePluginMap.put(ftpPlugin.getId(), ftpPlugin);
		storagePluginMap.put(ossPlugin.getId(), ossPlugin);
	}

	/**
	 * 获取支付插件
	 * 
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins() {
		Collections.sort(paymentPlugins);
		return paymentPlugins;
	}

	/**
	 * 获取存储插件
	 * 
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins() {
		Collections.sort(storagePlugins);
		return storagePlugins;
	}

	/**
	 * 获取登录插件
	 * 
	 * @return 登录插件
	 */
	public List<LoginPlugin> getLoginPlugins() {
		Collections.sort(loginPlugins);
		return loginPlugins;
	}

	/**
	 * 获取支付插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins(final boolean isEnabled) {
		List<PaymentPlugin> result = new ArrayList<PaymentPlugin>();
		CollectionUtils.select(paymentPlugins, new Predicate() {
			public boolean evaluate(Object object) {
				PaymentPlugin paymentPlugin = (PaymentPlugin) object;
				return paymentPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取存储插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins(final boolean isEnabled) {
		List<StoragePlugin> result = new ArrayList<StoragePlugin>();
		CollectionUtils.select(storagePlugins, new Predicate() {
			public boolean evaluate(Object object) {
				StoragePlugin storagePlugin = (StoragePlugin) object;
				return storagePlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取登录插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 登录插件
	 */
	public List<LoginPlugin> getLoginPlugins(final boolean isEnabled) {
		List<LoginPlugin> result = new ArrayList<LoginPlugin>();
		CollectionUtils.select(loginPlugins, new Predicate() {
			public boolean evaluate(Object object) {
				LoginPlugin loginPlugin = (LoginPlugin) object;
				return loginPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取支付插件
	 * 
	 * @param id
	 *            ID
	 * @return 支付插件
	 */
	public PaymentPlugin getPaymentPlugin(String id) {
		return paymentPluginMap.get(id);
	}

	/**
	 * 获取存储插件
	 * 
	 * @param id
	 *            ID
	 * @return 存储插件
	 */
	public StoragePlugin getStoragePlugin(String id) {
		return storagePluginMap.get(id);
	}

	/**
	 * 获取登录插件
	 * 
	 * @param id
	 *            ID
	 * @return 登录插件
	 */
	public LoginPlugin getLoginPlugin(String id) {
		return loginPluginMap.get(id);
	}

}