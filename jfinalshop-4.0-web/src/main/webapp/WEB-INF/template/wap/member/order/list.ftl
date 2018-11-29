[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/member.order.js"></script>
		<div class="hd-grid filter-items bg-white">
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/order/list.jhtml" class="filter-item [#if status == 'all'] current[/#if]"">全部</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/order/list.jhtml?status=pendingPayment" class="filter-item [#if status == 'pendingPayment'] current[/#if]">待付款</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/order/list.jhtml?status=pendingShipment" class="filter-item [#if status == 'pendingShipment'] current[/#if]">待发货</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/order/list.jhtml?status=shipped" class="filter-item [#if status == 'shipped'] current[/#if]">待收货</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/order/list.jhtml?status=completed" class="filter-item [#if status == 'completed'] current[/#if]">已完成</a>
			</div>
		</div>
		<div id="refreshContainer" class="mui-content">
			<div class="mui-pull-top-pocket">
				<div class="mui-pull">
					<div class="mui-pull-loading mui-icon mui-icon-pulldown"></div>
					<div class="mui-pull-caption">下拉可以刷新</div>
				</div>
			</div>
			<div class="mui-scroll has-scorll-top" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
				<ul class="order-lists mui-clearfix">
				[#if pages?? && pages.list?has_content]
					[#list pages.list as order]
						<li class="order-list list-col-10">
							<div class="order-stuats padding-tb lh-icon-15 list-item">
								<span class="icon icon-15"><img src="${base}/statics/images/ico_17.png"></span>
								<span>订单号：${order.sn}</span>
								<!-- <span class="mui-pull-right text-org">${order.statusNameValue}</span> -->
								[#if order.statusName == 'pendingPayment' && order.hasExpired()]
									<span class="mui-pull-right text-org">${message("Order.Status." + order.statusName)}(${message("admin.order.hasExpired")})</span>
								[#else]
									<span class="mui-pull-right text-org">${message("Order.Status." + order.statusName)}</span>
								[/#if]
							</div>
							<!-- 循环子订单 -->
							<div class="padding-tb mui-text-right list-item">
								<span class="shop-name mui-pull-left">官方自营</span>
								<span class="margin-big-right">共 ${order.quantity} 件商品</span>
								<span>订单总额：<b class="text-org">${currency(order.amount, true, true)}</b></span>
							</div>
							<div class="order-pic padding-top list-item mui-clearfix">
								[#if order?? && order.orderItems?has_content]
									[#list order.orderItems as orderItem]
										<a href="${base}/wap/member/order/view.jhtml?sn=${order.sn}" class="img-full" data-skuid="${orderItem.product.id}" data-nums="${orderItem.quantity}">
											<img src="${orderItem.thumbnail!setting.defaultThumbnailProductImage}" onerror="javascript:this.src='${base}/statics/images/default_no_upload.png';">
										</a>
									[/#list]
								[/#if]
							</div>
							<div class="order-hand padding-tb mui-text-right list-item" data-sub_sn="${order.sn}">
								[#if order.statusName == 'shipped' || order.statusName == 'received' || order.statusName == 'completed']
									<a href="${base}/wap/member/order/view.jhtml?sn=${order.sn}" class="mui-btn hd-btn-gray">查看订单</a>
									<a href="${base}/wap/member/order/track.jhtml?sn=${order.sn}" class="mui-btn hd-btn-gray">查看物流</a>
					        	[#else]
					        		<a href="${base}/wap/member/order/view.jhtml?sn=${order.sn}" class="mui-btn hd-btn-gray">查看订单</a>
					        	[/#if]
								[#if order.statusName == 'pendingPayment' && order.expire?? && !order.hasExpired()]
				                	<a href="${base}/wap/order/payment.jhtml?sn=${order.sn}" class="mui-btn hd-btn-gray">支付订单</a>
				                [/#if]
				                [#if order.expire?? && !order.hasExpired() && (order.statusName == 'pendingPayment' || order.statusName == 'pendingReview' || order.statusName == 'pendingShipment')]
				                	<a data-action="cancel" href="javascript:;" class="mui-btn hd-btn-blue">取消订单</a>
				                [/#if]
				                [#if order.expire?? && order.hasExpired() || order.statusName == 'completed' || order.statusName == 'received' ||  order.statusName == 'canceled' || order.statusName == 'failed' || order.statusName == 'denied']
				                	<a data-action="again" href="javascript:;" class="mui-btn hd-btn-gray">再次购买</a>
				                [/#if]
				                [#if order.statusName == 'shipped']
				                	<a data-action="finish" href="javascript:;" o-d-id="${order.id}" class="mui-btn hd-btn-blue">确认收货</a>
				                [/#if]
							</div>
						</li>
					[/#list]
				[#else]
					<!-- 无订单时 -->
					<li class="user-list-none mui-text-center">
						<img src="${base}/statics/images/bg_3.png">
						<p class="margin-top text-black hd-h5">您还没有相关的订单</p>
					</li>
				[/#if]
				</ul>
				<!--<div class="mui-pull-bottom-pocket">
					<div class="mui-pull">
						<div class="mui-pull-loading mui-icon mui-spinner"></div>
						<div class="mui-pull-caption">上拉显示更多</div>
					</div>
				</div>-->
			</div>
			<div class="mui-scrollbar mui-scrollbar-vertical">
				<div class="mui-scrollbar-indicator" style="transition-duration: 0ms; display: block; height: 8px; transform: translate3d(0px, 682px, 0px) translateZ(0px);"></div>
			</div>
		</div>
		<footer class="footer posi">
			<div class="mui-text-center copy-text">
				<span></span>
			</div>
		</footer>

		<div id="cli_dialog_div"></div>
	</body>

</html>