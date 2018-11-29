import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinalshop.api.config.URL;
import com.jfinalshop.api.utils.AARProtocol;
import com.jfinalshop.api.utils.RsaKit;

public class Testq {

	public static void main(String[] args) {
		String accountId = "1120180126093952001";
		String orderNo = "2018050961408";
		JSONObject msg = new JSONObject();
        msg.put("accountId",accountId);
        msg.put("orderNo",orderNo);
        String msgText = msg.toJSONString();
        JSONObject result = new JSONObject();
        result.put("msg",msgText);
        result.put("sign", RsaKit.sign(msgText,URL.pricateKeys));
        System.out.println("快捷订单查询："+result.toJSONString());
        result.put("accountId",accountId);
        String result_msg = AARProtocol.encrypt(result.toJSONString(), URL.publicKeys);
        String post = HttpKit.post("http://114.55.93.111:8080/routes/wap/queryOrderStatus", result_msg);
        String s = null;
         try {
             s = new String(AARProtocol.decrypt(post,URL.pricateKeys).getBytes(),"UTF-8");
             JSONObject s1 = JSONObject.parseObject(s);
             String ress = s1.getString("msg");
             String status=JSONObject.parseObject(ress).getString("status");
             String desc = JSONObject.parseObject(ress).getString("desc");
             System.out.println("订单的状态码result_code="+desc+">>>||>>>>>"+status);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
	}

}
