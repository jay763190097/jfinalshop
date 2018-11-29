package com.jfinalshop.controller.shop;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.codec.binary.Base64;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 共用
 * 
 * 
 */
@ControllerBind(controllerKey = "/common")
@Before(ThemeInterceptor.class)
public class CommonController extends BaseController {

	@Inject
	private RSAService rsaService;
	@Inject
	private AreaService areaService;

	/**
	 * 网站关闭
	 */
	public void siteClose() {
		Setting setting = SystemUtils.getSetting();
		if (setting.getIsSiteEnabled()) {
			redirect("/");
		} else {
			redirect("/shop/${theme}/common/site_close.ftl");
		}
	}

	/**
	 * 公钥
	 */
	public void publicKey() {
		RSAPublicKey publicKey = rsaService.generateKey(getRequest());
		Map<String, String> data = new HashMap<String, String>();
		data.put("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		data.put("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		renderJson(data);
	}

	/**
	 * 地区
	 */
	public void area() {
		Long parentId = getParaToLong("parentId");
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Area parent = areaService.find(parentId);
		Collection<Area> areas = parent != null ? parent.getChildren() : areaService.findRoots();
		for (Area area : areas) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", area.getName());
			item.put("value", area.getId());
			data.add(item);
		}
		renderJson(data);
	}


	/**
	 * 错误提示
	 */
	public void error() {
		render("/shop/${theme}/common/error.ftl");
	}

	/**
	 * 资源不存在
	 */
	public void resourceNotFound() {
		render("/shop/${theme}/common/resource_not_found.ftl");
	}

}