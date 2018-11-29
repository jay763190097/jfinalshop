[#include "/wap/include/header.ftl" /]
	<div id="refreshContainer" class="mui-content">
		<div class="mui-pull-top-pocket">
			<div class="mui-pull">
				<div class="mui-pull-loading mui-icon mui-icon-pulldown"></div>
				<div class="mui-pull-caption">下拉可以刷新</div>
			</div>
		</div>
		<div class="mui-scroll" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
			<ul class="layout-list-common comment-form margin-none bg-none consult-lists">
			[#if pages?? && pages.list?has_content]
				[#list pages.list as consultation]
					<li class="mui-table-view-cell margin-bottom border-top bg-white">
						 <a href="${base}/wap/goods/detail.jhtml?id=${consultation.goods.id}" class="mui-navigate-right">
							 <span class="mui-pull-left margin-right"><img src="${consultation.goods.image!setting.defaultThumbnailProductImage}"></span>
							 <div class="title margin-small-bottom">
								 <span>
									 [#if consultation.goods.caption?has_content]
										<span title="${goods.name}">${abbreviate(consultation.goods.name, 24)}</span>
										<em title="${goods.caption}">${abbreviate(consultation.goods.caption, 24)}</em>
									 [#else]
										${abbreviate(consultation.goods.name, 48)}
									 [/#if]
								 </span>
							 </div>
							<!--  <span class="text-ellipsis text-gray">规格：[]</span> -->
						 </a>
						 <div class="margin-top padding-small-top border-top">
							 <div class="consult-info-list">
								 <span class="mui-pull-left">咨询时间：</span>
								 <p class="text-drak">${consultation.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
							 </div>
							 <div class="consult-info-list">
								 <span class="mui-pull-left">咨询内容：</span>
								 <p class="text-blue">${consultation.content}</p>
							 </div>
							 [#if consultation.replyConsultations?has_content]
							 	<div class="consult-info-list">
							 		<span class="mui-pull-left">卖家回复：</span>
							 		[#list consultation.replyConsultations as replyConsultation]
							 			<p class="text-red">${replyConsultation.content}</p>
							 		[/#list]
							 	</div>
							 [/#if]
						 </div>
					</li>
				[/#list]
			[#else]
				<li class="user-list-none order-lh-40 mui-text-center">
					<img src="${base}/statics/images/bg_4.png" />
					<p class="text-black hd-h4">您还没有咨询记录</p>
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
	</div>
	[#include "/wap/include/footer.ftl" /]
	<footer class="footer posi">
	</footer>
	<div id="cli_dialog_div"></div>
</body>

</html>