package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.Feedback;

/**
 * Service - 意见反馈
 * 
 * 
 */
@Singleton
public class FeedbackService extends BaseService<Feedback> {

	/**
	 * 构造方法
	 */
	public FeedbackService() {
		super(Feedback.class);
	}
	
	
}
