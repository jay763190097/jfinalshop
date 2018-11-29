<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

		<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
		<title>JFinalShop演示站 - 企业级电子商务系统软件</title>
		<meta name="description" content="">
		<meta name="keywords" content="">
		<link rel="stylesheet" type="text/css" href="${base}/statics/css/mui.min.css">
		<link rel="stylesheet" type="text/css" href="${base}/statics/css/jfinalshop.css">
		<link rel="stylesheet" type="text/css" href="${base}/statics/css/jfinalshop.mobile.css">
		<script type="text/javascript" src="${base}/statics/js/mui.min.js"></script>
		<style type="text/css" adt="123"></style>
		<script type="text/javascript" src="${base}/statics/js/jquery-2.1.1.min.js"></script>
		<script type="text/javascript" src="${base}/statics/js/jfinalshop.js"></script>
		<script type="text/javascript" src="${base}/statics/js/jfinalshop.slider.js"></script>
		<script type="text/javascript" src="${base}/statics/js/jquery.lazyload.js"></script>
		<script type="text/javascript" charset="utf-8">
			mui.init();
		</script>
	</head>

	<body class="mui-ios mui-ios-9 mui-ios-9-1">
		<style>
			body {
				background-color: #f9f9f9;
			}
		</style>
		<header class="mui-bar mui-bar-nav header" style="background-color: #0068b7">
			<div class="logo mui-pull-left">
				<a href="${base}/wap.jhtml"><img src="${base}/statics/img/logo.png" height="30"></a>
			</div>
			<h1 class="mui-title">JFinalShop演示站</h1>
			<a href="${base}/wap/product_category.jhtml" class="hd-menu"><span class="mui-icon mui-icon-more"></span></a>
		</header>
		<div class="mui-content">

			<div class="mui-slider">
				<div class="mui-slider-group" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
				[#list adPosition.ads as ad]
					[#if ad.typeName == "image" && ad.hasBegun() && !ad.hasEnded()]
						[#if ad.url??]
							<a class="mui-slider-item" href="${ad.url}"><img src="${ad.path}"></a>
						[/#if]
					[/#if]
				[/#list]
				</div>
				<div class="mui-slider-indicator">
					<div class="mui-indicator mui-active"></div>
					<div class="mui-indicator"></div>
					<div class="mui-indicator"></div>
				</div>
			</div>
			<!-- <a class="custom-notice custom-notice-jd" href="/index.php">
				<div class="notice-img"><img src="${base}/statics/img/57ede501f32fd.gif"></div>
				<div class="notice-text mui-ellipsis" style="color: #0c0303;">jfinalshop</div>
			</a> -->
			<div class="hd-search">
				<form name="form_search" action="/wap/goods/search.jhtml" method="post">
					<input type="search" placeholder="搜索商品名称" name="keyword">
				</form>
			</div>
			<nav class="quick-entry-nav hd-grid">
				<a href="${base}/wap/product_category.jhtml" class="hd-col-xs-e4 quick-entry-link"><span class="nav-img"><img src="${base}/statics/img/57edea6a61c06.png"></span><span class="title">全部分类</span></a>
				<a href="${base}/wap/member/order/list.jhtml" class="hd-col-xs-e4 quick-entry-link"><span class="nav-img"><img src="${base}/statics/img/57edea224d7c6.png"></span><span class="title">我的订单</span></a>
				<a href="${base}/wap/member/favorite/list.jhtml?period=day" class="hd-col-xs-e4 quick-entry-link"><span class="nav-img"><img src="${base}/statics/img/57edea3497a25.png"></span><span class="title">我的收藏</span></a>
				<a href="${base}/wap/member.jhtml" class="hd-col-xs-e4 quick-entry-link"><span class="nav-img"><img src="${base}/statics/img/57edea3e91326.png"></span><span class="title">会员中心</span></a>
			</nav>
			<ul class="custom-goods-items mui-clearfix">
				[#list goodsList as goods]
				<li class="goods-item-list">
					<a class="list-item" href="${base}/wap/goods/detail.jhtml?id=${goods.id}">
						<div class="list-item-pic">
							<div class="square-item"><img class="lazy" src="${base}/upload/image/blank.gif" data-original="${base}${goods.image!setting.defaultThumbnailProductImage}" style="display: block;"></div>
						</div>
						<div class="list-item-bottom">
							<div class="list-item-title">
								<span>
									[#if goods.caption?has_content]
										<span title="${goods.name}">${abbreviate(goods.name, 24)}</span>
										<em title="${goods.caption}">${abbreviate(goods.caption, 24)}</em>
									[#else]
										${abbreviate(goods.name, 48)}
									[/#if]
								</span>
							</div>
							<div class="list-item-text"><span class="price-org">${currency(goods.price, true)}</span></div>
						</div>
					</a>
				</li>
				[/#list]
			</ul>
		</div>
		<footer class="footer">
			<div class="text-gray mui-text-center copy-text"></div>
		</footer>
		<script>
			$("[name=form_search]").submit(function() {
				if($("[type=search]").val() == "") {
					return false;
				}
			});
		</script>
		[#include "/wap/include/footer.ftl" /]
		<script type="text/javascript">
			mui(".nav-menu").on("tap", ".nav-item", function() {
				if($(this).hasClass("current")) {
					$(this).removeClass("current");
				} else {
					var tw = $(this).outerWidth(true);
					var ch = $(this).children(".submenu");
					var cw = ch.outerWidth(true);
					ch.css({
						left: (tw - cw) / 2 + "px"
					});
					$(".nav-menu .nav-item").removeClass("current");
					$(this).addClass("current");
				}
			});
		</script>
		<div class="nav-menu-mask"></div>
		<script>
			if($(".mui-content.mui-scroll-wrapper").length > 0) {
				$(".mui-content.mui-scroll-wrapper").css({
					bottom: "1.05rem"
				})
			}
			
		</script>
		<div id="cli_dialog_div"></div>
	</body>

</html>