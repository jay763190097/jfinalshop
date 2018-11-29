[#include "/wap/include/header.ftl" /]
	[#if cart?? && cart?has_content]
		<div class="mui-content has-footer-bar mui-clearfix">
			[#if cart?? && cart.cartItems?has_content]
			    <dl class="cart-lists margin-none mui-clearfix">
			    	<dt><h3 class="shop-title">商家自营</h3></dt>
					[#list cart.cartItems as cartItem]
				    	<dd class="cart-list-item" data-skuid="${cartItem.product.id}" data-id="${cartItem.id}">
				    		<div class="hd-checkbox item-checkbox">
			    				<input name="check_sku" type="checkbox" [#if cartItem.product?has_content] checked="checked" [#else] disabled="disabled" [/#if]/>
			    			</div>
			    			<div class="item-pic"><img src="${cartItem.product.thumbnail!setting.defaultThumbnailProductImage}" /></div>
			    			<div class="item-text">
			    				<p class="mui-clearfix">
			    					[#if cartItem.product.specifications?has_content]
										<a href="${base}/wap/goods/detail.jhtml?id=${cartItem.product.goods.id}>${abbreviate(cartItem.product.name, 50, "...")} [${cartItem.product.specifications?join(", ")}]</a>
									[#else]
										<a href="${base}/wap/goods/detail.jhtml?id=${cartItem.product.goods.id}">${abbreviate(cartItem.product.name, 50, "...")}</a>
									[/#if]
			    					[#if !cartItem.isMarketable]
			    					 	<span class="shelves"><i class="icon-shelves"></i>[已下架]</span>
			    					[/#if]
			    					[#if cartItem.isLowStock]
			    						<span class="shelves"><i class="icon-shelves"></i>[${message("shop.cart.lowStock")}]</span>
									[/#if]
			    				</p>
			    				<span class="text-org price">￥<em>${currency(cartItem.price, false)}</em></span>
			    				<div class="number full mui-clearfix">
				    				<button class="num-btn num-decrease" data-event="number">-</button>
				    				<input class="num-input" type="number" data-max="[#if cartItem.product.availableStock?has_content] ${cartItem.product.availableStock} [#else] 1 [/#if]" value="${cartItem.quantity}" data-id="buy-num"/>
				    				<button class="num-btn num-increase" data-event="number">+</button>
				    				<em class="delelte mui-pull-right" data-id="del-sku"><img src="${base}/statics/images/ico_27.png"></em>
				    			</div>
			    			</div>
				    	</dd>
		    		[/#list]
			    </dl>
			[/#if]
		</div>
		<nav class="cart-footer-bar">
			<div class="cart-footer-box full">
				<div class="hd-checkbox">
					<label>全选</label>
					<input name="check_all" type="checkbox" data-id="chekced-all" checked="true" />
				</div>
			    <p class="mui-pull-right cart-total mui-text-right">
					<span class="text-org">合计：￥<em class="normal" data-id="totle">${currency(cart.effectivePrice, true, true)}</em></span><br/>
					结算商品数量：<em class="normal" data-id="sku-numbers">${cart.quantity}</em> 件
				</p>
			</div>
			<a href="javascript:;" data-id="sub-btn" class="cart-footer-btn mui-text-center">结算</a>
		</nav>
	[#else]
	<div class="mui-content">
		<ul class="user-lists mui-clearfix">
	    	<li class="user-list-none order-lh-40 mui-text-center">
				<img src="${base}/statics/images/bg_5.png" />
				<p class="text-black hd-h4">您的购物车还是空的</p>
				<a href="${base}/wap/index.jhtml" class="mui-btn mui-btn-primary mui-btn-outlined border-small">先去逛逛</a>
			</li>
		</ul>
	</div>
	[/#if]
	</body>
</html>

<script type="text/javascript" src="${base}/statics/js/cart.js?v=2.6.0.161014"></script>
<script>
	$.numbers();
	$.checkedAll({
		master: $("[data-id='chekced-all']"),
		child: $(".cart-lists .cart-list-item .hd-checkbox")
	});
	update_total();
</script>
		