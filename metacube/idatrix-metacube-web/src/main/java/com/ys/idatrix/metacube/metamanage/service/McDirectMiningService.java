package com.ys.idatrix.metacube.metamanage.service;

import com.alibaba.fastjson.util.DeserializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;

import java.lang.reflect.Type;
import java.util.List;

public interface McDirectMiningService {

	/**
	 * 获取表的信息
	 * @param databaseType
	 * @param schemaId
	 * @return
	 */
	public  List<? extends TableVO>  getTableAllInfo(Integer databaseType, Long schemaId );
	
	/**
	 * 获取视图信息
	 * @param databaseType
	 * @param schemaId
	 * @return
	 */
	public List<? extends ViewVO>  getViewAllInfo( Integer databaseType, Long schemaId  );
	
	/**
	 * 保存需要采集的表
	 * @param databaseType
	 * @param schemaId
	 * @param metadataBase
	 * @return
	 */
	public List<? extends TableVO>  directMiningTables(Integer databaseType , Long schemaId, MetadataBaseVO... metadataBase ) ;
	
	/**
	 * 保存需要采集的视图
	 * @param databaseType
	 * @param schemaId
	 * @param metadataBase
	 * @return
	 */
	public List<? extends ViewVO>  directMiningViews(Integer databaseType, Long schemaId,MetadataBaseVO... metadataBase ) ;
	
	
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
