package com.jfinalshop.service;

import java.util.List;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.util.Assert;

/**
 * Service - 商品
 * 
 * 
 */
@Singleton
public class ProductService extends BaseService<Product> {

	/**
	 * 构造方法
	 */
	public ProductService() {
		super(Product.class);
	}
	
	@Inject
	private ProductDao productDao;
	@Inject
	private StockLogDao stockLogDao;
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		return productDao.snExists(sn);
	}

	/**
	 * 根据编号查找商品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public Product findBySn(String sn) {
		return productDao.findBySn(sn);
	}
	
	/**
	 * 根据货品查找商品
	 * 
	 * @param goodsId
	 * @return 商品，若不存在则返回null
	 */
	public Product findByGoodsId(Long goodsId) {
		return productDao.findByGoodsId(goodsId);
	}
    
	/**
	 * 根据货品查找商品规格
	 * 
	 * @param goodsId
	 * @return 商品，若不存在则返回null
	 */
	public List<Product> findSpecifications(Long goodsId) {
		return productDao.findSpecifications(goodsId);
	}
	
	/**
	 * 通过编号、名称查找商品
	 * 
	 * @param type
	 *            类型
	 * @param keyword
	 *            关键词
	 * @param excludes
	 *            排除商品
	 * @param count
	 *            数量
	 * @return 商品
	 */
	public List<Product> search(Goods.Type type, String keyword, Set<Product> excludes, Integer count) {
		return productDao.search(type, keyword, excludes, count);
	}

	/**
	 * 增加库存
	 * 
	 * @param product
	 *            商品
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param operator
	 *            操作员
	 * @param memo
	 *            备注
	 */
	public void addStock(Product product, int amount, StockLog.Type type, Admin operator, String memo) {
		Assert.notNull(product);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		Assert.notNull(product.getStock());
		Assert.state(product.getStock() + amount >= 0);

		boolean previousOutOfStock = product.getIsOutOfStock();

		product.setStock(product.getStock() + amount);
		productDao.update(product);

		Goods goods = product.getGoods();
		if (goods != null) {
			if (product.getIsOutOfStock() != previousOutOfStock) {
				goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
			} else {
				goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			}
		}

		StockLog stockLog = new StockLog();
		stockLog.setType(type.ordinal());
		stockLog.setInQuantity(amount > 0 ? amount : 0);
		stockLog.setOutQuantity(amount < 0 ? Math.abs(amount) : 0);
		stockLog.setStock(product.getStock());
		stockLog.setOperator(operator);
		stockLog.setMemo(memo);
		stockLog.setProductId(product.getId());
		stockLogDao.save(stockLog);
	}

	/**
	 * 增加已分配库存
	 * 
	 * @param product
	 *            商品
	 * @param amount
	 *            值
	 */
	public void addAllocatedStock(Product product, int amount) {
		Assert.notNull(product);

		if (amount == 0) {
			return;
		}

		Assert.notNull(product.getAllocatedStock());
		Assert.state(product.getAllocatedStock() + amount >= 0);

		boolean previousOutOfStock = product.getIsOutOfStock();

		product.setAllocatedStock(product.getAllocatedStock() + amount);
		productDao.update(product);

		Goods goods = product.getGoods();
		if (goods != null) {
			if (product.getIsOutOfStock() != previousOutOfStock) {
				goods.setGenerateMethod(Goods.GenerateMethod.eager.ordinal());
			} else {
				goods.setGenerateMethod(Goods.GenerateMethod.lazy.ordinal());
			}
		}
	}

	/**
	 * 商品过滤
	 * 
	 * @param products
	 *            商品
	 */
	public void filter(List<Product> products) {
		CollectionUtils.filter(products, new Predicate() {
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getStock() != null;
			}
		});
	}

}