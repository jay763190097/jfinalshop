package com.jfinalshop.controller;

import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.util.SystemUtils;

import java.math.BigDecimal;


public class BaseController extends Controller {

    /** 错误视图 */
    protected static final String ERROR_VIEW = "/common/error.jhtml";

    /** 错误消息 */
    protected static final Message ERROR_MESSAGE = Message.error("shop.message.error");

    /** 成功消息 */
    protected static final Message SUCCESS_MESSAGE = Message.success("shop.message.success");

    /** "验证结果"属性名称 */
    //private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

    /**
     * 货币格式化
     *
     * @param amount
     *            金额
     * @param showSign
     *            显示标志
     * @param showUnit
     *            显示单位
     * @return 货币格式化
     */
    protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
        Setting setting = SystemUtils.getSetting();
        String price = setting.setScale(amount).toString();
        if (showSign) {
            price = setting.getCurrencySign() + price;
        }
        if (showUnit) {
            price += setting.getCurrencyUnit();
        }
        return price;
    }

    /**
     * 获取国际化消息
     *
     * @param code
     *            代码
     * @param args
     *            参数
     * @return 国际化消息
     */
    protected String message(String code, Object... args) {
        Res resUtil = I18n.use();
        return resUtil.format(code, args);
    }

    /**
     * 添加瞬时消息
     *
     * @param
     *
     * @param message
     *            消息
     */
    protected void addFlashMessage(Message message) {
        if (message != null) {
            setSessionAttr(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
        }
    }

}
