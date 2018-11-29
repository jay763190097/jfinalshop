[#include "/wap/include/header.ftl" /]
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
<script type="text/javascript" src="${base}/statics/js/jquery.cookie.js?v=2.6.0.161014"></script>
		<div class="mui-content">
			<div class="padding form-wrap bg-white">
				<form class="padding-small" action="${base}/wap/member/mobile/update.jhtml" name="resetmobile">
		        <input type="text" class="input" value="" placeholder="请输入新手机号" name="mobile"/>
		        <div class="margin-bottom"><span>请输入您的新手机号码</span></div>
		        <div class="relative">
		        	<input type="text" class="number" placeholder="请输入手机验证码" name="vcode" disabled="disabled" id="vcode"/>
		        	<button class="mui-btn mui-btn-primary hd-h5 ver-code" id="sendsms">发送验证码</button>
		        </div>
		        <input type="submit" class="mui-btn margin-top full bg-white" value="保存手机号" />
		    </form>
			</div>
		</div>
		<footer class="footer posi">
			<div class="mui-text-center copy-text">
				<span></span>
			</div>
		</footer>
		<div id="cli_dialog_div"></div>
	</body>
</html>
<script>
	var resetmobile = $("form[name=resetmobile]").Validform({
		ajaxPost: true,
		beforeCheck: function() {
			if($("input[name=mobile]").val() == '') {
				$.tips({
					content: '手机号不能为空'
				});
				$("input[name=mobile]").focus();
				return false;
			}
			if($("input[name=vcode]").val() == '') {
				$.tips({
					content: '验证码不能为空'
				});
				$("input[name=vcode]").focus();
				return false;
			}
		},
		callback: function(ret) {
			if(ret.status == 1) {
				$.tips({
					contnet: ret.message
				});
				window.location.href = '${base}' + ret.referer;
			} else {
				$.tips({
					content: ret.message
				});
			}
		}
	});
	/*仿刷新：检测是否存在cookie*/
	if($.cookie("captcha")) {
		reget($.cookie("captcha"));
	}
	//发送验证码
	$("#sendsms").on("click", function() {
			var mobj = $('input[name="mobile"]');
			var str = mobj.val();
			if(str.length != 0) {
				var reg = /^(1(([35][0-9])|(47)|[7][0678]|[8][0123456789]))\d{8}$/;
				var r = str.match(reg);
				if(r == null) {
					$.tips({
						content: '您输入的手机格式不正确!'
					});
					mobj.focus();
					return false;
				}
				if(confirm('确认将验证码发送到 ' + str + ' 吗?')) {
					checkAndSend(str);
				} else {
					return false;
				}

			} else {
				$.tips({
					content: '手机号不能为空'
				});
				mobj.focus();
				return false;
			}
	})
		
	//检查验证
	function checkAndSend(mobile, code) {
		var ajaxurl = "${base}/wap/member/mobile/send.jhtml";
		$.post(ajaxurl, {
			'mobile': mobile
		}, function(data) {}, 'json');
		$("#vcode").removeAttr("readonly disabled");
		reget(60);
	}
	//重新获取验证码
	function reget(count) {
		var mobj = $('input[name="mobile"]');
		var btn = $("#sendsms");
		var count = count;
		var resend = setInterval(function() {
			count--;
			if(count > 0) {
				btn.text(count + "s后再试");
				mobj.attr('readonly', true);
				$.cookie("captcha", count, {
					path: '/',
					expires: (1 / 86400) * count
				});
			} else {
				clearInterval(resend);
				mobj.removeAttr('disabled readonly');
				btn.text("重获验证码").removeAttr('disabled').css({
					'cursor': '',
					'background': '#046bb3'
				});
			}
		}, 1000);

		btn.attr('disabled', true).css({
			'cursor': 'not-allowed',
			'background': '#989898'
		});
	}
</script>
