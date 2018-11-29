[#include "/wap/include/header.ftl" /]
	<div class="mui-content">
	    <div class="all-classify">
	    [#if rootProductCategories?? && rootProductCategories?has_content]
			[#list rootProductCategories as rootProductCategory]
		    	<h3><span>${rootProductCategory.name}</span></h3>
		    	<ul class="mui-row bg-white">
			    	<li class="mui-col-xs-3">
			    		<a href="${base}${rootProductCategory.wapPath}?productCategoryId=${rootProductCategory.id}">全部</a>
			    	</li>
			    	[#if rootProductCategory.children?? && rootProductCategory.children?has_content]
						[#list rootProductCategory.children as productCategory]
				    		<li class="mui-col-xs-3">
				    			<a href="${base}${productCategory.wapPath}?productCategoryId=${productCategory.id}">
				    				<span>${productCategory.name}</span>
				    			</a>
				    		</li>
			    		[/#list]
			    		[#assign size = rootProductCategory.children?size + 1]
			    		[#assign count = size % 4]
			    		[#if count > 0]
			    			[#list 1..count as t]
								<li class="mui-col-xs-3">
				    				<a href="jvascript:;">&nbsp;</a>
				    			</li>
							[/#list]
			    		[/#if]
			    	[/#if]
		    	</ul>
	    	  [/#list]
	     [/#if]
	    </div>
	</div>
</body>
</html>
