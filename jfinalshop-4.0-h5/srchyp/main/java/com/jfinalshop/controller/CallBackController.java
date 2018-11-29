package com.jfinalshop.controller;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * java类简单作用描述
 *
 * @ProjectName: jfinalshop-4.0$
 * @Package: com.jfinalshop.controller$
 * @ClassName: $TYPE_NAME$
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/4/19$ 17:57$
 * @UpdateUser: 作者姓名
 * @UpdateDate: 2018/4/19$ 17:57$
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018$</p>
 */
@ControllerBind(controllerKey = "/callback")
public class CallBackController extends Controller{
    private static Logger logger = LoggerFactory.getLogger(CallBackController.class);
    public void update (){
        try {
            // 总金额
            String amount = getRequest().getParameter("amount");
            // 返回状态码
            String result_code = getRequest().getParameter("result_code");
            // 商户订单号
            String orderNo = getRequest().getParameter("orderNo");
            if(orderNo==null){
                orderNo =getRequest().getParameter("orderId");
            }
            // 交易状态
            String status = getRequest().getParameter("status");
            //结果描述descdes
            String  desc= getRequest().getParameter("desc");
            //银行名称
            String  bankName= getRequest().getParameter("bankName");
            //用户名称
            String  userid= getRequest().getParameter("userId");
            // 交易完成回调时间，格式为yyyyMMddHHmmss
            //String time_end = getRequest().getParameter("time_end");
            Map<String,String> mapp = new LinkedHashMap<String,String>(10);
            mapp.put("result_code",result_code);
            mapp.put("total_fee",amount);
            mapp.put("sn",orderNo);
            mapp.put("status",status);
            mapp.put("bankName",bankName);
            mapp.put("username",userid);
            String res = HttpKit.post(PropKit.get("paymentPay"),mapp,null);
            if(res!=null){
                logger.info("回调成功");
            }
            renderJson("message","SUCCESS");
        } catch (Exception e) {
            logger.warn("",e);
        }
    }

}
