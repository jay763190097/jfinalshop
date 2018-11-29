[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
<link type="text/css" rel="stylesheet" href="${base}/statics/js/upload/uploader.css?v=2.6.0.161014" />
<script type="text/javascript" src="${base}/statics/js/upload/uploader.js?v=2.6.0.161014"></script>
	<div class="mui-content">
	<form action="${base}/wap/member/review/save.jhtml" method="post">
	    <ul class="mui-table-view layout-list-common comment-form margin-none">
	    	<li class="mui-table-view-cell">
	    		<a href="${base}/wap/goods/detail.jhtml?id=${goods.id}" class="mui-navigate-right">
	    			<span class="mui-pull-left margin-right"><img src="${goods.image!setting.defaultThumbnailProductImage}"></span>
	    			<div class="title margin-small-bottom">
	    				<span>
							[#if goods.caption?has_content]
								<span title="${goods.name}">${abbreviate(goods.name, 24)}</span>
								<em title="${goods.caption}">${abbreviate(goods.caption, 24)}</em>
							[#else]
								${abbreviate(goods.name, 48)}
							[/#if]
						</span>
	    			</div>
	    			<!-- <span class="text-ellipsis text-gray">{$goods[_sku_spec]}</span> -->
	    		</a>
	    		<div class="margin-top padding-top border-top">
	    			<span class="mui-btn margin-small-right hd-btn-blue" data-score="0">好&nbsp;&nbsp;&nbsp;评</span>
	    			<span class="mui-btn margin-small-right hd-btn-gray" data-score="1">中&nbsp;&nbsp;&nbsp;评</span>
	    			<span class="mui-btn margin-small-right hd-btn-gray" data-score="2">差&nbsp;&nbsp;&nbsp;评</span>
	    		</div>
	    		<input type="hidden" name="score" value="0">
	    	</li>
	    	<li>
	    		<textarea class="border-none margin-none" name="content" placeholder="发表您的商品评价，与给多人一同分享"></textarea>
	    	</li>
	    </ul>
	   <div class="list-col-10 padding-lr">
	    	<div class="padding-tb border-bottom">
	    		<span class="icon-15 mui-pull-left margin-right"><img src="${base}/statics/images/ico_1.png" /></span>
	    		<span>上传图片完成晒单，最多可以上传5张照片</span>
	    	</div>
	    	<ul class="comment-upload-list padding-top-15 padding-small-bottom mui-clearfix">
	    		<li><div class="upload"> </div></li>
	    	</ul>
	    </div>
	    <div class="padding">
	    	<input type="hidden" name="id" value="${orderItem.id}">
	    	<input type="submit" class="mui-btn full mui-btn-primary" value="发表评论"/>
	    </div>
	</form>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
<script type="text/javascript">
	$('.mui-btn-primary').bind('click',function(){
		var ajax_return = $("form").Validform({
			showAllError:true,
			ajaxPost:true,
			callback:function(ret) {
				if(ret.status == 0) {
					$.tips({
						content:ret.message,
						callback:function() {
							return false;
						}
					});
				} else {
					$.tips({
						content:ret.message,
						callback:function() {
							window.location.href = "${base}/wap/member/review/list.jhtml";
						}
					});					
				}
			}
		})
	})
	window.onload = function(){
		
		var uploader = WebUploader.create({
	        auto:true,
	        fileNumLimit:5,
	        fileVal:'upfile',
	        // swf文件路径
	        swf: '${base}/statics/js/upload/uploader.swf',
	        // 文件接收服务端。
	        server: "/wap/member/review/upload.jhtml",
	        // 选择文件的按钮。可选
	        formData:{
	            file : 'upfile',
	            //upload_init : ''
	        },
	        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
	        pick: {
	            id: '.upload',
	            multiple:true
	        },
	        // 压缩图片大小
	        compress:false,
	        accept:{
	            title: '图片文件',
	            extensions: 'gif,jpg,jpeg,bmp,png',
	            mimeTypes: 'image/*'
	        },
	        chunked: false,
	        chunkSize:1000000,
	        resize: false
	    })
	
	    uploader.onUploadSuccess = function(file, response) {
	    	if(response.status == 1) {
	    		$('.upload').parents('.comment-upload-list').prepend('<li><img src="' + response.url + '" /><input type="hidden" name="images" value="' + response.url + '"/><span class="remove">×</span></li>');
	    	} else {
	    		$.tips({content: response.message})
	    		return false;
	    	}
	    }
	    $('.margin-small-right').bind('click',function(){
	    	$this = $(this);
	    	if($this.hasClass('hd-btn-gray')){
	    		$this.addClass('hd-btn-blue').removeClass('hd-btn-gray').siblings().removeClass('hd-btn-blue').addClass('hd-btn-gray');
	    		$('input[name=score]').val($this.attr('data-score'));
	    	}
	    });
	    
	    mui(".comment-upload-list").on('tap','.remove',function(){
	    	var li = $(this).parents("li");
	    	$.confirms("是否确认删除？",function(){
	    		li.fadeOut(300,function(){
		    		li.remove();
		    	});
	    	});
	    });
		
	}
    
</script>
