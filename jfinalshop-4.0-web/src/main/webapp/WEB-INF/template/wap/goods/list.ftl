[#include "/wap/include/header.ftl" /]
	<div class="hd-grid filter-items bg-white">
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/goods/list.jhtml?productCategoryId=${productCategory.id}" class="filter-item [#if orderType == 'all'] current [/#if]">综合</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/goods/list.jhtml?productCategoryId=${productCategory.id}&orderType=salesDesc" class="filter-item [#if orderType == 'salesDesc'] current [/#if]">销量</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/goods/list.jhtml?productCategoryId=${productCategory.id}&orderType=priceAsc" class="filter-item [#if orderType == 'priceAsc'] current [/#if]">价格</a>
			</div>
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/goods/list.jhtml?productCategoryId=${productCategory.id}&orderType=topDesc" class="filter-item [#if orderType == 'topDesc'] current [/#if]">人气</a>
			</div>
	</div>
	<!--下拉刷新容器-->
	<div id="refreshContainer" class="mui-content">
	  	<div class="mui-scroll">
	  		<div class="has-scorll-top"></div>
			<!--数据列表-->
	  		<ul id="goods-lists" class="margin-top custom-goods-items  mui-clearfix ">
	  		[#if pages?? && pages.list?has_content]
	  			[#list pages.list as goods]
	  				[#assign defaultProduct = goods.defaultProduct /]
		  			<li class="goods-item-list">
						<a class="list-item" href="${base}/wap/goods/detail.jhtml?id=${goods.id}">
							<div class="list-item-pic">
								<div class="square-item"><img src="${goods.thumbnail!setting.defaultThumbnailProductImage}" class="lazy" data-original="${goods.thumbnail!setting.defaultThumbnailProductImage}" style="display: block;"></div>
							</div>
							<div class="list-item-bottom">
								[#if goods.caption?has_content]
									<div class="list-item-title">${abbreviate(goods.name, 24)} <br><font style="color: #ff7700;">${abbreviate(goods.caption, 24)}</font></div>
								[#else]
									<div class="list-item-title">${abbreviate(goods.name, 48)}</div>
								[/#if]
								<div class="list-item-text"><span class="price-org">${currency(defaultProduct.price, true)}</span></div>
							</div>
						</a>
					</li>
				[/#list]
			[/#if]
			</ul>
		</div>
	</div>
[#include "/wap/include/footer.ftl" /]
<script>
	$(function(){
		mui(".filter-items").on('tap','.filter-more',function(){
			if($(".filter-wrap").hasClass("open")){
				$(".filter-wrap").hide(0,function(){
					$(".filter-wrap").removeClass("open");
				});
			}else{
				$(".filter-wrap").show(0,function(){
					$(".filter-wrap").addClass("open");
				});
			}
		});
	})
	
</script>
</body>
</html>
