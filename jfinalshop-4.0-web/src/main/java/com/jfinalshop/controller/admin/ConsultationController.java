package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.service.ConsultationService;

/**
 * Controller - 咨询
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/consultation")
public class ConsultationController extends BaseController {

	@Inject
	private ConsultationService consultationService;

	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("id");
		setAttr("consultation", consultationService.find(id));
		render("/admin/consultation/reply.ftl");
	}

	/**
	 * 回复
	 */
	public void replySubmit() {
		Long id = getParaToLong("id");
		String content = getPara("content");
		Consultation consultation = consultationService.find(id);
		if (consultation == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Consultation replyConsultation = new Consultation();
		replyConsultation.setContent(content);
		replyConsultation.setIp(getRequest().getRemoteAddr());
		consultationService.reply(consultation, replyConsultation);

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("reply.jhtml?id=" + id);
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("consultation", consultationService.find(id));
		render("/admin/consultation/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Long id = getParaToLong("id");
		Boolean isShow = getParaToBoolean("is_show", false);
		Consultation consultation = consultationService.find(id);
		if (consultation == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (isShow != consultation.getIsShow()) {
			consultation.setIsShow(isShow);
			consultationService.update(consultation);
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", consultationService.findPage(null, null, null, pageable));
		setAttr("pageable", pageable);
		render("/admin/consultation/list.ftl");
	}

	/**
	 * 删除回复
	 */
	public void deleteReply() {
		Long id = getParaToLong("id");
		Consultation consultation = consultationService.find(id);
		if (consultation == null || consultation.getForConsultation() == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		consultationService.delete(consultation);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			consultationService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}