[#include "/wap/include/header.ftl" /]
		<script type="text/javascript" src="${base}/statics/js/member.order.js?v=2.6.0.161014"></script>

		<div class="mui-content has-footer-bar order-pic">
			<div class="bg-white border-bottom padding-lr">
				<div class="order-lh-40 border-bottom order-detail-info">
					<span class="icon-15 mui-pull-left margin-small-right"><img src="/statics/images/ico_20.png"></span>
					<span class="mui-pull-left margin-small-right">订单状态：
					[#if order.statusName == 'pendingPayment' && order.expire?? && order.hasExpired()]
						<em class="text-org">[${message("shop.member.order.hasExpired")}]</em></span>
					[#else]
						<em class="text-org">[${message("Order.Status." + order.statusName)}]</em></span>
					[/#if]
					<span class="text-ellipsis mui-block mui-text-right">订单号：<em class="text-org">${order.sn}</em></span>
				</div>
				<div class="padding-tb ">
					<div class="full hd-h4">
						<span class=" mui-pull-left w50 text-ellipsis">收货人：${order.consignee}</span>
						<span class="mui-pull-right text-ellipsis w50 mui-text-right">电话：${order.phone}</span>
					</div>
					<div style="clear: both;"></div>
					<p class="text-black margin-top">收货地址：${order.address}</p>
				</div>
			</div>
			<div class="list-col-10 padding">
				<div class="padding-bottom border-bottom">
					<span class="hd-h4">支付方式</span>
					<span class="mui-pull-right">[#if order?? && order.paymentMethodName?has_content] ${order.paymentMethodName} [#else] - [/#if]</span>
				</div>
				<div class="padding-top">
					<span class="hd-h4">配送方式</span>
					<span class="mui-pull-right">[#if order?? && order.shippingMethodName?has_content] ${order.shippingMethodName} [#else] - [/#if]</span>
				</div>
			</div>
			<div class="list-col-10 padding">
		    	<div class="padding-bottom border-bottom">
					<span class="hd-h4">发票信息</span>
				</div>
				<div class="padding-top text-ellipsis">
					[#if order??]
						<span class="hd-h4">发票抬头：[#if order.invoiceTitle?has_content] ${order.invoiceTitle} [#else] - [/#if]</span>
						<br />
						<span class="hd-h4">发票内容：[#if order.invoiceContent?has_content] ${order.invoiceContent} [#else] - [/#if]</span>
					[#else]
						无
					[/#if]
				</div>
		    </div>
			<ul class="custom-goods-items custom-goods-row row1 custom-list-goods margin-top bg-white mui-clearfix">
				<li class="goods-item-list border-top">
					<div class="padding-small-top padding-small-bottom hd-h5">
						<span class="padding-lr mui-block">买家留言：[#if order.memo?has_content] ${order.memo} [#else] - [/#if]</span>
					</div>
				</li>
				[#if order.orderItems?? && order.orderItems?has_content]
					[#list order.orderItems as orderItem]
						<li class="goods-item-list">
				    		<div class="list-item">
				    			<div class="list-item-pic">
									<a href="${base}/wap/goods/detail.jhtml?id=${orderItem.product.goods.id}"><img src="${orderItem.thumbnail!setting.defaultThumbnailProductImage}"></a>
								</div>
								<div class="list-item-bottom">
									<div class="list-item-title">
										<a href="${base}/wap/goods/detail.jhtml?id=${orderItem.product.goods.id}" data-skuid="${orderItem.product.id}" data-nums="${orderItem.quantity}">${orderItem.name}</a>
									</div>
									<div class="list-item-text hd-h6">
										<span class="text-ellipsis"><em class="price-org hd-h4">￥${orderItem.price?default('线下确定')}</em> × ${orderItem.quantity}</span>
										[#if order?? && order.statusName == 'received']
											<span class="mui-pull-right">
												<a href="${base}/wap/member/service/alert_refund.jhtml?orderItemId=${orderItem.id}" class="mui-btn hd-btn-gray">申请售后</a>
											</span>
										[/#if]
									</div>
								</div>
							</div>
						</li>
					[/#list]
				[/#if]
			</ul>
			<div class="padding-lr bg-white order-detail-price mui-clearfix">
				<ul class="order-settle border-bottom">
					<li>
						<span class="hd-h4">应付总额</span>
						<span class="mui-pull-right text-org">${currency(order.amount, true, true)}</span>
					</li>
					<li>
						<span>+运费</span>
						<span class="mui-pull-right text-org">${currency(order.freight, true, true)}</span>
					</li>
					<!-- <li>
						<span>+发票税额</span>
						<span class="mui-pull-right text-org">￥0.00</span>
					</li>
					<li>
					<span>- 订单促销</span>
					<span class="mui-pull-right text-org">￥0.00</span>
					</li> -->
				</ul>
				<div class="order-total mui-text-right padding-bottom">
					<span class="hd-h4 lh-30">实付款：<em class="text-org">${currency(order.amount, true, true)}</em></span><br>
					<span class="text-gray">下单时间：${order.createDate?string("yyyy-MM-dd HH:mm:ss")}</span>
				</div>
			</div>
		</div>
		<nav data-sub_sn="${order.sn}" class="mui-bar mui-bar-tab padding-lr cart-footer-bar" style="padding: .16rem .2rem;"> 
			[#if order.expire?? && !order.hasExpired() && order.statusName == 'pendingPayment']
				<a href="${base}/wap/order/payment.jhtml?sn=${order.sn}" class="mui-pull-right mui-btn mui-btn-primary">立即付款</a>
	        [/#if]
			[#if order.expire?? && !order.hasExpired() && (order.statusName == 'pendingPayment' || order.statusName == 'pendingReview' || order.statusName == 'pendingShipment')]
				<a data-action="cancel" href="javascript:;" class="mui-pull-right mui-btn">取消订单</a>
	        [/#if]
			[#if order.expire?? && order.hasExpired() || order.statusName == 'completed' || order.statusName == 'received' ||  order.statusName == 'canceled' || order.statusName == 'failed' || order.statusName == 'denied']
		    	<a data-action="again" class="mui-pull-right mui-btn" href="javascript:;">再次购买</a>
		    [/#if]
		    [#if order.statusName == 'shipped' || order.statusName == 'received' || order.statusName == 'completed']
		    	<a class="mui-pull-right mui-btn" href="${base}/wap/member/order/track.jhtml?sn=${order.sn}">查看物流</a>
		    	[#if order.statusName == 'shipped']
		    		<a data-action="finish" href="javascript:;" class="mui-pull-right mui-btn mui-btn-primary" o-d-id="{$detail[_delivery][id]}">确认收货</a>
		    	[/#if]
			[/#if]
		</nav>

		<div id="cli_dialog_div"></div>
	</body>

</html>