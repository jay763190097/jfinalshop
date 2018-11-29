package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.io.FilenameUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.Theme;
import com.jfinalshop.service.CacheService;
import com.jfinalshop.service.ThemeService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 主题
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/theme")
public class ThemeController extends BaseController {

	@Inject
	private ThemeService themeService;
	@Inject
	private CacheService cacheService;

	/**
	 * 设置
	 */
	public void setting() {
		setAttr("themes", themeService.getAll());
		render("/admin/theme/setting.ftl");
	}

	/**
	 * 设置
	 */
	public void save() {
		UploadFile themeFile = getFile("themeFile");
		String id = getPara("id");
		
		if (themeFile != null) {
			if (!FilenameUtils.isExtension(themeFile.getOriginalFileName(), "zip")) {
				addFlashMessage(Message.error("admin.upload.invalid"));
				redirect("setting.jhtml");
			}
			if (!themeService.upload(themeFile)) {
				addFlashMessage(Message.error("admin.theme.uploadInvalid"));
				redirect("setting.jhtml");
			}
		}
		Theme theme = themeService.get(id);
		if (theme == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		setting.setTheme(theme.getName());
		SystemUtils.setSetting(setting);
		cacheService.clear();
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/theme/setting.jhtml");
	}

}