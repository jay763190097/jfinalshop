[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
<link type="text/css" rel="stylesheet" href="${base}/statics/js/upload/uploader.css?v=2.6.0.161014" />
<script type="text/javascript" src="${basae}/statics/js/upload/uploader.js?v=2.6.0.161014"></script>
	<div class="mui-content">
		<ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="mui-table-view-cell">
	    		<a href="${base}/wap/goods/detail.jhtml?id=${review.goods.id}" class="mui-navigate-right">
	    			<span class="mui-pull-left margin-right"><img src="${review.goods.image!setting.defaultThumbnailProductImage}"></span>
	    			<div class="title margin-small-bottom">
	    				<span>
	    					[#if review.goods.caption?has_content]
								<span title="${goods.name}">${abbreviate(review.goods.name, 24)}</span>
								<em title="${goods.caption}">${abbreviate(review.goods.caption, 24)}</em>
							[#else]
								${abbreviate(review.goods.name, 48)}
							[/#if]
	    				</span>
	    			</div>
	    			<span class="text-ellipsis text-gray"></span>
	    		</a>
	    		<div class="margin-top padding-top border-top">
	    		[#if review.score == 0]
	    			<span class="mui-btn margin-small-right hd-btn-blue">好&nbsp;&nbsp;&nbsp;评</span>
	    		[#elseif review.score == 1]
	    			<span class="mui-btn margin-small-right hd-btn-blue">中&nbsp;&nbsp;&nbsp;评</span>
	    		[#elseif review.score == 2]
	    			<span class="mui-btn margin-small-right hd-btn-blue">差&nbsp;&nbsp;&nbsp;评</span>
	    		</div>
	    		[/#if]
	    		<input type="hidden" name="score" value="">
	    	</li>
	    	<li>
	    		<textarea class="border-none margin-none" disabled="disabled" readonly="readonly">${review.content}</textarea>
	    	</li>
	    </ul>
	    <div class="list-col-10 padding-lr">
	    	<ul class="comment-upload-list padding-top mui-clearfix">
	    	[#if review.imagesConverter?has_content]
	    		[#list review.imagesConverter as images]
	    			<li><img src="${images}" /></li>
	    		[/#list]
	    	[/#if]
	    	</ul>
	    </div>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
	<div class="comment-slider hide">
		<div class="mui-slider">
		    <div class="mui-slider-group"></div>
		</div>
	</div>
	<script>
		mui(".comment-upload-list").on('tap','li',function(){
			var imgs =  $(this).parent(".comment-upload-list").find("img");
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
