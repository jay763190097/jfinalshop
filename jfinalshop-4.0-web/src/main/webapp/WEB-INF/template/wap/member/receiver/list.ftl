[#include "/wap/include/header.ftl" /]
		<div class="mui-content">
			<ul class="member-address mui-table-view layout-list-common margin-none mui-clearfix">
				[#if pages?? && pages.list?has_content]
					[#list pages.list as receiver]
			        <li class="address-list">
						<a href="${base}/wap/member/receiver/edit.jhtml?id=${receiver.id}" class="mui-block mui-navigate-right padding-big-right">
						[#if receiver.isDefault]
				        	<div class="address-text">
				        		<span class="name text-ellipsis">${receiver.consignee}</span>
			        			<span class="address-btn margin-small-right">默认</span>
			        			<span class="mui-pull-right">${receiver.phone}</span>
				        		<p>[默认]${receiver.area_name}  ${receiver.address} [#if receiver.zipCode?has_content]  邮编：${receiver.zipCode}[/#if]</p>
				        	</div>
						[#else]
							<div class="address-text" data-event="default">
				        		<span class="name text-ellipsis">${receiver.consignee}</span>
			        			<span class="mui-pull-right">${receiver.phone}</span>
				        		<p>${receiver.area_name}  ${receiver.address} [#if receiver.zipCode?has_content]  邮编：${receiver.zipCode}[/#if]</p>
				        	</div>
						[/#if]
						</a>
			        </li>
			        [/#list]
				[/#if]
		    </ul>
	    	<div class="padding-lr margin-top">
	    		<a href="${base}/wap/member/receiver/add.jhtml" class="mui-btn mui-btn-primary full hd-h4">添加新收货地址</a>
	    	</div>			
		</div>
		<footer class="footer posi">
			<div class="mui-text-center copy-text">
				<span></span>
			</div>
		</footer>

		<script type="text/javascript" src="${base}/statics/js/order.js?v=2.6.0.161014"></script>
		<script>
			/* 添加新收货地址 */
			mui("body").on("tap", '#address_add', function() {
				window.location.href = '/wap/member/receiver/add.jhtml';
			})
		</script>
		<div id="cli_dialog_div"></div>
	</body>

</html>