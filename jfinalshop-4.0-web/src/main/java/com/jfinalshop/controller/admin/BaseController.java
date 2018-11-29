package com.jfinalshop.controller.admin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.AdminInterceptor;
import com.jfinalshop.model.Log;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 基类
 * 
 * 
 */
@Before(AdminInterceptor.class)
public class BaseController extends Controller {

	/** Logger */
	//private final Logger logger = LoggerFactory.getLogger(getClass());

	/** 错误视图 */
	protected static final String ERROR_VIEW = "/admin/common/error.jhtml";

	/** 错误消息 */
	protected static final Message ERROR_MESSAGE = Message.error("admin.message.error");

	/** 成功消息 */
	protected static final Message SUCCESS_MESSAGE = Message.success("admin.message.success");

	/** "验证结果"属性名称 */
	//private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

	
	/**
	 * 获取前端传来的数组对象并响应成Bean列表
	 */
	public <T> List<T> getBeans(Class<T> beanClass, String beanName) {
		List<String> indexes = getIndexes(beanName);
		List<T> list = new ArrayList<T>();
		for (String index : indexes) {
			T b = getBean(beanClass, beanName + "[" + index + "]");
			if (b != null) {
				list.add(b);
			}
		}
		return list;
	}
	
	/**
	 * 获取对象数组
	 * @param modelClass
	 * @return
	 */
	public <T> List<T> getModels(Class<T> modelClass) {
		return getModels(modelClass, StrKit.firstCharToLowerCase(modelClass.getSimpleName()));
	}

	/**
	 * 获取前端传来的数组对象并响应成Model列表
	 */
	public <T> List<T> getModels(Class<T> modelClass, String modelName) {
		List<String> indexes = getIndexes(modelName);
		List<T> list = new ArrayList<T>();
		for (String index : indexes) {
			T m = getModel(modelClass, modelName + "[" + index + "]");
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 提取model对象数组的标号
	 */
	private List<String> getIndexes(String modelName) {
		// 提取标号
		List<String> list = new ArrayList<String>();
		String modelNameAndLeft = modelName + "[";
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndLeft)) {
				String subKey = paraKey.substring(0, modelNameAndLeft.length() + 3);
				String no = paraKey.substring(paraKey.lastIndexOf("[") + 1, subKey.lastIndexOf("]"));
				//String no = paraKey.substring(paraKey.lastIndexOf("[") + 1, paraKey.lastIndexOf("]"));
				
				if (!list.contains(no)) {
					list.add(no);
				}
			}
		}
		return list;
	}
	
	
    /**
     * 对List对象按照某个成员变量进行排序
     * @param list       List对象
     * @param sortField  排序的属性名称
     * @param sortMode   排序方式：ASC，DESC 任选其一
     */
    public static <T> void sortList(List<T> list, final String sortField, final String sortMode) {
        if(list == null || list.size() < 2) {
            return;
        }
        Collections.sort(list, new Comparator<T>() {
        	
            public int compare(T o1, T o2) {
                try {
                    @SuppressWarnings("rawtypes")
					Class clazz = o1.getClass();
                    Field field = clazz.getDeclaredField(sortField); //获取成员变量
                    field.setAccessible(true); //设置成可访问状态
                    String typeName = field.getType().getName().toLowerCase(); //转换成小写

                    Object v1 = field.get(o1); //获取field的值
                    Object v2 = field.get(o2); //获取field的值

                    boolean ASC_order = (sortMode == null || "ASC".equalsIgnoreCase(sortMode));

                    //判断字段数据类型，并比较大小
                    if(typeName.endsWith("string")) {
                        String value1 = v1.toString();
                        String value2 = v2.toString();
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("short")) {
                        Short value1 = Short.parseShort(v1.toString());
                        Short value2 = Short.parseShort(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("byte")) {
                        Byte value1 = Byte.parseByte(v1.toString());
                        Byte value2 = Byte.parseByte(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("char")) {
                        Integer value1 = (int)(v1.toString().charAt(0));
                        Integer value2 = (int)(v2.toString().charAt(0));
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("int") || typeName.endsWith("integer")) {
                        Integer value1 = Integer.parseInt(v1.toString());
                        Integer value2 = Integer.parseInt(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("long")) {
                        Long value1 = Long.parseLong(v1.toString());
                        Long value2 = Long.parseLong(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("float")) {
                        Float value1 = Float.parseFloat(v1.toString());
                        Float value2 = Float.parseFloat(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("double")) {
                        Double value1 = Double.parseDouble(v1.toString());
                        Double value2 = Double.parseDouble(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("boolean")) {
                        Boolean value1 = Boolean.parseBoolean(v1.toString());
                        Boolean value2 = Boolean.parseBoolean(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("date")) {
                        Date value1 = (Date)(v1);
                        Date value2 = (Date)(v2);
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else if(typeName.endsWith("timestamp")) {
                        Timestamp value1 = (Timestamp)(v1);
                        Timestamp value2 = (Timestamp)(v2);
                        return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);
                    }
                    else {
                        //调用对象的compareTo()方法比较大小
                        Method method = field.getType().getDeclaredMethod("compareTo", new Class[]{field.getType()});
                        method.setAccessible(true); //设置可访问权限
                        int result  = (Integer)method.invoke(v1, new Object[]{v2});
                        return ASC_order ? result : result*(-1);
                    }
                }
                catch (Exception e) {
                    String err = e.getLocalizedMessage();
                    System.out.println(err);
                    e.printStackTrace();
                }

                return 0; //未知类型，无法比较大小
            }
        });
    }
	
	

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
	 * @param redirectAttributes
	 *            RedirectAttributes
	 * @param message
	 *            消息
	 */
	protected void addFlashMessage(Message message) {
		if (message != null) {
			setSessionAttr(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
		}
	}

	/**
	 * 添加日志
	 * 
	 * @param content
	 *            内容
	 */
	protected void addLog(String content) {
		if (content != null) {
			setAttr(Log.LOG_CONTENT_ATTRIBUTE_NAME, content);
		}
	}

}