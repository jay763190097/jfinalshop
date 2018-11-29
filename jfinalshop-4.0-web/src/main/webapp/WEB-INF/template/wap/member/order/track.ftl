[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
	    <div class="logistics-company padding">
	    	<div class="mui-pull-left margin-right">
	    		<img src="${base}/statics/images/deliverys/shunfeng.png"/>
	    	</div>
	    	<div class="text-white">
	    		<h4 class="hd-h5">${order.shippingMethodName}</h4>
	    		<h4>运单号：[#if order?? && order.shippings?has_content] ${order.shippings[0].trackingNo} [#else] - [/#if]</h4>
	    	</div>
	    </div>
	    <div class="list-col-10 padding-lr">
	    	<div class="padding-tb border-bottom">
	    		<span class="hd-h5">物流跟踪</span>
	    	</div>
	    	<ul class="logistics-info padding-left-15">
	    	[#if order?? && order.shippings?has_content]
	    		[#list order.shippings as shipping]
					<li class="tracking-list {if $k == 0}new{/if}">
		    			<div class="box">
		    				<p>${shipping.address}</p>
		    				<p>${shipping.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
		    			</div>
		    		</li>
				[/#list]
	    	[#else]
	    		<li class="tracking-list {if $k == 0}new{/if}">
    			<div class="box">
    				<p>暂无物流信息！</p>
    			</div>
    		</li>
			[/#if]
	    	</ul>
	    </div>
	</div>
</body>
</html>