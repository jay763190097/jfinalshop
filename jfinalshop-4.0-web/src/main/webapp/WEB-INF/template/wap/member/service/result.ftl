[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
	    <ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="padding border-bottom mui-text-center">
	    		<span class="service-apply-ok"></span>
	    		[#if status?? && status == 1]
	    			<h2 class="hd-h3 margin-tb strong">退款成功！</h2>
	    		[#elseif status == 3]
	    			<h2 class="hd-h3 margin-tb strong"><b class="text-default">您的退款申请已失败，请联系人工处理！</b></h2>
	    		[#else]
	    			<h2 class="hd-h3 margin-tb strong">等待卖家确认收货并退款</h2>
	    		[/#if]
	    	</li>
	    	<li class="padding">
	    		<div class="lh-20">
	    		[#if track?? && track?has_content]
					<p>·物流状态：</p>
					<p>·物流公司：</p>
					<p>·物流单号：</p>
				[#else]
	    			<p>暂无物流信息！</p>
				[/#if]
	    		</div>
	    	</li>
	    </ul>
	    <div class="padding">
	    	<a href="" class="mui-btn full mui-btn-primary hd-h4">查看快递信息</a>
	    </div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
</html>
