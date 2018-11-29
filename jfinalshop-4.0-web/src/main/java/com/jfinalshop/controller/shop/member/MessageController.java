package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;

/**
 * Controller - 会员中心 - 消息
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/message")
@Before(MemberInterceptor.class)
public class MessageController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MessageService messageService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查用户名是否合法
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		renderJson(!StringUtils.equalsIgnoreCase(username, memberService.getCurrentUsername()) && memberService.usernameExists(username));
	}

	/**
	 * 发送
	 */
	public void send() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && memberService.getCurrent().equals(draftMessage.getSender())) {
			setAttr("draftMessage", draftMessage);
		}
		render("/shop/${theme}/member/message/send.ftl");
	}

	/**
	 * 发送
	 */
	public void sendSubmit() {
		Long draftMessageId = getParaToLong("draftMessageId");
		String username = getPara("username");
		String title = getPara("title");
		String content = getPara("content");
		Boolean isDraft = getParaToBoolean("isDraft", false);

		Member member = memberService.getCurrent();
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && member.equals(draftMessage.getSender())) {
			messageService.delete(draftMessage);
		}
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (member.equals(receiver)) {
				redirect(ERROR_VIEW);
				return;
			}
		}
		Message message = new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(isDraft);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(member.getId());
		message.setReceiverId(StrKit.notNull(receiver) ? receiver.getId() : null);
		message.setForMessage(null);
		message.setReplyMessages(null);
		messageService.save(message);
		if (isDraft) {
			addFlashMessage(com.jfinalshop.Message.success("shop.member.message.saveDraftSuccess"));
			redirect("/shop/${theme}/member/message/draft.jhtml");
		} else {
			addFlashMessage(com.jfinalshop.Message.success("shop.member.message.sendSuccess"));
			redirect("/shop/${theme}/member/message/list.jhtml");
		}
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Message message = messageService.find(id);
		if (message == null || message.getIsDraft() || message.getForMessage() != null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if ((!member.equals(message.getSender()) && !member.equals(message.getReceiver())) || (member.equals(message.getReceiver()) && message.getReceiverDelete()) || (member.equals(message.getSender()) && message.getSenderDelete())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (member.equals(message.getReceiver())) {
			message.setReceiverRead(true);
		} else {
			message.setSenderRead(true);
		}
		messageService.update(message);
		setAttr("memberMessage", message);
		setAttr("member", member);
		render("/shop/${theme}/member/message/view.ftl");
	}

	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("id");
		String content = getPara("content");

		Message forMessage = messageService.find(id);
		if (forMessage == null || forMessage.getIsDraft() || forMessage.getForMessage() != null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if ((!member.equals(forMessage.getSender()) && !member.equals(forMessage.getReceiver())) || (member.equals(forMessage.getReceiver()) && forMessage.getReceiverDelete()) || (member.equals(forMessage.getSender()) && forMessage.getSenderDelete())) {
			redirect(ERROR_VIEW);
			return;
		}
		Message message = new Message();
		message.setTitle("reply: " + forMessage.getTitle());
		message.setContent(content);
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(member.getId());
		
		Long sMember = StrKit.notNull(forMessage.getSender()) ? forMessage.getSender().getId() : null;
		Long rMember = StrKit.notNull(forMessage.getReceiver()) ? forMessage.getReceiver().getId() : null;
		message.setReceiverId(member.getId() == forMessage.getReceiver().getId() ? sMember : rMember);
		message.setForMessage(null);
		message.setReplyMessages(null);
		if ((member.equals(forMessage.getReceiver()) && !forMessage.getSenderDelete()) || (member.equals(forMessage.getSender()) && !forMessage.getReceiverDelete())) {
			message.setForMessage(forMessage);
		}
		messageService.save(message);

		if (member.equals(forMessage.getSender())) {
			forMessage.setSenderRead(true);
			forMessage.setReceiverRead(false);
		} else {
			forMessage.setSenderRead(false);
			forMessage.setReceiverRead(true);
		}
		messageService.update(forMessage);

		if ((member.getId() == forMessage.getReceiver().getId()) && !forMessage.getSenderDelete() || (member.getId() == forMessage.getSender().getId()) && !forMessage.getReceiverDelete()) {
			addFlashMessage(SUCCESS_MESSAGE);
			redirect("/shop/${theme}/member/message/view.jhtml?id=" + forMessage.getId());
		} else {
			addFlashMessage(com.jfinalshop.Message.success("shop.member.message.replySuccess"));
			redirect("/shop/${theme}/member/message/list.jhtml");
		}
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Member member = memberService.getCurrent();
		setAttr("page", messageService.findPage(member, pageable, null));
		setAttr("member", member);
		render("/shop/${theme}/member/message/list.ftl");
	}

	/**
	 * 草稿箱
	 */
	public void draft() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Member member = memberService.getCurrent();
		setAttr("page", messageService.findDraftPage(member, pageable));
		setAttr("member", member);
		render("/shop/${theme}/member/message/draft.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Member member = memberService.getCurrent();
		messageService.delete(id, member);
		renderJson(SUCCESS_MESSAGE);
	}

}