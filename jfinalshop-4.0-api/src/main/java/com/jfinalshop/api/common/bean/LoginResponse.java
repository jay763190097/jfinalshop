package com.jfinalshop.api.common.bean;

import com.jfinal.plugin.activerecord.Model;

public class LoginResponse extends BaseResponse {
	
	private Model<?>	info;
	
	private String				token;
	
	private String              cartkey;

	public LoginResponse() {
		super();
	}

	public LoginResponse(Integer code) {
		super(code);
	}

	public LoginResponse(Integer code, String message) {
		super(code, message);
	}

	
	public Model<?> getInfo() {
		return info;
	}

	public void setInfo(Model<?> info) {
		this.info = info;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getCartKey() {
		return cartkey;
	}

	public void setCartKey(String cartkey) {
		this.cartkey = cartkey;
	}

}