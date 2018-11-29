package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 评论
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/review")
public class ReviewController extends BaseController {

	@Inject
	private ReviewService reviewService;

	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("id");
		setAttr("review", reviewService.find(id));
		render("/admin/review/reply.ftl");
	}

	/**
	 * 回复
	 */
	public void replySubmit() {
		Long id = getParaToLong("id");
		String content = getPara("content");
		Review review = reviewService.find(id);
		if (review == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Review replyReview = new Review();
		replyReview.setContent(content);
		replyReview.setIp(getRequest().getRemoteAddr());
		reviewService.reply(review, replyReview);

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/review/reply.jhtml?id=" + id);
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("review", reviewService.find(id));
		render("/admin/review/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Long id = getParaToLong("id");
		Boolean isShow = getParaToBoolean("isShow", false);
		Review review = reviewService.find(id);
		if (review == null) {
			redirect(ERROR_VIEW);
			return;
		}
		review.setIsShow(isShow);
		reviewService.update(review);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/review/list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		String typeName = getPara("type");
		Review.Type type = StrKit.notBlank(typeName) ? Review.Type.valueOf(typeName) : null;
		
		Pageable pageable = getBean(Pageable.class);
		
		setAttr("pageable", pageable);
		setAttr("type", type);
		setAttr("types", Review.Type.values());
		setAttr("page", reviewService.findPage(null, null, type, null, pageable));
		render("/admin/review/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		reviewService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}