[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
		<ul class="address-lists bg-white mui-clearfix" data-id="address_box">
			[#if pages?? && pages.list?has_content]
				[#list pages.list as receiver]
					<li class="address-list">
		        	   	<div class="address-text" data-id="${receiver.id}" data-district="${receiver.areaId}" data-addressid="${receiver.id}">
		        	   		<a class="mui-block">
		        	   			<span class="name text-ellipsis">${receiver.consignee}</span>
		        	   			<span class="address-btn margin-small-right [#if receiverId != receiver.id] hide [/#if]" data-id="now">当前选中</span>
		        	   			<span class="mui-pull-right">${receiver.phone}</span>
			    	       		<p>[#if receiver.isDefault] [默认] [/#if] ${receiver.areaName} ${receiver.address}</p>
		        	   		</a>
		        	   	</div>
		        	   	<div class="edit">
		        	   		<a href="${base}/wap/member/receiver/edit.jhtml?url_forward=${curRedirectUrl}&id=${receiver.id}"><img src="${base}/statics/images/ico_21.png" /></a>
		        	   	</div>
		        	</li>
	        	[/#list]
	        [#else]
	    		<p class="padding text-org mui-text-center">请先添加一个地址！</p>
	    	[/#if]
	    </ul>
	   <!--  <div class="padding-lr margin-top">
			<a id="comfirm" href="#" class="mui-btn mui-btn-primary full hd-h4">设为收货地址</a>
		</div> -->
    	<div class="padding-lr margin-top">
    		<a id="address_add" href="#" class="mui-btn mui-btn-primary full hd-h4">添加新收货地址</a>
    	</div>
	</div>
</body>
</html>
<!-- <script type="text/javascript" src="${base}/statics/js/order.js?v=2.6.0.161014"></script> -->
<script>
	
	/* 添加新收货地址 */
	mui("body").on("tap", '#address_add', function() {
		window.location.href = '${base}/wap/member/receiver/add.jhtml?url_forward=${curRedirectUrl}';
	})
	
	/* 确定收货地址 */
	/* mui("body").on("tap", '#comfirm', function() {
		var addressid = $("[data-id='now']").parent().parent().data("addressid");
		if(addressid == null || addressid == 'undefined'){
			$.tips({icon: 'error',content: '请选择其中一个地址'});
			return ;
		}
		var ajaxurl = "${base}/wap/order/addAddressCache.jhtml";
		$.post(ajaxurl,{receiverId :addressid},function(ret){
			if(ret.status == 1){
				window.location.href = ret.referer;
			} 
		},'json');
	}); */
	
	function addrSelection() {
		var addressid = $("[data-id='now']").parent().parent().data("addressid");
		if(addressid == null || addressid == 'undefined'){
			$.tips({icon: 'error',content: '请选择其中一个地址'});
			return ;
		}
		var ajaxurl = "${base}/wap/order/addAddressCache.jhtml";
		$.post(ajaxurl,{receiverId :addressid},function(ret){
			if(ret.status == 1){
				window.location.href = ret.referer;
			} 
		},'json');
	}
	
	/* 选择收货地址 */
	mui(document).on("tap", ".address-text", function() {
		$(".address-text").each(function() {
			$(this).find(".address-btn").addClass("hide");
			$(this).find(".address-btn").attr("data-id","");
		});
		$(this).find(".address-btn").removeClass("hide");
		$(this).find(".address-btn").attr("data-id","now");
		addrSelection();
	});
	
</script>