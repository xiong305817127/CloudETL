package com.ys.idatrix.quality.reference.agentproxy;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idatrix.es.api.dto.req.index.NewSynonymIndexDto;
import com.idatrix.es.api.dto.resp.AnalyzedTokenDto;
import com.idatrix.es.api.dto.resp.RespResult;
import com.idatrix.es.api.service.IIndexManageService;
import com.idatrix.es.api.service.IIndexMetaService;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.logger.CloudLogger;

@Service
public class EsAgentService {

	@Reference(check = false)
	private IIndexMetaService indexMetaService;
	
	@Reference(check = false)
	private IIndexManageService indexManageService ;

	public List<AnalyzedTokenDto> analyzedContentByDicName(String username, String analyzedDicId, String analyzeContent) {

		if(Utils.isEmpty(username)) {
			username = CloudSession.getLoginUser();
		}
		if( Utils.isEmpty(analyzeContent) || Utils.isEmpty(analyzedDicId) ) {
			return null ;
		}
		
		CloudLogger.getInstance().addNumber().info(this, "analyzedContentByDicName ,进行ES数据字段分析["+username+","+ analyzedDicId+","+ analyzeContent+"]...");

		 RespResult<List<AnalyzedTokenDto>> result = indexMetaService.analyzeContentForQA(username, analyzedDicId, analyzeContent);

		CloudLogger.getInstance().info(this,"analyzedContentByDicName  result:", result);

		if( result.isSuccess()) {
			return result.getData() ;
		}
		return null ;
	}

	/**
	 * 字典在es中创建索引
	 * @param username
	 * @param renterId
	 * @param dictIds
	 * @param isReCreate 是否重新创建( 删除重建 )
	 * @return
	 */
	public Boolean dictCreateIndex(String username, String renterId, List<String> dictIds ){
		
		CloudLogger.getInstance().addNumber().info(this, "dictCreateIndex ,创建Es索引["+username+","+ renterId+", dictIds: "+ Arrays.toString(dictIds.toArray())+"]...");
		
		NewSynonymIndexDto synonyIndex = new NewSynonymIndexDto();
		synonyIndex.setTenantId(renterId);
		synonyIndex.setUsername(username);
		synonyIndex.setRecreated(true);
		synonyIndex.setAnalyzedDicIds(dictIds);
		RespResult<Boolean> result = indexManageService.createIndex(synonyIndex) ;
		
		CloudLogger.getInstance().info(this,"dictCreateIndex  result:", result);
		if( result.isSuccess() ) {
			return result.getData() ;
		}
		
		return false;
	}

}
