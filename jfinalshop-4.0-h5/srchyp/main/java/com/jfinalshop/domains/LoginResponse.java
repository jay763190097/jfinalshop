package com.jfinalshop.domains;

import com.jfinal.plugin.activerecord.Model;

public class LoginResponse {

    private  String cartkey;

    public String getCartkey() {
        return cartkey;
    }

    public void setCartkey(String cartkey) {
        this.cartkey = cartkey;
    }

    private Integer	code;
    private String	message;
    private String imageUrl;
    private String siteName;

    private LoginInfo info;

    private String				token;

    public LoginResponse() {

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginInfo getInfo() {
        return info;
    }

    public void setInfo(LoginInfo info) {
        this.info = info;
    }
}
