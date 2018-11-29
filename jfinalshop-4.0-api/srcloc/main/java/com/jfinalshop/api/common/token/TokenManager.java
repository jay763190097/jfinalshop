package com.jfinalshop.api.common.token;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.api.utils.TokenUtil;
import com.jfinalshop.model.Member;
import com.jfinalshop.shiro.session.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenManager {

	private static TokenManager	me	= new TokenManager();

	private int	expirationTime	= 3600 * 24 * 7;	 // 超时时间，秒
	private RedisManager redisManager = new RedisManager();

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static TokenManager getMe() {
		return me;
	}

	/**
	 * 验证token
	 * 
	 * @param token
	 * @return
	 */
	public Member validate(String token) {
		String memberStr = redisManager.get(token);
		Member member = null;
		if (StringUtils.isNotEmpty(memberStr)) {
			member = TokenSerializationUtils.deserialize(memberStr);
		}
		return member;
	}

	/**
	 * 生成token值
	 * 
	 * @param member
	 * @return
	 */
	public String generateToken(Member member) {
		String token = TokenUtil.generateToken();
		String memberStr = TokenSerializationUtils.serialize(member);
		redisManager.set(token, memberStr, expirationTime);
		return token;
	}
	
	/**
	 * 注销
	 * @param token
	 */
	public void remove(String token){
		redisManager.del(token);
	}
	
}
