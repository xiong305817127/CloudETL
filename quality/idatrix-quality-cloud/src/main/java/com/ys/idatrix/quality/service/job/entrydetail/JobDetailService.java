/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job.entrydetail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.springframework.stereotype.Service;

import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.repository.CloudRepository;


/**
 *  AccessInput related Detail Service
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class JobDetailService implements EntryDetailService {


	@Override
	public String getEntryDetailType() {
		return "JOB";
	}

	/**
	 * flag : getParameters 
	 * @throws Exception 
	 */
	@Override
	public List<Object> dealEntryDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			throw new KettleException("flag is null!");
		}

		switch (flag) {
		case "getParameters":
			return getParameters(param);
		default:
			throw new KettleException("flag is not support!");

		}

	}

	/**
	 * @param fileName tableName
	 * @return Access Fields list
	 * @throws Exception 
	 */
	private List<Object> getParameters(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "jobName");
		
		String owner = Const.NVL((String)params.get("owner"),CloudSession.getResourceUser());
		String jobName = params.get("jobName").toString();
		String group = Const.NVL((String)params.get("group"),null);
		
		JobMeta JobMeta = CloudRepository.loadJobByName(owner,jobName,group);
		return Arrays.asList((Object[])JobMeta.listParameters());
	
	}

	
}
