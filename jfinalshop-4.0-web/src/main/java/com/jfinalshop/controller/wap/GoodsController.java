package com.jfinalshop.controller.wap;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.entity.GoodsVO;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.service.SearchService;


/**
 * Controller - 货品
 * 
 *
 */
@ControllerBind(controllerKey = "/wap/goods")
public class GoodsController extends BaseController {
	
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ConsultationService consultationService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private MemberService memberService;
	@Inject
	private SearchService searchService;
	@Inject
	private ReviewService reviewService;
	
	/**
	 * 列表
	 */
	public void list() {
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}
		
		String orderTypeName = getPara("orderType");
		Goods.OrderType orderType = StrKit.notBlank(orderTypeName) ? Goods.OrderType.valueOf(orderTypeName) : null;
		
		String startPriceStr = getPara("startPrice", null);
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		
		String endPriceStr = getPara("endPrice", null);
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("title" , productCategory.getName());
		setAttr("productCategory", productCategory);
		setAttr("orderType", orderType == null ? "all" : orderType);
		//setAttr("pages", goodsService.findPage(null, productCategory, null, null, null, null, startPrice, endPrice, true, true, null, null, null, null, orderType, pageable));
		render("/wap/goods/list.ftl");
	}

	/**
	 * 详情
	 */
	public void detail() {
		Long id = getParaToLong("id");
		Goods goods = goodsService.find(id);
		Pageable pageable = new Pageable(1, 20);
		Boolean favorite = false;
		
		if (goods == null) {
			redirect("/wap/index.html");
			return;
		}
		
		RequestContextHolder.setRequestAttributes(getRequest());
		if (goods.getFavoriteMembers().contains(memberService.getCurrent())) {
			favorite = true;
		}
		Page<Consultation> consultationPages = consultationService.findPage(null, goods, true, pageable);
		Page<Review> reviewPages = reviewService.findPage(null, goods, null, null, pageable);
		setAttr("goods", goods);
		setAttr("favorite", favorite);
		setAttr("consultationPages", consultationPages);
		setAttr("reviewPages", reviewPages);
		setAttr("title" , goods.getName());
		render("/wap/goods/detail.ftl");
	}
	
	/**
	 * 搜索
	 */
	public void search() {
		String keyword = getPara("keyword");
		//com.ld.zxw.page.Page<GoodsVO> goods = searchService.search(keyword, null, null, null, null);
		//setAttr("goodsList", goods == null ? null : goods);
		//setAttr("title" , "搜索到[" + goods.getTotalRow() + "]结果");
		setAttr("keyword", keyword);
		render("/wap/goods/search.ftl");
	}
	
	
}
