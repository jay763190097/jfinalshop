var goods_detail = (function() {
    
    return {
        init : function() {
			this.getCartCount();
        },
        
        /* 获取购物车总数 */
		getCartCount : function () {
			$.getJSON('/wap/cart/getCartCount.jhtml',{format:'json'}, function(ret) {
				cart_nums = ret.sku_counts;
				$('.nums').text(cart_nums);
			});
		},
        
        /* 加入购物车 */
		cart_add : function() {
			if(goods.number < 1) return;
            sku_id = goods.sku_id,
            num = $('.number .num-input').val();
			$.getJSON("/wap/cart/add.jhtml", {productId:sku_id, quantity:num, buy_now: false}, function(ret) {
				if (ret.status == 1) {
                    $.tips({
                    	content : '添加购物车成功',
                    	callback:function() {
							$(".hd-cover").removeClass("show");
							$(".cover-decision").hide();
							window.location.href = '#';
							cart_nums++;
							$('.nums').text(cart_nums);
							setTimeout(function(){
								$(".hd-cover").hide();
							},200);
						}
                    });
				} else {
                    $.tips({content : ret.message});
					return false;
				}
			});
		},
		
		/* 立即购买 */
		buy_now : function(obj) {
			sku_id = goods.sku_id
			if (sku_id == null || sku_id == undefined) {
				$.tips({content: '请选择您要购买的商品'});
				return false;
			}
			
			var buy_nums = $('.number .num-input').val() ? $('.number .num-input').val() : 1;
			$.ajax({
				url: '/wap/cart/add.jhtml',
				data: {productId: sku_id, quantity: buy_nums, buy_now: true},
				type: 'GET',
				dataType: 'json',
				success:function(ret) {
					if (ret.status == 0) {
						$.tips({content : ret.message});
						return false;
					}
					// 直接跳转到结算页面
					window.location.href = '/wap/order/checkout.jhtml?skuids=' + sku_id;
				}
			});
		}
    }
})();