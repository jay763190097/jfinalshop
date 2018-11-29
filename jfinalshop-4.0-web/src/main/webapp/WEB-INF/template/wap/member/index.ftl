[#include "/wap/include/header.ftl" /]
<div class="mui-content">
    <div class="user-brief mui-clearfix">
        <div class="user-head mui-pull-left">
		[#if member.avatar?? && member.avatar?has_content]
            <a><img class="full" src="${base}${member.avatar}"></a>
		[#else]
            <a><img class="full" src="${base}/statics/img/default_head.png"></a>
		[/#if]
        </div>
        <ul class="user-text mui-pull-left">
            <li>会员昵称：${member.username}</li>
            <li>会员等级：${member.memberRank.name}</li>
            <li>积分：<em class="text-org">${member.point}</em></li>
            <!-- <li>经验值：<em class="text-org">0</em></li> -->
        </ul>
    </div>
    <div class="mui-row member-quick-nav bg-white">
        <a href="${base}/wap/member/favorite/list.jhtml?period=day" class="mui-col-xs-3 item-collect"><span>${favoriteCount}</span>收藏的商品</a>
        <a href="${base}/wap/member/coupon_code/list.jhtml" class="mui-col-xs-3 item-balance border-left"><span>${couponCodeCount?default(0)}</span>我的优惠券</a>
        <a href="${base}/wap/member/message/list.jhtml" class="mui-col-xs-3 item-coupon border-left"><span>${messageCount}</span>站内消息</a>
        <a href="${base}/wap/member/deposit/log.jhtml" class="mui-col-xs-3 item-balance border-left"><span class="mui-block text-ellipsis">${currency(member.balance, true, true)}</span>我的余额</a>
    </div>
    <ul class="mui-table-view layout-list-common">
        <li class="mui-table-view-cell">
            <a href="${base}/wap/member/order/list.jhtml" class="mui-navigate-right">
                <span class="icon-20"><img src="${base}/statics/images/ico_3.png"></span>
                <span class="hd-h4">我的订单</span>
                <p class="mui-pull-right">查看全部订单</p>
            </a>
        </li>
    </ul>
    <div class="mui-row wait-work bg-white border-bottom">
        <a href="${base}/wap/member/order/list.jhtml?status=pendingPayment" class="mui-col-xs-3 icon-20">
            <img src="${base}/statics/images/ico_7.png">
            <span>待付款</span>
		[#if memberPendingPaymentOrderCount > 0]
            <em class="tag">${memberPendingPaymentOrderCount}</em>
		[/#if]
        </a>
        <a href="${base}/wap/member/order/list.jhtml?status=pendingShipment" class="mui-col-xs-3 icon-20">
            <img src="${base}/statics/images/ico_8.png">
            <span>待发货</span>
		[#if memberPendingShipmentOrderCount > 0]
            <em class="tag">${memberPendingShipmentOrderCount}</em>
		[/#if]
        </a>
        <a href="${base}/wap/member/order/list.jhtml?status=received" class="mui-col-xs-3 icon-20">
            <img src="${base}/statics/images/ico_9.png">
            <span>待收货</span>
		[#if memberReceivedOrderCount > 0]
            <em class="tag">${memberReceivedOrderCount}</em>
		[/#if]
        </a>
        <a href="${base}/wap/member/review/list.jhtml?type=positive" class="mui-col-xs-3 icon-20">
            <img src="${base}/statics/images/ico_10.png">
            <span>待评价</span>
		[#if pendingReviewCount > 0]
            <em class="tag">${pendingReviewCount}</em>
		[/#if]
        </a>
    </div>
    <ul class="mui-table-view layout-list-common">
        <li class="mui-table-view-cell">
            <a href="${base}/wap/member/consultation/list.jhtml" class="mui-navigate-right">
                <span class="icon-20"><img src="${base}/statics/images/ico_6.png"></span>
                <span class="hd-h6">我的咨询</span>
                <p class="mui-pull-right">查看咨询</p>
            </a>
        </li>
        <li class="mui-table-view-cell">
            <a href="${base}/wap/member/service/list.jhtml?status=-1" class="mui-navigate-right">
                <span class="icon-20"><img src="${base}/statics/images/ico_36.png" /></span>
                <span class="hd-h6">我的售后</span>
                <p class="mui-pull-right">查看售后</p>
            </a>
        </li>
        <li class="mui-table-view-cell">
            <a href="${base}/wap/member/profile/edit.jhtml" class="mui-navigate-right">
                <span class="icon-20"><img src="${base}/statics/images/ico_4.png"></span>
                <span class="hd-h6">个人资料</span>
                <p class="mui-pull-right">修改信息</p>
            </a>
        </li>
        <li class="mui-table-view-cell">
            <a href="${base}/wap/member/password/edit.jhtml" class="mui-navigate-right">
                <span class="icon-20"><img src="${base}/statics/images/ico_5.png"></span>
                <span class="hd-h6">账号安全</span>
                <p class="mui-pull-right">修改密码</p>
            </a>
        </li>
    </ul>
    <div class="margin padding-small">
        <a href="/jfinalshop-4.0-web/wap/logout.jhtml" class="mui-btn mui-btn-blue full mui-h5">退出当前账号</a>
    </div>
</div>
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