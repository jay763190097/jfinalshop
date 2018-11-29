[#include "/wap/include/header.ftl" /]
			<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
			<div class="mui-content">
				<div class="padding bg-white login-wrap">
					<form class="padding-small" action="${base}/wap/login/submit.jhtml" method="POST" name="login_form">
						<div class="list">
							<input type="text" class="input" name="username" placeholder="用户名/手机号/邮箱" datatype="s2-15|mobile|email" nullmsg="请输入用户名/邮箱/手机号" errmsg="格式不正确">
							<span class="validform_checktip"></span></div>
						<div class="list">
							<input type="password" class="input" name="password" placeholder="密码" datatype="*" nullmsg="请输入密码">
							<span class="validform_checktip"></span></div>
						<input type="submit" class="mui-btn full" value="登录">
						<a class="mui-btn full margin-top margin-bottom mui-btn-danger" href="${base}/wap/register/register.jhtml">还没有账号？注册</a>
						<div class="mui-clearfix">
							<input type="hidden" name="url_forward" value="">
							<label class="mui-pull-left text-gray"><input type="checkbox" checked="checked"> 记住登录</label>
							<a class="mui-pull-right text-gray" href="${base}/wap/password/find.jhtml">忘记密码？</a>
						</div>
					</form>
				</div>
			</div>
			<footer class="footer posi">
				<div class="mui-text-center copy-text">
					<span></span>
				</div>
			</footer>
			<script>
				//弹窗提示，在验证返回结果后调用
				//$.tips({content:"登录成功！"});
				var login = $("form[name=login_form]").Validform({
					showAllError: true,
					ajaxPost: true,
					callback: function(ret) {
						if(ret.status == 0) {
							$.tips({
								icon: 'error',
								content: ret.message,
								callback: function() {
									return false;
								}
							});
						} else {
							$.tips({
								icon: 'success',
								content: ret.message,
								callback: function() {
									window.location.href = ret.referer;
								}
							});
						}
					}
				})
				mui(".other-login").on('tap', '.login-item', function() {
					var login_code = $(this).attr("login-code");
					$.post("plugin.php?id=login:third_login", {
						login_code: login_code
					}, function(ret) {
						if(ret.status != 1) {
							$.tips({
								content: ret.message,
								callback: function() {
									return false;
								}
							});
						} else {
							window.location.href = ret.referer;
						}
					}, "json")
				})
			</script>

			<div id="cli_dialog_div"></div>
		</body>

</html>