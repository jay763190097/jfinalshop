[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
	<div class="mui-content">
	<form class="double-line clearfix" action="${base}/wap/member/service/save_info.jhtml" method="post" name="ajax_delivery">
	    <ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="padding border-bottom">
	    		<h3 class="margin-bottom hd-h5">请把商品快递至以下地址(未经卖家同意，请勿使用到付或平邮)</h3>
	    		<div class="lh-20">
	    			<p>收件人：张三</p>
					<p>邮编：421819</p>
					<p>联系电话：13800138000</p>
					<p>邮寄地址：佛山市顺德区大良新城区嘉信城市一期东区</p>
	    		</div>
	    	</li>
	    	<li class="padding border-bottom hd-h5">
	    		<span class="margin-right-15">物流公司</span>
	    		<span class="inline">
	    			<select class="margin-none padding-none" name="returns.delivery_corp">
	    			 	<option value="">请选择物流公司</option>
	    				[#if deliveryCorps?? && deliveryCorps?has_content]
	    					[#list deliveryCorps as deliveryCorp]
								<option value="${deliveryCorp.name}">${deliveryCorp.name}</option>
							[/#list]
						[/#if]
	    			</select>
	    		</span>
	    	</li>
	    	<li class="padding-lr border-bottom hd-h5">
	    		<span class="margin-right-15">物流单号</span>
	    		<span class="inline"><input name="returns.tracking_no" type="text" class="padding-none margin-none border-none" value="" placeholder="请填写物流单号" /></span>
	    	</li>
	    	<li>
	    		<textarea class="border-none margin-none hd-h5" name="returns.memo" placeholder="如有特别说明请注明"></textarea>
	    	</li>
	    </ul>
	    <div class="padding">
	    	<input type="hidden" name="returns.id" value="${returns.id}" />
	    	<input type="submit" class="mui-btn full mui-btn-primary hd-h3 ajax_delivery" value="提交申请">
	    </div>
	</form>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
<script type="text/javascript">
	$('.ajax_delivery').bind('click',function(){
		var ajax_refund = $("form[name=ajax_delivery]").Validform({
			showAllError:true,
			ajaxPost:true,
			callback:function(ret) {
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
							//window.location.reload();
							window.location.href = ret.referer;
						}
					});
				}
			}
		})
	})
</script>