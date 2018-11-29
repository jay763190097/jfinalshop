[#include "/wap/include/header.ftl" /]
	<div class="filter-items full bg-white border-bottom mui-text-left mui-clearfix">
		<div class="inline padding-lr-15">
			<a href="${base}/wap/member/review/list.jhtml?isReview=false" class="filter-item [#if isReview == false]current[/#if]">待评价</a>
		</div>
		<div class="inline padding-lr-15">
			<a href="${base}/wap/member/review/list.jhtml?isReview=true" class="filter-item [#if isReview == true]current[/#if]">已评价</a>
		</div>
	</div>
	<div id="refreshContainer" class="mui-content">
		<div class="mui-content">
		   	<div class="has-scorll-top"></div>
		   	[#if orderItems?? && orderItems.list?has_content]
				[#list orderItems.list as orderItem]
					<ul class="margin-top custom-goods-items custom-goods-row custom-list-goods border-top mui-clearfix">
						<li class="goods-item-list padding-none">
							<div class="list-item">
							 	<div class="list-item-pic">
							 		<a href="${base}/wap/goods/detail.jhtml?id=${orderItem.product.goods.id}"><img src="${orderItem.product.thumbnail!setting.defaultThumbnailProductImage}" /></a>
							 	</div>
								<div class="list-item-bottom">
								 	<div class="list-item-title">
								 		<a href="${base}/wap/goods/detail.jhtml?id=${orderItem.product.goods.id}">
										[#if orderItem.name?has_content]
											<span title="${orderItem.name}">${abbreviate(orderItem.name, 24)}</span>
										[#else]
											${abbreviate(orderItem.name, 48)}
										[/#if]
										</a>
								 	</div>
									<div class="list-item-text text-ellipsis comment-list-text">
									 	<span class="text-gray hd-h6 inline">${orderItem.product.specifications?join(", ")}</span>
    									[#if orderItem.isReview]
				    						<a href="${base}/wap/member/review/view.jhtml?id=${orderItem.reviewId}" class="mui-btn hd-btn-blue mui-pull-right">查看评价</a>
				    					[#else]
				    						<a href="${base}/wap/member/review/add.jhtml?id=${orderItem.id}" class="mui-btn hd-btn-blue mui-pull-right">评价晒单</a>
				    					[/#if]
									</div>
								</div>
							</div>
						 </li>
					</ul>
				[/#list]
			[#else]
				<li class="user-list-none order-lh-40 mui-text-center">
					<img src="${base}/statics/images/bg_4.png" />
					<p class="text-black hd-h4">暂无内容！</p>
				</li>
			[/#if]	
		</div>
	</div>
	<div class="mui-pull-bottom-pocket">
		<div class="mui-pull">
			<div class="mui-pull-loading mui-icon mui-spinner"></div>
			<div class="mui-pull-caption">上拉显示更多</div>
		</div>
	</div>
	[#include "/wap/include/footer.ftl" /]
	<footer class="footer posi">
	</footer>
	
</body>
