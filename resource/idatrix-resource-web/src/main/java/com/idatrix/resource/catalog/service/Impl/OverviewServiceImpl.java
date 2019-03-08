package com.idatrix.resource.catalog.service.Impl;

import static com.idatrix.resource.common.utils.ResourceTools.FormatType.DB;
import static com.idatrix.resource.common.utils.ResourceTools.FormatType.SERVICE_INTERFACE;
import static com.idatrix.resource.common.utils.ResourceTools.ResourceStatus.PUB_SUCCESS;
import static com.idatrix.resource.subscribe.utils.SubscribeStatusEnum.FAILED;
import static com.idatrix.resource.subscribe.utils.SubscribeStatusEnum.SUCCESS;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.catalog.dao.CatalogNodeDAO;
import com.idatrix.resource.catalog.dao.DeptLimitedDAO;
import com.idatrix.resource.catalog.dao.MonthStatisticsDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.dao.ResourceStatisticsDAO;
import com.idatrix.resource.catalog.es.bean.EsResultBean;
import com.idatrix.resource.catalog.es.bean.EsResultBean.HitsBean;
import com.idatrix.resource.catalog.es.bean.EsResultBean.HitsBean.HighlightBean;
import com.idatrix.resource.catalog.es.utils.ElasticsearchUtil;
import com.idatrix.resource.catalog.po.CatalogNodePO;
import com.idatrix.resource.catalog.po.DeptLimitedPO;
import com.idatrix.resource.catalog.po.MonthStatisticsPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.po.ResourceStatisticsPO;
import com.idatrix.resource.catalog.service.IOverviewService;
import com.idatrix.resource.catalog.vo.MonthStatisticsVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.catalog.vo.ResourceStatisticsVO;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.LibInfo;
import com.idatrix.resource.common.utils.ResourceTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Robin Wing on 2018-5-29.
 */

@Transactional
@Service("overviewService")
public class OverviewServiceImpl implements IOverviewService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 资源目录index
     */
    private static final String RESOURCE_CATALOG_INDEX = "resource_catalog";
    /**
     * 资源目录type
     */
    private static final String RESOURCE_CATALOG_TYPE = "resource_content";
    /**
     * 资源目录默认返回结果高亮字段
     */
    private static final String DEFAULT_HIGHLIGHT_FIELD = "content";

    /**
     * 过滤换行符、制表符等
     */
    private static Pattern PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    @Autowired
    private MonthStatisticsDAO monthStatisticsDAO;

    @Autowired
    private ResourceStatisticsDAO resourceStatisticsDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private CatalogNodeDAO catalogNodeDAO;

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptLimitedDAO deptLimitedDAO;

    @Value("${is_use_full_text_search}")
    private Boolean isUseFullTextSearch;

    @Override
    public MonthStatisticsVO getOverall() {
        MonthStatisticsPO msPO = monthStatisticsDAO.getAllCount();

        MonthStatisticsVO rsVO = new MonthStatisticsVO();
        String name = "all";
        rsVO.setCatalogName(name);
        String month = month = DateTools.formatDate(new Date(), "yyyyMM");
        int pubCount = 0;
        int regCount = 0;
        int subCount = 0;
        if (msPO != null) {
            month = msPO.getMonth();
            pubCount = msPO.getPubCount();
            regCount = msPO.getRegCount();
            subCount = msPO.getSubCount();
        }
        rsVO.setMonthName(month);
        rsVO.setPubCount(pubCount);
        rsVO.setRegCount(regCount);
        rsVO.setSubCount(subCount);
        return rsVO;
    }

    @Override
    public List<ResourceStatisticsVO> getLatestResourceInfo(Long num) {
        List<ResourceStatisticsPO> rsPoList = new ArrayList<ResourceStatisticsPO>();
        rsPoList = resourceStatisticsDAO.getLatest(num);
        return transferResourceStatisticsPoToVo(rsPoList);
    }

    private List<ResourceStatisticsVO> transferResourceStatisticsPoToVo(
            List<ResourceStatisticsPO> rsPoList) {
        List<ResourceStatisticsVO> rsVOList = new ArrayList<ResourceStatisticsVO>();
        for (ResourceStatisticsPO rsPO : rsPoList) {
            ResourceStatisticsVO rsVO = new ResourceStatisticsVO();

            Long resourceId = rsPO.getId();
            ResourceConfigPO resourceConfigPO = resourceConfigDAO.getConfigById(resourceId);
            if (resourceConfigPO == null) {
                return rsVOList;
            }
            if (!StringUtils.equals(resourceConfigPO.getStatus(), PUB_SUCCESS.getStatusCode())) {
                continue;
            }

            rsVO.setResourceId(resourceId);
            rsVO.setSubCount(rsPO.getSubCount());
            rsVO.setVisitCount(rsPO.getVisitCount());
            rsVO.setName(resourceConfigPO.getName());
            rsVO.setRemark(resourceConfigPO.getRemark());
            Date dateUpdate = rsPO.getDataUpdateTime();
            if (dateUpdate != null) {
                String updateTime = DateTools.formatDate(dateUpdate);
                rsVO.setUpdateTime(updateTime);
            }

            rsVOList.add(rsVO);
        }
        return rsVOList;
    }

    @Override
    public List<MonthStatisticsVO> getMonthlyTotalAmount(int months) {

        List<MonthStatisticsVO> targetList = new ArrayList<MonthStatisticsVO>();
        List<MonthStatisticsPO> statisticslist = monthStatisticsDAO.getMonthlyTotalAmount(months);

        List<String> monthList = CommonUtils.getRecentMonthStr(months);
        for (String month : monthList) {

            boolean ownFlag = false;
            MonthStatisticsVO mvo = new MonthStatisticsVO();
            mvo.setMonthName(CommonUtils.formatMonthStr(month));
            if (statisticslist != null && statisticslist.size() > 0) {
                for (MonthStatisticsPO model : statisticslist) {
                    if (StringUtils.equals(model.getMonth(), month)) {
                        ownFlag = true;
                        mvo.setRegCount(model.getRegCount());
                        mvo.setPubCount(model.getPubCount());
                        mvo.setSubCount(model.getSubCount());
                        break;
                    }
                }
            }
            if (!ownFlag) {
                mvo.setRegCount(0);
                mvo.setPubCount(0);
                mvo.setSubCount(0);
            }
            targetList.add(mvo);
        }
        return targetList;
    }

    private List<ResourceConfigVO> transferResourceConfigPoTOVo(List<ResourceConfigPO> rcPOList) {
        List<ResourceConfigVO> rcVOList = new ArrayList<ResourceConfigVO>();
        for (ResourceConfigPO rcPO : rcPOList) {
            ResourceConfigVO rcVO = new ResourceConfigVO();
            //TODO: 资源名称需要从数据库里面获取名字
            rcVO.setCatalogName(rcPO.getCatalogFullName());
            rcVO.setCode(rcPO.getCode());
            rcVO.setName(rcPO.getName());
            rcVO.setId(rcPO.getId());
            rcVO.setDeptCode(rcPO.getDeptCode());
            rcVO.setDeptName(rcPO.getDeptName());
            rcVO.setCatalogCode(rcPO.getCatalogCode());
            rcVO.setStatus(ResourceTools.ResourceStatus.getStatusByCode(rcPO.getStatus()));
            rcVOList.add(rcVO);
        }
        return rcVOList;
    }

    /**
     * 获取环境属性is_use_full_text_search 判断是否开启了全文搜索 如果为true 则到es搜索并返回资源id
     */
    @Override
    public ResultPager<ResourceOverviewVO> getPublishedResourcesByCondition(
            ResourceCatalogSearchVO catalogSearchVO) {
        EsResultBean esResultBean;
        Map<String, HighlightBean> highlightBeanMap = null;
        List<Long> resourceIds = null;

        // 开启了全文搜索
        if (Boolean.TRUE.equals(isUseFullTextSearch) && StringUtils
                .isNotEmpty(catalogSearchVO.getKeyword())) {
            StringBuilder matchPrefix = new StringBuilder("content=");
            esResultBean = ElasticsearchUtil.search(
                    RESOURCE_CATALOG_INDEX, RESOURCE_CATALOG_TYPE, false, DEFAULT_HIGHLIGHT_FIELD,
                    matchPrefix.append(catalogSearchVO.getKeyword()).toString());

            List<HitsBean> hits = esResultBean.getHits();
            if (CollectionUtils.isNotEmpty(hits)) {
                highlightBeanMap = hits.stream()
                        .collect(HashMap::new, (k, v) ->
                                k.put(v.getId().substring(0, v.getId().indexOf("_")),
                                        v.getHighlight()), HashMap::putAll);
                resourceIds = highlightBeanMap.keySet().stream().map(key -> Long.parseLong(key))
                        .collect(Collectors.toList());
                catalogSearchVO.setResourceIds(resourceIds);
            }

            // ES搜索结果为空 直接返回
            if (CollectionUtils.isEmpty(resourceIds)) {
                return new ResultPager<>(catalogSearchVO.getPage(), 0, new ArrayList<>());
            }

        }

        PageHelper.startPage(catalogSearchVO.getPage(), catalogSearchVO.getPageSize());
        List<ResourceConfigPO> rsPOList = resourceConfigDAO.getPublishedResourcesByCondition(catalogSearchVO);
        List<ResourceOverviewVO> rcOvewviewList = transferRCPoToOverviewVO(rsPOList);

        fillHighlight(rcOvewviewList, highlightBeanMap);

        //用PageInfo对结果进行包装
        PageInfo<ResourceConfigPO> pi = new PageInfo<>(rsPOList);
        Long totalNum = pi.getTotal();
        ResultPager<ResourceOverviewVO> rp = new ResultPager<>(pi.getPageNum(),
                totalNum, rcOvewviewList);

        return rp;
    }

    /**
     * 填充ES查询高亮内容
     */
    private void fillHighlight(List<ResourceOverviewVO> overviewVOList,
            Map<String, HighlightBean> highlightBeanMap) {
        if (MapUtils.isEmpty(highlightBeanMap)) {
            return;
        }

        for (ResourceOverviewVO vo : overviewVOList) {
            List<String> high = new ArrayList<>();
            List<String> content = highlightBeanMap.get(String.valueOf(vo.getId())).getContent();

            for (int i = 0; i < content.size(); i++) {
                high.add(PATTERN.matcher(content.get(i)).replaceAll(""));
            }
            vo.setHighlight(high);
        }
    }

    @Override
    public ResultPager<ResourceOverviewVO> getLibResourcesByCondition(String resourceStatus,
            Map<String, String> conditionMap,
            Integer pageNum, Integer pageSize) {
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;

        //供DAO 按照资源状态去查询
        conditionMap.put("status", resourceStatus);
        String libValue = null;
        String libName = conditionMap.get("lib_name");
        if (StringUtils.isNotEmpty(libName)) {
            libValue = LibInfo.getLibValue(libName);
        }

        List<CatalogNodePO> catalogNodeList = catalogNodeDAO.getCatalogByParentId(0L);  //父节点为0的基础库
        if (catalogNodeList == null && catalogNodeList.size() <= 0) {
            throw new RuntimeException(LibInfo.getLibNameZH(libName) + "没有配置");
        }
        Long catalogId = null;
        for (CatalogNodePO cnPO : catalogNodeList) {
            if (StringUtils.equals(cnPO.getResourceEncode(), libValue)) {
                catalogId = cnPO.getId();
            }
        }
        if (catalogId == null) {
            throw new RuntimeException(LibInfo.getLibNameZH(libName) + "没有配置");
        }
        conditionMap.put("catalogId", catalogId.toString());
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceConfigPO> rsPOList = resourceConfigDAO.queryLibResourceByCondition(conditionMap);
        List<ResourceOverviewVO> rcOvewviewList = transferRCPoToOverviewVO(libName, rsPOList);

        //用PageInfo对结果进行包装
        PageInfo<ResourceConfigPO> pi = new PageInfo<ResourceConfigPO>(rsPOList);
        Long totalNum = pi.getTotal();
        ResultPager<ResourceOverviewVO> rp = new ResultPager<ResourceOverviewVO>(pi.getPageNum(),
                totalNum, rcOvewviewList);
        return rp;
    }

    private List<ResourceOverviewVO> transferRCPoToOverviewVO(List<ResourceConfigPO> rcPoList) {
        return transferRCPoToOverviewVO("lastest", rcPoList);
    }

    /*返回订阅标志Flag-0表示没有订阅权限，1表示可以订阅，2，表示已经订阅*/
    private int getSubscribeFlag(ResourceConfigPO rcPO) {
        int flag = 0;  //0表示没有订阅权限
        Long resourceId = rcPO.getId();

        //发布用户即为当前用户时候，不可以订阅，因为没意义
        String creatUser = rcPO.getCreator();
        String user = (String) UserHolder.getUser().getProperty("username");
        if(StringUtils.equalsIgnoreCase(creatUser, user)){
            return flag;
        }

        //资源数据量为0表示不能够订阅
        ResourceConfigPO rcPo = resourceConfigDAO.getConfigById(resourceId);
        int formatType = rcPo.getFormatType();
        if(ResourceTools.FormatType.getFormatType(formatType)!= SERVICE_INTERFACE){
            ResourceStatisticsPO rsPO = resourceStatisticsDAO.getLatestByResourceId(resourceId);
            if (null == rsPO || rsPO.getDataCount().equals(0L)) {
                return flag;
            }
        }


        //当前用户所在部门和资源提供方为同一个部门，则用户最资源不可订阅
        User userInfo = userService.findByUserName(creatUser);
        //Long resourceCreaterDeptId = userInfo.getDeptId();
        String[] depts = rcPO.getDeptNameIds().split(",");
        Long resourceCreaterDeptId = userInfo.getDeptId();
        if (depts.length > 1) {
            resourceCreaterDeptId = Long.valueOf(depts[depts.length - 1]);
        }

        Long userId = Long.valueOf(UserHolder.getUser().getId());
        Organization organization = userService.getUserOrganizationByUserId(userId);
        Long deptId = organization.getId();
        if (deptId.equals(resourceCreaterDeptId)) {
            return flag;
        }

        /*共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）
         *   根据共享类型来 限定资源是否能够共享
         */
        int shareType = rcPO.getShareType();
        if (shareType == 3) {
            return flag;
        } else if (shareType == 2) {

            boolean deptFlag = false;
            List<DeptLimitedPO> detpPoList = deptLimitedDAO.getByResourceId(resourceId);
            if (detpPoList != null && detpPoList.size() > 0) {
                for (DeptLimitedPO deptPo : detpPoList) {
                    if (deptPo.getDeptId() == deptId) {
                        deptFlag = true;
                        break;
                    }
                }
            }
            if (!deptFlag) {
                return 0;
            }
        }

        /*根据用户审批历史限制是否可以继续审批资源*/
        flag = 1; //进行到这里表示用户可以订阅

        List<SubscribePO> sPoList = subscribeDAO.getByResourceIdAndProposer(resourceId, user);
        if (sPoList != null && sPoList.size() > 0) {
            for (SubscribePO subPO : sPoList) {
                String status = subPO.getStatus();
                if (StringUtils.equals(status, SUCCESS.getStatus())) {
                    Date nowTime = new Date();
                    Date endTime = subPO.getEndDate();
                    if (endTime.after(nowTime)) {
                        flag = 2;
                    }
                } else if (StringUtils.equals(status, FAILED.getStatus())) {
                    //订阅状态为失败的时候 ，可以直接重新订阅
                } else {
                    //草稿状态时候，如果有相同直接返回
                    flag = 2;
                }
            }
        }
        return flag;
    }

    private List<ResourceOverviewVO> transferRCPoToOverviewVO(String libType,
            List<ResourceConfigPO> rcPoList) {
        List<ResourceOverviewVO> rcOvervieweList = new ArrayList<ResourceOverviewVO>();
        for (ResourceConfigPO rcPO : rcPoList) {
            ResourceOverviewVO rcOverviewVO = new ResourceOverviewVO();
            rcOverviewVO.setId(rcPO.getId());
            rcOverviewVO.setCatalogName(rcPO.getCatalogFullName());
            rcOverviewVO.setDeptCode(rcPO.getDeptCode());
            rcOverviewVO.setDeptName(rcPO.getDeptName());
            rcOverviewVO.setResourceCode(rcPO.getCode());
            rcOverviewVO.setResourceName(rcPO.getName());

            //获取用户是否能够继续订阅
            rcOverviewVO.setSubscribeFlag(getSubscribeFlag(rcPO));

            if (StringUtils.isNotEmpty(libType)) {
                rcOverviewVO.setLibType(libType);
            }

            ResourceStatisticsPO rcStatisticsPO = resourceStatisticsDAO
                    .getLatestByResourceId(rcPO.getId());
            Long dataCount = null;
            if (rcStatisticsPO == null) {
                dataCount = 0L;
            } else {
                dataCount = rcStatisticsPO.getDataCount();
            }
            rcOverviewVO.setDataCount(dataCount);

            if (StringUtils.equals(libType, "lastest")) {
                Date pubTime = rcPO.getPubDate();
                if (pubTime == null) {
                    rcOverviewVO.setUpdateTime("无");
                } else {
                    rcOverviewVO.setUpdateTime(DateTools.formatDate(pubTime));
                }
            } else {
                //从 rc_resource_statistics 读取 数据更新时间和 数据总量
                String updateTime = null;
                if (rcStatisticsPO == null) {
                    updateTime = "无";
                } else {
                    Date updateTimeDate = rcStatisticsPO.getDataUpdateTime();
                    if (updateTimeDate == null) {
                        updateTime = "无";
                    } else {
                        updateTime = DateTools.formatDate(updateTimeDate);
                    }
                }
                rcOverviewVO.setUpdateTime(updateTime);
            }

            rcOvervieweList.add(rcOverviewVO);
        }
        return rcOvervieweList;
    }
}
