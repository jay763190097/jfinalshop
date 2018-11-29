<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${message("admin.login.title")} - Powered By jfinalshop.com</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.7 -->
  <link rel="stylesheet" href="${base}/adminlte/bootstrap/dist/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${base}/adminlte/font-awesome/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${base}/adminlte/Ionicons/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="${base}/adminlte/dist/css/AdminLTE.min.css">
  <!-- iCheck -->
  <link rel="stylesheet" href="${base}/adminlte/plugins/iCheck/square/blue.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Google Font -->
  <link rel="stylesheet" href="${base}/adminlte/font/fontcss.css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
  <script type="text/javascript">
		//防止页面嵌套在Iframe中
		if (window != top){
			top.location.href = location.href; 
		}
  </script>
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
    <a href=""><b>JFinal</b>Shop</a>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body">
    <p class="login-box-msg">欢迎登录管理中心</p>
    [#if failureMessage??] 
		<div class="alert alert-danger alert-dismissible">
            ${failureMessage.content}
        </div>
	[/#if]
    <form action="login.jhtml" method="post" id="loginForm">
      <div class="form-group has-feedback">
           <input type="text" class="form-control" id="username" name="username" placeholder="请输入登录名">
           <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
       </div>
       <div class="form-group has-feedback">
       	   <input type="hidden" id="enPassword" name="enPassword" />
           <input type="password" class="form-control" id="password" name="password" placeholder="请输入密码">
           <span class="glyphicon glyphicon-lock form-control-feedback"></span>
       </div>
       <div class="form-group has-feedback">
       	<div class="input-group">
           	<input type="text" class="form-control"  id="captcha" name="captcha" placeholder="请输入验证码" style="width: 60%">&nbsp;&nbsp;<img id="captchaImage" class="captchaImage" src="${base}/admin/common/captcha.jhtml?width=100&height=34&fontsize=30" title="${message("admin.captcha.imageTitle")}" />
           </div>
      </div>
      <div class="row">
        <div class="col-xs-8">
          <div class="checkbox icheck">
            <label>
              <input type="checkbox"> 记住我
            </label>
          </div>
        </div>
        <!-- /.col -->
        <div class="col-xs-12">
          <button type="submit" class="btn btn-danger btn-block btn-flat">登 录</button>
        </div>
        <!-- /.col -->
      </div>
    </form>

    <div class="social-auth-links text-center">
    </div>
    <!-- /.social-auth-links -->
  </div>
  <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<!-- jQuery 3 -->
<script src="${base}/adminlte/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script src="${base}/adminlte/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="${base}/adminlte/plugins/iCheck/icheck.min.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jsbn.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/prng4.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/rng.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/rsa.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/base64.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script>
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
  });
  
  var $captchaImage = $("#captchaImage");
	
	$().ready( function() {
		// 更换验证码
  	$captchaImage.click( function() {
  		var d = new Date();
  		$captchaImage.attr("src", "${base}/admin/common/captcha.jhtml?width=100&height=34&fontsize=30&time=" + d.toString(40));
  	});
	});
	
    var $loginForm = $("#loginForm");
	var $enPassword = $("#enPassword");
	var $username = $("#username");
	var $password = $("#password");
	
	// 表单验证、记住用户名
	$loginForm.submit( function() {
		var rsaKey = new RSAKey();
		rsaKey.setPublic(b64tohex("${modulus}"), b64tohex("${exponent}"));
		var enPassword = hex2b64(rsaKey.encrypt($password.val()));
		$enPassword.val(enPassword);
	});
  
</script>
</body>
</html>
