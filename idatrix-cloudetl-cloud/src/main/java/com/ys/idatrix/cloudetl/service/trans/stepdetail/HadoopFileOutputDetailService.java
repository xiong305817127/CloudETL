/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AccessInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class HadoopFileOutputDetailService implements StepDetailService {

	@Autowired
	private HadoopFileInputDetailService HadoopFileInputDetailService;

	@Override
	public String getStepDetailType() {
		return "HadoopFileOutputPlugin";
	}

	/**
	 * flag : getFile , getFields
	 * @throws Exception 
	 */
	@Override
	public List<Object> dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getFile":
			return HadoopFileInputDetailService.dealStepDetailByflag("getFile", param);
		default:
			return null;

		}

	}

}
