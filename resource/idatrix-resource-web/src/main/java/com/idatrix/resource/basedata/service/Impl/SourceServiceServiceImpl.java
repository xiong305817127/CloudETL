package com.idatrix.resource.basedata.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.SourceServiceDAO;
import com.idatrix.resource.basedata.po.SourceServicePO;
import com.idatrix.resource.basedata.service.ISourceServiceService;
import com.idatrix.resource.basedata.vo.SourceServiceVO;
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
@Service("sourceServiceService")
public class SourceServiceServiceImpl implements ISourceServiceService {

	@Autowired
	private SourceServiceDAO sourceServiceDAO;

	private static final Logger LOG = LoggerFactory.getLogger(SourceServiceServiceImpl.class);

	/**
	 * 在添加新的源服务时，校验当前服务编码是否已存在，如果已存在则不许保存
	 * 在修改源服务SourceServiceA的服务编码ISO754时，校验是否存在SourceServiceB的服务编码等于ISO754，如果存在则不允许保存
	 * @param sourceServiceId
	 * @param sourceServiceCode
	 * @return
	 */
	private boolean isExistedSourceCode(Long sourceServiceId, String sourceServiceCode) {
		SourceServicePO existedService =
				sourceServiceDAO.getSourceServiceByServiceCode(sourceServiceCode);

		if (existedService != null && !existedService.getId().equals(sourceServiceId))
			return true;

		return false;
	}

	private boolean isExistedSourceServiceName(Long sourceServiceId, String sourceServiceName) {
		SourceServicePO existedService =
				sourceServiceDAO.getSourceServiceByServiceName(sourceServiceName);

		if (existedService != null && !existedService.getId().equals(sourceServiceId))
			return true;

		return false;
	}

	private String validateBeforeSaving(SourceServiceVO ss) {

		StringBuffer sb = new StringBuffer("");

		if (CommonUtils.isEmptyStr(ss.getProviderId()))
			sb.append("服务提供方未选 ");

		if (CommonUtils.isEmptyStr(ss.getServiceName()))
			sb.append("服务名称未填 ");

		if (CommonUtils.isOverLimitedLength(ss.getServiceName(), 255))
			sb.append("服务名称超出限定长度 ");

		if (isExistedSourceServiceName(ss.getId(), ss.getServiceName()))
			sb.append("唯一的服务名称已存在 ");

		if (CommonUtils.isEmptyStr(ss.getServiceCode()))
			sb.append("服务代码未填 ");

		if (isExistedSourceCode(ss.getId(), ss.getServiceCode()))
			sb.append("唯一的服务代码已存在 ");

		if (CommonUtils.isOverLimitedLength(ss.getServiceCode(), 100))
			sb.append("服务代码超出限定长度 ");

		if (CommonUtils.isEmptyStr(ss.getServiceType()))
			sb.append("服务类型未选 ");

		if (CommonUtils.isEmptyStr(ss.getUrl()))
			sb.append("访问地址未填 ");

		if (ss.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)){
			if (CommonUtils.isOverLimitedLength(ss.getUrl(), 500))
				sb.append("访问地址超出限定长度 ");

			if (CommonUtils.isEmptyStr(ss.getWsdl()))
				sb.append("服务描述不能为空 ");
		}

		if (CommonUtils.isOverLimitedLength(ss.getRemark(), 500))
			sb.append("服务说明超出限定长度");

		return sb.toString();
	}

	public String saveOrUpdateSourceService(Long rentId, String user, SourceServiceVO sourceServiceVO) {
		SourceServicePO sourceServicePO = new SourceServicePO();

		//保存之前需要对各项进行非空、长度等方面的校验
		String errMsg = validateBeforeSaving(sourceServiceVO);

		//如果不包含错误信息,则可以进行下一步操作
		if (errMsg.equals("")) {
			sourceServicePO.setProviderId(sourceServiceVO.getProviderId());
			sourceServicePO.setProviderName(sourceServiceVO.getProviderName());
			sourceServicePO.setServiceName(sourceServiceVO.getServiceName());
			sourceServicePO.setServiceCode(sourceServiceVO.getServiceCode());
			sourceServicePO.setServiceType(sourceServiceVO.getServiceType());
			sourceServicePO.setRemark(sourceServiceVO.getRemark());
			sourceServicePO.setUrl(sourceServiceVO.getUrl());
            sourceServicePO.setStatus(CommonConstants.STATUS_Y);
			sourceServicePO.setRentId(rentId);

			//如果源服务类型为RESTful类型的话，则不需要填写Url和Wsdl；反之，如果是SOAP类型的话，则这两个字段必填
			if (sourceServiceVO.getServiceType().equals(CommonConstants.SERVICE_TYPE_SOAP)
					&& !CommonUtils.isEmptyStr(sourceServiceVO.getUrl())) {
				//经李斌建议, 前台获取的wsdl文件内容仅作为前台展示使用, 后台通过httpClient的方式来重新获取
				String wsdl = CommonUtils.getWSDLContentsByRemoteAddress(sourceServicePO.getUrl());
				sourceServicePO.setWsdl(wsdl.getBytes());
			} else
				sourceServicePO.setWsdl(null);

			sourceServicePO.setModifyTime(new Date());
			sourceServicePO.setModifier(user);

			// 根据sourceServiceID是否存在来判断是否为新增，或是修改操作
			if (CommonUtils.isEmptyLongValue(sourceServiceVO.getId())) {
				sourceServicePO.setCreateTime(new Date());
				sourceServicePO.setCreator(user);
				sourceServiceDAO.insert(sourceServicePO);
			} else {
				sourceServicePO.setId(sourceServiceVO.getId());
				sourceServiceDAO.updateSourceService(sourceServicePO);
			}
		}

		return errMsg;
	}

	public SourceServiceVO getSourceServiceById(Long id) {
		SourceServicePO ssPO = sourceServiceDAO.getSourceServiceById(id);
		SourceServiceVO ssVO = new SourceServiceVO();

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


	public void deleteSourceServiceByIds(List<Long> idList) {
		sourceServiceDAO.deleteByIds(idList);
	}

	@Override
	public List<SourceServiceVO> getAllSourceService(Long rentId) {
		List<SourceServicePO> list =  sourceServiceDAO.getAllSourceService(rentId);

		List<SourceServiceVO> resultList = new ArrayList<SourceServiceVO>();

		if (list != null && !list.isEmpty()) {
			for (SourceServicePO model : list) {
				SourceServiceVO svo = new SourceServiceVO();
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

	public ResultPager<SourceServiceVO> getSourceServicesByCondition(Map<String, String> conditionMap,
																	 Integer pageNum, Integer pageSize) {
		pageNum = null == pageNum ? 1 : pageNum;
		pageSize = null == pageSize ? 10 : pageSize;
		PageHelper.startPage(pageNum, pageSize);

		List<SourceServiceVO> servicesVOList = new ArrayList<SourceServiceVO>();

		List<SourceServicePO> servicesPOList = sourceServiceDAO.getSourceServicesByCondition(conditionMap);

		if (servicesPOList != null && !servicesPOList.isEmpty()) {

			for (SourceServicePO model : servicesPOList) {
				SourceServiceVO svo = new SourceServiceVO();
				svo.setId(model.getId());
				svo.setServiceName(model.getServiceName());
				svo.setServiceCode(model.getServiceCode());
				svo.setServiceType(model.getServiceType());
				svo.setProviderId(model.getProviderId());
				svo.setProviderName(model.getProviderName());
				svo.setCreateTime(DateTools.getDateTime(model.getCreateTime()));
				svo.setCreator(model.getCreator());
				svo.setModifier(model.getModifier());
				svo.setModifyTime(model.getModifyTime());

				//Changed By robin,页面概览不需要限制wsdl内容 2018/09/19
//				if (model.getWsdl() != null)
//					svo.setWsdl(new String(model.getWsdl()));

				servicesVOList.add(svo);
			}

			//用PageInfo对结果进行包装
			PageInfo<SourceServicePO> pi = new PageInfo<SourceServicePO>(servicesPOList);
			Long totalNum = pi.getTotal();
			ResultPager<SourceServiceVO> rp = new ResultPager<SourceServiceVO>(pi.getPageNum(),
					totalNum, servicesVOList);

			return rp;
		}

		return null;
	}
}
