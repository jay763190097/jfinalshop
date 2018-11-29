import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinalshop.Utils.AARProtocol;
import com.jfinalshop.Utils.Md5Util;
import com.jfinalshop.Utils.RsaKit;
import com.jfinalshop.config.URL;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/21.
 */
public class WapController extends Controller {

    public void index(){
        render("/wapForm.jsp");
        return;
    }

    public void wapForm(){
        Map<String,String> map = new LinkedHashMap<String, String>(10);
        map.put("accountId",getPara("accountId"));
        map.put("customerId",getPara("customerId"));
        map.put("orderNo",getPara("orderNo"));
        map.put("commodityName",getPara("commodityName"));
        map.put("amount",getPara("amount"));
        map.put("responseUrl",getPara("responseUrl"));
        map.put("pageResponseUrl",getPara("pageResponseUrl"));
        map.put("key",getPara("key"));
        map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
        map.remove("key");
        System.out.println(Md5Util.buildWithQueryString(map));
        String post = HttpKit.post(URL.WAP_URL, map, null).replaceAll("href=\"", "href=\"http://180.166.114.155:18083").replaceAll("src=\"","src=\"http://180.166.114.155:18083");
        renderHtml(post);
        return;
    }

    public void order(){
        renderJsp("/wapOrder.jsp");
        return;
    }

    public void wapOrder(){
        JSONObject msg = new JSONObject();
        msg.put("accountId",getPara("accountId"));
        msg.put("orderNo",getPara("orderNo"));
        String msgText = msg.toJSONString();
        JSONObject result = new JSONObject();
        result.put("msg",msgText);
        result.put("sign", RsaKit.sign(msgText,getPara("privatekey")));
        System.out.println("快捷订单查询："+result.toJSONString());
        result.put("accountId",getPara("accountId"));
        String result_msg = AARProtocol.encrypt(result.toJSONString(), getPara("publickey"));
        String post = HttpKit.post(URL.WAPORDER_URL, result_msg);
        String s = null;
        try {
            s = new String(AARProtocol.decrypt(post, getPara("privatekey")).getBytes(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        renderText(s);
        return;
    }

    public static void main(String[] args) {
        Map<String,String> map = new LinkedHashMap<String,String>(10);
        map.put("accountId","1120180126093952001");
        map.put("customerId","201803229980000138");
        map.put("orderNo","201803229980000139");
        map.put("commodityName","watch");
        map.put("amount","100.00");
        map.put("responseUrl","http://211.75.237.89/myPayCenter/callBack470.jsp");
        map.put("pageResponseUrl","http://211.75.237.89/myPayCenter/callBack470.jsp");
        map.put("key","hangyipai1");
        map.put("key","123456");
        map.put("mac", Md5Util.md5For((LinkedHashMap<String, String>) map));
        map.remove("key");
        System.out.println("他的"+Md5Util.buildWithQueryString(map));
        String post = HttpKit.post(URL.WAP_URL, map, null).replaceAll("href=\"", "href=\"http://180.166.114.155:18083").replaceAll("src=\"","src=\"http://180.166.114.155:18083");
        System.out.println("我的"+post);

        /*JSONObject msg = new JSONObject();
        msg.put("accountId","1120130523134348001");
        msg.put("orderNo","100201802050001");
        String msgText = msg.toJSONString();
        JSONObject result = new JSONObject();
        result.put("msg",msgText);
        String privatekey = "30820275020100300d06092a864886f70d01010105000482025f3082025b020100028181009af6b2e41f05f4baee0e8e4190612556dd946ca8832f38c9759e332222f9194148bcda2cca319ad75511f3c003617b86f628f99b487d002d49b9f8960332a02cbebe5df4550d0cf644f2deffb64113590fb299910551f3e6c6d8c345b91a1a7c6731cfa9adc279d9cad4e64c19d7887e878f3039a7b3ecfd50e65386fb7a64e302030100010281807bad22ef3ff74ff014e8d70e6abdadc05b4e073ea29c809500e346ea1ac1762f8d6ef42dc51dbbf7d9f45ef6c37bfccba510e4cdd317b8996c7b1974188f4217d44af44139908c0cd230433fa2ddb5a4471d5f875a0841b3a1efbd7ba88ff39345d18235df81e7f5949c9c0080ba189c2276838d6b12db65c35ae86d8e8a7e51024100ddce8c80b3567fa66ae67162e43b87a6023261b74d05137da9ccd085fc632871c3992e5825c94cb19e2cd62c1cbde70ff35059be165879c690581a822e34c30b024100b2da3b37d7cc07c6ebba027eb1eaf4fdf79728ea08f6200d791a6132c12f62acb1d0bfb4ac41a29fd305241f6fdb0026cc4363b706c454460618abbb103c8c890240010bb943de1499537a8b8d9b4ec22d188e481887d9b81d6869458af226c77ce85468c37c2ce96f102353c8caba73f6c3ef7cc07f91f166d995d2a4855a9c6b1302407c8fa68c86db8f5307db5d5f757408ba53d44fe766e45b8066a66ba9588832f5c945742e5585c4c800c464db0c20ff230641acd1cc81b19baf25417d1f9abbd9024052851674764f956e19810c20276115b11d1cca99bf8a3df3975d0fb51ea0b19bf1953aa5016b4a72b90cac5eedf3c4ddd294d171040cae45cdbff246ca77bc60";
        String publickey ="30819f300d06092a864886f70d010101050003818d00308189028181009af6b2e41f05f4baee0e8e4190612556dd946ca8832f38c9759e332222f9194148bcda2cca319ad75511f3c003617b86f628f99b487d002d49b9f8960332a02cbebe5df4550d0cf644f2deffb64113590fb299910551f3e6c6d8c345b91a1a7c6731cfa9adc279d9cad4e64c19d7887e878f3039a7b3ecfd50e65386fb7a64e30203010001";
        result.put("sign", RsaKit.sign(msgText,privatekey));
        System.out.println("快捷订单查询："+result.toJSONString());
        result.put("accountId","1120130523134348001");
        String result_msg = AARProtocol.encrypt(result.toJSONString(), publickey);
        try{
            String post = HttpKit.post(URL.WAPORDER_URL, result_msg);
            System.out.println(post);
        } catch(RuntimeException e){
            e.printStackTrace();
        }*/


    }

}
