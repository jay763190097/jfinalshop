package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.dao.AreaDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.util.Assert;

/**
 * Service - 地区
 * 
 * 
 */
@Singleton
public class AreaService extends BaseService<Area> {

	/**
	 * 构造方法
	 */
	public AreaService() {
		super(Area.class);
	}
	
	@Inject
	private AreaDao areaDao;
	
	/**
	 * 查找顶级地区
	 * 
	 * @return 顶级地区
	 */
	public List<Area> findRoots() {
		return areaDao.findRoots(null);
	}

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	public List<Area> findRoots(Integer count) {
		return areaDao.findRoots(count);
	}

	/**
	 * 查找上级地区
	 * 
	 * @param area
	 *            地区
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级地区
	 */
	public List<Area> findParents(Area area, boolean recursive, Integer count) {
		return areaDao.findParents(area, recursive, count);
	}

	/**
	 * 查找下级地区
	 * 
	 * @param area
	 *            地区
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级地区
	 */
	public List<Area> findChildren(Area area, boolean recursive, Integer count) {
		return areaDao.findChildren(area, recursive, count);
	}
	
	/**
	 * 拼接成json类型
	 */
	public String createJSONData() {
		// 查询一级节点
		List<Area> areas = findRoots();
		StringBuffer sb = new StringBuffer("["); // 初始化根节点
		if (CollectionUtils.isNotEmpty(areas)) {
			for (Area area : areas) {
				sb.append("{\"value\":\"").append(area.getId()).append("\",");
				sb.append("\"text\":\"").append(area.getName()).append("\"");
				List<Area> childrens = area.getChildren();
				if (CollectionUtils.isNotEmpty(childrens)) {
					sb.append(",\"children\":[");
					for (Area children : childrens) { 
						sb.append("{\"value\":\"").append(children.getId()).append("\",");
						sb.append("\"text\":\"").append(children.getName()).append("\"");
						sb.append(getNodes(children).toString());    // 下级节点  
						//sb.append("},");
					}
					sb = new StringBuffer(sb.substring(0,sb.lastIndexOf(",")) + "]},");
				}else {
					sb.append("},");
				}
			}
			sb = new StringBuffer(sb.substring(0, sb.length() - 1)+ "]");
		}
		return sb.toString();
	}
	 
	/** 
     * 获得节点  
     */  
	private String getNodes(Area area) {
		List<Area> childrens = area.getChildren();
		StringBuffer sb = new StringBuffer();
		if (CollectionUtils.isNotEmpty(childrens)) {
			sb.append(",\"children\":[");
			for (Area children : childrens) {
				sb.append("{\"value\":\"").append(children.getId()).append("\",");
				sb.append("\"text\":\"").append(children.getName()).append("\"");
				sb.append(getNodes(children).toString());
			}
		} else {
			sb.append("},");
		}
		if (sb.toString().contains("children")) {
			sb = new StringBuffer(sb.substring(0, sb.lastIndexOf(",")));
			sb.append("]},");
		}
		return sb.toString();
	}
	
	public Area save(Area area) {
		Assert.notNull(area);

		setValue(area);
		return super.save(area);
	}

	public Area update(Area area) {
		Assert.notNull(area);

		setValue(area);
		for (Area children : areaDao.findChildren(area, true, null)) {
			setValue(children);
		}
		return super.update(area);
	}

//	public Area update(Area area, String... ignoreProperties) {
//		return super.update(area, ignoreProperties);
//	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(Area area) {
		super.delete(area);
	}

	/**
	 * 设置值
	 * 
	 * @param area
	 *            地区
	 */
	private void setValue(Area area) {
		if (area == null) {
			return;
		}
		Area parent = area.getParent();
		if (parent != null) {
			area.setFullName(parent.getFullName() + area.getName());
			area.setTreePath(parent.getTreePath() + parent.getId() + Area.TREE_PATH_SEPARATOR);
		} else {
			area.setFullName(area.getName());
			area.setTreePath(Area.TREE_PATH_SEPARATOR);
		}
		area.setGrade(area.getParentIds().length);
	}


}