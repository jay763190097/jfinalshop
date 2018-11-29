package com.jfinalshop.service;

import com.jfinalshop.model.Ad;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.AdPositionDao;
import com.jfinalshop.model.AdPosition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.jfinalshop.security.ShiroUtil.getId;

/**
 * Service - 广告位
 * 
 * 
 */
@Singleton
public class AdPositionService extends BaseService<AdPosition> {

	/**
	 * 构造方法
	 */
	public AdPositionService() {
		super(AdPosition.class);
	}
	
	@Inject
	private AdPositionDao adPositionDao;

	@Inject
	private AdPosition adPosition;
	/**
	 * 查找广告位
	 * 
	 * @param id
	 *            ID
	 * @param useCache
	 *            是否使用缓存
	 * @return 广告位
	 */
	public AdPosition find(Long id, boolean useCache) {
		return adPositionDao.find(id);
	}
	public List<AdPosition> finds(Long id, boolean useCache,String channel) {
		return adPositionDao.find(id,channel);
	}


	public AdPosition save(AdPosition adPosition) {
		return super.save(adPosition);
	}

	public AdPosition update(AdPosition adPosition) {
		return super.update(adPosition);
	}

//	public AdPosition update(AdPosition adPosition, String... ignoreProperties) {
//		return super.update(adPosition, ignoreProperties);
//	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(AdPosition adPosition) {
		super.delete(adPosition);
	}

}