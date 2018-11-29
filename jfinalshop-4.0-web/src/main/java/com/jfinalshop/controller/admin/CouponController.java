package com.jfinalshop.controller.admin;

import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;

/**
 * Controller - 优惠券
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/coupon")
public class CouponController extends BaseController {

	@Inject
	private CouponService couponService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private AdminService adminService;

	/**
	 * 检查价格运算表达式是否正确
	 */
	public void checkPriceExpression() {
		String priceExpression = getPara("coupon.price_expression");
		if (StringUtils.isEmpty(priceExpression)) {
			renderJson(false);
			return;
		}
		renderJson(couponService.isValidPriceExpression(priceExpression));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/coupon/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Coupon coupon = getModel(Coupon.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isExchange = getParaToBoolean("isExchange", false);
		coupon.setIsEnabled(isEnabled);
		coupon.setIsExchange(isExchange);
		
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		coupon.setCouponCodes(null);
		coupon.setPromotions(null);
		coupon.setOrders(null);
		couponService.save(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("coupon", couponService.find(id));
		render("/admin/coupon/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Coupon coupon = getModel(Coupon.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isExchange = getParaToBoolean("isExchange", false);
		coupon.setIsEnabled(isEnabled);
		coupon.setIsExchange(isExchange);
		
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			redirect(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			redirect(ERROR_VIEW);
			return;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			redirect(ERROR_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		
		couponService.update(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list.jhtml");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", couponService.findPage(pageable));
		render("/admin/coupon/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		couponService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 生成优惠码
	 */
	public void generate() {
		Long id = getParaToLong("id");
		Coupon coupon = couponService.find(id);
		setAttr("coupon", coupon);
		setAttr("totalCount", couponCodeService.count(coupon, null, null, null, null));
		setAttr("usedCount", couponCodeService.count(coupon, null, null, null, true));
		render("/admin/coupon/generate.ftl");
	}

	/**
	 * 下载优惠码
	 */
	public void download() {
		Long id = getParaToLong("id");
		Integer count = getParaToInt("count");
		if (count == null || count <= 0) {
			count = 100;
		}
		Coupon coupon = couponService.find(id);
		List<CouponCode> data = couponCodeService.generate(coupon, null, count);
		String filename = "coupon_code_" + DateFormatUtils.format(new Date(), "yyyyMMdd") + ".xls";
		String[] contents = new String[4];
		contents[0] = message("admin.coupon.type") + ": " + coupon.getName();
		contents[1] = message("admin.coupon.count") + ": " + count;
		contents[2] = message("admin.coupon.operator") + ": " + adminService.getCurrentUsername();
		contents[3] = message("admin.coupon.date") + ": " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		String[] headers = new String[]{message("admin.coupon.title")};
		String[] columns = new String[]{"code"};
	    render(PoiRender.me(data).fileName(filename).sheetName(filename).headers(headers).columns(columns).cellWidth(9000));
	}

}