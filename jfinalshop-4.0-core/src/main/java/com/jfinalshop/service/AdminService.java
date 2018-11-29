package com.jfinalshop.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.AdminDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.AdminRole;
import com.jfinalshop.model.Role;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 管理员
 * 
 * 
 */
@Singleton
public class AdminService extends BaseService<Admin> {

	/**
	 * 构造方法
	 */
	public AdminService() {
		super(Admin.class);
	}
	
	@Inject
	private AdminDao adminDao;
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		return adminDao.usernameExists(username);
	}

	/**
	 * 根据用户名查找管理员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	public Admin findByUsername(String username) {
		return adminDao.findByUsername(username);
	}


	/**
	 * 判断管理员是否登录
	 * 
	 * @return 管理员是否登录
	 */
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			return subject.isAuthenticated();
		}
		return false;
	}

	/**
	 * 获取当前登录管理员
	 * 
	 * @return 当前登录管理员，若不存在则返回null
	 */
	public Admin getCurrent() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Admin principal = SubjectKit.getAdmin();
			if (principal != null) {
				return adminDao.find(principal.getId());
			}
		}
		return null;
	}

	/**
	 * 获取当前登录用户名
	 * 
	 * @return 当前登录用户名，若不存在则返回null
	 */
	public String getCurrentUsername() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Admin principal = SubjectKit.getAdmin();
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	/**
	 * 获取登录令牌
	 * 
	 * @return 登录令牌
	 */
	public String getLoginToken() {
		return DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30));
	}

	/**
	 * 登陆验证
	 * @param loginName
	 * @param password
	 * @param rememberMe
	 * @param captchaToken
	 * @return
	 */
	public Message login(String username, String password, boolean isRememberUsername, String captcha, HttpServletRequest request) {
		Message failureMessage = null;
//		if (!SubjectKit.doCaptcha("captcha", captcha)) {
//			return failureMessage = Message.error("admin.captcha.invalid");
//		}
		Admin admin = adminDao.findByUsername(username);
		if (admin == null) {
			return failureMessage = Message.error("admin.login.unknownAccount");
		}
		if (!admin.getIsEnabled()) {
			return failureMessage = Message.error("admin.login.disabledAccount");
		}
		
		Setting setting = SystemUtils.getSetting();
		if (admin.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.admin)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					return failureMessage = Message.error("admin.login.accountLockCount", setting.getAccountLockCount());
				}
				Date lockedDate = admin.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					admin.setLoginFailureCount(0);
					admin.setIsLocked(false);
					admin.setLockedDate(null);
					admin.update();
				} else {
					return failureMessage = Message.error("admin.login.lockedAccount");
				}
			} else {
				admin.setLoginFailureCount(0);
				admin.setIsLocked(false);
				admin.setLockedDate(null);
				admin.update();
			}
		}
		if (!SubjectKit.login(username, password, isRememberUsername)) {
			int loginFailureCount = admin.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				admin.setIsLocked(true);
				admin.setLockedDate(new Date());
			}
			admin.setLoginFailureCount(loginFailureCount);
			admin.update();
			return failureMessage = Message.error("admin.login.incorrectCredentials");
		}
		admin.setLoginIp(request.getRemoteHost());
		admin.setLoginDate(new Date());
		admin.setLoginFailureCount(0);
		admin.update();
		return failureMessage;
	}
	
	@Before(Tx.class)
	public Admin save(Admin admin) {
		super.save(admin);
		List<Role> roles = admin.getRoles();
		if (CollectionUtils.isNotEmpty(roles)) {
			for (Role role : roles) {
				AdminRole adminRole = new AdminRole();
				adminRole.setAdmins(admin.getId());
				adminRole.setRoles(role.getId());
				adminRole.save();
			}
		}
		return admin;
	}
	
	@Before(Tx.class)
	public Admin update(Admin admin) {
		super.update(admin);
		List<Role> roles = admin.getRoles();
		if (CollectionUtils.isNotEmpty(roles)) {
			AdminRole.dao.delete(admin.getId());
			for (Role role : roles) {
				AdminRole adminRole = new AdminRole();
				adminRole.setAdmins(admin.getId());
				adminRole.setRoles(role.getId());
				adminRole.save();
			}
		}
		return admin;
	}
	
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Before(Tx.class)
	public void delete(Long... ids) {
		if (ids != null) {
			for (Long id : ids) {
				AdminRole.dao.delete(id);
			}
		}
		super.delete(ids);
	}
	
	public void delete(Admin admin) {
		super.delete(admin);
	}
	
}