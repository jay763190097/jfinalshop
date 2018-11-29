package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.service.FileService;

/**
 * Controller - 文件
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/file")
public class FileController extends BaseController {

	@Inject
	private FileService fileService;

	/**
	 * 上传
	 */
	public void upload() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		
		Map<String, Object> data = new HashMap<String, Object>();
		if (fileType == null || file == null || file.getFile().length() <= 0) {
			data.put("message", ERROR_MESSAGE);
			data.put("state", message("admin.message.error"));
			renderJson(data);
			return;
		}
		if (!fileService.isValid(fileType, file)) {
			data.put("message", Message.warn("admin.upload.invalid"));
			data.put("state", message("admin.upload.invalid"));
			renderJson(data);
			return;
		}
		String url = fileService.upload(fileType, file, false);
		if (StringUtils.isEmpty(url)) {
			data.put("message", Message.warn("admin.upload.error"));
			data.put("state", message("admin.upload.error"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("state", "SUCCESS");
		data.put("url", url);
		renderJson(data);
	}

}