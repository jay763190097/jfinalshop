package com.jfinalshop.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Product;
import com.jfinalshop.util.SqlUtils;

/**
 * Dao - 商品
 * 
 * 
 */
public class ProductDao extends BaseDao<Product> {
	
	/**
	 * 构造方法
	 */
	public ProductDao() {
		super(Product.class);
	}
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return false;
		}

		String sql = "SELECT COUNT(*) FROM product WHERE sn = LOWER(?)";
		Long count = Db.queryLong(sql, sn);
		return count > 0;
	}


	/**
	 * 根据编号查找商品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public Product findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}

		String sql = "SELECT * FROM product WHERE sn = LOWER(?)";
		try {
			return modelManager.findFirst(sql, sn);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * 根据货品查找商品
	 * 
	 * @param goodsId
	 * @return 商品，若不存在则返回null
	 */
	public Product findByGoodsId(Long goodsId) {
		String sql = "SELECT * FROM product WHERE goods_id = ?";
		
		try {
			return modelManager.findFirst(sql, goodsId);
		} catch (Exception e) {
			return null;
		}
	}

	
	/**
	 * 根据货品查找商品规格列表
	 * 
	 * @param goodsId
	 * @return 商品，若不存在则返回null
	 */
	public List<Product> findSpecifications(Long goodsId) {
    //String sql = "SELECT * FROM product WHERE goods_id = ?";
		return super.findSpecifications(goodsId);
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
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}

		String sql = "SELECT p.* FROM product p LEFT JOIN goods g ON p.goods_id = g.id WHERE 1 = 1 ";
		if (type != null) {
			sql += "AND g.type = " + type.ordinal() + " ";
		}
		sql += "AND (p.sn LIKE '%" + keyword + "%' OR g.name LIKE '%" + keyword + "%') ";
		if (CollectionUtils.isNotEmpty(excludes)) {
			List<Long> ids = Arrays.asList(getExcludesIds(excludes));
			sql += " NOT IN " + SqlUtils.getSQLIn(ids) + " "; 
		}
		return super.findList(sql, null, count, null, null);
	}
	
	/**
	 * 获取所有Set<Product> ID
	 * 
	 * @return 所有Long<Long>ID
	 */
	public Long[] getExcludesIds(Set<Product> excludes) {
		Long[] result = new Long[excludes.size()];
		int i = 0;
		for (Iterator<Product> iterator = excludes.iterator(); iterator.hasNext();) {
			result[i] = iterator.next().getId();
			i++;
		}
		return result;
	}


	/**
	 * 根据goods删除参数
	 * @param goods
	 * @return
	 */
	public boolean delete(Long goods) {
		return Db.deleteById("product", "goods_id", goods);
	}
}