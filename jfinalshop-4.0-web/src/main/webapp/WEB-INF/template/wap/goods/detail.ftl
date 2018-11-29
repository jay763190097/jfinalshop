[#include "/wap/include/header.ftl" /]
[#assign defaultProduct = goods.defaultProduct /]
<script type="text/javascript">
	mui.init();
    var goods = {
	    "sku_id" : "${defaultProduct.id}",
	    "number" : 1
	};
    
    var productData = {};
    [#if goods.hasSpecification()]
		[#list goods.products as product]
			productData["${product.specificationValueIds?join(",")}"] = {
				id: ${product.id},
				price: ${product.price},
				marketPrice: ${product.marketPrice},
				rewardPoint: ${product.rewardPoint},
				exchangePoint: ${product.exchangePoint},
				isOutOfStock: ${product.isOutOfStock?string("true", "false")}
			};
		[/#list]
	[/#if]
</script>
<script type="text/javascript" src="${base}/statics/js/goods_detail.js?v=2.6.0.161014"></script>
<script type="text/javascript" src="${base}/statics/js/jquery.lazyload.js?v=2.6.0.161014"></script>

<div id="detail-nav" class="mui-row bg-white padding-lr">
	<span class="mui-col-xs-4 active" data-scroll="0"><a href="javascript:;" data-id="detail">商品详情</a></span>
	<span class="mui-col-xs-4" data-scroll="0"><a href="javascript:;" data-id="comment">商品评价</a></span>
	<span class="mui-col-xs-4" data-scroll="0"><a href="javascript:;" data-id="consult">商品咨询</a></span>
</div>
<div class="hd-detail-content">
	<div id="hd_detail">
		<div class="hd-scroller">
			<div class="mui-slider bg-white">
			    <div class="mui-slider-group">
			    [#if goods.productImagesConverter?has_content]
					[#list goods.productImagesConverter as productImage]
				        <div class="mui-slider-item">
				            <a href="javascript:;"><img src="${base}${productImage.large}" class="lazy" data-original="${base}${productImage.large}" /></a>
				        </div>
					[/#list]
				[#else]
					<div class="mui-slider-item">
			            <a href="javascript:;"><img src="${base}${setting.defaultLargeProductImage}" class="lazy" data-original="${base}${setting.defaultLargeProductImage}" /></a>
			        </div>
				[/#if]
			    </div>
			    <div class="mui-slider-indicator">
		    	[#if goods.productImagesConverter?has_content]
  					[#list goods.productImagesConverter as productImage]
		        		<div class="mui-indicator [#if productImage.orders == 1] mui-active [/#if]"></div>
					[/#list]
				[/#if]
			    </div>
			    
			</div>

			<div class="basic-info list-col-10 margin-none mui-clearfix">
				<p class="pro-title text-black" data-skuid="${goods.id}">${goods.name} ${goods.caption}</p>
				<div class="pro-act act-link lh-18" style="color:#ff0000">
					商品仅作为演示效果，下单支付不予发货，特此说明。
				</div>
				<!-- <div class="pro-act act-link">
                    【商家自营】吃心不改，不做饿梦，调味品爽12，爆款直降，还有买一得二，更有满59元减10元！
                    <a href="" class="act-link text-pink">点击进入</a>
                </div> -->
				<div class="pro-price hd-h3">
					<span class="price-org"><em id="prom_price">${currency(defaultProduct.price, true)}</em></span>
					[#if goods.validPromotions?has_content]
						<span class="price-promotion">限时促销</span>
						<p class="price-origin">${currency(defaultProduct.marketPrice, true)}</p>
						<p class="count-down timer" data-time="">还剩<em class="text-org t-d">00</em>天<em class="text-org t-h">00</em>小时<em class="text-org t-m">00</em>分<em class="text-org t-s">00</em>秒结束</p>
					[/#if]
				</div>
			</div>
			[#if goods.validPromotions?has_content]
				<div class="border-bottom margin-top bg-white">
					[#list goods.validPromotions as promotion]
						<div class="padding hd-h4 border-top">
							<span class="mui-pull-left"><em class="text-org">促&nbsp;&nbsp;&nbsp;&nbsp;销</em>：</span>
							<span class="margin-large-left padding-large-left mui-block">${promotion.name}</span>
						</div>
					[/#list]
				</div>
			[/#if]
			
		</div>
		<div class="detail-tips">
			<span class="fixed" id="pull-text">继续拖动，查看详情</span>
			<div class="bd"></div>
		</div>
		<div class="hd-scroller" style="display: block; min-height: 580px;">
		<!-- <div class="hd-scroller" style="display: none;"> -->
			[#if goods.introduction?has_content]
				<div id="detail" class="mui-control-content goods-detail mui-active">
					<p style="text-align: center;"><br></p>
					<p style="text-align: center;">${base}${goods.introduction}</p>
					<p style="text-align: center;"><br></p>
				</div>
			[#else]
				<li class="user-list-none mui-text-center">
					<img src="${base}/statics/images/bg_3.png">
					<p class="margin-top text-black hd-h5">还没有添加商品详情</p>
				</li>
			[/#if]
			<div id="comment" class="mui-control-content goods-evaluate">
				<div class="top mui-clearfix">
					<span class="mui-pull-left">只有购买过该商品的用户才能评论</span>
					<!-- <span class="mui-btn mui-btn-primary mui-pull-right comment_btn" data-url="${base}/wap/member/review/add.jhtml?id=${goods.id}">我要评论</span> -->
					<a class="mui-btn mui-btn-primary mui-pull-right comment_btn" href="${base}/wap/member/review/list.jhtml?type=positive">我要评论</a>
				</div>
				[#if reviewPages?? && reviewPages.list?has_content]
					<ul class="comment-lists margin-none padding-lr list-col-10 mui-clearfix">
						[#list reviewPages.list as review]
							  [#if !review.forReviewId?has_content]
								<li data-id="">
									<div class="full">
										<span class="head"><img src="${base}/statics/img/default_head.png"/></span>
										<span class="mui-pull-left">${review.member.username}</span>
									</div>
									<div class="user-text">
										<p>${review.content}</p>
									</div>
									<div class="comment-imgs mui-clearfix drag-box">
										[#if review.imagesConverter?has_content]
								    		[#list review.imagesConverter as images]
								    			<span><img src="${base}${images}"/></span>
								    		[/#list]
								    	[/#if]
									</div>
								[/#if]
								[#if review.replyReviews?has_content]
									[#list review.replyReviews as replyReviews]
										<div class="admin-text">
											<p><span class="text-blue">商家回复：</span>${replyReviews.content}</p>
										</div>
										<p class="hd-h6 text-gray">${replyReviews.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
									[/#list]
								[/#if]
							</li>
						[/#list]
					</ul>
				[#else]					
					<ul class="comment-lists margin-none padding-lr list-col-10 mui-clearfix"></ul>
					<a class="top mui-block border-bottom mui-text-center text-black more" href="javascript:;" data-mark="comment">没有更多了</a>
				[/#if]
			</div>
			<div id="consult" class="mui-control-content goods-consult">
				<div class="top mui-clearfix">
					<span class="mui-pull-left">对商品有任何疑问可在线咨询</span>
					<a class="mui-btn mui-btn-primary mui-pull-right" href="${base}/wap/member/consultation/add.jhtml?id=${goods.id}">我要咨询</a>
				</div>
				[#if consultationPages?? && consultationPages.list?has_content]
					<ul class="comment-lists margin-none padding-lr list-col-10 mui-clearfix">
						[#list consultationPages.list as consultation]
							<li data-id="3">
								<div class="full"> <span class="head"><img src="${base}/statics/img/default_head.png"></span> <span>${consultation.member.username}</span> </div>
								<div class="user-text">
									<p>${consultation.content}</p>
								</div>
								<p class="hd-h6 text-gray">${consultation.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
							</li>
							[#if consultation.replyConsultations?has_content]
							 	<li data-id="replyConsultation.id">
									[#list consultation.replyConsultations as replyConsultation]
										<div class="admin-text">
											<p><span class="text-blue">商家回复：</span>${replyConsultation.content}</p>
										</div>
										<p class="hd-h6 text-gray">${replyConsultation.createDate?string("yyyy-MM-dd HH:mm:ss")}</p>
									[/#list]
								</li>
							[/#if]
						[/#list]
					</ul>
				[#else]
					<ul class="comment-lists margin-none padding-lr list-col-10 mui-clearfix"></ul>
					<a class="top mui-block border-bottom mui-text-center text-black more" href="javascript:;" data-mark="consult">没有更多了</a>
				[/#if]
			</div>
		</div>
	</div>
</div>

<nav class="mui-bar mui-bar-tab mui-row foot-bar">
	<div class="mui-col-xs-4 mui-row bar-icon">
		<a class="mui-col-xs-6 collect-btn [#if favorite] cancel_favorite [/#if]" href="javascript:;">
			<span class="mui-icon" [#if !favorite] data-skuid="${goods.id}" data-url="${base}/wap/goods/detail.jhtml?id=${goods.id}" data-price="${defaultProduct.price}"[/#if]><img src="${base}/statics/images/ico_35[#if favorite]a[/#if].png" /></span>
			<span class="mui-tab-label text-gray collect_text">[#if favorite]取消[/#if]收藏</span>
		</a>
		<a class="mui-col-xs-6" href="${base}/wap/cart/list.jhtml">
			<span class="mui-icon"><img src="${base}/statics/images/ico_13.png" /><em class="nums">0</em></span>
			<span class="mui-tab-label text-gray">购物车</span>
		</a>
	</div>
	<a id="join-cart" class="mui-col-xs-4 item btn mui-btn-primary">加入购物车</a>
	<a id="buy" class="mui-col-xs-4 item btn mui-btn-danger" data-event="buy_now" data-skuids="${goods.id}" href="javascript:;">立即购买</a>
</nav>
<div id="spec" class="hd-cover">
	<div class="body">
		[#if goods.hasSpecification()]
			[#assign defaultSpecificationValueIds = defaultProduct.specificationValueIds /]
			[#list goods.specificationItemsConverter as specificationItem]
				<dl class="goods-spec-item">
					<dt>${abbreviate(specificationItem.name, 8)}：</dt>
					<dd>
						[#list specificationItem.entries as entry]
							[#if entry.isSelected]
								<label data-id="${entry.id}" data-name="${entry.value}" data-value="${entry.value}" [#if defaultSpecificationValueIds[specificationItem_index] == entry.id] class="selected" [/#if]>${entry.value}</label>
							[/#if]
						[/#list]
					</dd>
				</dl>
			[/#list]
		[/#if]
		<div class="padding-left-15 padding-right-15 padding-big-bottom">
			<h2 class="border-top padding-tb">数量</h2>
			<div class="number mui-clearfix">
				<button class="num-btn num-decrease disabled">-</button>
				<input class="num-input" type="number" data-max="[#if defaultProduct.availableStock?has_content] ${defaultProduct.availableStock} [#else] 1 [/#if]" value="1" />
				<button class="num-btn num-increase">+</button>
			</div>
		</div>
	</div>
	<div class="summary">
		<div class="img">
			<img src="${base}${goods.image}" />
		</div>
		<div class="main">
			<span class="text-org mui-h4">${currency(defaultProduct.price, true)}</span>
			<div class="hd-h5 margin-small-top"><span class="stock">库存 <em>${defaultProduct.availableStock}</em>件</span></div>
		</div>
		<a class="close mui-icon mui-icon-plus"></a>
	</div>
	<div class="option">
		[#if !cartItem.isMarketable && !cartItem.isLowStock]
			<button class="mui-btn mui-btn-primary full hd-h4 radius-none">确定</button>
		[/#if]
		[#if cartItem.isMarketable]
			<button class="mui-btn mui-btn-primary full hd-h4 radius-none disabled">商品已下架</button>
		[/#if]
		[#if cartItem.isLowStock]
			<button class="mui-btn mui-btn-primary full hd-h4 radius-none disabled">商品已售罄</button>
		[/#if]
	</div>
</div>
<div class="cover-decision"></div>
<div class="comment-slider hide">
	<div class="mui-slider">
		<div class="mui-slider-group"></div>
	</div>
</div>
	<div id="cli_dialog_div"></div>
</body>
</html>
<script type="text/javascript" src="${base}/statics/js/detail.js"></script>
<script type="text/javascript">
	
	//倒计时插件，使用时间戳
	$(".timer").each(function(){
		var $this = $(this);
		window.setInterval(function(){
			$this.each(function(){
				var timer = $(this).data("time");//总时间
				if(timer<=0) return false;
				timer--;
				var d = parseInt(timer/3600/24);
			    var h = parseInt(timer/3600%24);
			    var m = parseInt(timer%3600/60);
			    var s = timer%60;
			    $(this).children(".t-d").text((d>9?d:"0"+d));
			    $(this).children(".t-h").text((h>9?h:"0"+h));
			    $(this).children(".t-m").text((m>9?m:"0"+m));
			    $(this).children(".t-s").text((s>9?s:"0"+s));
				$(this).data("time",timer)
			});
		},1000);
	});
	//加入收藏
	mui(".foot-bar").on('tap', '.collect-btn', function() {
		var $_this = $(this);
		var _this = $(this).find('.mui-icon');
		var url = "${base}/wap/member/favorite/delete.jhtml";
		var sku_id = "${goods.id}";
		if($_this.hasClass('cancel_favorite')) {
			$.post(url, {
				sku_id: sku_id
			}, function(ret) {
				if(ret.status == 1) {
					$('.collect-btn img').attr('src', '${base}/statics/images/ico_35.png');
					$('.collect_text').text('收藏');
					$_this.removeClass('cancel_favorite');
				} else {
					return false;
				}
			}, 'json');
		} else {
			var url = "${base}/wap/member/favorite/add.jhtml";
			var sku_price = _this.attr('data-price');
			var url_forward = _this.data('url');
			$.post(url, {
				sku_id: sku_id,
				sku_price: sku_price,
				url_forward: url_forward
			}, function(data) {
				if(data.status == 1) {
					$('.collect-btn img').attr('src', '${base}/statics/images/ico_35a.png');
					$('.collect_text').text('取消收藏');
					$_this.addClass('cancel_favorite');
				} else {
					$.tips({
						content: data.message,
						callback: function() {
							if(data.message == '请登录后操作') {
								window.location.href = data.referer;
							}
						}
					});
				}
			}, 'json');
		}
	});
	
	$(window).load(function(){
		hdTouch.init({
			outer: document.getElementById("hd_detail"),
			pull: document.getElementById("pull-text"),
			toper: document.getElementsByClassName("header")[0].clientHeight,
			footer: document.getElementsByClassName("foot-bar")[0].clientHeight,
			pullHeight: document.getElementsByClassName("detail-tips")[0].clientHeight
		})
	})
	$(function(){
		//商品详情页面加载完后加载图片
		$(".de-lazy").each(function(){
			var $this = $(this);
			$this.attr("src", $this.data("original"));
		});
	
		var cart_nums = 0;
		goods_detail.init();
	
		$('.mui-control-item').on('tap','a',function(){
			var obj = $(this).data('id');
			$(".goods-intro .mui-control-content").removeClass("mui-active");
			$("#"+obj).addClass("mui-active");
		})
	
		mui('.option').on('tap','.mui-btn',function(){
			if(!$(this).hasClass('disabled')){
				var url = window.location.href;
				var strCart = new RegExp('#cart'),
					strBuy = new RegExp('#buy');
				if(strCart.test(url)){
					goods_detail.cart_add();
				}else if(strBuy.test(url)){
					goods_detail.buy_now($('#buy'));
				}
			}
		});
	
		mui("#detail-nav").on('tap','span',function(e){
			var $cid = $(this).children("a").data("id");
			$(this).addClass("active").siblings().removeClass("active");
			$("#hd_detail .mui-control-content").removeClass("mui-active");
			$('#'+$cid).addClass("mui-active");
			hdTouch.resetHeight($('#'+$cid).height() + hdTouch.opts.footer + hdTouch.opts.pullHeight);
		});
	
		mui(".mui-control-content").on('tap','.more',function(){
			var mark = $(this).data("mark");
			//loadLists(mark);
		})
	
	});
	
	$(".number").numberSet();
	
	mui("#comment").on('tap','.comment-imgs span',function(){
		var imgs =  $(this).parent(".comment-imgs").find("img");
		var lists = '';
		var index = $(this).index();
		$.each(imgs, function() {
			lists += '<div class="mui-slider-item"><img src="'+$(this).attr("src")+'" /></div>'
		});
		$(".comment-slider").removeClass("hide").find(".mui-slider-group").html(lists);
		mui('.comment-slider').slider().gotoItem(index);
	});
	
	mui(".comment-slider").on('tap','.mui-slider-item',function(){
		$(".comment-slider").addClass("hide");
	});

</script>