[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.articleCategory.edit")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"articleCategory.name": "required",
			"articleCategory.orders": "digits"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.articleCategory.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="articleCategory.id" value="${articleCategory.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ArticleCategory.name")}:
				</th>
				<td>
					<input type="text" id="name" name="articleCategory.name" class="text" value="${articleCategory.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ArticleCategory.parent")}:
				</th>
				<td>
					<select name="parentId">
						<option value="">${message("admin.articleCategory.root")}</option>
						[#list articleCategoryTree as category]
							[#if category != articleCategory && !children?seq_contains(category)]
								<option value="${category.id}"[#if category == articleCategory.parent] selected="selected"[/#if]>
									[#if category.grade != 0]
										[#list 1..category.grade as i]
											&nbsp;&nbsp;
										[/#list]
									[/#if]
									${category.name}
								</option>
							[/#if]
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					${message("ArticleCategory.seoTitle")}:
				</th>
				<td>
					<input type="text" name="articleCategory.seo_title" class="text" value="${articleCategory.seo_title}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ArticleCategory.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="articleCategory.seo_keywords" class="text" value="${articleCategory.seo_keywords}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ArticleCategory.seoDescription")}:
				</th>
				<td>
					<input type="text" name="articleCategory.seo_description" class="text" value="${articleCategory.seo_description}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="articleCategory.orders" class="text" value="${articleCategory.orders}" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
[/#escape]