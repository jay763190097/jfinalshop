[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
	    <ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="padding border-bottom mui-text-center">
	    		<span class="service-apply-ok"></span>
	    		<h2 class="hd-h3 margin-tb strong">${title}</h2>
	    	</li>
	    	<li class="padding">
	    		<span class="hd-h5">订单号：<em class="text-org">${order.sn}</em></span>
	    	</li>
	    	<li class="padding">
	    		<span class="hd-h5">支付金额：<em class="text-org">${order.amountPaid}</em></span>
	    	</li>
	    </ul>
	    <div class="padding">
	    	<a href="${base}/wap/member/order/list.jhtml" class="mui-btn full mui-btn-primary hd-h4">查看订单</a>
	    </div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
</html>
