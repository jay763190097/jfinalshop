[#include "/wap/include/header.ftl" /]

	<div class="mui-content">
    	<div class="padding-lr-15 bg-white hd-h5">
    		<div class="order-lh-40">发票抬头</div>
    		<div class="order-note">
    			<input type="text" data-id="invoice_title" placeholder="请填写发票抬头" />
    		</div>
    	</div>
    	<div class="margin-top-15 padding-lr-15 bg-white hd-h5">
    		<div class="order-lh-40 border-bottom">发票信息</div>
    		<div class="padding-top" data-id="invoice_box">
				<div class="hd-radio full margin-bottom"><label>办公用品</label><input name="radio" type="radio" data-isinvoice="3"></div>
				<div class="hd-radio full margin-bottom"><label>生活用品</label><input name="radio" type="radio" data-isinvoice="2"></div>
				<div class="hd-radio full margin-bottom"><label>商品清单</label><input name="radio" type="radio" data-isinvoice="1"></div>
				<div class="hd-radio full margin-bottom"><label>不开发票</label><input name="radio" type="radio" data-isinvoice="0" checked="checked"></div>
			</div>
    	</div>
    	<div class="padding-15" data-id="invoice">
    		<a href="javascript:;" class="mui-btn mui-btn-primary full hd-h4">确定发票信息</a>
    	</div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
</html>
<script type="text/javascript">
	
	// 确定发票信息
	$("[data-id='invoice']").on("tap",function() {
		var isinvoice = parseInt($("[data-id='invoice_box']").find("input[type=radio]:checked").data("isinvoice"));
		var invoice_title = $("[data-id='invoice_title']").val();
		
		if ((invoice_title == null || invoice_title == undefined || invoice_title == '') && isinvoice != 0) {
			$.tips({content:'请填写发票抬头'});
			return false;
		}
		var invoice_content = "";
		if (isinvoice == 1) {
			invoice_content = "商品清单";
		} else if (isinvoice == 2) {
			invoice_content = "生活用品";
		} else if (isinvoice == 3) {
			invoice_content = "办公用品";
		} else {
			invoice_content = "不开发票";
		}
		var ajaxurl = "${base}/wap/order/addInvoiceCache.jhtml";
		$.post(ajaxurl,{invoiceContent :invoice_content, invoiceTitle :invoice_title},function(ret){
			if(ret.status == 1){
				window.location.href = ret.referer;
			} 
		},'json');
	});
	
</script>