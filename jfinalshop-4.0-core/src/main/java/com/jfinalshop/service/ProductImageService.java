package com.jfinalshop.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.ImageUtils;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.TemplateException;

/**
 * Service - 商品图片
 * 
 * 
 */
@Singleton
public class ProductImageService {

	/** 目标扩展名 */
	private static final String DEST_EXTENSION = "jpg";
	/** 目标文件类型 */
	private static final String DEST_CONTENT_TYPE = "image/jpeg";

	/** ServletContext */
	private ServletContext servletContext = JFinal.me().getServletContext();

	private ExecutorService executorService = Executors.newFixedThreadPool(4);  
	
	@Inject
	private FileService fileService;
	@Inject
	private PluginService pluginService;
	
	/**
	 * 添加图片处理任务
	 * 
	 * @param storagePlugin
	 *            存储插件
	 * @param sourcePath
	 *            原图片上传路径
	 * @param largePath
	 *            图片文件(大)上传路径
	 * @param mediumPath
	 *            图片文件(小)上传路径
	 * @param thumbnailPath
	 *            图片文件(缩略)上传路径
	 * @param tempFile
	 *            原临时文件
	 * @param contentType
	 *            原文件类型
	 */
	private void addTask(final StoragePlugin storagePlugin, final String sourcePath, final String largePath, final String mediumPath, final String thumbnailPath, final File tempFile, final String contentType) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				Setting setting = SystemUtils.getSetting();
				File watermarkFile = new File(servletContext.getRealPath(setting.getWatermarkImage()));
				File largeTempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "." + DEST_EXTENSION);
				File mediumTempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "." + DEST_EXTENSION);
				File thumbnailTempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + "." + DEST_EXTENSION);
				try {
					ImageUtils.zoom(tempFile, largeTempFile, setting.getLargeProductImageWidth(), setting.getLargeProductImageHeight());
					ImageUtils.addWatermark(largeTempFile, largeTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
					ImageUtils.zoom(tempFile, mediumTempFile, setting.getMediumProductImageWidth(), setting.getMediumProductImageHeight());
					ImageUtils.addWatermark(mediumTempFile, mediumTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
					ImageUtils.zoom(tempFile, thumbnailTempFile, setting.getThumbnailProductImageWidth(), setting.getThumbnailProductImageHeight());
					storagePlugin.upload(sourcePath, tempFile, contentType);
					storagePlugin.upload(largePath, largeTempFile, DEST_CONTENT_TYPE);
					storagePlugin.upload(mediumPath, mediumTempFile, DEST_CONTENT_TYPE);
					storagePlugin.upload(thumbnailPath, thumbnailTempFile, DEST_CONTENT_TYPE);
				} finally {
					FileUtils.deleteQuietly(tempFile);
					FileUtils.deleteQuietly(largeTempFile);
					FileUtils.deleteQuietly(mediumTempFile);
					FileUtils.deleteQuietly(thumbnailTempFile);
				}
			}
		});
	}
	
	
	/**
	 * 商品图片过滤
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void filter(List<ProductImage> productImages) {
		CollectionUtils.filter(productImages, new Predicate() {
			public boolean evaluate(Object object) {
				ProductImage productImage = (ProductImage) object;
				return productImage != null && !productImage.isEmpty() && isValid(productImage);
			}
		});
	}
	

	/**
	 * 商品图片文件验证
	 * 
	 * @param productImage
	 *            商品图片
	 * @return 是否验证通过
	 */
	public boolean isValid(ProductImage productImage) {
		Assert.notNull(productImage);

		return productImage.getFile() == null || productImage.getFile().getFile().length() <= 0 || fileService.isValid(FileType.image, productImage.getFile());
	}

	/**
	 * 生成商品图片(异步)
	 * 
	 * @param productImage
	 *            商品图片
	 */
	public void generate(ProductImage productImage) {
		if (productImage == null || productImage.getFile() == null || productImage.getFile().getFile().length() <= 0) {
			return;
		}

		try {
			Setting setting = SystemUtils.getSetting();
			UploadFile uploadFile = productImage.getFile();
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("uuid", UUID.randomUUID().toString());
			String uploadPath = FreeMarkerUtils.process(setting.getImageUploadPath(), model);
			String uuid = UUID.randomUUID().toString();
			String sourcePath = uploadPath + uuid + "-source." + FilenameUtils.getExtension(uploadFile.getOriginalFileName());
			String largePath = uploadPath + uuid + "-large." + DEST_EXTENSION;
			String mediumPath = uploadPath + uuid + "-medium." + DEST_EXTENSION;
			String thumbnailPath = uploadPath + uuid + "-thumbnail." + DEST_EXTENSION;
			for (StoragePlugin storagePlugin : pluginService.getStoragePlugins(true)) {
				File tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID() + ".tmp");
				FileUtils.copyFile(uploadFile.getFile(), tempFile);
				addTask(storagePlugin, sourcePath, largePath, mediumPath, thumbnailPath, tempFile, uploadFile.getContentType());
				productImage.setSource(storagePlugin.getUrl(sourcePath));
				productImage.setLarge(storagePlugin.getUrl(largePath));
				productImage.setMedium(storagePlugin.getUrl(mediumPath));
				productImage.setThumbnail(storagePlugin.getUrl(thumbnailPath));
				break;
			}
		} catch (IllegalStateException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 生成商品图片(异步)
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void generate(List<ProductImage> productImages) {
		if (CollectionUtils.isEmpty(productImages)) {
			return;
		}
		for (ProductImage productImage : productImages) {
			generate(productImage);
		}
	}

}