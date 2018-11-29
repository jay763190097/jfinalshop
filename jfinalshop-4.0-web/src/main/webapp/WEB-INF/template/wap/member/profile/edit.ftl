[#include "/wap/include/header.ftl" /]
		<div class="mui-content">
			<ul class="mui-table-view layout-list-common user-info-detail margin-none">
				<li class="mui-table-view-cell webuploader-container" id="file-avatar">
					<div class="webuploader-pick">
						<a class="mui-navigate-right user-head" href="#">
							<span class="hd-h4 user-info-h4">头像</span>
							<span class="mui-pull-right margin-right">
							[#if member.avatar?? && member.avatar?has_content]
								<img class="full" src="${base}${member.avatar}">
							[#else]
								<img class="full" src="${base}/statics/img/default_head.png">
							[/#if]
							</span>
						</a>
					</div>
					<div id="rt_rt_1avg0sdi31ldir2t1tim3f91lme1" style="position: absolute; top: 12.5px; left: 12.5px; width: 389px; height: 62px; overflow: hidden; bottom: auto; right: auto;"><input type="file" name="file" class="webuploader-element-invisible" accept=""><label style="opacity: 0; width: 100%; height: 100%; display: block; cursor: pointer; background: rgb(255, 255, 255);"></label></div>
				</li>
				<li class="mui-table-view-cell">
					<a href="${base}/wap/member/mobile/edit.jhtml" class="mui-navigate-right">
						<span class="hd-h4">手机号码</span>
						<span class="mui-pull-right margin-right text-gray">${member.mobile}</span>
					</a>
				</li>
				<li class="mui-table-view-cell">
					<a href="${base}/wap/member/receiver/list.jhtml" class="mui-navigate-right">
						<span class="hd-h4">收货地址</span>
						<span class="mui-pull-right margin-right text-gray">立即修改</span>
					</a>
				</li>
			</ul>
		</div>
		<footer class="footer posi">
			<div class="mui-text-center copy-text">
				<span></span>
			</div>
		</footer>
		<div id="cli_dialog_div"></div>
	</body>

</html>
		<link type="text/css" rel="stylesheet" href="${base}/statics/js/upload/uploader.css?v=2.6.0.161014">
		<script type="text/javascript" src="${base}/statics/js/upload/uploader.js?v=2.6.0.161014"></script>
		<script>
			var uploader = WebUploader.create({
				auto: true,
				chunked: false,
				fileVal: 'upfile',
				// 允许上传的类型
				accept: {
					title: '图片文件',
					extensions: 'jpg,jpeg,png,gif,bmp',
					mimeType: 'image/*'
				},
				// 指定选择文件的按钮容器
				pick: {
					id: '#file-avatar',
					multiple: false
				},
				// swf文件路径
				swf: '${base}/statics/js/upload/uploader.swf',
				// 文件接收服务端
				server: '/wap/member/upload.jhtml',
				// 附加参数
				formData: {
					file: 'upfile',
					//upload_init: '08cbU0bW1SwiOAWtMjtn1/Px3CAi69lIcgmcGLnLKcTQ7I1vBfxGtApuWNbsPWwD/N8+oJa+zSA/93AWQK2Guu9fHCdSzW3su8BTmKRTycD1ksXQaDMcInRgyDMIcMK4Z98aqLWWzBW2XSskSxBvd0T6vu0USQFoQ3umKTFCGmUNxJirxpC7WBLpG9xzCkF8vj4xiOY'
				},
				// 压缩图片
				compress: {
					width: 408,
					height: 408,
					allowMagnify: false
				}
			});
			uploader.onFileQueued = function(file) {
				$.tips({
					content: '图片上传中...'
				});
			}
			uploader.onUploadError = function(file, reason) {
				$.tips({
					content: '上传失败'
				});
			}
			uploader.onUploadSuccess = function(file, response) {
				if(response.status == 1) {
					/* if(response.result.width < 200 || response.result.height < 200) {
						$.tips({
							content: '图片分辨率至少是200*200'
						});
						return false;
					} */
					window.location.reload();
					/* var ajaxurl = '/index.php?m=member&c=account&a=avatar';
					$.post(ajaxurl, {avatar: response.result.url, w: response.result.width, h: response.result.height}, function(ret) {
						if(ret.status == 1) {
							window.location.href = ret.referer;
						} else {
							$.tips({
								content: ret.message
							});
							return false;
						}
					}, 'json'); */
					return true;
				} else {
					$.tips({
						content: response.message
					});
					return false;
				}
			}
		</script>
