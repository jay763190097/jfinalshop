[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
	    <ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="padding border-bottom mui-text-center">
	    		<span class="service-apply-ok"></span>
	    		<h2 class="hd-h3 margin-tb strong">
	    		[#if returnsItem.status == 0]
	    			您已申请成功，等待卖家处理
				[#elseif returnsItem.status == 1] 
					您的申请已通过
				[#elseif returnsItem.status == 2] 
					您的退款申请已退款成功
				[#elseif returnsItem.status == 3] 
					您的退款申请失败，请联系人工处理
				[#elseif returnsItem.status == 4] 
					您的退款申请已取消！
				[/#if] 
	    		</h2>
	    	</li>
	    	<li class="padding">
	    		<div class="lh-20">
	    			<p>·如果卖家同意，您可以退货给卖家并完成退款</p>
					<p>·如果卖家拒绝，需要您修改退货并退款申请</p>
					<p>·每个商品&订单您只有一次售后维权的机会</p>
	    		</div>
	    	</li>
	    </ul>
	    <div class="padding">
	    [#if returnsItem.status?? && returnsItem.status != 3 && returnsItem.status != 4]
		    [#if returnsItem.status == 1] 
		    	<a href="${base}/wap/member/service/return_info.jhtml?id=${returnsItem.returns.id}" class="mui-btn full mui-btn-primary hd-h4">退货并填写退货信息</a>
		    [#elseif returnsItem.status == 0] 
		    	<button type="button" class="mui-btn full mui-btn-primary [#if type == 'refund'] cancel_refund_delivery [#else] cancel_return_delivery[/#if] hd-h4 [#if returnsItem.status == 1]margin-top[/#if]">取消售后[#if returnsItem.status == 0]申请[/#if]</button>
		    [/#if]
		[/#if]
		</div>
		
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
<script type="text/javascript">
	$('.cancel_return_delivery').bind('click',function(){
			var url = "/wap/member/service/cancel.jhtml";
			var id = "${returnsItem.id}";
			$.confirms("每件商品仅有一次售后机会，是否确认取消?",function(){
				$.post(url,{id:id},function(ret){
					if(ret.status == 0) {
						$.tips({
							content:ret.message,
							callback:function() {
								return false;
							}
						});
					} else {
						$.tips({
							content:ret.message,
							callback:function() {
								window.location.reload();
							}
						});					
					}
				},'json')
			})
		})
		/* $('.cancel_refund_delivery').bind('click',function(){
			var url = "<?php echo url('ajax_refund_cancel')?>";
			var id = "<?php echo $_GET['id']?>";
			$.post(url,{id:id},function(ret){
				if(ret.status == 0) {
					$.tips({
						content:ret.message,
						callback:function() {
							return false;
						}
					});
				} else {
					$.tips({
						content:ret.message,
						callback:function() {
							window.location.reload();
						}
					});					
				}
			},'json')
		}) */
</script>
