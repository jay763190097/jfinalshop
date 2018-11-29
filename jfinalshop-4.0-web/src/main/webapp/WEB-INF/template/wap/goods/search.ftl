[#include "/wap/include/header.ftl" /]
    <div class="hd-grid filter-items bg-white">
			<div class="hd-col-xs-e5">
				<a href="${base}/wap/goods/search.jhtml?keyword=${keyword}" class="filter-item current">全部</a>
			</div>
		</div>
	<!--下拉刷新容器-->
	<div id="refreshContainer" class="mui-content">
	  	<div class="mui-scroll">
	  		<div class="has-scorll-top"></div>
			<!--数据列表-->
	  		<ul class="margin-top custom-goods-items custom-goods-row custom-list-goods mui-clearfix">
	  			[#if goodsList?? && goodsList?has_content]
	  				[#list goodsList as goods]
		  				<li class="goods-item-list">
						<div class="list-item">
							<div class="list-item-pic">
								<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}"><img src="${setting.imageUrl}${goods.image!setting.defaultThumbnailProductImage}"></a>
							</div>
							<div class="list-item-bottom">
								<div class="list-item-title">
									<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}">
									[#if goods.caption?has_content]
										<span title="${goods.name}">${abbreviate(goods.name, 24)}</span>
										<em title="${goods.caption}">${abbreviate(goods.caption, 24)}</em>
									[#else]
										${abbreviate(goods.name, 48)}
									[/#if]
									</a>
								</div>
								<div class="list-item-text">
									<span class="price-org">${currency(goods.price, true)}</span>
								</div>
							</div>
						</div>
						</li>
	  				[/#list]
	  			[#else]
		  			<li class="user-list-none mui-text-center"><img src="${base}/statics/images/bg_6.png">
						<p class="text-black hd-h4">
							没有搜索到商品
						</p>
					</li>
	  			[/#if]
			</ul>
		</div>
	</div>
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
