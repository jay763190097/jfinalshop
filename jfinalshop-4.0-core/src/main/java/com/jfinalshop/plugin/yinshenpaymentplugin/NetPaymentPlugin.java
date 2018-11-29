package com.jfinalshop.plugin.yinshenpaymentplugin;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinalshop.Util.Md5Util;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
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
public class NetPaymentPlugin extends PaymentPlugin {
    String redirectUrl =null;
    private static Logger logger = LoggerFactory.getLogger(NetPaymentPlugin.class);
    @Override
    public String getName() {
        return "银生宝网关";
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
        return "net_payment/install.jhtml";
    }

    @Override
    public String getUninstallUrl() {
        return "net_payment/uninstall.jhtml";
    }

    @Override
    public String getSettingUrl() {
        return "net_payment/setting.jhtml";
    }

    @Override
    public String getRequestUrl() {
        return redirectUrl;
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
        return null;
    }

    @Override
    public JSONObject getParameterMaps(String sn, String description, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public String getResult(String sn, String description, HttpServletRequest request) throws Exception {
        PluginConfig pluginConfig = getPluginConfig();
        PaymentLog paymentLog = getPaymentLog(sn);
        Map<String, String> map = new LinkedHashMap<String, String>(10);
        map.put("merchantId", PropKit.get("accounntId"));
        map.put("merchantUrl",PropKit.get("responseUrl"));
        map.put("responseMode","1");
        map.put("orderId",sn);
        map.put("currencyType","CNY");
        map.put("amount", paymentLog.getAmount().setScale(2).toString());
        map.put("assuredPay","flase");
        map.put("time", date());
        map.put("remark","");
        map.put("merchantKey","hangyipai1");
        map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
        map.remove("merchantKey");
        map.put("bankCode","cmb");
        map.put("b2b","false");
        map.put("commodity","手机");
        map.put("frontURL",PropKit.get("pageResponseUrl"));
        map.put("cardAssured","0");
        map.put("version","1.0.1");
        String build = Md5Util.buildWithQueryString(map);
        System.out.println("-----"+build);
        redirectUrl="http://api.chinavalleytech.com/routes/gateway/userPay?"+ build;
        return redirectUrl;
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
            String  userid= request.getParameter("userId");
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
