[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
		<div class="padding-lr list-col-10 margin-none">
			<div class="border-bottom mui-clearfix order-lh-40">
				<span class="icon-15 mui-inline margin-small-right"><img src=""></span>请选择支付方式
			</div>
			<div class="padding-tb" data-id="type_box">
				[#list paymentMethods as paymentMethod]
					<a class="mui-btn [#if paymentMethodId == paymentMethod.id] hd-btn-blue [#else] hd-btn-gray [/#if] margin-small-right" data-id="${paymentMethod.id}">${paymentMethod.name}</a>
				[/#list]
			</div>
		</div>
		
		<!-- <div class="padding-lr list-col-10 border-bottom">
			<div class="border-bottom mui-clearfix order-lh-40">
				<span class="icon-15 mui-inline margin-small-right"><img src=""></span>请选择并确认您的配送方式
			</div>
			<div data-id="delivery_box">
				<div class="border-bottom padding-bottom" data-sellerid="0">
					<div class="margin-top">
					[#list shippingMethods as shippingMethod]
						<a class="mui-btn hd-btn-gray margin-small-right" data-id="${shippingMethod.id}">${shippingMethod.name}</a>
					[/#list]
					</div>
				</div>
			</div> -->
			
		</div>
		<div class="padding-big" data-id="delivery">
			<a href="javascript:;" class="mui-btn mui-btn-primary full hd-h4">确定支付配送方式</a>
		</div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
</html>

<script type="text/javascript" src="${base}/statics/js/order.js?v=2.6.0.161014"></script>
<script type="text/javascript">
	
	mui(document).on("tap", ".mui-btn", function(){
		if(!$(this).hasClass('hd-btn-blue') && !$(this).hasClass('full')){
			$(this).addClass('hd-btn-blue').removeClass('hd-btn-gray');
			$(this).siblings().removeClass('hd-btn-blue').addClass('hd-btn-gray');
		}
	});
	
	//确定 支付
	$("[data-id='delivery']").on("tap",function() {
		var paymentMethodId = $("[data-id='type_box']").find(".hd-btn-blue").data("id");
		//var deliveryMethod = $("[data-id='delivery_box']").find(".hd-btn-blue").data("id");
		
		if (paymentMethodId == null) {
			$.tips({icon: 'error',content: '未选择[支付]方式'});
			return;
		}
		/* if (deliveryMethod == null) {
			$.tips({icon: 'error',content: '未选择[物流]方式'});
			return;
		} */
		//window.location.href="${url_forward}&paymentMethodId=" + paymentMethodId;
		var ajaxurl = "${base}/wap/order/addDeliveryCache.jhtml";
		$.post(ajaxurl,{paymentMethodId :paymentMethodId},function(ret){
			if(ret.status == 1){
				window.location.href = ret.referer;
			} 
		},'json');
	});
	//hd_order.setDeliver();
	
</script>