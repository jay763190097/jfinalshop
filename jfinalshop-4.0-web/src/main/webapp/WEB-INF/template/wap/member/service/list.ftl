[#include "/wap/include/header.ftl" /]
	<div class="filter-items bg-white full mui-text-left mui-clearfix">
		<div class="inline padding-lr-15">
			<a href="${base}/wap/member/service/list.jhtml?status=-1" class="filter-item [#if status == -1] current[/#if]">处理中</a>
		</div>
		<div class="inline padding-lr-15">
			<a href="${base}/wap/member/service/list.jhtml?status=5" class="filter-item [#if status == 5] current[/#if]">已完成</a>
		</div>
	</div>
	<div id="refreshContainer" class="mui-content">
	<div class="mui-content">
	   	<div class="has-scorll-top"></div>
		<ul class="margin-top custom-goods-items custom-goods-row custom-list-goods border-top mui-clearfix">
			[#if returnsItems?? && returnsItems?has_content]
				[#list returnsItems as returnsItem]
					<li class="goods-item-list padding-none">
						 <div class="list-item">
						 	<div class="list-item-pic">
						 		<a href="${base}/wap/goods/detail.jhtml?id=${returnsItem.product.goods.id}"><img src="${returnsItem.product.thumbnail!setting.defaultThumbnailProductImage}" /></a>
						 	</div>
							 <div class="list-item-bottom">
							 	<div class="list-item-title">
							 		<a href="">${returnsItem.name}</a>
							 	</div>
							 	<div class="list-item-text text-ellipsis comment-list-text">
							 		<span class="text-gray hd-h6">${returnsItem.specifications}</span>
									<a href="${base}/wap/member/service/view_detail.jhtml?id=${returnsItem.id}" class="mui-btn hd-btn-blue mui-pull-right">售后详情</a>
							 	</div>
							 </div>
						 </div>
					</li>
				[/#list]
			[#else]
				<li class="user-list-none mui-text-center">
					<img src="${base}/statics/images/bg_7.png" />
					<p class="text-black hd-h4">[#if status == 5]没有已完成的订单[#else]没有处理中的订单[/#if]</p>
				</li>
			[/#if]
		</ul>
	</div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
</body>
</html>
