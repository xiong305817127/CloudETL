package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.idatrix.unisecurity.api.domain.Organization;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.Theme;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.ThemeMapper;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2019/1/15.
 */
@Transactional
@Service
public class MetadataServiceImpl implements MetadataService {

    @Autowired(required = false)
    private MetadataMapper metadataMapper;

    @Autowired(required = false)
    private McDatabaseMapper databaseMapper;

    @Autowired(required = false)
    private McSchemaMapper mcSchemaMapper;

    @Autowired(required = false)
    private ThemeMapper themeMapper;

    @Autowired
    private SecurityConsumer securityConsumer;

    @Override
    public PageResultBean<Metadata> search(MetadataSearchVo searchVo) {

        Preconditions.checkNotNull(searchVo, "请求参数为空");

        if (searchVo.getRenterId() == null) {
            searchVo.setRenterId(UserUtils.getRenterId());
        }

        // 分页
        PageHelper.startPage(searchVo.getPageNum(), searchVo.getPageSize());

        // 查询
        List<Metadata> list = metadataMapper.search(searchVo);

        PageInfo<Metadata> pageInfo = new PageInfo<>(list);

        return PageResultBean.builder(pageInfo.getTotal(), list);

    }

    @Override
    public Metadata findById(Long id) {
        return metadataMapper.selectByPrimaryKey(id);
    }

    @Override
    public int findMetadataByThemeId(Long themeId) {
        return metadataMapper.findMetadataByThemeId(themeId);
    }

    @Override
    public int findByMetadata(Metadata metadata) {
        return metadataMapper.findByMetadata(metadata);
    }

    @Override
    public int insertSelective(Metadata metadata) {
        return metadataMapper.insertSelective(metadata);
    }

    @Override
    public int updateByPrimaryKeySelective(Metadata metadata) {
        return metadataMapper.updateByPrimaryKeySelective(metadata);
    }

    @Override
    public void wrapMetadataBaseVO(MetadataBaseVO metadataBaseVO) {
        //模式名称
        if (null != metadataBaseVO.getSchemaId()) {
            McSchemaPO mcSchemaPO = mcSchemaMapper.findById(metadataBaseVO.getSchemaId());
            if (null == mcSchemaPO) {
                throw new MetaDataException("模式不存在");
            }
            DatasourceVO datasource = databaseMapper.getDatasourceInfoById(mcSchemaPO.getDbId());
            switch (datasource.getType()) {
                case "2":
                    metadataBaseVO.setSchemaName(mcSchemaPO.getServiceName());
                    break;
                default:
                    metadataBaseVO.setSchemaName(mcSchemaPO.getName());
                    break;
            }
        }

        //主题
        if (null != metadataBaseVO.getThemeId()) {
            Theme theme = themeMapper.selectByPrimaryKey(metadataBaseVO.getThemeId());
            if (null != theme) {
                String themeName = theme.getName();
                metadataBaseVO.setThemeName(themeName);
            }
        }

        //组织
        if (null != metadataBaseVO.getDeptCodes()) {
            String deptCode = metadataBaseVO.getDeptCodes();
            if (StringUtils.isNotBlank(deptCode)) {
                List<String> deptCodes = Arrays.asList(deptCode.split(","));
                List<Organization> organizationList = securityConsumer.listAscriptionDept(metadataBaseVO.getRenterId());
                if (CollectionUtils.isNotEmpty(organizationList)) {
                    List<String> deptNames = organizationList.stream().filter(dept -> deptCodes.contains(dept.getDeptCode())).map(dept -> dept.getDeptName()).collect(Collectors.toList());
                    String deptName = StringUtils.join(deptNames, ",");
                    metadataBaseVO.setDeptNames(deptName);
                }
            }

        }


    }
}
