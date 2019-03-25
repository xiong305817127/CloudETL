package com.idatrix.resource.portal.service.Impl;

import com.google.common.collect.Lists;
import com.idatrix.es.api.dto.req.doc.DocUpdateReq;
import com.idatrix.es.api.dto.req.doc.OpPermissionDto;
import com.idatrix.es.api.dto.req.index.FieldDto;
import com.idatrix.es.api.dto.req.index.IndexDto;
import com.idatrix.es.api.dto.req.index.MappingDto;
import com.idatrix.es.api.dto.req.index.NewIndexDto;
import com.idatrix.es.api.dto.resp.RespResult;
import com.idatrix.es.api.enums.FieldType;
import com.idatrix.es.api.service.IIndexDocService;
import com.idatrix.es.api.service.IIndexManageService;
import com.idatrix.resource.catalog.dao.ResourceColumnDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceColumnPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.portal.common.IndexResourceConstant;
import com.idatrix.resource.portal.service.IResourceIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.idatrix.resource.portal.common.IndexResourceConstant.RESOURCE_FIELDS;

/**
 * @ClassName: ResourceIndexServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/7
 */
@Slf4j
@Service("resourceIndexService")
public class ResourceIndexServiceImpl implements IResourceIndexService {


    @Autowired(required = false)
    private IIndexManageService indexManageService;

    @Autowired(required = false)
    private IIndexDocService indexDocService;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceColumnDAO resourceColumnDAO;


    @Override
    public void indexPublishedResourceByResourceId(String username, Long resourceId) {

        ResourceConfigPO resource = resourceConfigDAO.getConfigById(resourceId);
        OpPermissionDto permissionDto = new OpPermissionDto();
        permissionDto.setNeedPermission(false);

        if (createResourceIndex(username, resource.getRentId())) {

            DocUpdateReq document = assembleEsDocument(resource);
            try {

                RespResult<Boolean> result = indexDocService.upsert(username, document, permissionDto);
                if (result.isSuccess()) {
                    log.info("资源目录:{},摘要:{},信息项:{} 索引成功", resource.getName(), resource.getRemark(),
                            document.getDocDatum().get("infoItem"));
                } else {
                    log.error("资源目录:{},索引失败,失败信息:{}", resource.getName(), result.getMsg());
                }

            } catch (Exception e) {
                log.error("资源目录:{},索引接口调用失败,信息:{}", resource.getName(), e.getMessage());
            }

        }

    }


    @Override
    public void indexPublishedResourceByRenterId(Long renterId) {

        OpPermissionDto permissionDto = new OpPermissionDto();
        permissionDto.setNeedPermission(false);
        RespResult<Boolean> result = indexManageService.deleteIndex(Lists.newArrayList(getResourceIndex(renterId).getIndexCode()));
        if (result.isSuccess()) {

            if (createResourceIndex(IndexResourceConstant.REINDEX_USER, renterId)) {

                List<ResourceConfigPO> resourceList = resourceConfigDAO.getAllPublishedResourceByRentId(renterId);
                if (CollectionUtils.isNotEmpty(resourceList)) {

                    List<DocUpdateReq> esDocuments = Lists.newArrayList();
                    for (ResourceConfigPO resourceConfigPO : resourceList) {
                        esDocuments.add(assembleEsDocument(resourceConfigPO));
                    }
                    log.info("租户:{} 下共有已发布成功的资源目录:{}", renterId, resourceList.size());

                    result = indexDocService.bulkAdd("", esDocuments, null,  permissionDto);
                    if (result.isSuccess()) {
                        log.info("租户:{} 下的所有资源目录索成功", renterId);
                    } else {
                        log.error("租户:{} 下的所有资源目录索引失败,失败信息:{}", renterId, result.getMsg());
                    }
                }

            }

        } else {
            log.error("删除索引:{} 失败,信息:{}", getResourceIndex(renterId).getIndexName(), result.getMsg());
        }

    }


    @Override
    public boolean createResourceIndex(String username, Long renterId) {

        IndexDto index = getResourceIndex(renterId);

        //判断是否存在索引，不存在则新建
        RespResult<Boolean> result = indexManageService.hasExistsIndex(index.getIndexCode());

        //不存在
        if (!result.isSuccess()) {
            log.warn("索引[{}]已存在，不用再创建", index.getIndexName());
            return true;

        } else {

            MappingDto indexMapping = new MappingDto();
            indexMapping.setType(IndexResourceConstant.INDEX_TYPE);

            //资源名称、资源摘要、信息项
            for (String resourceField : RESOURCE_FIELDS) {

                FieldDto mappingParameter = new FieldDto();
                mappingParameter.setName(resourceField);
                mappingParameter.setType(FieldType.Text);
                mappingParameter.setIncludeSource(true);
                indexMapping.getFieldDtoList().add(mappingParameter);
            }

            NewIndexDto createdIndex = new NewIndexDto();
            createdIndex.setIndex(index);
            createdIndex.setUsername(username);
            createdIndex.setMappings(Lists.newArrayList(indexMapping));

            result = indexManageService.createIndex(createdIndex);
            if (result.isSuccess()) {
                log.error("创建索引[{}]成功", index.getIndexName());
                return true;
            } else {
                log.error("创建索引[{}]失败,失败原因:{}", index.getIndexName());
                return false;
            }
        }
    }


    /**
     * 组装资源目录索引的数据文档
     *
     * @param resource
     * @return
     */
    private DocUpdateReq assembleEsDocument(ResourceConfigPO resource) {

        //资源配置信息项查询
        List<ResourceColumnPO> rcList = resourceColumnDAO.getColumnByResourceId(resource.getId());
        String infoItem = null;
        if (CollectionUtils.isNotEmpty(rcList)) {

            StringBuilder sbColNames = new StringBuilder();
            for (ResourceColumnPO resourceColumnPO : rcList) {
                if (StringUtils.isNotBlank(resourceColumnPO.getColName())) {
                    sbColNames.append(resourceColumnPO.getColName()).append(",");
                }
            }

            // 删除最后一个逗号
            if (sbColNames.lastIndexOf(IndexResourceConstant.ITEMS_SEPARATE) > -1) {
                sbColNames.deleteCharAt(sbColNames.lastIndexOf(IndexResourceConstant.ITEMS_SEPARATE));
            }

            infoItem = sbColNames.toString();

        }

        //索引文档内容
        DocUpdateReq document = new DocUpdateReq();
        document.setIndex(getResourceIndex(resource.getRentId()).getIndexName());
        document.setType(IndexResourceConstant.INDEX_TYPE);
        document.setId(resource.getId() + "");
        document.getDocDatum().put("name", resource.getName());
        document.getDocDatum().put("digest", resource.getRemark());
        document.getDocDatum().put("infoItem", infoItem);
        return document;
    }


    /**
     * 根据租户获取租户资源目录索引
     *
     * @param renterId
     * @return
     */
    private IndexDto getResourceIndex(Long renterId) {
        IndexDto indexDto = new IndexDto();
        indexDto.setVersion(IndexResourceConstant.INDEX_VERSION);
        indexDto.setIndexCode(IndexResourceConstant.INDEX_PRENAME + renterId);
        return indexDto;

    }

}
