[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
		<form action="" method="post" name="dopay">
			<input type="hidden" name="type" value="payment" />
			<input type="hidden" name="sn" value="${order.sn}" />
			<!-- <input type="hidden" name="is_balance" value="[#if member.balance > 0]1[/#if]" /> -->
			<ul class="mui-table-view layout-list-common hd-h4 margin-none">
				<li class="mui-table-view-cell">订单号：<em class="text-org">${order.sn}</em></li>
			</ul>
			<ul class="mui-table-view layout-list-common hd-h4 margin-top">
				<li class="mui-table-view-cell">应付总额：<em class="text-org">[#if amount??]${currency(amount, true, true)}[#else]${currency(order.amountPayable, true, true)}[/#if]</em></li>
				<li class="mui-table-view-cell">支付方式：在线支付</li>
				<!-- [#if member.balance > 0]
					<li class="mui-table-view-cell">
						<label data-id="balance_pay">
							<div class="hd-checkbox hd-h5">
			    				<input type="checkbox" />
			    				<span class="label">账户余额：${currency(member.balance, true, true)}</span>
			    			</div>
			    			[#if amount - member.balance > member.balance]
				    			<div class="mui-block padding-large-left" data-id="pay_amount">
				    				<span class="text-org hd-h5">您还需在线支付：<em>0</em> 元</span>
				    			</div>
			    			[/#if]
						</label>
					</li>
				[/#if] -->
			</ul>
			<ul class="pay-lists list-col-10 mui-clearfix" data-id="pays">
			[#if order.paymentMethod.methodName == "online"]
				[#if paymentPlugins?has_content]
					[#list paymentPlugins?chunk(4, "") as row]
						[#list row as paymentPlugin]
							[#if paymentPlugin?has_content]
								<li class="pay-list">
									<label class="mui-block">
										<div class="hd-radio"><input name="paymentPluginId"  value="${paymentPlugin.id}" data-id="pay_method" type="radio" data-code="ws_wap" data-bank="ws_wap" /></div>
										[#if paymentPlugin.logo?has_content]
											<div class="pay-icon"><img src="${paymentPlugin.logo}"></div>
										[/#if]
										<span class="hd-h5">${paymentPlugin.paymentName}</span>
										<p class="lh-18">${paymentPlugin.description}</p>
									</label>
								</li>
							[/#if]
						[/#list]
					[/#list]
				[#else]
					<li class="pay-list"> 后台暂未开启支付方式 </li>
				[/#if]
			[/#if]
			</ul>
		    <div class="margin padding-small">
		    	<a data-id="subbtn" href="javascript:wxpay();" class="mui-btn mui-btn-blue full hd-h4">确认支付</a>
		    </div>
	    </form>
	</div>
</body>
</html>

<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.1.0.js"></script>
<script type="text/javascript">
	$("[data-id='pay_method']:first").prop("checked" ,true);	// 默认选中支付方式第一个
	
	/* 微信支付 */
	function wxpay(){
		// 是否勾选余额支付
		var isBalance = $("[data-id='balance_pay']").find("input[type='checkbox']").prop("checked");
		var paymentPluginId = $("[data-id='pay_method']:checked").val();
		
		// 支付方式
		var pay_code = $("[data-id='pay_method']:checked").data("code");
		if (pay_code == undefined && (is_balance == false || (real_amount > member_money))) {
			$.tips({content: '请选择支付方式'});
			return false;
		}
		$.post("${base}/wap/payment/submit.jhtml", {sn: ${order.sn}, type: "payment", is_balance: isBalance, paymentPluginId: paymentPluginId}, function(res) { 
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
				alert(JSON.stringify(res));
				// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回 ok，但并不保证它绝对可靠。
				var success = false;
				if(res.err_msg == "get_brand_wcpay_request:ok" ) {
					success = true;
					$.tips({content: '支付成功'});
				} else if(res.err_msg == "get_brand_wcpay_request:cancel" ) {
					$.tips({content: '支付取消'});
				} else {
					$.tips({content: '支付失败'});
				}
				window.location.href = '${base}/wap/payment/success.jhtml?success=' + success + '&sn=' + ${order.sn};
			}
		); 
	}
	
</script>
