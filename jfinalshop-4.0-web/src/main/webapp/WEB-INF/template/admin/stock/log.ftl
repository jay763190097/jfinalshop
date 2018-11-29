[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.stock.log")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.stock.log")} <span>(${message("admin.page.total", page.totalRow)})</span>
	</div>
	<form id="listForm" action="log.jhtml" method="post">
		<div class="bar">
			<div class="buttonGroup">
				<a href="stockIn.jhtml" class="button">
					${message("admin.stock.stockIn")}
				</a>
				<a href="stockOut.jhtml" class="button">
					${message("admin.stock.stockOut")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div id="pageSizeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if pageable.pageSize == 10] class="current"[/#if] val="10">10</li>
						<li[#if pageable.pageSize == 20] class="current"[/#if] val="20">20</li>
						<li[#if pageable.pageSize == 50] class="current"[/#if] val="50">50</li>
						<li[#if pageable.pageSize == 100] class="current"[/#if] val="100">100</li>
					</ul>
				</div>
			</div>
			<div id="searchPropertyMenu" class="dropdownMenu">
				<div class="search">
					<span class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="pageable.searchValue" value="${pageable.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<ul>
					<li[#if pageable.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Product.sn")}</li>
					<li[#if pageable.searchProperty == "operator"] class="current"[/#if] val="operator">${message("StockLog.operator")}</li>
				</ul>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th>
					<a href="javascript:;" class="sort" name="product.sn">${message("Product.sn")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="product_id">${message("StockLog.product")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="type">${message("StockLog.type")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="in_quantity">${message("StockLog.inQuantity")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="out_quantity">${message("StockLog.outQuantity")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="stock">${message("StockLog.stock")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="operator">${message("StockLog.operator")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="memo">${message("StockLog.memo")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="create_date">${message("admin.common.createDate")}</a>
				</th>
			</tr>
			[#list page.list as stockLog]
				<tr>
					<td>
						${stockLog.product.sn}
					</td>
					<td>
						<span title="${stockLog.product.name}">${abbreviate(stockLog.product.name, 50, "...")}</span>
						[#if stockLog.product.specifications?has_content]
							<span class="silver">[${stockLog.product.specifications?join(", ")}]</span>
						[/#if]
					</td>
					<td>
						${message("StockLog.Type." + stockLog.typeName)}
					</td>
					<td>
						${stockLog.inQuantity}
					</td>
					<td>
						${stockLog.outQuantity}
					</td>
					<td>
						${stockLog.stock}
					</td>
					<td>
						${stockLog.operator}
					</td>
					<td>
						[#if stockLog.memo??]
							<span title="${stockLog.memo}">${abbreviate(stockLog.memo, 50, "...")}</span>
						[/#if]
					</td>
					<td>
						<span title="${stockLog.createDate?string("yyyy-MM-dd HH:mm:ss")}">${stockLog.createDate}</span>
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPage]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>
[/#escape]