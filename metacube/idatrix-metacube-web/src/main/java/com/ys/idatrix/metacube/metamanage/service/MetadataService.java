package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.PageResultVO;

import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
public interface MetadataService {

    // 查询，带分页
    PageResultBean<Metadata> search(MetadataSearchVo searchVo);

    // 根据id查询
    Metadata findById(Long id);

    // 根据主题id查询元数据
    int findMetadataByThemeId(Long id);

    // 根据元数据信息查询数据（主要用于判断是否存在此数据）
    int findByMetadata(Metadata metadata);

    int insertSelective(Metadata metadata);

    int updateByPrimaryKeySelective(Metadata view);

    /**
     * 填充MetadataSearchVo对象中的id转value
     * @param metadataBaseVO
     */
    void wrapMetadataBaseVO(MetadataBaseVO metadataBaseVO);
}
