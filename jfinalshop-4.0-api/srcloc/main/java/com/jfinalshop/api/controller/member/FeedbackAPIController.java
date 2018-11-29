package com.jfinalshop.api.controller.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Feedback;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 意见反馈
 * 
 */
@ControllerBind(controllerKey = "/api/member/feedback")
@Before(TokenInterceptor.class)
public class FeedbackAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(FeedbackAPIController.class);

	@Inject
	private FeedbackService feedbackService;
	
	/**
	 * 意见反馈
	 */
	public void index() {
		String suggestion = getPara("suggestion");
		String mobile = getPara("mobile");
		
		if (StrKit.isBlank(suggestion)) {
			renderArgumentError("请输入您的反馈意见，我们将努力改进!");
			return;
		}
		
		Member member = getMember();
		if (member == null) {
			renderArgumentError("当前用户不能为空!");
			return;
		}
		Feedback feedback = new Feedback();
		feedback.setSuggestion(suggestion);
		feedback.setMobile(mobile);
		feedback.setMemberId(member.getId());
		feedbackService.save(feedback);
		renderJson(new DatumResponse("反馈成功!"));
	}

}
