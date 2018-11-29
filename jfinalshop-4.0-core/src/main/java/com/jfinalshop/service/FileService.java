package com.jfinalshop.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;

import net.hasor.core.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Setting;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.TemplateException;

/**
 * Service - 文件
 * 
 * 
 */

public class FileService {

	/** ServletContext */
	private ServletContext servletContext = JFinal.me().getServletContext();

	@Inject
	private PluginService pluginService;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(4); 
	
	/**
	 * 添加文件上传任务
	 * 
	 * @param storagePlugin
	 *            存储插件
	 * @param path
	 *            上传路径
	 * @param file
	 *            上传文件
	 * @param contentType
	 *            文件类型
	 */
	private void addUploadTask(final StoragePlugin storagePlugin, final String path, final File file, final String contentType) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				upload(storagePlugin, path, file, contentType);
			}
		});
		executorService.shutdown();
	}
	
	/**
	 * 上传文件
	 * 
	 * @param storagePlugin
	 *            存储插件
	 * @param path
	 *            上传路径
	 * @param file
	 *            上传文件
	 * @param contentType
	 *            文件类型
	 */
	private void upload(StoragePlugin storagePlugin, String path, File file, String contentType) {
		Assert.notNull(storagePlugin);
		Assert.hasText(path);
		Assert.notNull(file);
		Assert.hasText(contentType);

		try {
			storagePlugin.upload(path, file, contentType);
		} finally {
			FileUtils.deleteQuietly(file);
		}
	}
	
	
	/**
	 * 文件验证
	 * 
	 * @param fileType
	 *            文件类型
	 * @param uploadFile
	 *            上传文件
	 * @return 文件验证是否通过
	 */
	public boolean isValid(FileType fileType, UploadFile uploadFile) {
		Assert.notNull(fileType);
		Assert.notNull(uploadFile);

		Setting setting = SystemUtils.getSetting();
		if (setting.getUploadMaxSize() != null && setting.getUploadMaxSize() != 0 && uploadFile.getFile().length() > setting.getUploadMaxSize() * 1024L * 1024L) {
			return false;
		}
		String[] uploadExtensions;
		switch (fileType) {
		case media:
			uploadExtensions = setting.getUploadMediaExtensions();
			break;
		case file:
			uploadExtensions = setting.getUploadFileExtensions();
			break;
		default:
			uploadExtensions = setting.getUploadImageExtensions();
			break;
		}
		if (ArrayUtils.isNotEmpty(uploadExtensions)) {
			return FilenameUtils.isExtension(uploadFile.getOriginalFileName(), uploadExtensions);
		}
		return false;
	}

	/**
	 * 文件上传
	 * 
	 * @param fileType
	 *            文件类型
	 * @param uploadFile
	 *            上传文件
	 * @param async
	 *            是否异步
	 * @return 访问URL
	 */
	public String upload(FileType fileType, UploadFile uploadFile, boolean async) {
		Assert.notNull(fileType);
		Assert.notNull(uploadFile);

		Setting setting = SystemUtils.getSetting();
		String uploadPath;
		switch (fileType) {
		case media:
			uploadPath = setting.getMediaUploadPath();
			break;
		case file:
			uploadPath = setting.getFileUploadPath();
			break;
		default:
			uploadPath = setting.getImageUploadPath();
			break;
		}
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("uuid", UUID.randomUUID().toString());
			String path = FreeMarkerUtils.process(uploadPath, model);
			String destPath = path + UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadFile.getOriginalFileName());
			for (StoragePlugin storagePlugin : pluginService.getStoragePlugins(true)) {
				File tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + ".tmp");
				FileUtils.copyFile(uploadFile.getFile(), tempFile);
				String contentType = uploadFile.getContentType();
				if (async) {
					addUploadTask(storagePlugin, destPath, tempFile, contentType);
				} else {
					upload(storagePlugin, destPath, tempFile, contentType);
				}
				return storagePlugin.getUrl(destPath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 文件上传(异步)
	 * 
	 * @param fileType
	 *            文件类型
	 * @param uploadFile
	 *            上传文件
	 * @return 访问URL
	 */
	public String upload(FileType fileType, UploadFile uploadFile) {
		Assert.notNull(fileType);
		Assert.notNull(uploadFile);

		return upload(fileType, uploadFile, true);
	}

	/**
	 * 文件上传至本地(同步)
	 * 
	 * @param fileType
	 *            文件类型
	 * @param uploadFile
	 *            上传文件
	 * @return 路径
	 */
	public String uploadLocal(FileType fileType, UploadFile uploadFile) {
		Assert.notNull(fileType);
		Assert.notNull(uploadFile);

		Setting setting = SystemUtils.getSetting();
		String uploadPath;
		switch (fileType) {
		case media:
			uploadPath = setting.getMediaUploadPath();
			break;
		case file:
			uploadPath = setting.getFileUploadPath();
			break;
		default:
			uploadPath = setting.getImageUploadPath();
			break;
		}
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("uuid", UUID.randomUUID().toString());
			String path = FreeMarkerUtils.process(uploadPath, model);
			String destPath = path + UUID.randomUUID() + "." + FilenameUtils.getExtension(uploadFile.getOriginalFileName());
			File destFile = new File(servletContext.getRealPath(destPath));
			new File(path).mkdirs();
			FileUtils.copyFile(uploadFile.getFile(), destFile);
			return destPath;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}