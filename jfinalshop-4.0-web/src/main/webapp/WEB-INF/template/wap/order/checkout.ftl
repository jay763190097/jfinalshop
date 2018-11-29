[#include "/wap/include/header.ftl" /]
	<div class="mui-content has-footer-bar">
	    <ul class="mui-table-view layout-list-common margin-none">
	    	<li class="mui-table-view-cell">
	    		<div class="full mui-pull-left padding-bottom border-bottom lh-icon-15">
	    			<span class="icon-15 mui-pull-left margin-small-right"><img src="${base}/statics/images/ico_22.png" /></span>请选择并确认您的收货地址
	    		</div>
	    		<div class="mui-clearfix"></div>
				<a href="${base}/wap/order/address.jhtml?url_forward=${redirectUrl}&receiverId=${defaultReceiver.id}" class="mui-navigate-right settlement-address">
					<input data-id="receiverId" type="hidden" value="${defaultReceiver.id}"/>
					<span class="hd-h4" data-show="address_name">收货人：${defaultReceiver.consignee}</span>
					<span class="mui-pull-right" data-show="address_mobile">${defaultReceiver.phone}</span>
	        		<p class="margin-small-top text-drak " data-show="address_detail">收货地址：${defaultReceiver.areaName}  ${defaultReceiver.address}</p>
				</a>
			</li>
		</ul>
		<ul class="mui-table-view layout-list-common margin-top">
			<li class="mui-table-view-cell">
				<a href="${base}/wap/order/delivery.jhtml?url_forward=${redirectUrl}&paymentMethodId=${paymentMethod.id}" class="mui-navigate-right">
					<span class="hd-h4" data-show="title">支付配送</span>
					<input data-id="paymentMethodId" type="hidden" value="${paymentMethod.id}"/>
					<span class="mui-pull-right mui-text-right" data-show="pay_delivery">${paymentMethod.name}</span>
				</a>
			</li>
			[#if order.typeName == "general" && setting.isInvoiceEnabled]
				<li class="mui-table-view-cell">
					<a href="${base}/wap/order/invoice.jhtml?url_forward=${redirectUrl}" class="mui-navigate-right">
						<span class="hd-h4">发票信息</span>
						<input data-id="title" type="hidden" value="${invoice.title}"/>
						<input data-id="content" type="hidden" value="${invoice.content}"/>
						<span class="mui-pull-right" data-show="invoice_content">${invoice.content}</span>
					</a>
				</li>
			[/#if]
		</ul>
		
		<ul class="mui-table-view layout-list-common hd-h4 margin-top">
			[#if member.balance > 0]
				<li class="mui-table-view-cell">
					<label data-id="balance_pay">
						<div class="hd-checkbox hd-h5">
		    				<input type="checkbox" />
		    				<span class="label">账户余额：${currency(member.balance, true, true)}</span>
		    				<input type="hidden" name="is_balance" value="[#if member.balance > 0]1[/#if]" />
		    			</div>
		    			
		    			[#if order.amount - member.balance > 0]
			    			<div class="mui-block padding-large-left" data-id="pay_amount">
			    				<span class="text-org hd-h5">您还需在线支付：<em>0</em> 元</span>
			    			</div>
		    			[/#if]
					</label>
				</li>
			[/#if]
		</ul>
			
		<ul data-sellerid="{$sellerid}" class="custom-goods-items custom-goods-row row1 custom-list-goods list-col-10 mui-clearfix">
			<li class="padding border-bottom lh-icon-15">
				<span class="mui-pull-left icon icon-15"><img src="${base}/statics/images/ico_shop.png"></span>
				<span class="margin-left">${setting.siteName}自营</span>
				[#if order.isDelivery]
					<span class="mui-pull-right">运费：<em data-show="delivery_price">${currency(order.freight, true)}</em></span>
				[/#if]
			</li>
			
			<li class="bg-white border-bottom padding-lr">
				<div class="deliverys [#if defaultReceiver?has_content] mui-hidden [/#if]"><p class="margin-top mui-text-center text-org">您所选择的收货地址暂时无法配送</p></div>
				<div class="padding-tb order-note">
					<input data-id="remarks" class="margin-none" type="text" placeholder="对商家的留言">
				</div>
				[#if order.promotionDiscount != 0]
					<a href="#" class="padding-tb border-top prom-nav text-drak mui-block mui-navigate-right">
		    			<span class="hd-h5">订单促销</span>
						<span class="mui-pull-right margin-big-right" data-show="order_prom">请选择</span>
		    		</a>
	    		[/#if]
			</li>
			
			[#list order.orderItems as orderItem]
		    	<li class="goods-item-list" data-skuid="#">
					<div class="list-item">
						<div class="list-item-pic">
							<a href="#"><img src="${orderItem.product.thumbnail!setting.defaultThumbnailProductImage}"></a>
						</div>
						<div class="list-item-bottom">
							<div class="list-item-title">
								<a href="#">${abbreviate(orderItem.product.name, 50, "...")}</a>
							</div>
							<div class="list-item-text hd-h6 mui-row">
								<span class="text-ellipsis text-gray mui-col-xs-6">
									[#if orderItem.product.specifications?has_content]
										<span class="silver">[${orderItem.product.specifications?join(", ")}]</span>
									[/#if]
								</span>
								<span class="mui-text-right mui-col-xs-6"><em class="price-org hd-h4">[#if orderItem.typeName == "general"] ${currency(orderItem.price, true)} [#else] - [/#if]</em>× ${orderItem.quantity}</span>
							</div>
						</div>
					</div>
					[#if order.couponDiscount != 0]
					<div class="mui-pull-left full padding-lr-15">
						<a href="#" class="padding-tb full border-top prom-nav text-drak mui-block mui-navigate-right">
			    			<span class="bg-red promotion-btn">促销优惠</span>
			    			<span class="mui-pull-right margin-big-right" data-show="goods_prom">请选择</span>
			    		</a>
					</div>
				    [/#if]
				</li>
			[/#list]
	    </ul>
	   
	</div>
	<nav class="cart-footer-bar">
		<div class="cart-footer-box full">
		    <p class="mui-pull-right cart-total mui-text-right">
				<span class="text-org">合计：<em class="normal" data-show="real_amount">${currency(order.amount, true, true)}</em></span><br/>
				结算商品数量：<em class="normal" data-show="sku_numbers">${order.productQuantity}</em> 件
			</p>
		</div>
		<input data-id="cartToken" type="hidden" value="${cartToken}"/>
		<a id="settlement-submit" data-id="submit" href="javascript:;" class="cart-footer-btn mui-text-center">结算</a>
	</nav>
</body>
</html>
<script type="text/javascript" src="${base}/statics/js/order-pay.js?v=2.6.0.161014"></script>
<!-- <script type="text/javascript" src="${base}/statics/js/order.js?v=2.6.0.161014"></script> -->
<script type="text/javascript">
	var order = {};
	order.real_amount = ${order.amount};
	order.balance_amount =0;
	
	var member = {};
	member.money = ${member.balance};
	hd_pay.init();
</script>
<script>
	window.onload = function(){
		//提交
		mui(document).on("tap", "#settlement-submit", function(){
			if ($(this).hasClass('disabled')) return false;
			
			var params = {};
			var cartToken = $("[data-id='cartToken']").val();
			params.cartToken = cartToken;
			var receiverId = $("[data-id='receiverId']").val();
			params.receiverId = receiverId;
			var paymentMethodId = $("[data-id='paymentMethodId']").val();
			params.paymentMethodId = paymentMethodId;
			
			var title = $("[data-id='title']").val();
			params.title = title;
			var content = $("[data-id='content']").val();
			params.content = content;
			
			var remarks = $("[data-id='remarks']").val();
			params.memo = remarks;
			
			var isBalance = $("[data-id='balance_pay']").find("input[type='checkbox']").prop("checked");
			params.is_balance = isBalance;
			
			
			$.post('${base}/wap/order/create.jhtml', params, function(ret) {
				$("#settlement-submit").text('提交中...');
				$("[data-id=submit]").addClass('disabled');
				
				if(ret.status == 1) {
					$("[data-id='submit']").text('提交成功');
					setTimeout(window.location.href = ret.referer, 500);
					return false;
				} else {
					$.tips({icon: 'error',content: ret.message});
					$("[data-id='submit']").text('重新提交');
					$("[data-id=submit]").removeClass('disabled');
					return false;
				}
			}, 'json');
		});
		
	}
</script>