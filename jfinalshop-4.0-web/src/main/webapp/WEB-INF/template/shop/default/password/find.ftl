[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.password.find")}[#if showPowered] - Powered By JFinalShop[/#if]</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/password.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $passwordForm = $("#passwordForm");
	var $username = $("#username");
	var $email = $("#email");
	var $code = $("#code");
	var $smscode = $("#smscode");
	var $captcha = $("#captcha");
	var $newPassword = $("#newPassword");
	var $rePassword = $("#rePassword");
	var $captchaImage = $("#captchaImage");
	var $submit = $("input:submit");
	var InterValObj; //timer变量，控制时间 
    var count = 30; //间隔函数，1秒执行 
    var curCount;//当前剩余秒数 
         // 更换验证码
	$captchaImage.click(function() {
		$captchaImage.attr("src", "/jfinalshop-4.0-web/admin/common/captcha.jhtml?width=100&height=45&fontsize=30&time=" + new Date().toString(40));
	});
	     //发送短信验证码
	     $("#smscode").bind("click", function(event) { 
	     curCount = count; 
　　                       //设置button效果，开始计时 
         $smscode.attr("disabled", "true"); 
         $smscode.val(curCount + "秒后可重新发送"); 
         InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次 
　　  
          //请求后台发送验证码 TODO  
  
          //timer处理函数 
          function SetRemainTime() { 
            if (curCount == 0) {         
              window.clearInterval(InterValObj);//停止计时器 
              $smscode.removeAttr("disabled");//启用按钮 
              $smscode.val("重新发送验证码"); 
            } 
            else { 
              curCount--; 
              $smscode.val(curCount + "秒后可重新发送"); 
            } 
          } 
	      
	      });
		  $smscode.click(function(){
		  var $smscode = $("#smscode");
		  var phone = $username.val();
		  console.log(phone);
		  $.ajax({
				url: "${base}/password/getCode.jhtml",
				type: "POST",
				data:{
				   mobile:phone
				},
				dataType: "json",
				cache: false,
				success: function (msg) {
                console.log(msg.success);//打印服务端返回的数据(调试用)
                 if ("YES"==msg.success) {
                     alert("发送成功，请注意接收短信！");
                     console.log("发送成功，请注意接收短信！");
                 }else{
                	 alert("发送失败！请联系客服");
                	 console.log("发送失败！请联系客服");
                 }
	             },
	             error : function() {
	                 alert("服务器异常！");
	             }
	             });
                 });
	
	// 表单验证
	$passwordForm.validate({
		rules: {
			username: {
				required: true,
				pattern: /^1+[0-9]{10}$/
			},
			newPassword: {
				required: true,
				pattern: /^[0-9a-zA-Z]{4,16}$/
			},
			rePassword: {
				required: true,
				equalTo: "#newPassword"
			},
			code: {
				required: true,
				pattern: /^\d{6}$/,
				remote: {
					url: "${base}/password/checkSMS.jhtml",
					cache: false
				}
			}
		},
		messages: {
			username: {
				pattern: "${message("shop.password.phoneError")}"
			},
			newPassword: {
				pattern: "${message("shop.password.passwordError")}"
			},
			code: {
				pattern: "${message("shop.password.codeError")}",
				remote: "${message("shop.register.misscode")}"
			},
		},
		submitHandler: function(form) {
			$.ajax({
				url: $passwordForm.attr("action"),
				type: "POST",
				data: $passwordForm.serialize(),
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$submit.prop("disabled", true);
				},
				success: function(message) {
					$.message(message);
					if (message.type == "success") {
						setTimeout(function() {
							$submit.prop("disabled", false);
							location.href = "/jfinalshop-4.0-web/";
						}, 3000);
					} else {
						$submit.prop("disabled", false);
							$captcha.val("");
							$captchaImage.attr("src", "/jfinalshop-4.0-web/admin/common/captcha.jhtml?width=100&height=45&fontsize=30&time=" + new Date().toString(40));
					}
				}
			});
		}
	});

});
</script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container password">
		<div class="row">
			<div class="span12">
				<div class="wrap">
					<div class="main">
						<div class="title">
							<strong>${message("shop.password.find")}</strong>FORGOT PASSWORD
						</div>
						<form id="passwordForm" action="findSubmit.jhtml" method="post">
							<input type="hidden" name="captchaId" value="${captchaId}" />
							<table>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("shop.password.mobile")}:
									</th>
									<td>
										<input type="text" id="username" name="username" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("shop.password.newPassword")}:
									</th>
									<td>
										<input type="text" id="newPassword" name="newPassword" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("shop.password.rePassword")}:
									</th>
									<td>
										<input type="text" id="rePassword" name="rePassword" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("shop.register.code")}:
									</th>
									<td>
										<input type="text" id="code" name="code" class="text" maxlength="200" /> <input type=button id="smscode" name="smscode" value="${message("shop.register.send")}"/>
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="submit" class="submit" value="${message("shop.password.submit")}" />
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]