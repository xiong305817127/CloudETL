package com.idatrix.resource.basedata.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.ServiceDAO;
import com.idatrix.resource.basedata.po.FilePO;
import com.idatrix.resource.basedata.po.ServicePO;
import com.idatrix.resource.basedata.service.IFileService;
import com.idatrix.resource.basedata.service.IServiceService;
import com.idatrix.resource.basedata.vo.FileVO;
import com.idatrix.resource.basedata.vo.ServiceQueryVO;
import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Transactional(rollbackFor = RuntimeException.class)
@Service
public class ServiceServiceImpl implements IServiceService {

    @Autowired
    private ServiceDAO serviceDAO;

    @Autowired
    private IFileService fileService;

    private static final Logger LOG = LoggerFactory.getLogger(ServiceServiceImpl.class);

    @Override
    public String saveOrUpdateService(Long rentId, String user, ServiceVO serviceVO) {
        ServicePO servicePO = new ServicePO();

        //保存之前需要对各项进行非空、长度等方面的校验
        String errMsg = validateBeforeSaving(serviceVO);

        //如果不包含错误信息,则可以进行下一步操作
        if ("".equals(errMsg)) {
            BeanUtils.copyProperties(serviceVO, servicePO);
            servicePO.setRentId(rentId);
            servicePO.setModifyTime(new Date());
            servicePO.setModifier(user);

            // 根据id判断是新增还是是修改操作
            if (CommonUtils.isEmptyLongValue(serviceVO.getId())) {
                servicePO.setCreateTime(new Date());
                servicePO.setCreator(user);
                serviceDAO.insert(servicePO);
            } else {
                servicePO.setId(serviceVO.getId());
                serviceDAO.updateService(servicePO);
            }

            // 若文件id列表不为空 则更新文件parentId
            if (!CollectionUtils.isEmpty(serviceVO.getFileIds())) {
                fileService.batchUpdateParentIdByIds(serviceVO.getFileIds(),
                        servicePO.getId());
            }
        }
        return errMsg;
    }

    @Override
    public String deleteServiceByIds(List<Long> idList) {
        List<ServicePO> servicePOList = serviceDAO.getOccupiedServicePOList(idList);
        StringBuffer sb = new StringBuffer();

        //如果要被删除的服务已被资源绑定，则不允许被删除
        if (servicePOList == null || servicePOList.isEmpty()) {
            serviceDAO.deleteByIds(idList);
            return null;
        } else {
            for (ServicePO model : servicePOList) {
                sb.append(model.getServiceName()).append(" ");
            }
            return sb.toString();
        }
    }

    @Override
    public ServiceVO getServiceById(Long id, String username) {
        ServicePO servicePO = serviceDAO.getServiceById(id);
        ServiceVO serviceVO = new ServiceVO();

        if (servicePO == null) {
            return null;
        }

        BeanUtils.copyProperties(servicePO, serviceVO);
        List<FilePO> filePOList = fileService
                .getFilesBySourceAndParentIdAndCreator(1, id, username);
        List<FileVO> fileVOList = filePOList.stream().map(e -> new FileVO(e.getId(),
                e.getOriginFileName())).collect(Collectors.toList());
        serviceVO.setFileList(fileVOList);

        return serviceVO;
    }

    @Override
    public List<ServiceVO> getAllService(Long rentId) {
        List<ServicePO> list = serviceDAO.getAllService(rentId);
        List<ServiceVO> resultList = new ArrayList<>();

        if (CollectionUtils.isEmpty(list)) {
            return resultList;
        }
        for (ServicePO servicePO : list) {
            ServiceVO serviceVO = new ServiceVO();
            BeanUtils.copyProperties(servicePO, serviceVO);
            resultList.add(serviceVO);
        }
        return resultList;
    }

    @Override
    public ResultPager<ServiceVO> getServicesByCondition(ServiceQueryVO queryVO) {
        PageHelper.startPage(queryVO.getPage(), queryVO.getPageSize());

        List<ServiceVO> servicesVOList = new ArrayList<>();
        List<ServicePO> servicesPOList = serviceDAO.getServicesByCondition(queryVO);
        if (CollectionUtils.isEmpty(servicesPOList)) {
            return null;
        }
        for (ServicePO servicePO : servicesPOList) {
            ServiceVO serviceVO = new ServiceVO();
            BeanUtils.copyProperties(servicePO, serviceVO);
            serviceVO.setCreateTime(DateTools.getDateTime(servicePO.getCreateTime()));
            servicesVOList.add(serviceVO);
        }

        //用PageInfo对结果进行包装
        PageInfo<ServicePO> pi = new PageInfo<>(servicesPOList);
        Long totalNum = pi.getTotal();
        ResultPager<ServiceVO> rp = new ResultPager<>(pi.getPageNum(),
                totalNum, servicesVOList);
        return rp;
    }

    /**
     * 在添加新的服务时，校验当前服务编码是否已存在，如果已存在则不许保存 在修改服务SourceServiceA的服务编码ISO754时，校验是否存在SourceServiceB的服务编码等于ISO754，如果存在则不允许保存
     */
    private boolean isExistedServiceCode(Long serviceId, String serviceCode) {
        ServicePO existedService = serviceDAO.getServiceByServiceCode(serviceCode);
        if (existedService != null && !existedService.getId().equals(serviceId)) {
            return true;
        }
        return false;
    }

    private boolean isExistedServiceName(Long serviceId, String serviceName) {
        ServicePO existedService = serviceDAO.getServiceByServiceName(serviceName);
        if (existedService != null && !existedService.getId().equals(serviceId)) {
            return true;
        }
        return false;
    }

    private String validateBeforeSaving(ServiceVO ss) {
        StringBuffer sb = new StringBuffer("");
        if (CommonUtils.isEmptyStr(ss.getProviderId())) {
            sb.append("服务提供方未选 ");
        }
        if (CommonUtils.isEmptyStr(ss.getServiceName())) {
            sb.append("服务名称未填 ");
        }
        if (CommonUtils.isOverLimitedLength(ss.getServiceName(), 255)) {
            sb.append("服务名称超出限定长度 ");
        }
        if (isExistedServiceName(ss.getId(), ss.getServiceName())) {
            sb.append("唯一的服务名称已存在 ");
        }
        if (CommonUtils.isEmptyStr(ss.getServiceCode())) {
            sb.append("服务代码未填 ");
        }
        if (isExistedServiceCode(ss.getId(), ss.getServiceCode())) {
            sb.append("唯一的服务代码已存在 ");
        }
        if (CommonUtils.isOverLimitedLength(ss.getServiceCode(), 100)) {
            sb.append("服务代码超出限定长度 ");
        }
        if (CommonUtils.isEmptyStr(ss.getServiceType())) {
            sb.append("服务类型未选 ");
        }
        if (CommonUtils.isEmptyStr(ss.getUrl())) {
            sb.append("访问地址未填 ");
        }
        if (CommonUtils.isOverLimitedLength(ss.getRemark(), 500)) {
            sb.append("服务说明超出限定长度");
        }
        return sb.toString();
    }
}
