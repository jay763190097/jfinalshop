package com.jfinalshop.api.common.sms;
import com.jfinalshop.shiro.session.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SMSManager {
	private static Logger logger = LoggerFactory.getLogger(SMSManager.class);
	private static SMSManager sm = new SMSManager();

	private int	expirationTime	= 60 * 10;	 // 超时时间，秒
	private int	expirationTime1 = 60 * 60 * 24; //
	private RedisManager redisManager = new RedisManager();
	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static SMSManager getMe() {
		return sm;
	}
	/**
	 * 验证smsCode
	 *
	 * @return
	 */
	public String validate(String ID,String code) {
		String smscode = redisManager.get(ID);
		if(smscode==null){
			return "0";
		}else if(!smscode.equals(code)){
			return "2";
		}
		return "1";
	}

	/**
	 * 生成smsCode值并存入redis缓存之中
	 *
	 * @return
	 */
	public String generateSMS(String username,String SMSCODE) {
		String ID = username + "code";
		logger.info("redis存入的短信验证码"+SMSCODE);
		redisManager.set(ID, SMSCODE, expirationTime);
		return SMSCODE;
	}
	
	/**
	 * 将token存入redis缓存之中
	 *
	 * @return
	 */
	public String generateToken(String sn,String token) {
		String ID = sn + "code";
		logger.info("redis存入的token"+token);
		redisManager.set(ID, token, expirationTime1);
		return token;
	}
	
	/**
	 * 注销smsCode
	 */
	public void remove(String ID){
		redisManager.del(ID);
	}
	
}
