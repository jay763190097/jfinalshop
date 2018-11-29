[#include "/wap/include/header.ftl" /]
<link href="${base}/statics/css/mui.picker.css" rel="stylesheet" />
<link href="${base}/statics/css/mui.poppicker.css" rel="stylesheet" />
<script type="text/javascript" src="${base}/statics/js/jfinalshop.validate.js?v=2.6.0.161014"></script>
<script src="${base}/statics/js/mui.picker.js"></script>
<script src="${base}/statics/js/mui.poppicker.js"></script>
		<div class="mui-content">
		    <form name="ajax_district" action="${base}/wap/member/receiver/save.jhtml" method="POST">
		    	<div class="mui-input-group add-address">
			        <div class="mui-input-row">
			            <label>收货人</label>
			            <input type="text" class="mui-input-clear" placeholder="请输入收货人地址" name="receiver.consignee"/>
			        </div>
			        <div class="mui-input-row">
			            <label>手机号码</label>
			            <input type="text" class="mui-input-clear"  placeholder="请输入手机号码" name="receiver.phone"/>
			        </div>
			       <!--  <div class="mui-input-row">
			            <label>邮政编码</label>
			            <input type="number" class="mui-input-clear"  placeholder="请输入邮政编码" name="receiver.zip_code"/>
			        </div> -->
			        <div class="mui-input-row">
			              <label>所在省市</label>
			            <input class="mui-input" type="text"  placeholder="请选择所在省市" id='areaName' name="receiver.area_name"/>
						<input class="mui-input" type="hidden" id='areaId' name="receiver.area_id"/>
			       </div>
			        <div class="mui-input-row">
			            <label>详细地址</label>
			            <input type="text" class="mui-input-clear"  placeholder="请输入详细地址" name="receiver.address"/>
			        </div>
			    </div>
		    	<div class="padding-lr margin-top">
		    		<button type="submit" class="mui-btn mui-btn-primary full hd-h4">确认添加收货地址</button>
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
(function($, doc) {
	$.ready(function() {
		//级联示例
		var cityPicker = new $.PopPicker({
			layer: 3
		});
		var url = '${base}/wap/member/receiver/area.jhtml';
		 mui.ajax(url,{
            dataType:'json',
            type:'GET',//HTTP请求类型
            timeout:10000,//超时时间设置为10秒；
            success:function(data){
                cityPicker.setData(data);
            },
            error:function(xhr, type, errorThrown){
                console.log(type);
            }
        });
		
		var areaName = doc.getElementById('areaName');
		var areaId = doc.getElementById('areaId');
		areaName.addEventListener('tap', function(event) {
			cityPicker.show(function(items) {
				var province = typeof((items[0] || {}).text) == 'undefined' ? '' : (items[0] || {}).text;
				var city = typeof((items[1] || {}).text) == 'undefined' ? '' : (items[1] || {}).text;
				var district = typeof((items[2] || {}).text) == 'undefined' ? '' : (items[2] || {}).text;
				areaName.value= province + " " + city + " " + district;
				areaId.value=typeof((items[2] || {}).value) == 'undefined' ? (items[1] || {}).value : (items[2] || {}).value;
				//返回 false 可以阻止选择框的关闭
				//return false;
			});
		}, false);
	});
})(mui, document);
</script>

<script>
var ajax_district=$("form[name=ajax_district]").Validform({
	ajaxPost:true,
	beforeCheck: function() {
		if($("input[name='receiver.consignee']").val() == '') {
			$.tips({
				content: '收货人不能为空'
			});
			$("input[name='receiver.consignee']").focus();
			return false;
		}
		if($("input[name='receiver.phone']").val() == '') {
			$.tips({
				content: '手机号码不能为空'
			});
			$("input[name='receiver.phone']").focus();
			return false;
		}
		/* if($("input[name='receiver.zip_code']").val() == '') {
			$.tips({
				content: '邮政编码不能为空'
			});
			$("input[name='receiver.zip_code']").focus();
			return false;
		} */
		if($("input[name='receiver.address']").val() == '') {
			$.tips({
				content: '详细地址不能为空'
			});
			$("input[name='receiver.address']").focus();
			return false;
		}
	},
	callback:function(ret){
		if(ret.status == 1){
			$.tips({content:ret.message});
			window.location.href = ret.referer;
		}else{
			$.tips({content:ret.message});
		}
	}
});

</script>