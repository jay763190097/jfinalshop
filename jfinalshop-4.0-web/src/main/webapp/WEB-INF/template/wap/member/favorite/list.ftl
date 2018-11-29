[#include "/wap/include/header.ftl" /]
		<div class="hd-grid filter-items bg-white">
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/favorite/list.jhtml?period=day" class="filter-item [#if closing == 'day'] current[/#if]">今天</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/favorite/list.jhtml?period=week" class="filter-item [#if closing == 'week'] current[/#if]">本周</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/favorite/list.jhtml?period=month" class="filter-item [#if closing == 'month'] current[/#if]">本月</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/favorite/list.jhtml?period=year" class="filter-item [#if closing == 'year'] current[/#if]">本年</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/member/favorite/list.jhtml?period=lastyear" class="filter-item [#if closing == 'lastyear'] current[/#if]">历史</a>
			</div>
		</div>
		<div id="refreshContainer" class="mui-content">
			<div class="mui-pull-top-pocket">
				<div class="mui-pull">
					<div class="mui-pull-loading mui-icon mui-icon-pulldown"></div>
					<div class="mui-pull-caption">下拉可以刷新</div>
				</div>
			</div>
			<div class="mui-content" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
				<div class="has-scorll-top"></div>
				<ul class="order-lists user-lists mui-clearfix favorite-lists">
				[#if page?? && page.list?has_content]
					[#list page.list as goods]
						<li class="order-list list-col-10">
							<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}" class="order-collect padding-tb"><img src="${goods.image}">
								<p>${goods.name} </p><span class="text-org hd-h4">￥${currency(goods.price, true)}</span></a>
							<div class="order-hand padding-tb border-top mui-clearfix">
								<a href="" class="mui-btn hd-btn-blue mui-pull-right favorite-cancel" data-id="${goods.id}">取消收藏</a>
							</div>
						</li>
					[/#list]
				[#else]
					<li class="user-list-none order-lh-40 mui-text-center">
						<img src="${base}/statics/images/bg_4.png"><p class="text-black hd-h4">您还没有收藏记录</p>
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
		</footer>
		<script>
			$('body').on('tap', '.favorite-cancel', function() {
				var sku_id = $(this).data('id');
				var url = '${base}/wap/member/favorite/delete.jhtml';
				$.confirms("是否取消收藏", function() {
					$.post(url, {
						sku_id: sku_id
					}, function(ret) {
						if(ret.status == 1) {
							$.tips({
								content: ret.message
							});
							window.location.href = '${base}/wap/member/favorite/list.jhtml?period=lastyear';
						} else {
							$.tips({
								content: ret.message
							});
						}
					}, 'json');
				});
			});
			mui('.order-list').on('tap', '.order-collect', function() {
				window.location.href = $(this).data('href');
			});
		</script>

		<div id="cli_dialog_div"></div>
	</body>

</html>