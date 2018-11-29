[#include "/wap/include/header.ftl" /]
		<div class="mui-content">
			<div class="filter-items full border-bottom bg-white mui-clearfix">
				<div class="mui-pull-left padding-big-left padding-big-right">
					<a href="${base}/wap/member/message/list.jhtml" class="filter-item [#if read == 'all'] current[/#if]">全部</a>
				</div>
				<div class="mui-pull-left padding-big-left padding-big-right">
					<a href="${base}/wap/member/message/list.jhtml?read=true" class="filter-item [#if read == true] current[/#if]">已读</a>
				</div>
				<div class="mui-pull-left padding-big-left padding-big-right">
					<a href="${base}/wap/member/message/list.jhtml?read=false" class="filter-item [#if read == false] current[/#if]">未读</a>
				</div>
			</div>
			<div class="has-scorll-top"></div>
			<ul class="mui-table-view layout-list-common list-col-10 mui-clearfix">
			[#if pages?? && pages.list?has_content]
				[#list pages.list as message]
					<li class="mui-table-view-cell" data-id="${message.id}">
						<a href="#" class="mui-navigate-right hd-h5">
							<p class="text-black text-ellipsis">时间：${message.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
							<p class="text-black">标题：<span class="text-blue">${message.title}</span></p>
						</a>
						<div class="message-detail">
							${message.content}
						</div>
					</li>
				[/#list]
			[#else]
				<div class="user-list-none order-lh-40 mui-text-center">
					<img src="${base}/statics/images/bg_3.png">
					<p class="text-black hd-h4">您还没有消息记录</p>
				</div>
			[/#if]
			</ul>
		</div>
		<footer class="footer posi">
		</footer>

		<script>
			$(".mui-table-view-cell").on('tap', function() {
				$(this).find('.message-detail').toggle(250);
				var _this = $(this);
				if(_this.find("p:last span").attr('class')) {
					var url = '${base}/wap/member/message/read.jhtml',
					ids = $(this).data('id');
					$.post(url, {
						id: ids
					}, function(ret) {
						if(ret.status == 1)
							_this.find("p:last span").removeClass('text-blue');
					}, 'json');
				}
			});
		</script>
		<div id="cli_dialog_div"></div>
	</body>

</html>