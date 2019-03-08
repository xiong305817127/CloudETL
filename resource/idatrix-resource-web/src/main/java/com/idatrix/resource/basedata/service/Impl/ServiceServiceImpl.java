package com.idatrix.resource.basedata.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.ServiceDAO;
import com.idatrix.resource.basedata.po.ServicePO;
import com.idatrix.resource.basedata.service.IServiceService;
import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service("srvService")
public class ServiceServiceImpl implements IServiceService {

	@Autowired
	private ServiceDAO serviceDAO;

	private static final Logger LOG = LoggerFactory.getLogger(ServiceServiceImpl.class);

	/**
	 * 在添加新的服务时，校验当前服务编码是否已存在，如果已存在则不许保存
	 * 在修改服务SourceServiceA的服务编码ISO754时，校验是否存在SourceServiceB的服务编码等于ISO754，如果存在则不允许保存
	 * @param serviceId
	 * @param serviceCode
	 * @return
	 */
	private boolean isExistedServiceCode(Long serviceId, String serviceCode) {
		ServicePO existedService =
				serviceDAO.getServiceByServiceCode(serviceCode);

		if (existedService != null && !existedService.getId().equals(serviceId))
			return true;

		return false;
	}

	private boolean isExistedServiceName(Long serviceId, String serviceName) {
		ServicePO existedService =
				serviceDAO.getServiceByServiceName(serviceName);

		if (existedService != null && !existedService.getId().equals(serviceId))
			return true;

		return false;
	}

	private String validateBeforeSaving(ServiceVO ss) {

		StringBuffer sb = new StringBuffer("");

		if (CommonUtils.isEmptyStr(ss.getProviderId()))
			sb.append("服务提供方未选 ");

		if (CommonUtils.isEmptyStr(ss.getServiceName()))
			sb.append("服务名称未填 ");

		if (CommonUtils.isOverLimitedLength(ss.getServiceName(), 255))
			sb.append("服务名称超出限定长度 ");

		if (isExistedServiceName(ss.getId(), ss.getServiceName()))
			sb.append("唯一的服务名称已存在 ");

		if (CommonUtils.isEmptyStr(ss.getServiceCode()))
			sb.append("服务代码未填 ");

		if (isExistedServiceCode(ss.getId(), ss.getServiceCode()))
			sb.append("唯一的服务代码已存在 ");

		if (CommonUtils.isOverLimitedLength(ss.getServiceCode(), 100))
			sb.append("服务代码超出限定长度 ");

		if (CommonUtils.isEmptyStr(ss.getServiceType()))
			sb.append("服务类型未选 ");

		if (CommonUtils.isEmptyStr(ss.getUrl()))
			sb.append("访问地址未填 ");

		if (ss.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)){

			if (CommonUtils.isOverLimitedLength(ss.getUrl(), 500))
				sb.append("访问地址超出限定长度").append(" ");

			if (CommonUtils.isEmptyStr(ss.getWsdl()))
				sb.append("服务描述不能为空").append(" ");
		}

		if (CommonUtils.isOverLimitedLength(ss.getRemark(), 500))
			sb.append("服务说明超出限定长度");

		return sb.toString();
	}

	public String saveOrUpdateService(String user, ServiceVO serviceVO) {
		ServicePO servicePO = new ServicePO();

		//保存之前需要对各项进行非空、长度等方面的校验
		String errMsg = validateBeforeSaving(serviceVO);

		//如果不包含错误信息,则可以进行下一步操作
		if (errMsg.equals("")) {
			servicePO.setProviderId(serviceVO.getProviderId());
			servicePO.setProviderName(serviceVO.getProviderName());
			servicePO.setServiceName(serviceVO.getServiceName());
			servicePO.setServiceCode(serviceVO.getServiceCode());
			servicePO.setServiceType(serviceVO.getServiceType());
			servicePO.setRemark(serviceVO.getRemark());
			servicePO.setUrl(serviceVO.getUrl());
			servicePO.setStatus(CommonConstants.STATUS_Y);

			//如果服务类型为RESTful类型的话，则不需要填写Url和Wsdl；反之，如果是SOAP类型的话，则这两个字段必填
			if (serviceVO.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)
					&& !CommonUtils.isEmptyStr(serviceVO.getUrl())) {
				//经李斌建议, 前台获取的wsdl文件内容仅作为前台展示使用, 后台通过httpClient的方式来重新获取
				String wsdl = CommonUtils.getWSDLContentsByRemoteAddress(serviceVO.getUrl());
				servicePO.setWsdl(wsdl.getBytes());
			}
			else
				servicePO.setWsdl(null);

			servicePO.setModifyTime(new Date());
			servicePO.setModifier(user);

			// 根据sourceServiceID是否存在来判断是否为新增，或是修改操作
			if (CommonUtils.isEmptyLongValue(serviceVO.getId())) {
				servicePO.setCreateTime(new Date());
				servicePO.setCreator(user);
				serviceDAO.insert(servicePO);
			} else {
				servicePO.setId(serviceVO.getId());
				servicePO.setStatus(servicePO.getStatus());
				serviceDAO.updateService(servicePO);
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

	public ServiceVO getServiceById(Long id) {
		ServicePO ssPO = serviceDAO.getServiceById(id);
		ServiceVO ssVO = new ServiceVO();

		if (ssPO != null) {
			ssVO.setId(ssPO.getId());
			ssVO.setProviderId(ssPO.getProviderId());
			ssVO.setProviderName(ssPO.getProviderName());
			ssVO.setServiceName(ssPO.getServiceName());
			ssVO.setServiceCode(ssPO.getServiceCode());
			ssVO.setServiceType(ssPO.getServiceType());
			ssVO.setRemark(ssPO.getRemark());
			ssVO.setUrl(ssPO.getUrl());

			if (ssPO.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)) {
				if (ssPO.getWsdl() != null)
				ssVO.setWsdl(new String(ssPO.getWsdl()));
			}

			return ssVO;
		}

		return null;
	}

	public List<ServiceVO> getAllService() {
		List<ServicePO> list =  serviceDAO.getAllService();

		List<ServiceVO> resultList = new ArrayList<ServiceVO>();

		if (list != null && !list.isEmpty()) {
			for (ServicePO model : list) {
				ServiceVO svo = new ServiceVO();
				svo.setId(model.getId());
				svo.setProviderId(model.getProviderId());
				svo.setServiceName(model.getServiceName());
				svo.setServiceCode(model.getServiceCode());
				svo.setServiceType(model.getServiceType());
				svo.setRemark(model.getRemark());
				svo.setUrl(model.getUrl());

				if (model.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)) {
					if (model.getWsdl() != null)
						svo.setWsdl(new String(model.getWsdl()));
				}

				resultList.add(svo);
			}
		}

		return resultList;
	}

	@Override
	public ResultPager<ServiceVO> getServicesByCondition(Map<String, String> conditionMap,
														 Integer pageNum, Integer pageSize) {
		pageNum = null == pageNum ? 1 : pageNum;
		pageSize = null == pageSize ? 10 : pageSize;
		PageHelper.startPage(pageNum, pageSize);

		List<ServiceVO> servicesVOList = new ArrayList<ServiceVO>();

		List<ServicePO> servicesPOList = serviceDAO.getServicesByCondition(conditionMap);

		if (servicesPOList != null && !servicesPOList.isEmpty()) {

			for (ServicePO model : servicesPOList) {
				ServiceVO svo = new ServiceVO();
				svo.setId(model.getId());
				svo.setServiceName(model.getServiceName());
				svo.setServiceCode(model.getServiceCode());
				svo.setServiceType(model.getServiceType());
				svo.setProviderId(model.getProviderId());
				svo.setProviderName(model.getProviderName());
				svo.setCreator(model.getCreator());
				svo.setCreateTime(DateTools.getDateTime(model.getCreateTime()));
				svo.setModifier(model.getModifier());
				svo.setModifyTime(model.getModifyTime());

				if (model.getWsdl() != null)
					svo.setWsdl(new String(model.getWsdl()));

				servicesVOList.add(svo);
			}

			//用PageInfo对结果进行包装
			PageInfo<ServicePO> pi = new PageInfo<ServicePO>(servicesPOList);
			Long totalNum = pi.getTotal();
			ResultPager<ServiceVO> rp = new ResultPager<ServiceVO>(pi.getPageNum(),
					totalNum, servicesVOList);
			return rp;
		}

		return null;
	}
}
