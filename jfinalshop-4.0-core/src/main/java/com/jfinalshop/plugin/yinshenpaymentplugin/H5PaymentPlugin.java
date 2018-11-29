package com.jfinalshop.plugin.yinshenpaymentplugin;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinalshop.Util.AARProtocol;
import com.jfinalshop.Util.RsaKit;
import com.jfinalshop.Util.URL;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * java类简单作用描述
 *
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/7/26 10:23
 * @UpdateUser: jiaorongguo
 * @UpdateDate: 2018/7/26 10:23
 * @Version: 1.0
 * 身无彩凤双飞翼，心有灵犀一点通。
 */
public class H5PaymentPlugin extends PaymentPlugin {

    private static Logger logger = LoggerFactory.getLogger(H5PaymentPlugin.class);
    @Override
    public String getName() {
        return "银生宝H5";
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
        return "h5_payment/install.jhtml";
    }

    @Override
    public String getUninstallUrl() {
        return "h5_payment/uninstall.jhtml";
    }

    @Override
    public String getSettingUrl() {
        return "h5_payment/setting.jhtml";
    }

    @Override
    public String getRequestUrl() {
        return null;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.post;
    }

    @Override
    public String getRequestCharset() {
        return "UTF-8";
    }

    @Override
    public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();
        PluginConfig pluginConfig = getPluginConfig();
        PaymentLog paymentLog = getPaymentLog(sn);
        JSONObject json = new JSONObject();
        JSONObject obj = new JSONObject();
        obj.put("accountId", PropKit.get("accounntId"));
        obj.put("payType", "1");
        obj.put("orderId", sn);
        obj.put("commodity", "watch");
        obj.put("amount", paymentLog.getAmount().setScale(2).toString());
        obj.put("responseUrl",  PropKit.get("responseUrl"));
        obj.put("ext", "");
        String msgText = obj.toJSONString();
        //此处的私钥是商户私钥
        json.put("accountId", PropKit.get("accounntId"));
        json.put("msg", msgText);
        json.put("sign", RsaKit.sign(msgText, URL.pricateKey));
        String data = AARProtocol.encrypt(json.toJSONString(), URL.publicKey);
        try {
            String sop = HttpKit.post(URL.H5_URL, data);
            if (sop != null) {
                //解密返回信息 此处是商户私钥
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
                    map.put("message",resultCode);
                    map.put("qrcode",qrcode);
                    return map;
                }else if("1111".equals(resultCode)){
                    map.put("message",resultCode);
                    map.put("qrcode",qrcode);
                    return map;
                } else {
                    logger.info("操作失败：返回码为" + resultCode);
                    map.put("message",resultCode);
                    map.put("qrcode",qrcode);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public JSONObject getParameterMaps(String sn, String description, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public String getResult(String sn, String description, HttpServletRequest request) throws Exception {

        return null;
    }

    @Override
    public boolean verifyNotify(NotifyMethod notifyMethod, HttpServletRequest request) throws Exception {
        PaymentLog paymentLog = getPaymentLog(request.getParameter("order_no"));
        if (paymentLog != null && paymentLog.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(request.getParameter("order_amt"))) == 0) {
            // 总金额
            String amount = request.getParameter("amount");
            // 返回状态码
            String respCode =request.getParameter("result_code");
            // 商户订单号
            String order_no = request.getParameter("orderNo");
            if(order_no==null){
                order_no =request.getParameter("orderId");
            }
            // 交易状态
            String status =request.getParameter("status");
            //用户名称
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
    public String getNotifyMessage(NotifyMethod notifyMethod, HttpServletRequest request) {
        if (PaymentPlugin.NotifyMethod.async.equals(notifyMethod)) {
            return "success";
        }
        return null;
    }
    public String date(){
        Date ss = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        return format1.format(ss.getTime());
    }
}
