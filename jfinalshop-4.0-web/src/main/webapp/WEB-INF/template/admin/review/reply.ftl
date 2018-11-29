[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.review.reply")} - Powered By JFinalShop</title>
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
			content: {
				required: true,
				maxlength: 200
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.review.reply")}
	</div>
	<form id="inputForm" action="replySubmit.jhtml" method="post">
		<input type="hidden" name="id" value="${review.id}" />
		<table class="input">
			<tr>
				<th>
					${message("Review.goods")}:
				</th>
				<td colspan="2">
					<a href="${consultation.goods.url}" target="_blank">${review.goods.name}</a>
				</td>
			</tr>
			<tr>
				<th>
					${message("Review.member")}:
				</th>
				<td colspan="2">
					[#if review.member??]
						<a href="../member/view.jhtml?id=${review.member.id}">${review.member.username}</a>
					[#else]
						${message("admin.review.anonymous")}
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					${message("Review.content")}:
				</th>
				<td colspan="2">
					${review.content}
				</td>
			</tr>
			[#if review.replyReviews?has_content]
				<tr>
					<td colspan="3">
						&nbsp;
					</td>
				</tr>
				[#list review.replyReviews as replyReview]
					<tr>
						<th>
							&nbsp;
						</th>
						<td>
							${replyReview.content}
						</td>
						<td width="80">
							<span title="${review.createDate?string("yyyy-MM-dd HH:mm:ss")}">${review.createDate}</span>
						</td>
					</tr>
				[/#list]
			[/#if]
			<tr>
				<th>
					${message("Review.content")}:
				</th>
				<td colspan="2">
					<textarea name="content" class="text"></textarea>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td colspan="2">
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
[/#escape]