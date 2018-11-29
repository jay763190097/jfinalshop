package com.jfinalshop.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinalshop.Utils.AARProtocol;
import com.jfinalshop.Utils.RsaKit;
import com.jfinalshop.config.LoginInterceptor;
import com.jfinalshop.config.URL;
import com.jfinalshop.Utils.HttpUtils;
import com.jfinalshop.Utils.Md5Util;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * java类简单作用描述
 *
 * @ProjectName: jfinalshop-4.0$
 * @Package: com.jfinalshop.controller$
 * @ClassName: $TYPE_NAME$
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/4/15$ 13:23$
 * @UpdateUser: 作者姓名
 * @UpdateDate: 2018/4/15$ 13:23$
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018$</p>
 */
@Before(LoginInterceptor.class)
@ControllerBind(controllerKey = "/pay")
public class PayOrderController extends BaseController  {
    private static Logger logger = LoggerFactory.getLogger(PayOrderController.class);
    MemberService memberService = new MemberService();
    /**
     * 普通订单-创建
     */
    public void createPay() {
       // Cache cache = Redis.use();
        String cartKey = getPara("cartKey");
       // String type = getPara("type");
       // String continuid ="";
       // String oldSn="";
        /*if("continue".equals(type)){
            oldSn = getPara("oldSn");//===============
        }
        int j=1 ;
        if("continue".equals(type)){
            String result = getPara("items");
            String[] a = result.split(";");
            for (String b:a) {
                String[] c =b.split(",");
                String productId =c[0];
                String quantity = c[1];
                if( j!= 1){
                    continuid= continuid+",";
                }
                continuid+=productId;
                j+=1;
                System.out.println(continuid);
                Object username = getSession().getAttribute("username");
                String memberid= null;
                //post请求获取cartKey并更新至redis
                cartKey = cache.get(username+"_cartkey");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("productId",productId);
                map.put("quantity",quantity);
                map.put("cartKey",cartKey);
                try {
                    String res = HttpUtils.POSTMethod(PropKit.get("addCart_url"),map);
                    String code = JSON.parseObject(res).getString("code");
                    String message = JSON.parseObject(res).getString("message");
                    if("2".equals(code)){
                        renderJson("message",message);
                        return;
                    }
                    System.out.println("添加成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("",e);
                }

            }
        }*/

            Map<String, Object> mapp = new HashMap<String, Object>();
            mapp.put("cartKey",cartKey);
            String res = null;
            try {
                res = HttpUtils.POSTMethod(PropKit.get("cartlist_url"),mapp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(res==null){
                logger.info("购物车不能为空");
                setAttr("message","购物车不能为空");
                redirect("/pages/index.html");
                return;
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONObject jsonDatum =JSONObject.parseObject( jsonObject.getString("datum"));
            JSONArray cartItems = jsonDatum.getJSONArray("cartItems");
            ListIterator<Object> objectListIterator = cartItems.listIterator();
            String ids="";
            int i=1;
            while (objectListIterator.hasNext()){
                String productid = objectListIterator.next().toString();
                JSONObject product = JSONObject.parseObject(productid);
                String product_id =product.getString("product_id");
                if( i != 1){
                    ids= ids+",";
                }
                ids+=product_id;
                i+=1;
                System.out.println(ids);
            }
            //结算
            Map<String,String> map1 = new LinkedHashMap<String,String>(10);
            String token = getSessionAttr("username_token");
            map1.put("token",token);
            map1.put("channel",PropKit.get("resource"));
            map1.put("productIds",ids);
            map1.put("cartKey",cartKey);
            String ress = HttpKit.post(PropKit.get("getToken_url"), map1, null);
            JSONObject respons = JSONObject.parseObject(ress);
            String datum = respons.getString("datum");

            JSONObject datums = JSONObject.parseObject(datum);
            String  cardtoken = datums.getString("cartToken");
           //收货地址
            String defaultReceiver = datums.getString("defaultReceiver");
            if(defaultReceiver==null){
                System.out.println("进来了");
                setAttr("result",defaultReceiver);
                setAttr("message","请添加默认地址");
                render("/pages/add-address.html");
				return;
            }
            JSONObject area = JSONObject.parseObject(defaultReceiver);
            String address = area.getString("address");//地址
            String id = area.getString("id");//地址
            String area_name = area.getString("area_name");//省市
            String consignee = area.getString("consignee");//姓名
            String phone = area.getString("phone");//电话
            String zip_code = area.getString("zip_code");//邮编

            //支付方式
           // String paymentMethods = datums.getString("paymentMethods");
            JSONArray paymentMethods = datums.getJSONArray("paymentMethods");

            //订单详情
            String order = datums.getString("order");
            JSONObject orders = JSONObject.parseObject(order);
            String orderTotal = orders.getString("orderTotal");
            String quantity = orders.getString("quantity");
            JSONArray order_items = orders.getJSONArray("order_items");
            setAttr("id",id);
            setAttr("cardtoken",cardtoken);
            setAttr("name",consignee);
            setAttr("phone",phone);
            setAttr("area_name",area_name);
            setAttr("address",address);
            setAttr("detail",order_items);
            setAttr("zip_code",zip_code);
            setAttr("orderTotal",orderTotal);
            setAttr("quantity",quantity);
        render("/pages/shops-orders.html");
    }


    public void payment() {
        //订单生成
        String cardtoken = getPara("param1");
        String name = getPara("param2");
        String phone = getPara("param3");
        String adess = getPara("param4");
        String id = getPara("param5");
        String token = getSessionAttr("username_token");
        Map<String,String> map = new LinkedHashMap<String,String>(10);
        map.put("token",token);//必填
        map.put("receiverId",id);//必填
        map.put("paymentMethodId","1");//必填
        map.put("shippingMethodId","");
        map.put("code","");
        map.put("invoiceTitle","");
        map.put("invoiceContent","");
        map.put("channel",PropKit.get("resource"));
        map.put("isBalance","");
        map.put("shippingDate","");
        map.put("source","H5");//必填
        map.put("cartToken",cardtoken);//必填
        String rea = HttpKit.post(PropKit.get("createOrder_url"), map, null);
        String datum = JSONObject.parseObject(rea).getString("datum");
        if(datum==null){
            System.out.println("回退后取继续支付页面");
            redirect("/member/list");
            return;
        }
        String sn =JSONObject.parseObject(JSONObject.parseObject(rea).getString("datum")).getString("sn");
        String amount =JSONObject.parseObject(JSONObject.parseObject(rea).getString("datum")).getString("amount");
        setAttr("sn", sn);
        setAttr("amount", amount);
        setAttr("name", name);
        setAttr("phone", phone);
        setAttr("address", adess);
        render("/pages/pay-orders.html");
    }

    //继续支付
    public void continuePay(){
        String token = getSessionAttr("username_token");
        String type = getPara("type");
        String oldSn = getPara("oldSn");
        if("continue".equals(type)){
            Map<String,String> map = new LinkedHashMap<String,String>(10);
            map.put("sn",oldSn);//必填
            map.put("token",token);//必填
            String rea = HttpKit.post(PropKit.get("updateSn"), map, null);
            String datum = JSONObject.parseObject(rea).getString("datum");
            logger.info("重新生成订单取消旧订单返回结果"+datum);
            String adess = JSONObject.parseObject(datum).getString("address");
            String amount = JSONObject.parseObject(datum).getString("amount");
            String phone = JSONObject.parseObject(datum).getString("phone");
            String sn = JSONObject.parseObject(datum).getString("sn");
            String name = JSONObject.parseObject(datum).getString("consignee");
            Double amounts = Double.parseDouble(amount);
            DecimalFormat df = new DecimalFormat("0.00");
            String CNY = df.format(amounts);
            setAttr("sn", sn);
            setAttr("amount", CNY);
            setAttr("name", name);
            setAttr("phone", phone);
            setAttr("address", adess);
            render("/pages/pay-orders.html");
        }
    }

    public void payorder() {
        String paymentMothod = getPara("PaymentMothod");
        System.out.println(paymentMothod);
        String orderNo = getPara("orderNo");
        String userid = getPara("accountId");
        String token = getSessionAttr("username_token");
        String typeName = getPara("type", "payment");
        String amount = getPara("amount");
        Map<String, Object> mapp = new HashMap<String, Object>();
        mapp.put("token",token);
        mapp.put("type",typeName);
        mapp.put("amount",amount);
        mapp.put("sn",orderNo);
        mapp.put("paymentMothod", paymentMothod);
        String res=null;
        try {
                res = HttpUtils.POSTMethod(PropKit.get("savepayment"),mapp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        JSONObject result = JSONObject.parseObject(res);
        logger.info(result.getString("message"));
        switch(paymentMothod){
            case "银行卡支付": {
                Map<String, String> map = new LinkedHashMap<String, String>(10);
                map.put("accountId", PropKit.get("accounntId"));
                map.put("customerId", userid);
                map.put("orderNo", orderNo);
                map.put("commodityName", "watch");
                map.put("amount", amount);
                map.put("responseUrl", PropKit.get("responseUrl"));
                map.put("pageResponseUrl",PropKit.get("pageResponseUrl"));
                map.put("key", PropKit.get("hangyipai1"));
                map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
                map.remove("key");
                System.out.println(Md5Util.buildWithQueryString(map));
                try {
                    String post = HttpKit.post(URL.WAP_URL, map, null).replaceAll("href=\"", "href=\"http://180.166.114.155:18083").replaceAll("src=\"","src=\"http://180.166.114.155:18083");
                    if("".equals(post)){
                        System.out.println("回退后取继续支付页面");
                        redirect("/member/list");
                        return;

                    }
                    renderHtml(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                }

            case "H5支付": {
                JSONObject json = new JSONObject();
                JSONObject obj = new JSONObject();
                obj.put("accountId", PropKit.get("accounntId"));
                obj.put("payType", "1");
                obj.put("orderId", orderNo);
                obj.put("commodity", "watch");
                obj.put("amount", amount);
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
                            setAttr("message", message);
                            setAttr("qrcode", qrcode);
                            render("/pages/pay-codess.html");
                        }else if("1111".equals(resultCode)){
                            setAttr("message", message);
                            render("/pages/pay-codess.html");
                        } else {
                            logger.info("操作失败：返回码为" + resultCode);
                            redirect("/member/list");
                        }
                    } else {
                        System.out.println("返回为空");
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
                obj.put("accountId", PropKit.get("accounntId"));
                obj.put("payType", "1");
                obj.put("orderId", orderNo);
                obj.put("commodity", "watch");
                obj.put("amount", amount);
                obj.put("responseUrl",  PropKit.get("responseUrl"));
                obj.put("ext", "");
                String msgText = obj.toJSONString();
                System.out.println(msgText);
                //此处的私钥是商户私钥
                json.put("accountId", PropKit.get("accounntId"));
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
                            setAttr("message", message);
                            setAttr("qrcode", qrcode);
                            render("/pages/pay-codes.html");
                        }else if("1111".equals(resultCode)){
                            setAttr("message", message);
                            render("/pages/pay-codes.html");
                        } else {
                            logger.info("操作失败：返回码为" + resultCode);
                            redirect("/member/list");
                        }
                    }else {
                        System.out.println("返回为空");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case "网关支付": {
                Map<String,String> map = new LinkedHashMap<String, String>(10);
                map.put("merchantId",PropKit.get("accounntId"));
                map.put("merchantUrl",PropKit.get("responseUrl"));
                map.put("responseMode","1");
                map.put("orderId",orderNo);
                map.put("currencyType","CNY");
                map.put("amount",amount);
                map.put("assuredPay","flase");
                map.put("time", date());
                map.put("remark","");
                map.put("merchantKey","hangyipai1");
                map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
//                System.out.println(Md5Util.buildWithQueryString(map));
                map.remove("merchantKey");
                map.put("bankCode","cmb");
                map.put("b2b","false");
                map.put("commodity","手机");
                map.put("frontURL",PropKit.get("pageResponseUrl"));
                map.put("cardAssured","0");
                map.put("version","1.0.1");
                String build = Md5Util.buildWithQueryString(map);
                System.out.println("-----"+build);
//                String sop = HttpKit.post("http://api.chinavalleytech.com/routes/gateway/userPay", build);
//                System.out.println(sop);
                redirect("http://api.chinavalleytech.com/routes/gateway/userPay?"+ build);
                break;
            }
        }
    }
    public String date(){
        Date ss = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        return format1.format(ss.getTime());
    }
}
