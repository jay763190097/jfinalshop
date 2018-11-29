[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/member.order.js"></script>
		<div class="hd-grid filter-items bg-white">
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/coupon_code/list.jhtml" class="filter-item [#if isUsed == 'all'] current[/#if]">全部</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/coupon_code/list.jhtml?isUsed=false" class="filter-item [#if isUsed == false] current[/#if]">未使用</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/coupon_code/list.jhtml?isUsed=true" class="filter-item [#if isUsed == true] current[/#if]">已使用</a>
			</div>
		</div>
		<div id="refreshContainer" class="mui-content mui-scroll-wrapper" data-pullrefresh="1">
			<div class="mui-pull-top-pocket">
				<div class="mui-pull">
					<div class="mui-pull-loading mui-icon mui-icon-pulldown"></div>
					<div class="mui-pull-caption">下拉可以刷新</div>
				</div>
			</div>
			<div class="mui-scroll has-scorll-top" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
				<ul class="order-lists mui-clearfix">
					[#if pages?? && pages.list?has_content]
						[#list pages.list as couponCode]
							<li class="order-list list-col-10">
								<div class="order-stuats padding-tb lh-icon-15 list-item">
									<span class="icon icon-15"><img src="${base}/statics/images/ico_17.png"></span>
									<span class="[#if couponCode.isUsed == true] text-gray [#else] text-org [/#if] moneyVal" >${abbreviate(couponCode.coupon.name, 3, "")}</span>元代金券 
									<span class="mui-pull-right [#if couponCode.isUsed == true] text-black [#else] text-org [/#if]">${couponCode.isUsed?string('已使用','未使用')}</span>
								</div>
								<div class="padding-tb mui-text-right list-item">
									<span class="mui-badge">有效期：${couponCode.coupon.beginDate} ~ ${couponCode.coupon.endDate}</span>
								</div>
							</li>
						[/#list]
					[#else]
						<li class="user-list-none mui-text-center">
							<img src="${base}/statics/images/bg_3.png">
							<p class="margin-top text-black hd-h5">您还没有优惠券</p>
						</li>
					[/#if]
				</ul>
				<div class="mui-pull-bottom-pocket">
					<div class="mui-pull">
						<div class="mui-pull-loading mui-icon mui-spinner"></div>
						<div class="mui-pull-caption">上拉显示更多</div>
					</div>
				</div>
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