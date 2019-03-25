package com.ys.idatrix.metacube.metamanage.service;

import com.alibaba.fastjson.util.DeserializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.service.impl.McDirectMiningServiceImpl.MiningTaskDto;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;

import java.lang.reflect.Type;
import java.util.List;

public interface McDirectMiningService {

	/**
	 * 获取当前 schema 正在运行的直采任务 <br>
	 * 没有则返回 null, 否则 返回正在运行的,或者上次运行(已经结束)的 任务信息
	 * @param schemaId
	 * @return
	 */
	public  MiningTaskDto getMiningTask( Long schemaId ,int resourceType);
	
	
	/**
	 * 获取表的信息
	 * @param schemaId
	 * @return
	 */
	public  List<? extends TableVO>  getTableAllInfo( Long schemaId );
	
	/**
	 * 获取视图信息
	 * @param schemaId
	 * @return
	 */
	public List<? extends ViewVO>  getViewAllInfo( Long schemaId  );
	
	/**
	 * 保存需要采集的表
	 * @param schemaId
	 * @param metadataBase
	 * @return
	 */
	public void  directMiningTables( Long schemaId, MetadataBaseVO... metadataBase ) ;
	
	/**
	 * 保存需要采集的视图
	 * @param schemaId
	 * @param metadataBase
	 * @return
	 */
	public void  directMiningViews( Long schemaId,MetadataBaseVO... metadataBase ) ;
	
	/**
	 * 获取视图的列信息列表
	 * @param schemaId
	 * @param viewName
	 * @param viewSql
	 * @return
	 */
	public List<TableColumn> getViewColumns(Long schemaId, String viewName, String viewSql);
	
	default public  <B, S extends B>  void copyProperties(B source, S target) {

        try {
            if (source == null || target == null) {
                return;
            }
            DeserializeBeanInfo deserializeBeanInfo = DeserializeBeanInfo.computeSetters(target.getClass(), (Type) target.getClass());
            List<FieldInfo> getters = TypeUtils.computeGetters(source.getClass(), null);

            List<FieldInfo> setters = deserializeBeanInfo.getFieldList();
            Object v;
            FieldInfo getterfield;
            FieldInfo setterfidld;
            for (int j = 0; j < getters.size(); j++) {
                getterfield=getters.get(j);
                for (int i = 0; i < setters.size(); i++) {
                    setterfidld=setters.get(i);
                    if (setterfidld.getName().compareTo(getterfield.getName()) == 0) {
                        v = getterfield.getMethod().invoke(source);
                       setterfidld.getMethod().invoke(target,v);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
        	 throw new MetaDataException( ex );
        }
    }
}
