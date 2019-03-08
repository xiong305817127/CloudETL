package com.ys.idatrix.cloudetl.service.step;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.util.Utils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.entry.entries.bigdata.SPHadoopCopyFilesPlugin;
import com.ys.idatrix.cloudetl.dto.entry.parts.CopyFilessourceFilefolderDto;
import com.ys.idatrix.cloudetl.dto.hadoop.HadoopBriefDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.FileTransmitDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileCopyDto;

@Component
@Scope("prototype")
public class FileCopyService extends StepServiceInterface<FileCopyDto>{

	@Override
	public Object createParameter(Object... params) throws Exception {
		
		FileCopyDto fileCopyDto = getStepDto();
		
		SPHadoopCopyFilesPlugin f = new SPHadoopCopyFilesPlugin();
		f.setCopyEmptyFolders(fileCopyDto.isCopyEmptyFolders());
		f.setCreateDestinationFolder(true);
		f.setAddResultFilesname(true);
		f.setDestinationIsAfile(fileCopyDto.isDestinationIsAfile());
		f.setIncludeSubfolders(fileCopyDto.isIncludeSubfolders());
		f.setOverwriteFiles(fileCopyDto.isOverwriteFiles());
		f.setRemoveSourceFiles(fileCopyDto.isRemoveSourceFiles());

		List<FileTransmitDto> fts = fileCopyDto.getFiles();
		if (fts != null && fts.size() > 0) {
			List<CopyFilessourceFilefolderDto> filefolder = Lists.newArrayList();

			String hadoopNameCache = null ;
			for (FileTransmitDto ft : fts) {
				String destinationName = ft.getDestinationName();
				if (Utils.isEmpty(destinationName) || "hdfs".equals(destinationName)) {
					if( hadoopNameCache == null ) {
						String user = CloudSession.getLoginUser();
						Map<String, List<HadoopBriefDto>> hadoopMap = stepService.cloudHadoopService.getCloudHadoopList(user);
						if (hadoopMap!= null && hadoopMap.containsKey(user) && !hadoopMap.get(user).isEmpty()) {
							hadoopNameCache = hadoopMap.get(user).get(0).getName();
						}else {
							hadoopNameCache="";
						}
					}
					if (!Utils.isEmpty(hadoopNameCache)) {
						destinationName = hadoopNameCache;
					} else {
						destinationName = "local";
					}
				}

				String sourceName = ft.getSourceName();
				if (Utils.isEmpty(sourceName) || "hdfs".equals(sourceName)) {
					if( hadoopNameCache == null ) {
						String user = CloudSession.getLoginUser();
						Map<String, List<HadoopBriefDto>> hadoopMap = stepService.cloudHadoopService.getCloudHadoopList(user);
						if (hadoopMap!= null && hadoopMap.containsKey(user) && !hadoopMap.get(user).isEmpty()) {
							hadoopNameCache = hadoopMap.get(user).get(0).getName();
						}else {
							hadoopNameCache="";
						}
					}
					if (!Utils.isEmpty(hadoopNameCache)) {
						sourceName = hadoopNameCache;
					} else {
						sourceName = "local";
					}
				}

				CopyFilessourceFilefolderDto cfsf = new CopyFilessourceFilefolderDto();
				cfsf.setDestinationConfigurationName(destinationName);
				cfsf.setDestinationFilefolder(ft.getDestinationFile());
				cfsf.setSourceConfigurationName(sourceName);
				cfsf.setSourceFilefolder(ft.getSourceFile());
				cfsf.setWildcard(ft.getFileMask());
				filefolder.add(cfsf);
			}
			f.setFilefolder(filefolder);
		}
		return f;
	}

	@Override
	public List<String> addCurStepToMeta(String jobName, String group, Map<String, String> params)
			throws Exception {

		List<String> outStepNames = Lists.newArrayList() ;
		FileCopyDto fileCopyDto = getStepDto();
		String outName = getStepName() ;
		SPHadoopCopyFilesPlugin entry = (SPHadoopCopyFilesPlugin) createParameter();
		
		stepService.addAndUpdateEntryMeta(jobName, group , outName, fileCopyDto.getType(), entry);
		outStepNames.add(outName);
		
		return outStepNames ;
		
	}

	

}
