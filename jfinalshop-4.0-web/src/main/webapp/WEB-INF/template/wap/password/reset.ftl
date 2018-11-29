[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
<script type="text/javascript" src="${base}/statics/js/jquery.cookie.js?v=2.6.0.161014"></script>
	<div class="mui-content">
		<form name="reset_password" action="${base}/wap/password/submit.jhtml" method="POST">
			<div class="padding bg-white password-find">
				<div class="list">
		        	<input type="password" class="mui-input-clear" name="newPassword"  placeholder="请输入您的密码" />
		        </div>
		    	<div class="list">
		        	<input type="password" class="mui-input-clear" name="newPassword1" placeholder="请再次输入您的密码" />
		        </div>
		         <input type="hidden" name="username" value="${username}">
		         <input type="hidden" name="key" value="${key}">
		         <button type="submit" class="re-btn mui-btn full mui-btn-blue">确认修改</button>
			</div>
		 </form>
	</div>
	<footer class="footer posi">
		<div class="mui-text-center copy-text">
			<span></span>
		</div>
	</footer>
	</body>
</html>
<script>
$(".password-find").show();
var ajax_district = $("form[name=reset_password]").Validform({
	ajaxPost:true,
	beforeCheck: function() {
		if($("input[name=newPassword]").val() == '') {
			$.tips({
				content: '密码不能为空'
			});
			$("input[name=newPassword]").focus();
			return false;
		}
		if($("input[name=newPassword1]").val() == '') {
			$.tips({
				content: '重复密码不能为空'
			});
			$("input[name=newPassword1]").focus();
			return false;
		}
	},
	callback:function(ret){
		if(ret.status == 1){
			$.tips({content:ret.message});
			window.location.href = '${base}/wap/login.jhtml';
		} else {
			$.tips({content:ret.message});
		}
	}
});
</script>
		
		
