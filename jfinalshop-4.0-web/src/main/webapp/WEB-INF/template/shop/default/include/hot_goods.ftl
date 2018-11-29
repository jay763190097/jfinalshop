[#escape x as x?html]
[@goods_list product_category_id = productCategory.id count = 3 orderBy = "month_sales desc"]
	[#if goodsList?has_content]
		<div class="hotGoods">
			<dl>
				<dt>${message("shop.goods.hotGoods")}</dt>
				[#list goodsList as goods]
					<dd>
						<a href="${base}${goods.url}">
							<img src="${base}${goods.thumbnail!setting.defaultThumbnailProductImage}" alt="${goods.name}" />
							<span title="${goods.name}">${abbreviate(goods.name, 52)}</span>
						</a>
						<strong>
							${currency(goods.price, true)}
							[#if setting.isShowMarketPrice]
								<del>${currency(goods.marketPrice, true)}</del>
							[/#if]
						</strong>
					</dd>
				[/#list]
			</dl>
		</div>
	[/#if]
[/@goods_list]
[/#escape]