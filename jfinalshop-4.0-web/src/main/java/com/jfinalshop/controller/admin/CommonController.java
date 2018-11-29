package com.jfinalshop.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.captcha.CaptchaRender;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Order;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;

/**
 * Controller - 共用
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/common")
public class CommonController extends BaseController {

	@InjectSettings("${system.name}")
	private String systemName;
	@InjectSettings("${system.version}")
	private String systemVersion;
	@InjectSettings("${system.description}")
	private String systemDescription;
	
	@Inject
	private AreaService areaService;
	@Inject
	private OrderService orderService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private MemberService memberService;
	@Inject
	private MessageService messageService;

	/** ServletContext */
	private ServletContext servletContext = JFinal.me().getServletContext();


	/**
	 * 主页
	 */
	public void main() {
		setAttr("unreadMessageCount", messageService.count(null, false));
		setAttr("pendingReviewOrderCount", orderService.count(null, Order.Status.pendingReview, null, null, null, null, null, null, null, null));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, null, null, null, null, null, null, null, null));
		setAttr("pendingReceiveOrderCount", orderService.count(null, null, null, null, true, null, null, null, null, null));
		setAttr("pendingRefundsOrderCount", orderService.count(null, null, null, null, null, true, null, null, null, null));
		render("/admin/common/main.ftl");
	}

	/**
	 * 首页
	 */
	public void index() {
		setAttr("systemName", systemName);
		setAttr("systemVersion", systemVersion);
		setAttr("systemDescription", systemDescription);
		setAttr("javaVersion", System.getProperty("java.version"));
		setAttr("javaHome", System.getProperty("java.home"));
		setAttr("osName", System.getProperty("os.name"));
		setAttr("osArch", System.getProperty("os.arch"));
		setAttr("serverInfo", servletContext.getServerInfo());
		setAttr("servletVersion", servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
		setAttr("pendingReviewOrderCount", orderService.count(null, Order.Status.pendingReview, null, null, null, null, null, null, null, null));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, null, null, null, null, null, null, null, null));
		setAttr("pendingReceiveOrderCount", orderService.count(null, null, null, null, true, null, null, null, null, null));
		setAttr("pendingRefundsOrderCount", orderService.count(null, null, null, null, null, true, null, null, null, null));
		setAttr("marketableProductCount", goodsService.count(null, null, true, null, null, null, null));
		setAttr("notMarketableProductCount", goodsService.count(null, null, false, null, null, null, null));
		setAttr("stockAlertProductCount", goodsService.count(null, null, null, null, null, null, true));
		setAttr("outOfStockProductCount", goodsService.count(null, null, null, null, null, true, null));
		setAttr("memberCount", memberService.count());
		render("/admin/common/index.ftl");
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
	 * 验证码
	 */
	@Clear
	public void captcha() {
		int width = 0, height = 0, minnum = 0, maxnum = 0, fontsize = 0;
		CaptchaRender captcha = new CaptchaRender();
		if (isParaExists("width")) {
			width = getParaToInt("width");
		}
		if (isParaExists("height")) {
			height = getParaToInt("height");
		}
		if (width > 0 && height > 0)
			captcha.setImgSize(width, height);
		if (isParaExists("minnum")) {
			minnum = getParaToInt("minnum");
		}
		if (isParaExists("maxnum")) {
			maxnum = getParaToInt("maxnum");
		}
		if (minnum > 0 && maxnum > 0)
			captcha.setFontNum(minnum, maxnum);
		if (isParaExists("fontsize")) {
			fontsize = getParaToInt("fontsize");
		}
		if (fontsize > 0)
			captcha.setFontSize(fontsize, fontsize);
		// 干扰线数量 默认0
		captcha.setLineNum(2);
		// 噪点数量 默认50
		captcha.setArtifactNum(30);
		// 使用字符 去掉0和o 避免难以确认
		captcha.setCode("123456789");
		 //验证码在session里的名字 默认 captcha,创建时间为：名字_time
		// captcha.setCaptchaName("captcha");
	    //验证码颜色 默认黑色
		// captcha.setDrawColor(new Color(255,0,0));
	    //背景干扰物颜色  默认灰
		// captcha.setDrawBgColor(new Color(0,0,0));
	    //背景色+透明度 前三位数字是rgb色，第四个数字是透明度  默认透明
		// captcha.setBgColor(new Color(225, 225, 0, 100));
	    //滤镜特效 默认随机特效 //曲面Curves //大理石纹Marble //弯折Double //颤动Wobble //扩散Diffuse
		captcha.setFilter(CaptchaRender.FilterFactory.Curves);
		// 随机色 默认黑验证码 灰背景元素
		captcha.setRandomColor(true);
		render(captcha);
	}

	/**
	 * 错误提示
	 */
	public void error() {
		setAttr("errorMessage", getSessionAttr("errorMessage"));
		render("/admin/common/error.ftl");
	}

	/**
	 * 权限错误
	 */
	public void unauthorized() throws IOException {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		String requestType = request.getHeader("X-Requested-With");
		if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
			response.addHeader("loginStatus", "unauthorized");
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			renderNull();
			return;
		}
		render("/admin/common/unauthorized.ftl");
	}

}