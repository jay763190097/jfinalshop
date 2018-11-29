package com.jfinalshop.controller.wap;

import com.jfinal.core.Controller;


public class BaseController extends Controller {

	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String SUCCESS = "1";
	public static final String ERROR = "0";
	
	/**
	 * 将String数组转换为Long类型数组
	 * @param strs
	 * @return
	 */
	public static Long[] convertToLong(String[] strs) {
		Long[] longs = new Long[strs.length];
		for (int i = 0; i < strs.length; i++) {
			try {
				String str = strs[i];
				Long thelong = Long.parseLong(str);
				longs[i] = thelong;
			} catch (NumberFormatException e) {
			}
		}
		return longs;
	}
	
}
