(function(){

	var plus = $("i[data-type='plus']"); //增加按钮
	var minuse = $("i[data-type='minuse']"); //减少按钮
    var cart_key_1 = $("#cart_key_1").val();
	var selectAllDom = $("#selecAll"); //单选select按钮

	// 增加商品数量
	plus.on('click',function(){
		var inputDom = $(this).prev('input');
		var value = inputDom.val();
        var stcok = inputDom.prev();
		var pid = stcok.prev();
		var stco = stcok.val();
		var product_id = pid.val();
		value ++ ;
		if (value >=stco){
            value=stco;
            inputDom.val(stco)
		}
        $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/cart/setNums",{"productId":product_id,"cartKey":cart_key_1,"quantity":value},function (data) {
            if (data.code == "1"){

			}
        });

        inputDom.val(value);
		getAmount();

	});
	// 减少商品数量
	minuse.on('click',function(){
		/*var inputDom = $(this).next('input');*/
		var cartkey = $("#cart_key_1").val();
		var pid =  $(this).next('input');
        var sto = pid.next('input');
        var inputDom = sto.next('input');
		var value = inputDom.val();
        var product_id = pid.val();
        var ids = $("#items").val();
        var amount = $("sh-amount").val();
		value -- ;
		inputDom.val(value);
		if(value <1){
			inputDom.val(1);
            value=1;
            var a = confirm("是否将该商品移除?");
            if (a==true){
                $.ajax({
                    type : "POST",
                    url : "http://114.55.93.111:8080/jfinalshop-4.0-api/api/cart/delete",
                    data : "ids="+ids+ "&cartKey="+cartkey,
                    dataType : "json",
                    success : function(msg) {
                        alert("删除成功");
                        $("#itm"+product_id).remove();
                        getAmount()
                        return;
                    },
                    error:function(msg){
                        alert("服务器错误");
                    }
                });
			}
        }
        $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/cart/setNums",{"productId":product_id,"cartKey":cart_key_1,"quantity":value},function (data) {

        });
		getAmount();
	});
	
	// 单选select
	var select = $("div[data-type='checkbox']"); //单选select
	var bl = false;
	select.on('click',function(){
		if($(this).hasClass('icon-check-more')){
			$(this).removeClass('icon-check-more');
			getAmount();
		}else{
			$(this).addClass("icon-check-more");
			getAmount();
		}
		
		for(var i =0 ;i<select.length; i++){
			bl = select.eq(i).hasClass('icon-check-more');
		}
		
		if(bl){
			selectAllDom.addClass("icon-check-more");
		}else{
			selectAllDom.removeClass("icon-check-more");
		}
	});

	// 全选
	selectAllDom.on('click',function(){

		if($(this).hasClass('icon-check-more')){
			$(this).removeClass('icon-check-more');
			for(var i =0 ;i<select.length; i++){
				select.eq(i).removeClass("icon-check-more");
			}
			getAmount();
		}else{
			$(this).addClass("icon-check-more");
			for(var i =0 ;i<select.length; i++){
				select.eq(i).addClass("icon-check-more");
			}
			getAmount();
		}
		
	});

	getAmount();
})();


// 所选商品总价
function getAmount(){
	var feeDom = $("#feeAmount"); // 商品总价显示
	var arrDom = $("input[data-type='number']"); // 
	var leng = arrDom.length;
	var amount = 0;

	for(var i=0; i<leng;i++){
		var parentDom = arrDom.eq(i).parents('li').children("div[data-type='checkbox']");
		var bool = parentDom.hasClass('icon-check-more');
		if(bool){
			var data = arrDom.eq(i).val();
			var pri = arrDom.eq(i).parent().siblings("span[data-type='price']").find('i').text();
			amount = amount + data * pri ;
		}
		
	}

	feeDom.text(amount);
	
}