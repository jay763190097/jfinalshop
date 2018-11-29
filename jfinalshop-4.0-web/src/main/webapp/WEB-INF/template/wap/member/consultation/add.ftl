[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
		<ul class="custom-goods-items custom-goods-row custom-list-goods border-top mui-clearfix">
			<li class="goods-item-list">
				<div class="list-item mui-clearfix">
					<div class="list-item-pic">
						<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}"><img src="${goods.image!setting.defaultThumbnailProductImage}"></a>
					</div>
					<div class="list-item-bottom">
						<div class="list-item-title">
							<span>
								[#if goods.caption?has_content]
									<span title="${goods.name}">${abbreviate(goods.name, 24)}</span>
									<em title="${goods.caption}">${abbreviate(goods.caption, 24)}</em>
								[#else]
									<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}">${abbreviate(goods.name, 48)}</a>
								[/#if]
							</span>
							
						</div>
					</div>
				</div>
			</li>
		</ul>
		<div class="margin-top padding-15 bg-white">
			<textarea name="question" class="hd-h5 margin-none" id="question" placeholder="请输入咨询内容……"></textarea>
		</div>
		<div class="padding-15">
			<span class="mui-btn mui-btn-primary full hd-h4">发表咨询</span>
		</div>
	</div>
	<footer class="footer posi">
		<div class="copyright"></div>
	</footer>

	<script type="text/javascript">
		$('.mui-btn-primary').bind('click', function() {
			var url = '/wap/member/consultation/save.jhtml';
			var question = $("textarea[name=question]").val();
			$.post(url, {
				'id': ${goods.id},
				'question': question
			}, function(ret) {
				if(ret.status == 1) {
					$.tips({
						content: '咨询成功',
						callback: function() {
							window.location.href = ret.referer;
						}
					});
					return false;
				}
			}, 'json');
		})
	</script>
	<div id="cli_dialog_div"></div>
</body>

</html>