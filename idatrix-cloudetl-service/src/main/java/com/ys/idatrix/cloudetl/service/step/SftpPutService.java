package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.entry.entries.filetransfer.SPSftpput;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.SftpPutDto;

@Component
@Scope("prototype")
public class SftpPutService  extends StepServiceInterface<SftpPutDto>{

	@Override
	public Object createParameter(Object... params) throws Exception {
		
		SftpPutDto sftpPutDto = getStepDto();
		
		SPSftpput sp =new SPSftpput();
		
		sp.setServerName(sftpPutDto.getServerName());
		sp.setServerPort(sftpPutDto.getServerPort());
		sp.setUserName(sftpPutDto.getUserName());
		sp.setPassword(sftpPutDto.getPassword());
		
		sp.setSftpDirectory(sftpPutDto.getSftpDirectory());
		
		sp.setLocalDirectory(sftpPutDto.getLocalDirectory());
		sp.setIncludeSubFolders(sftpPutDto.isIncludeSubFolders());
		sp.setWildcard(sftpPutDto.getFileMask());;
		
		sp.setSuccessWhenNoFile(sftpPutDto.isSuccessWhenNoFile()); //本地没有文件时 成功
		sp.setAddFilenameResut(sftpPutDto.isAddFilenameResut());
		sp.setCreateRemoteFolder(sftpPutDto.isCreateRemoteFolder());
		
		sp.setUsekeyfilename(sftpPutDto.isUsekeyfilename());
		sp.setKeyfilename(sftpPutDto.getKeyfilename());
		sp.setKeyfilepass(sftpPutDto.getKeyfilepass());
		
		sp.setProxyType(sftpPutDto.getProxyType());
		sp.setProxyHost(sftpPutDto.getProxyHost());
		sp.setProxyPort(sftpPutDto.getProxyPort());
		sp.setProxyUsername(sftpPutDto.getProxyUsername());
		sp.setProxyPassword(sftpPutDto.getProxyPassword());
		
		sp.setAfterFTPS(sftpPutDto.getAfterFTPS()); // 0:什么事都不做  1:上传之后删除原文件 2:上传之后移动文件到destinationfolder
		if( sftpPutDto.getAfterFTPS() == 2 ) {
			sp.setDestinationfolder(sftpPutDto.getDestinationfolder());
		}
	
		return sp;
	}

	@Override
	public List<String> addCurStepToMeta(String jobName, String group, Map<String, String> params)
			throws Exception {

		List<String> outStepNames = Lists.newArrayList();
		SftpPutDto insertUpdate = getStepDto();
		String outputName = getStepName();

		SPSftpput  entry = (SPSftpput) createParameter();
		stepService.addAndUpdateEntryMeta(jobName, group , outputName, insertUpdate.getType(), entry);
		outStepNames.add(outputName);
		
		return outStepNames ;
		
	}

	

}
