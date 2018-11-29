package com.jfinalshop.plugin.yinshenpaymentplugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinalshop.Setting;
import com.jfinalshop.Util.Md5Util;
import com.jfinalshop.Util.URL;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.DecryptUtil;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
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
public class FastPaymentPlugin extends PaymentPlugin {
    String redirectUrl =null;
    private static Logger logger = LoggerFactory.getLogger(FastPaymentPlugin.class);
    @Override
    public String getName() {
        return "银生宝快捷";
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
        return "fast_payment/install.jhtml";
    }

    @Override
    public String getUninstallUrl() {
        return "fast_payment/uninstall.jhtml";
    }

    @Override
    public String getSettingUrl() {
        return "fast_payment/setting.jhtml";
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
    public String getResult(String sn, String description, HttpServletRequest request) {
        PluginConfig pluginConfig = getPluginConfig();
        PaymentLog paymentLog = getPaymentLog(sn);
        Map<String, String> map = new LinkedHashMap<String, String>(10);
        map.put("accountId", PropKit.get("accounntId"));
        map.put("customerId", pluginConfig.getAttribute("partner"));
        map.put("orderNo", sn);
        map.put("commodityName", "watch");
        map.put("amount", paymentLog.getAmount().setScale(2).toString());
        map.put("responseUrl",PropKit.get("responseUrl"));
        map.put("pageResponseUrl",PropKit.get("pageResponseUrl"));
        map.put("key", PropKit.get("hangyipai1"));
        map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
        map.remove("key");
        System.out.println(Md5Util.buildWithQueryString(map));
        try {
            redirectUrl  = HttpKit.post(URL.WAP_URL, map, null).replaceAll("href=\"", "href=\"http://180.166.114.155:18083").replaceAll("src=\"","src=\"http://180.166.114.155:18083");
            if("".equals(redirectUrl)){
                System.out.println("回退后取继续支付页面");
//                redirect("/member/list")
                return null;
            }
            return redirectUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getRequestUrl() {
        return redirectUrl;
    }
    @Override
    public JSONObject getParameterMaps(String sn, String description, HttpServletRequest request) throws Exception {
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
            //结果描述descdes
            String  desc= request.getParameter("desc");
            //银行名称
            String  bankName= request.getParameter("bankName");
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
}
