[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.2.161028"></script>
	<div class="mui-content">
		<form action="" method="post" target="_blank" name="recharge">
			<div class="padding bg-white border-bottom">
				<input class="margin-none" type="text" value="" placeholder="请输入充值金额" name="money">
			</div>
			<ul class="pay-lists list-col-10 mui-clearfix">
				<!-- <li class="pay-list" data-paycode="ws_wap" data-paybank="">
					<label class="mui-block">
						<div class="hd-radio">
		    				<input name="radio" value="0" type="radio">
		    			</div>
						<div class="pay-icon"><img src="${base}/statics/images/ws_wap.png"></div>
						<span class="hd-h5">支付宝手机支付</span>
					</label>
				</li> -->
				[#if paymentPlugins??]
					[#list paymentPlugins as paymentPlugin]
						<li class="pay-list" data-paycode="ws_wap" data-paybank="">
							<label class="mui-block">
								<div class="hd-radio">
				    				<input name="radio" name="paymentPluginId"  value="${paymentPlugin.id}" type="radio" data-id="pay_method">
				    			</div>
								<div class="pay-icon"><img src="${paymentPlugin.logo}"></div>
									<span class="hd-h5">${paymentPlugin.paymentName}</span>
							</label>
						</li>
					[/#list]
				[#else]
					<li class="pay-list"> 后台暂未开启支付方式 </li>
				[/#if]
			</ul>
			<div class="margin padding-small">
				<input type="hidden" name="pay_code" value="ws_wap">
				<input type="hidden" name="pay_bank" value="">
				<!-- <input type="submit" value="确认支付" class="mui-btn mui-btn-blue full hd-h4 recharge"> -->
				<a data-id="subbtn" href="javascript:wxpay();" class="mui-btn mui-btn-blue full hd-h4">确认支付</a>
			</div>
		</form>
	</div>
	<footer class="footer posi">
	</footer>
	<div id="cli_dialog_div"></div>
	</body>
</html>

<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
<script>
	$(function() {
		$("ul.pay-lists > li").eq(0).find('input').trigger("click"); // 默认选中第一个支付方式
	}) 
</script>
<script type="text/javascript">
	/* 微信支付 */
	function wxpay(){
		var money = $("input[name=money]").val();
		if(money == '' || money.match(/^[0-9]{1}\d*(\.\d{1,2})?$/) == null) {
			$.tips({
				content: '充值金额错误'
			});
			return;
		}
		
		var paymentPluginId = $("[data-id='pay_method']:checked").val();
		if(paymentPluginId == '') {
			$.tips({
				content: '支付方式选择错误'
			});
			return;
		}
		$.post("${base}/wap/payment/submit.jhtml", {amount: money, type: "recharge", paymentPluginId: paymentPluginId}, function(res) { 
	    	if (res.code == 0) {
	    		var data = $.parseJSON(res.data);
	    		if (typeof WeixinJSBridge == "undefined") {
	    			if ( document.addEventListener ) {
	    				document.addEventListener('WeixinJSBridgeReady', onBridgeReady(data), false);
	    			} else if (document.attachEvent){
	    				document.attachEvent('WeixinJSBridgeReady', onBridgeReady(data)); 
	    				document.attachEvent('onWeixinJSBridgeReady', onBridgeReady(data));
	    			}
	    		} else {
	    			onBridgeReady(data);
	    		}
	    	} else {
	    		if (res.code == 2) {
	    			$.tips({content: res.message});
	    		} else {
	    			$.tips({content: "参数错误："+res.message});
	    		}
	    	}
	    }); 
	}
	
	function onBridgeReady(json){
		WeixinJSBridge.invoke('getBrandWCPayRequest', json, function(res){
				// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回 ok，但并不保证它绝对可靠。
				var success = false;
				//alert(JSON.stringify(res));
				if(res.err_msg == "get_brand_wcpay_request:ok" ) {
					success = true;
					$.tips({content: '支付成功!'});
				} else {
					$.tips({content: '支付失败!'});
				}
				window.location.href = '${base}/wap/payment/result.jhtml?success=' + success + '&err_desc=' + res.err_desc;
			}
		); 
	}
</script>