[#include "/wap/include/header.ftl" /]
		<div id="refreshContainer" class="mui-content">
			<div class="mui-pull-top-pocket">
				<div class="mui-pull">
					<div class="mui-pull-loading mui-icon mui-icon-pulldown"></div>
					<div class="mui-pull-caption">下拉可以刷新</div>
				</div>
			</div>
			<div class="mui-content" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
				<div class="account-balance text-white">
					<span class="mui-block mui-text-right margin-bottom">冻结余额：0.00</span>
					<div class="balance-atten">
						<h2>${currency(member.balance, true, true)}</h2>
						<span>手机账户余额购买商品时只支持最大金额使用<br>请谨慎使用</span>
					</div>
				</div>
				<a href="${base}/wap/member/deposit/recharge.jhtml" class="balance-charge text-white mui-text-center mui-block hd-h3">我要充值</a>
				<ul class="border-top balance-lists list-col-10 mui-row mui-clearfix log-lists" style="display: none;">
					<li class="padding bg-white border-bottom clearfix"><span>收支明细</span></li>
				</ul>
				<ul class="order-lists margin-top border-top mui-clearfix">
					<li class="padding bg-white border-bottom"><span>收支明细</span></li>
					[#if pages?? && pages.list?has_content]
						[#list pages.list as depositLog]
						<li class="balance-list margin-left padding-tb mui-clearfix">
							<span class="mui-pull-left mui-col-xs-8">
								<span>${depositLog.typeNameValue}</span>
								<span class="text-gray">${depositLog.create_date?string("yyyy-MM-dd HH:mm:ss")}</span>
							</span>
							<span class="mui-pull-right mui-col-xs-4 padding-right text-pink hd-h2 text-ellipsis mui-text-right">${currency(depositLog.credit, true, true)}</span>
						</li>
						[/#list]
					[#else]
						<li class="user-list-none balance-none-tip mui-text-center">
							<img src="${base}/statics/images/bg_1.png">
							<p class="margin-top text-black hd-h5">您还没有相关的订单</p>
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
				<div class="mui-scrollbar-indicator" style="transition-duration: 0ms; display: block; height: 394px; transform: translate3d(0px, 296px, 0px) translateZ(0px);"></div>
			</div>
		</div>
		<footer class="footer posi">
		</footer>

		<div id="cli_dialog_div"></div>
	</body>

</html>