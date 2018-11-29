package com.jfinalshop.api.controller.member;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.service.MemberService;
import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 我的
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/member")
@Before(TokenInterceptor.class)
public class MemberAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(MemberAPIController.class);

	@Inject
	private GoodsService goodsService;
	@Inject
	private MemberService memberService;
	
	/**
	 * 我常买
	 */
	public void buy() {
		Member member = getMember();
		 List<Goods> goods = goodsService.findMemberBuyList(member, 10);
		 List<Goods> goodsList = new ArrayList<Goods>();
		 for(int i = 0; i < goods.size(); i++) {
			Goods pGoods = new Goods();
			pGoods.setId(goods.get(i).getDefaultProduct().getId());
			pGoods.setName(goods.get(i).getName());
			pGoods.setImage(goods.get(i).getImage());
			pGoods.setPrice(new BigDecimal(currency(goods.get(i).getPrice(), false, false)));
			pGoods.setUnit(goods.get(i).getUnit());
			pGoods.setWeight(goods.get(i).getWeight());
			pGoods.setBrandId(goods.get(i).getBrandId());
			pGoods.put("sales", goods.get(i).get("sales"));
			pGoods.put("brand", goods.get(i).getBrand() != null ? goods.get(i).getBrand().getName() : "");
			goodsList.add(pGoods);
		 }
		 renderJson(new DataResponse(goodsList));
	}

	public void update(){

		Member member  = getModel(Member.class);
		if (member.getUsername() != null){

		}

		Member pmember = memberService.find(member.getId());
		if(pmember == null){
			renderArgumentError("没有找到该用户");
		}
		memberService.update(member);
		renderJson(new BaseResponse(Code.SUCCESS, "更新成功!"));

	}




}
