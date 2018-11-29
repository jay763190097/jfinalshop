package com.jfinalshop.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.dao.GoodsDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductCategoryBrand;
import com.jfinalshop.model.ProductCategoryPromotion;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.util.Assert;

/**
 * Service - 商品分类
 * 
 * 
 */
@Singleton
public class ProductCategoryService extends BaseService<ProductCategory> {

	/**
	 * 构造方法
	 */
	public ProductCategoryService() {
		super(ProductCategory.class);
	}
	
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private GoodsDao goodsDao;
	
	/**
	 * 查找商品分类
	 * @param grade 层级
	 * @param isMarketable 是否上架
	 * @param isTop 是否置顶
	 * @return 分类列表
	 */
	public List<ProductCategory> findGrade(Integer grade, Boolean isMarketable, Boolean isTop,String channel) {
		return productCategoryDao.findGrade(grade, isMarketable, isTop,channel);
	}
	
	/**
	 * 查找顶级商品分类
	 * 
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots() {
		return productCategoryDao.findRoots(null);
	}
    
	/**
	 * 查找顶级商品分类
	 * 商城专用
	 * @param
	 *
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findShopRoots(String channel) {
		return productCategoryDao.findShopRoots(channel);
	}

	/**
	 * 查找顶级商品分类
	 * 商城专用
	 * @param
	 *
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findShopRoot3(Integer count, boolean useCache,String channel) {
		return productCategoryDao.findShopRoot3(count,channel);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count) {
		return productCategoryDao.findRoots(count);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count, boolean useCache) {
		return productCategoryDao.findRoots(count);
	}

	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(ProductCategory productCategory, boolean recursive, Integer count) {
		return productCategoryDao.findParents(productCategory, recursive, count);
	}

	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(Long productCategoryId, boolean recursive, Integer count, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return productCategoryDao.findParents(productCategory, recursive, count);
	}

	/**
	 * 查找商品分类树
	 * 
	 * @return 商品分类树
	 */
	public List<ProductCategory> findTree() {
		return productCategoryDao.findChildren(null, true, null);
	}
     
	/**
	 * 查找下级商品分类  非管理专用
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildrenAss(ProductCategory productCategory, boolean recursive, Integer count,String channel) {
		return productCategoryDao.findChildrenAss(productCategory, recursive, count, channel);
	}
	
	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory, boolean recursive, Integer count) {
		return productCategoryDao.findChildren(productCategory, recursive, count);
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(Long productCategoryId, boolean recursive, Integer count, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return productCategoryDao.findChildren(productCategory, recursive, count);
	}

	/**修改添加渠道标识
	 * 拼接成json类型
	 */
	public String createJSONData(Integer grade, Integer count,String channel) {
		// 查询2级分类节点
		List<ProductCategory> productCategorys = productCategoryDao.findGrade(grade, true ,true,channel);
		StringBuffer sb = new StringBuffer(); // 初始化根节点
		if (CollectionUtils.isNotEmpty(productCategorys)) {
			sb.append("[");
			for (ProductCategory productCategory : productCategorys) {
				List<Goods> goodsList = goodsDao.findList(productCategory, true,channel,null, null, null, null, count);
				if (CollectionUtils.isEmpty(goodsList)) continue; 
				sb.append("{\"id\":\"").append(productCategory.getId()).append("\",");
				sb.append("\"name\":\"").append(productCategory.getName()).append("\",");
				sb.append("\"image\":\"").append(productCategory.getImage()).append("\"");
				if (CollectionUtils.isNotEmpty(goodsList)) {
					sb.append(",\"children\":[");
					for (Goods goods : goodsList) { 
						sb.append("{\"id\":\"").append(goods.getDefaultProduct().getId()).append("\",");
						sb.append("\"name\":\"").append(goods.getName()).append("\",");
						sb.append("\"price\":\"").append(currency(goods.getPrice(), false, false)).append("\",");
						sb.append("\"unit\":\"").append(goods.getUnit()).append("\",");
						sb.append("\"weight\":\"").append(goods.getWeight()).append("\",");
						sb.append("\"brand\":\"").append(goods.getBrand() != null ? goods.getBrand().getName() : "").append("\",");
						sb.append("\"image\":\"").append(goods.getImage()).append("\",");
						sb.append(getPromotions(goods));
						sb.append("},");
					}
					sb = new StringBuffer(sb.substring(0,sb.lastIndexOf(",")) + "]},");
				} else {
					sb.append("},");
				}
			}
			sb = new StringBuffer(sb.substring(0, sb.length() - 1)+ "]");
		}
		return sb.toString();
	}
	
	
	/**
	 * 保存
	 * 
	 */
	@Before(Tx.class)
	public ProductCategory save(ProductCategory productCategory) {
		Assert.notNull(productCategory);
		setValue(productCategory);
		super.save(productCategory);
		List<Brand> brands = productCategory.getBrands();
		if (CollectionUtils.isNotEmpty(brands)) {
			for (Brand brand : brands) {
				ProductCategoryBrand productCategoryBrand = new ProductCategoryBrand();
				productCategoryBrand.setBrands(brand.getId());
				productCategoryBrand.setProductCategories(productCategory.getId());
				productCategoryBrand.save();
			}
				
		}
		List<Promotion> promotions = productCategory.getPromotions();
		if (CollectionUtils.isNotEmpty(promotions)) {
			for (Promotion promotion : promotions) {
				ProductCategoryPromotion productCategoryPromotion = new ProductCategoryPromotion();
				productCategoryPromotion.setPromotions(promotion.getId());
				productCategoryPromotion.setProductCategories(productCategory.getId());
				productCategoryPromotion.save();
			}
		}
		return productCategory;
	}

	/**
	 * 更新
	 * 
	 */
	@Before(Tx.class)
	public ProductCategory update(ProductCategory productCategory) {
		Assert.notNull(productCategory);

		setValue(productCategory);
		for (ProductCategory children : productCategoryDao.findChildren(productCategory, true, null)) {
			setValue(children);
		}
		super.update(productCategory);
		
		return productCategory;
	}

	@Before(Tx.class)
	public void delete(Long id) {
		ProductCategoryBrand.dao.delete(id);
		ProductCategoryPromotion.dao.delete(id);
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(ProductCategory productCategory) {
		super.delete(productCategory);
	}

	/**
	 * 设置值
	 * 
	 * @param productCategory
	 *            商品分类
	 */
	private void setValue(ProductCategory productCategory) {
		if (productCategory == null) {
			return;
		}
		ProductCategory parent = productCategory.getParent();
		if (parent != null) {
			productCategory.setTreePath(parent.getTreePath() + parent.getId() + ProductCategory.TREE_PATH_SEPARATOR);
		} else {
			productCategory.setTreePath(ProductCategory.TREE_PATH_SEPARATOR);
		}
		productCategory.setGrade(productCategory.getParentIds().length);
	}
	
	/**
	 * 返回是否有促销信息
	 * @param goods
	 * @return
	 */
	private StringBuffer getPromotions(Goods goods) {
		Promotion promotion = null;
		StringBuffer sb = new StringBuffer();
		if (CollectionUtils.isNotEmpty(goods.getValidPromotions())) {
			Set<Promotion> promotions = goods.getValidPromotions();
			for (Iterator<Promotion> iterator = promotions.iterator(); iterator.hasNext();) {
				promotion = iterator.next();
			}
			sb.append("\"promotions\":").append("[{\"name\":\"" + promotion.getName() + "\",\"title\":\"" + promotion.getTitle() + "\"}]");
		} else {
			sb.append("\"promotions\":").append("[]");
		}
		return sb;
	}
}