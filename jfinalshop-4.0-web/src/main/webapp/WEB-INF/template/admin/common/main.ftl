<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${message("admin.main.title")} - Powered By JFinalShop</title>
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
  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
  <link rel="stylesheet" href="${base}/adminlte/dist/css/skins/_all-skins.min.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Google Font -->
  <link rel="stylesheet" href="${base}/adminlte/font/fontcss.css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
   <script>
    	// iframe高度自适应的方法
       function setIframeHeight(iframe) {
    		var iframe = document.getElementById("iframe");
           if (iframe) {
               var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
               if (iframeWin.document.body) {
               	var header = document.getElementById("header");
				var h = header.offsetHeight;  //高度
                   iframe.height = document.body.clientHeight-h-5;
               }
           }
       };
   </script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">

  <header class="main-header" id="header">
    <!-- Logo -->
    <a class="logo">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini"><b>J</b>S</span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg"><b>JFinal</b>Shop <sup>5.0</sup></span>
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <!-- Sidebar toggle button-->
      <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </a>

      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
          <!-- Messages: style can be found in dropdown.less-->
          <li>
            <a href="${base}/admin/message/list.jhtml" target="iframe">
              <i class="fa fa-envelope-o"></i>
              <span class="label label-success">${unreadMessageCount}</span>
            </a>
          </li>
          <!-- Notifications: style can be found in dropdown.less -->
          <li class="dropdown notifications-menu">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <i class="fa fa-bell-o"></i>
              [#if pendingReviewOrderCount + pendingShipmentOrderCount + pendingReceiveOrderCount + pendingRefundsOrderCount > 0]
					<span class="label label-warning">${pendingReviewOrderCount + pendingShipmentOrderCount + pendingReceiveOrderCount + pendingRefundsOrderCount}</span>
			  [/#if]
            </a>
            <ul class="dropdown-menu">
              <li class="header">你有${pendingReviewOrderCount + pendingShipmentOrderCount + pendingReceiveOrderCount + pendingRefundsOrderCount}个待处理的任务</li>
              <li>
                <!-- inner menu: contains the actual data -->
                <ul class="menu">
                  <li>
					<a href="${base}/admin/order/list.jhtml?status=pendingReview" target="iframe">
						<span class="fa fa-credit-card text-aqua"></span>
						待审核订单计数 ${pendingReviewOrderCount}
					</a>
				</li>
				<li>
					<a href="${base}/admin/order/list.jhtml?status=pendingShipment" target="iframe">
						<span class="fa fa-user-o text-red"></span>
						等待发货订单计数 ${pendingShipmentOrderCount}
					</a>
				</li>
				<li>
					<a href="${base}/admin/order/list.jhtml?status=received" target="iframe">
						<span class="fa fa-truck text-green"></span>
						等待收货订单计数 ${pendingReceiveOrderCount}
					</a>
				</li>
				<li>
					<a href="${base}/admin/order/list.jhtml?status=refunded" target="iframe">
						<span class="fa fa-rmb text-yellow"></span>
						等待退款订单计数 ${pendingRefundsOrderCount}
					</a>
				</li>
                </ul>
              </li>
              <li class="footer"><a href="${base}/admin/order/list.jhtml" target="iframe">查看所有</a></li>
            </ul>
          </li>
          <!-- Tasks: style can be found in dropdown.less -->
          
          <!-- User Account: style can be found in dropdown.less -->
          <li class="dropdown user user-menu">
            <a href="../profile/edit.jhtml" target="iframe">
              <img src="${base}/adminlte/dist/img/user2-160x160.jpg" class="user-image" alt="User Image">
              <span class="hidden-xs">[@shiro.principal name="username" /]</span>
            </a>
          </li>
          <!-- Control Sidebar Toggle Button -->
          <li>
            <a class="logout" href="${base}/signout">
				<span class="fa fa-sign-out"></span>
				${message("admin.main.logout")}
			</a>
          </li>
        </ul>
      </div>
    </nav>
  </header>

  <!-- =============================================== -->

  <!-- Left side column. contains the sidebar -->
  <aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
      <!-- sidebar menu: : style can be found in sidebar.less -->
      <ul class="sidebar-menu" data-widget="tree">
        <li class="header">主要导航</li>
        <li class="treeview">
        [#list ["admin:goods", "admin:stock", "admin:productCategory", "admin:parameter", "admin:attribute", "admin:specification", "admin:brand", "admin:productNotify"] as permission]
			[@shiro.hasPermission name = permission]
	          <a href="#">
	            <i class="glyphicon glyphicon-th-large"></i> <span>${message("admin.main.productGroup")}</span>
	            <span class="pull-right-container">
	              <i class="fa fa-angle-left pull-right"></i>
	            </span>
	          </a>
	          <ul class="treeview-menu">
	          	[@shiro.hasPermission name="admin:goods"]
					<li><a href="../goods/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.goods")}</a></li>
				[/@shiro.hasPermission]
				 
				[@shiro.hasPermission name="admin:stock"]
					<li><a href="../stock/log.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.stock")}</a></li>
				[/@shiro.hasPermission]
				 
				[@shiro.hasPermission name="admin:productCategory"]
					<li><a href="../product_category/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.productCategory")}</a></li>
				[/@shiro.hasPermission]
				
				[@shiro.hasPermission name="admin:parameter"]
					<li><a href="../parameter/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.parameter")}</a></li>
				[/@shiro.hasPermission]
				
				[@shiro.hasPermission name="admin:attribute"]
					<li><a href="../attribute/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.attribute")}</a></li>
				[/@shiro.hasPermission]
				
				[@shiro.hasPermission name="admin:specification"]
					<li><a href="../specification/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.specification")}</a></li>
				[/@shiro.hasPermission]
				
				[@shiro.hasPermission name="admin:brand"]
					<li><a href="../brand/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.brand")}</a></li>
				[/@shiro.hasPermission]
				
				[@shiro.hasPermission name="admin:productNotify"]
					<li><a href="../product_notify/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.productNotify")}</a></li>
				[/@shiro.hasPermission]
	          </ul>
         		[#break /]
			[/@shiro.hasPermission]
		[/#list]
        </li>
        <li class="treeview">
        	[#list ["admin:order", "admin:payment", "admin:refunds", "admin:shipping", "admin:returns", "admin:deliveryCenter", "admin:deliveryTemplate"] as permission]
				[@shiro.hasPermission name = permission]
		        	<a href="#">
			            <i class="glyphicon glyphicon-th-list"></i> <span>${message("admin.main.orderGroup")}</span>
			            <span class="pull-right-container">
			              <i class="fa fa-angle-left pull-right"></i>
			            </span>
		           </a>
		           <ul class="treeview-menu">
		           		[@shiro.hasPermission name="admin:order"]
							<li><a href="../order/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.order")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:payment"]
							<li><a href="../payment/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.payment")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:refunds"]
							<li><a href="../refunds/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.refunds")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:shipping"]
							<li><a href="../shipping/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.shipping")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:returns"]
							<li><a href="../returns/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.returns")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:deliveryCenter"]
							<li><a href="../delivery_center/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.deliveryCenter")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:deliveryTemplate"]
							<li><a href="../delivery_template/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.deliveryTemplate")}</a></li>
						[/@shiro.hasPermission]
		           </ul>
			     [#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
        <li class="treeview">
        	[#list ["admin:member", "admin:memberRank", "admin:memberAttribute", "admin:point", "admin:deposit", "admin:review", "admin:consultation", "admin:messageConfig"] as permission]
				[@shiro.hasPermission name = permission]
			        	<a href="#">
				            <i class="glyphicon glyphicon-user"></i> <span>${message("admin.main.memberGroup")}</span>
				            <span class="pull-right-container">
				              <i class="fa fa-angle-left pull-right"></i>
				            </span>
			           </a>
			           <ul class="treeview-menu">
			           		[@shiro.hasPermission name="admin:member"]
								<li><a href="../member/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.member")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:memberRank"]
								<li><a href="../member_rank/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.memberRank")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:memberAttribute"]
								<li><a href="../member_attribute/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.memberAttribute")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:point"]
								<li><a href="../point/log.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.point")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:deposit"]
								<li><a href="../deposit/log.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.deposit")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:review"]
								<li><a href="../review/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.review")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:consultation"]
								<li><a href="../consultation/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.consultation")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:messageConfig"]
								<li><a href="../message_config/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.messageConfig")}</a></li>
							[/@shiro.hasPermission]
			           </ul>
           			[#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
        <li class="treeview">
        	 [#list ["admin:member", "admin:memberRank", "admin:memberAttribute", "admin:point", "admin:deposit", "admin:review", "admin:consultation", "admin:messageConfig"] as permission]
				[@shiro.hasPermission name = permission]
			        	<a href="#">
				            <i class="glyphicon glyphicon-folder-open"></i> <span>${message("admin.main.contentGroup")}</span>
				            <span class="pull-right-container">
				              <i class="fa fa-angle-left pull-right"></i>
				            </span>
			           </a>
			           <ul class="treeview-menu">
			           		[@shiro.hasPermission name="admin:navigation"]
								<li><a href="../navigation/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.navigation")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:article"]
								<li><a href="../article/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.article")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:articleCategory"]
								<li><a href="../article_category/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.articleCategory")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:tag"]
								<li><a href="../tag/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.tag")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:friendLink"]
								<li><a href="../friend_link/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.friendLink")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:adPosition"]
								<li><a href="../ad_position/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.adPosition")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:ad"]
								<li><a href="../ad/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.ad")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:template"]
								<li><a href="../template/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.template")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:theme"]
								<li><a href="../theme/setting.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.theme")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:cache"]
								<li><a href="../cache/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.cache")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:static"]
								<li><a href="../static/generate.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.static")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:index"]
								<li><a href="../index/generate.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.index")}</a></li>
							[/@shiro.hasPermission]
			           </ul>
           			[#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
        <li class="treeview">
        	[#list ["admin:promotion", "admin:coupon", "admin:seo", "admin:sitemap"] as permission]
				[@shiro.hasPermission name = permission]
			        	<a href="#">
				            <i class="glyphicon glyphicon-bullhorn"></i> <span>${message("admin.main.marketingGroup")}</span>
				            <span class="pull-right-container">
				              <i class="fa fa-angle-left pull-right"></i>
				            </span>
			           </a>
			           <ul class="treeview-menu">
			           		[@shiro.hasPermission name="admin:statistics"]
								<li><a href="../promotion/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.promotion")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:statistics"]
								<li><a href="../coupon/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.coupon")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:memberStatistic"]
								<li><a href="../seo/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.seo")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:orderStatistic"]
								<li><a href="../sitemap/generate.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.sitemap")}</a></li>
							[/@shiro.hasPermission]
			           </ul>
           			[#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
        <li class="treeview">
        	[#list ["admin:statistics", "admin:memberStatistic", "admin:orderStatistic", "admin:memberRanking", "admin:goodsRanking"] as permission]
				[@shiro.hasPermission name = permission]
		        	<a href="#">
			            <i class="glyphicon glyphicon-list-alt"></i> <span>${message("admin.main.statisticGroup")}</span>
			            <span class="pull-right-container">
			              <i class="fa fa-angle-left pull-right"></i>
			            </span>
		           </a>
		           <ul class="treeview-menu">
		           		[@shiro.hasPermission name="admin:statistics"]
							<li><a href="../statistics/view.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.statistics")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:statistics"]
							<li><a href="../statistics/setting.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.statisticsSetting")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:memberStatistic"]
							<li><a href="../member_statistic/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.memberStatistic")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:orderStatistic"]
							<li><a href="../order_statistic/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.orderStatistic")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:memberRanking"]
							<li><a href="../member_ranking/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.memberRanking")}</a></li>
						[/@shiro.hasPermission]
						
						[@shiro.hasPermission name="admin:goodsRanking"]
							<li><a href="../goods_ranking/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.goodsRanking")}</a></li>
						[/@shiro.hasPermission]
		           </ul>
		    		[#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
        <li class="treeview">
        	[#list ["admin:setting", "admin:area", "admin:paymentMethod", "admin:shippingMethod", "admin:deliveryCorp", "admin:paymentPlugin", "admin:storagePlugin", "admin:loginPlugin", "admin:admin", "admin:role", "admin:message", "admin:log"] as permission]
				[@shiro.hasPermission name = permission]
			        	<a href="#">
				            <i class="glyphicon glyphicon-cog"></i> <span>${message("admin.main.systemGroup")}</span>
				            <span class="pull-right-container">
				              <i class="fa fa-angle-left pull-right"></i>
				            </span>
			           </a>
			           <ul class="treeview-menu">
			           		[@shiro.hasPermission name="admin:setting"]
								<li><a href="../setting/edit.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.setting")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:area"]
								<li><a href="../area/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.area")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:paymentMethod"]
								<li><a href="../payment_method/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.paymentMethod")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:shippingMethod"]
								<li><a href="../shipping_method/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.shippingMethod")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:deliveryCorp"]
								<li><a href="../delivery_corp/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.deliveryCorp")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:paymentPlugin"]
								<li><a href="../payment_plugin/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.paymentPlugin")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:storagePlugin"]
								<li><a href="../storage_plugin/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.storagePlugin")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:loginPlugin"]
								<li><a href="../login_plugin/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.loginPlugin")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:admin"]
								<li><a href="../admin/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.admin")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:permission"]
								<li><a href="../permission/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.permission")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:role"]
								<li><a href="../role/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.role")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:message"]
								<li><a href="../message/send.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.send")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:message"]
								<li><a href="../message/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.message")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:message"]
								<li><a href="../message/draft.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.draft")}</a></li>
							[/@shiro.hasPermission]
							
							[@shiro.hasPermission name="admin:log"]
								<li><a href="../log/list.jhtml" target="iframe"><i class="fa fa-circle-o"></i>${message("admin.main.log")}</a></li>
							[/@shiro.hasPermission]
			           </ul>
           			[#break /]
				[/@shiro.hasPermission]
			[/#list]
        </li>
      </ul>
    </section>
    <!-- /.sidebar -->
  </aside>

  <!-- =============================================== -->

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper" id="mainDiv">
		<iframe src="index.jhtml" frameborder="0" scrolling="auto" id="iframe" name="iframe" onload="setIframeHeight(this)" height=100% width=100%></iframe>
  </div>
  <!-- /.content-wrapper -->

  <!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
  <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

<!-- jQuery 3 -->
<script src="${base}/adminlte/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script src="${base}/adminlte/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- AdminLTE App -->
<script src="${base}/adminlte/dist/js/adminlte.min.js"></script>
<script>
  $(document).ready(function () {
    $('.sidebar-menu').tree()
  })
</script>
</body>
</html>
