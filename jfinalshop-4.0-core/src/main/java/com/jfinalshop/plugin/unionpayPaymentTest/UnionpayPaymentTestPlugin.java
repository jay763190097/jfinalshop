package com.jfinalshop.plugin.unionpayPaymentTest;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.util.DecryptUtil;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.WebUtils;
import com.kuark.payment.sign.SignAndEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin - 银联在线支付
 * 
 * 
 */
public class UnionpayPaymentTestPlugin extends PaymentPlugin {

	/**
	 * 货币
	 */
	private static final String CURRENCY = "156";
	private static Logger logger = LoggerFactory.getLogger(UnionpayPaymentTestPlugin.class);
	String charset = "UTF-8";
	SignAndEncrypt instance = new SignAndEncrypt();
	String hexData = "308204bd020100300d06092a864886f70d0101010500048204a7308204a3020100028201010085e5d8ee574713117983fb51bf50313671c782c327c1c349141e272d4a36e7fc463eda245ec25a239c001869257a465b285a7f97cc82ce0d863f6adc2f8daac7c279eba27a7485d5114e323d6df85ff2138bb3c9cb82b23f327fab31606c666d6c11fcdf6e80e9ece6afe3339aea337d21a8ed687bb19cda8608be1cdf34c09e42182f77bba2bce11f16144d8430154e7b3468606b3a4a1e5ee7a2925db53ca18ea68404eaba852504da9fafcc3a7e67e29994e6a5aa29c060280f3eab24b99457baff02d8fc3420f4aff09e5336f90dfe80bf1c0a5de987b5ba4babbaa7032a4a411eec0870585701c2cd41b7e7f5efab0dac81521590d2aa18d290720243470203010001028201007bedfb54ce6f3854ea35a03d4f6777c53e963c8f4892ec9f0d01c39d059dc0261d163b6d824f3c82ccf6c1a14050e621a53dedeea0194981f709676c988ef4cca3519928ef523e34b129c1125e608edd4dfdf5d0068ed74feafb8385d60b3eda743649e8bb2c9256f4454373162542b6c49ac7f1f6dab3a5067dbd0898070967988a445221bea34e7e4b06cab617122f388710f405c1c73f3b95858d822b1b6de56d2a40283b618d9fe528c90f11b80d44a0f18942b0e93a871b3cae290f086d494ccdfbb0a60c48fe3f7c358e8618b6a345a54bacd93f3c346d3af29926371dc21e5cafcfdde6c241974250e31ab18805ecde1efe9921866375c25b656ad7d102818100cd7e08c61e0fc6845e73bc5be5f7bec112162f6b08380f56ef77985da7dc1f33ee46036011b33522bc6117cee274045baaf863a6b70f334d556b979f85711e16b6a6e0469edbb3d9a269dff492e9b62c9d368f5231b323820e56e1278718e14f2561b88d2c4dad8c9b88c885e7bce95b5fdfdbff77eacee614db9d1dda229f2502818100a6cef3f46609fd568ab0f55703645b042d7971ad078dc8f5a0cbcb6d75736e4507fdc7da64da0fff93713b0a95fb040aa94cf1c41016d07c350ad50e18039a4a72102a13db3ed32772615e795fc1fde864f5d819a73799527533b1d3216df023d8fb312247375829cdc7ae25cc40dfd09af0c0f7e6d832a711b06ae5eb9f32fb02818075a299c9387a556d7f5887d29ff2544bd85bc4e763bc1be3eb902e34271cd6ebf69233ea721781ea91a68f678ab159b1544d7f5677e306be5591132a69cc98a9ee03d7cdf5b0d5e3579b7e6e570fdd8eeaacca1d0680836a37653add52f4463ef5aa5c68cc9ce725dfffccb303375ceb471632adab6b4ae7e1b6dad9b4ff2ed5028180723932ca3f7945b6162098cfadadf1b9499e689e1097216e395e73fd8f38ace0ae59349a3307337f522a6e40907523ce5d22284796b0d8b0c4ed96f920ae8edfbd7d9e229cb647873286c4579e9fcb4b709efc0c8cf9c08ebb5cdbbef49c409d7b7e7ea5b75889918eacd66e619066f284b6a99cd7c161e60f71dbb177883b3302818100b82a6b0b34decfa550fd390b8070280f1f6c33448a4a76a28094e21f48c734c13643cbde97e8ffc8302f4d46e435f5cdc8d9a4f80281576ab23b0199c9a69ef2600260e43ebadbed21b964fed5810d040f82515b0f12eccc4067a13519072f17e9e38ac23e67e39b8ea0f8f72afe887f16f6db9b36e70bc6a1cd999e319979d6";
	String redirectUrl =null;
	byte[] merchantPrivate = hexStrToBytes(hexData);
	//第二套平台
	String hexData1 = "30820122300d06092a864886f70d01010105000382010f003082010a02820101008a41a9b8f8b6b8f94ed7108d59f0d53eccf796fcdb522a780a7ebb0284fdb229a65086d0f938459f070473c85b0bc02ace44757619913f7b0e26ca0333def18a1c9fd60ec8eb049577983229829dab306ae71c34aa89fa5d8d2249081d320e34a4c7bdb75cd9608f35365f13e2efd4639675e26a7e3605525bac93f55060344dc234eaaa15722125bb7e7cd921794be6491058ee1150cb4f7d3975403484e02e667ace20edf6d6ab7afc4b81e58b4110272007bd5d00d23c173e20bc5aa906465fdae239329308dc9f4c4f503b91baa4dde474a747b7e81585c83060746f06397c0583659c26eed347617b01a86b34358e8f38aa839c5437e3ac3a481ca5f8f90203010001";
	byte[] publicKeyData = hexStrToBytes(hexData1);
	private static final byte[] hexStrToBytes(String s) {
		byte[] bytes;
		bytes = new byte[s.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
					16);
		}
		return bytes;
	}

	@Override
	public String getName() {
		return "国际银联支付接口";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "JFinalShop";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.jfinalshop.com";
	}

	@Override
	public String getInstallUrl() {
		return "unionpay_payment_test/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "unionpay_payment_test/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "unionpay_payment_test/setting.jhtml";
	}

	@Override
	public PaymentPlugin.RequestMethod getRequestMethod() {
		return RequestMethod.post;
	}

	@Override
	public String getRequestCharset() {
		return "UTF-8";
	}

	@Override
	public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request) {
		return null;
	}

	@Override
	public JSONObject getParameterMaps(String sn,String description, HttpServletRequest request) throws Exception {
		PluginConfig pluginConfig = getPluginConfig();
		PaymentLog paymentLog = getPaymentLog(sn);
		JSONObject parameterMap = new JSONObject();
		JSONObject msg = new JSONObject();
		JSONObject respJSON = new JSONObject();
		String response = null;
			msg.put("order_no", sn);//订单号
			msg.put("tran_date", new Date());//交易时间
			msg.put("order_amt", paymentLog.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());//交易总金额
			msg.put("trade_type", TadeMethod.B2C);//交易类型
		    msg.put("notify_url", "http://114.55.93.111:8080/jfinalshop-4.0-web/payment/pluginNotify.jhtml?pluginId=unionpayPaymentTestPlugin&notifyMethod=async");//地址*/
		    parameterMap.put("mer_Id", pluginConfig.getAttribute("partner"));
			parameterMap.put("method","trade.page.pay");
			parameterMap.put("version", "1.0");
			parameterMap.put("msg", msg.toJSONString());
			parameterMap.put("signature", generateSign(msg));
			byte[] plainData = parameterMap.toJSONString().getBytes(charset);
			response = instance.encrypt(plainData);
			logger.info("组成的请求链接加密后为：" + response);
			byte[] result = WebUtils.posts("http://114.55.93.111:8080/zgfront/service_entry", response,"utf-8",30);
			String respStr = new String(result, "UTF-8");
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(merchantPrivate);
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey mechantPrivateKey = keyf.generatePrivate(priPKCS8);
			byte[] decryptData = DecryptUtil.decrypt(respStr, mechantPrivateKey);
			String respt = new String(decryptData, charset);
			logger.info("解密后的报文"+respt);
		    respJSON = JSON.parseObject(respt);
			redirectUrl =  JSON.parseObject( respJSON.get("msg").toString()).get("redirect_url").toString();
			///签名验证
			String data = respJSON.getString("msg");
			String signature = respJSON.getString("signature");
			boolean vfy = instance.verifySignature(signature,data.getBytes(charset));
			if(vfy){
				logger.info("验证签名成功");
			    return respJSON;
			}else{
				logger.info("验证签名失败");
				return null;
			}
	}

	@Override
	public String getResult(String sn, String description, HttpServletRequest request) throws Exception {
		return null;
	}

	@Override
	public String getRequestUrl() {
		return redirectUrl;
	}
	@Override
	public boolean verifyNotify(PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request) throws Exception {
		//商户
		String response = null;
		String charset = "UTF-8";
		PluginConfig pluginConfig = getPluginConfig();
		PaymentLog paymentLog = getPaymentLog(request.getParameter("order_no"));
		if (paymentLog != null && paymentLog.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(request.getParameter("order_amt"))) == 0) {
			JSONObject parameterMap = new JSONObject();
			JSONObject msg = new JSONObject();
			JSONObject respJSON = new JSONObject();
			msg.put("order_no", request.getParameter("order_no"));//订单号
			parameterMap.put("mer_Id", pluginConfig.getAttribute("partner"));
			parameterMap.put("method","trade.query");
			parameterMap.put("version", "1.0");
			parameterMap.put("msg", msg.toJSONString());
			parameterMap.put("signature", generateSign(msg));
			byte[] plainData = parameterMap.toJSONString().getBytes(charset);
				response = instance.encrypt(plainData);
				logger.info("组成的请求链接加密后为：" + response);
				byte[] result = WebUtils.posts("http://114.55.93.111:8080/zgfront/service_entry", response,"utf-8",30);
				String respStr = new String(result, "UTF-8");

			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(merchantPrivate);
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey mechantPrivateKey = keyf.generatePrivate(priPKCS8);
			byte[] decryptData = DecryptUtil.decrypt(respStr, mechantPrivateKey);

			String respt = new String(decryptData, charset);
			logger.info("查询接口解密后的报文"+respt);
			respJSON = JSON.parseObject(respt);
			String status =  JSON.parseObject( respJSON.get("msg").toString()).get("order_status").toString();
			String respCode =  JSON.parseObject( respJSON.get("msg").toString()).get("respCode").toString();
			String amount = JSON.parseObject( respJSON.get("msg").toString()).get("order_amt").toString();
			if ("10000".equals(respCode)&& "SUCCESS".equals(status)&&paymentLog.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(amount)) == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSn(HttpServletRequest request) {
		return request.getParameter("order_no");
	}

	@Override
	public String getNotifyMessage(PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request) {
		if (PaymentPlugin.NotifyMethod.async.equals(notifyMethod)) {
			return "success";
		}
		return null;
	}

	/**
	 * 生成签名
	 *
	 * @param parameterMap 参数
	 * @return 签名
	 */
	private String generateSign(JSONObject parameterMap) {
		String signed=null;
		try {
			instance.loadKey(merchantPrivate, publicKeyData);
			signed = instance.signatureData(parameterMap.toJSONString().getBytes(charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signed;
	}


}