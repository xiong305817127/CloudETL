package com.idatrix.resource.portal.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.idatrix.es.api.dto.req.doc.DocQueryReq;
import com.idatrix.es.api.dto.req.doc.FetchCriteriaDto;
import com.idatrix.es.api.dto.req.doc.HighlightDto;
import com.idatrix.es.api.dto.req.doc.QueryCriteriaDto;
import com.idatrix.es.api.dto.resp.RespResult;
import com.idatrix.es.api.dto.resp.doc.DocHitsDto;
import com.idatrix.es.api.dto.resp.doc.DocQueryDto;
import com.idatrix.es.api.enums.FetchMode;
import com.idatrix.es.api.enums.QueryMode;
import com.idatrix.es.api.service.IIndexDocService;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.service.IOverviewService;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.portal.po.ResourceQueryPO;
import com.idatrix.resource.portal.service.IQueryService;
import com.idatrix.resource.portal.vo.ResourceQueryRequestVO;
import com.idatrix.resource.portal.vo.ResourceQueryVO;
import com.idatrix.resource.portal.vo.ResourceSearchRequestVO;
import com.idatrix.resource.portal.vo.ResourceSearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.idatrix.resource.portal.common.IndexResourceConstant.*;

/**
 * 主要实现门户查询功能服务
 */
@Slf4j
@Transactional
@Service("queryService")
public class QueryServiceImpl implements IQueryService {

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;


    @Autowired
    private IIndexDocService indexDocService;

    @Autowired
    private IOverviewService overviewService;


    /**
     * 门户页面查询
     *
     * @param requestVO
     * @return
     */
    @Override
    public ResultPager<ResourceQueryVO> queryResourceByCondition(ResourceQueryRequestVO requestVO) {

        StringBuilder resourceType = new StringBuilder();
        if (StringUtils.isNotEmpty(requestVO.getQueryKeyWord())) {
            String keyWord = null;
            try {
                keyWord = URLDecoder.decode(requestVO.getQueryKeyWord(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("门户查询传递参数解码错误，编码为 " + requestVO.getQueryKeyWord());
            }
            requestVO.setQueryKeyWord(keyWord);
        }
        Boolean queryType = false;
        if (StringUtils.isNotEmpty(requestVO.getDbTypeFlag())) {
            resourceType.append("3,");
            queryType = true;
        }
        if (StringUtils.isNotEmpty(requestVO.getFileTypeFlag())) {
            resourceType.append("1,2,4,5,6,");
            queryType = true;
        }
        if (StringUtils.isNotEmpty(requestVO.getInterfaceTypeFlag())) {
            resourceType.append("7,");
            queryType = true;
        }
        if (queryType) {
            requestVO.setResourceType(resourceType.substring(0, resourceType.length() - 1));
        }

        if (StringUtils.isEmpty(requestVO.getSubCountFlag()) &&
                StringUtils.isEmpty(requestVO.getVisitCountFlag()) &&
                StringUtils.isEmpty(requestVO.getTimeFlag())) {
            requestVO.setTimeFlag("1");
        }

        int pageNum = null == requestVO.getPage() ? 1 : requestVO.getPage();
        int pageSize = null == requestVO.getPageSize() ? 10 : requestVO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceQueryPO> rsPOList = resourceConfigDAO.queryPortalResourceByCondition(requestVO);
        List<ResourceQueryVO> rcVOList = transferResourceQueryPoTOVo(rsPOList);

        //用PageInfo对结果进行包装
        PageInfo<ResourceQueryPO> pi = new PageInfo<ResourceQueryPO>(rsPOList);
        Long totalNums = pi.getTotal();
        ResultPager<ResourceQueryVO> rp = new ResultPager<ResourceQueryVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    private List<ResourceQueryVO> transferResourceQueryPoTOVo(List<ResourceQueryPO> queryPOList) {
        List<ResourceQueryVO> queryVOList = new ArrayList<>();
        if (queryPOList == null || queryPOList.size() == 0) {
            return null;
        }
        for (ResourceQueryPO po : queryPOList) {
            ResourceQueryVO vo = new ResourceQueryVO(po);
            queryVOList.add(vo);
        }
        return queryVOList;
    }


    @Override
    public ResultPager<ResourceSearchVO> queryResourceByKeyword(ResourceSearchRequestVO searchRequestVO) throws Exception {

        //ES搜索的资源标识id
        List<Long> resourceIds = null;

        //ES搜索资源高亮
        Map<String, Map<String, String>> docHighlightMap = null;

        //输入有关键字
        if (StringUtils.isNotBlank(searchRequestVO.getKeyword())) {

            //获取所有成功发布资源的租户
            List<Long> renterIds = resourceConfigDAO.getRentersByPublishedResource();

            if (CollectionUtils.isNotEmpty(renterIds)) {

                //搜索附加高亮参数
                Set<String> fields = new HashSet<>(RESOURCE_FIELDS);
                HighlightDto highlightParam = new HighlightDto();
                highlightParam.setFields(fields);
                highlightParam.setPreTags(new String[]{searchRequestVO.getHighlightPreTag()});
                highlightParam.setPostTags(new String[]{searchRequestVO.getHighlightPostTag()});

                //分页查询参数
                FetchCriteriaDto fetchCriteriaDto = new FetchCriteriaDto();
                fetchCriteriaDto.setFetchMode(FetchMode.PAGE);
                fetchCriteriaDto.setPageNum(searchRequestVO.getPage());
                fetchCriteriaDto.setPageSize(searchRequestVO.getPageSize());
                fetchCriteriaDto.setHighlight(highlightParam);

                List<QueryCriteriaDto> queryCriteriaDtoList = Lists.newArrayList();
                if (StringUtils.isNotBlank(searchRequestVO.getKeyword())) {
                    QueryCriteriaDto multiCriteria = new QueryCriteriaDto();
                    multiCriteria.setQueryMode(QueryMode.MATCH_MULTI);
                    multiCriteria.setValue(searchRequestVO.getKeyword());
                    multiCriteria.setFieldNames(RESOURCE_FIELDS);
                    queryCriteriaDtoList.add(multiCriteria);
                }

                //文档请求对象
                DocQueryReq docQueryReq = new DocQueryReq();
                Map<String, Object> multiFieldMap = Maps.newHashMap();
                multiFieldMap.put(StringUtils.join(RESOURCE_FIELDS.toArray(), ","), searchRequestVO.getKeyword().trim());
                docQueryReq.setIndex(INDEX_PRENAME + "*");
                docQueryReq.setType(INDEX_TYPE);
                docQueryReq.setFetchCriteria(fetchCriteriaDto);
                docQueryReq.setQueryCriterionList(queryCriteriaDtoList);

                //查询结果
                RespResult<DocQueryDto> result = indexDocService.searchDoc(searchRequestVO.getUserName(), docQueryReq, null);

                if (!result.isSuccess()) {
                    log.error("调用 DbProxy 检索发布资源关键字:{},失败信息:{}", searchRequestVO.getKeyword(), result.getMsg());
                    return null;
                }

                DocQueryDto queryDoc = result.getData();

                List<DocHitsDto> hitsDataList = queryDoc.getHitsDataList();
                if (CollectionUtils.isNotEmpty(hitsDataList)) {
                    //资源标识id集合
                    resourceIds = hitsDataList.stream().map(hit -> Long.parseLong(hit.getId())).collect(Collectors.toList());

                    //文档id对应高亮内容
                    docHighlightMap = hitsDataList.stream()
                            .collect(Collectors.toMap((key -> key.getId()), (value -> value.getHighlightContents())));

                } else {
                    return null;
                }
            }
        }

        ResourceCatalogSearchVO searchVO = new ResourceCatalogSearchVO();
        searchVO.setStatus(STATUS_PUBLISHED);
        searchVO.setResourceIds(resourceIds);

        PageHelper.startPage(searchRequestVO.getPage(), searchRequestVO.getPageSize());
        List<ResourceConfigPO> rsPOList = resourceConfigDAO.getPublishedResourcesByCondition(searchVO);
        List<ResourceSearchVO> rcVOList = transferQueryPo2SearchVo(searchRequestVO.getUserName(), rsPOList);

        //设置高亮
        if (MapUtils.isNotEmpty(docHighlightMap)) {
            Map<String, Map<String, String>> _docHighlightMap = docHighlightMap;
            rcVOList.stream().forEach(vo -> {
                //获取每条资源的高亮数据
                Map<String, String> fieldHighlightMap = _docHighlightMap.get(vo.getResourceId() + "");
                vo.setHighlightData(fieldHighlightMap);

                //根据指定高亮字段设置对象的响应字段
                for (String resourceField : RESOURCE_FIELDS) {
                    String fieldHighlightContent = fieldHighlightMap.get(resourceField);
                    if (StringUtils.isNotBlank(fieldHighlightContent)) {
                        switch (resourceField) {
                            //资源名称
                            case "name":
                                vo.setResourceName(fieldHighlightContent);
                                break;
                            //资源摘要
                            case "digest":
                                vo.setResourceRemark(fieldHighlightContent);
                                break;
                            //信息项（搜索页面暂时没有显示）
                            case "infoItem":
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }

        PageInfo<ResourceConfigPO> pi = new PageInfo<ResourceConfigPO>(rsPOList);
        ResultPager<ResourceSearchVO> rp = new ResultPager<ResourceSearchVO>(pi.getPageNum(), pi.getTotal(), rcVOList);
        return rp;
    }


    /**
     * 数据库查询资源目录对象转为搜索对象
     *
     * @param queryPOList
     * @return
     */
    private List<ResourceSearchVO> transferQueryPo2SearchVo(String userName, List<ResourceConfigPO> queryPOList) {
        if (CollectionUtils.isEmpty(queryPOList)) {
            return null;
        }

        List<ResourceSearchVO> searchVOList = Lists.newArrayList();
        for (ResourceConfigPO po : queryPOList) {
            ResourceSearchVO vo = new ResourceSearchVO(po);
            int flag = overviewService.getSubscribeFlagByUserAndResourceId(userName, vo.getResourceId());
            vo.setSubscribeFlag(flag);
            searchVOList.add(vo);
        }
        return searchVOList;
    }


}
