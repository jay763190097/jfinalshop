package com.jfinalshop.controller.admin;

import java.security.interfaces.RSAPublicKey;

import net.hasor.core.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.RSAService;

/**
 * Controller - 管理员登录
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/login")
public class LoginController extends Controller {

	private RSAService rsaService = new RSAService();
	@Inject
	private AdminService adminService;

	/**
	 * 登录
	 */
	public void index() {
		String username = getPara("username", "");
		String captcha = getPara("captcha");
		boolean remember = StringUtils.equals(getPara("remember"), "on") ? true : false;
		String password = rsaService.decryptParameter("enPassword", getRequest());
		rsaService.removePrivateKey(getRequest());
		Message failureMessage = null;
		if (StrKit.notBlank(username) && StrKit.notBlank(password)) {
			failureMessage = adminService.login(username, password, remember, captcha, getRequest());
		}
		RSAPublicKey publicKey = rsaService.generateKey(getRequest());
		setAttr("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		setAttr("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		setAttr("failureMessage", failureMessage);
		if (adminService.isAuthenticated()) {
			redirect("/admin/common/main.jhtml");
		} else {
			render("/admin/login/index.ftl");
		}
	}
	
}