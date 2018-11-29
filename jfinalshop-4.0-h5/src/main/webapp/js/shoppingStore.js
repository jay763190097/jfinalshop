(function(){

	var plus = $("i[data-type='plus']"); //增加按钮
	var minuse = $("i[data-type='minuse']"); //减少按钮

	var selectAllDom = $("#selecAll"); //减少按钮
	
	// 增加商品数量
	plus.on('click',function(){
		var inputDom = $(this).prev('input');
		var value = inputDom.val();
		value ++ ;

		inputDom.val(value);
		getAmount();
	});

	// 减少商品数量
	minuse.on('click',function(){
		var inputDom = $(this).next('input');
		var value = inputDom.val();
		value -- ;
		inputDom.val(value);

		if(value <= 1){
			inputDom.val(1);
		}
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