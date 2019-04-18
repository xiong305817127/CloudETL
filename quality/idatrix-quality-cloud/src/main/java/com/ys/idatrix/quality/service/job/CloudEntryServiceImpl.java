/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.job.JobHopMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.entry.EntryCopyDto;
import com.ys.idatrix.quality.dto.entry.EntryDetailsDto;
import com.ys.idatrix.quality.dto.entry.EntryHeaderDto;
import com.ys.idatrix.quality.dto.entry.EntryNameCheckResultDto;
import com.ys.idatrix.quality.dto.entry.EntryPositionDto;
import com.ys.idatrix.quality.dto.entry.JobEntryDto;

import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.repository.CloudRepository;
import com.ys.idatrix.quality.service.job.entrydetail.EntryDetailService;

/**
 * Entry service implementation.
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudEntryServiceImpl implements CloudEntryService {

	@Autowired(required = false)
	List<EntryDetailService> entryDetailService;

	@Override
	public ReturnCodeDto addEntry(EntryHeaderDto entryHeader) throws Exception {
		JobMeta jobMeta = CloudRepository.loadJobByName(entryHeader.getOwner() , entryHeader.getJobName(),entryHeader.getGroup());
		
		String entryType = entryHeader.getEntryType();
		if("DUMMY".equalsIgnoreCase(entryType)) {
			entryType = "SPECIAL";
		}
		PluginRegistry registry = PluginRegistry.getInstance();
		PluginInterface entryPlugin = registry.findPluginWithId(JobEntryPluginType.class, entryType);
		if (entryPlugin != null) {
			JobEntryInterface info = (JobEntryInterface) registry.loadClass(entryPlugin);
			info.setName(entryHeader.getEntryName());
			
			if("DUMMY".equalsIgnoreCase(entryHeader.getEntryType()) && info instanceof JobEntrySpecial ) {
				OsgiBundleUtils.invokeOsgiMethod(info, "setStart", false);
				OsgiBundleUtils.invokeOsgiMethod(info, "setDummy", true);
			}
			
			JobEntryCopy jobEntry = new JobEntryCopy(info);
			jobEntry.setName(entryHeader.getEntryName());
			jobEntry.setObjectId(null);
			jobEntry.setEntry(info);
			jobEntry.setLocation(jobMeta.nrJobEntries()*150+50, 100);
			jobEntry.setDrawn(true);
			jobEntry.setDescription("");
			if (!jobMeta.containsJobCopy(jobEntry)) {
				jobMeta.addJobEntry(jobEntry);
				CloudRepository.saveJob(jobMeta,entryHeader.getOwner() ,entryHeader.getGroup());
				return new ReturnCodeDto(0, "Succeeded");
			} else {
				return new ReturnCodeDto(1, "Duplicate entry name");
			}
		}
		return new ReturnCodeDto(1, "No plugin");
	}

	@Override
	public ReturnCodeDto copyEntry(EntryCopyDto entrycopy ) throws Exception{
		
			JobMeta fromJobMeta = CloudRepository.loadJobByName( Const.NVL(entrycopy.getFromOwner(),entrycopy.getOwner()) ,entrycopy.getFromJobName(),entrycopy.getFromGroup());
			JobMeta toJobMeta = fromJobMeta;
			if( !Utils.isEmpty(entrycopy.getToJobName()) && !entrycopy.getFromJobName().equals( entrycopy.getToJobName())) {
				toJobMeta =  CloudRepository.loadJobByName(  Const.NVL(entrycopy.getToOwner(),entrycopy.getOwner())  , entrycopy.getToJobName(),entrycopy.getToGroup());
			}
			JobEntryCopy fromEntryMeta = fromJobMeta.findJobEntry(entrycopy.getFromEntryName());
			if (fromEntryMeta == null ) {
				// fromEntryMeta existed.
				throw new KettleException("Not existed: JobName:"+entrycopy.getFromJobName()+" EntryName:"+entrycopy.getFromEntryName());
			}
			
			JobEntryCopy toEntryMeta = (JobEntryCopy) fromEntryMeta.clone();
			toEntryMeta.setName(entrycopy.getToEntryName());
			toEntryMeta.setLocation(fromEntryMeta.getLocation().x+20, fromEntryMeta.getLocation().y+20);
			toJobMeta.addJobEntry(toEntryMeta);
			
			CloudRepository.saveJob(toJobMeta,  Const.NVL(entrycopy.getToOwner(),entrycopy.getOwner()) ,entrycopy.getToGroup());
			return new ReturnCodeDto(0, toEntryMeta.getLocation());
	}
	
	@Override
	public EntryNameCheckResultDto checkEntryName(JobEntryDto jobEntry) throws Exception {
		if (jobEntry.getJobName() == null || jobEntry.getEntryName() == null) {
			throw new KettleException("JobName or EntryName is empty");
		}
		JobMeta jobMeta = CloudRepository.loadJobByName(jobEntry.getOwner() , jobEntry.getJobName(),jobEntry.getGroup());
		if (!jobMeta.getJobCopies().stream()
				.filter(copy -> jobEntry.getEntryName().equalsIgnoreCase(copy.getEntry().getName())).findAny()
				.isPresent()) {
			return new EntryNameCheckResultDto(false); // Not existed.
		}
		return new EntryNameCheckResultDto(true); // Existed.
	}

	@Override
	public EntryDetailsDto editEntry(JobEntryDto jobEntry) throws Exception {
		JobMeta jobMeta = CloudRepository.loadJobByName(jobEntry.getOwner() , jobEntry.getJobName(),jobEntry.getGroup());

		JobEntryCopy entryMeta = jobMeta.findJobEntry(jobEntry.getEntryName());
		if (entryMeta == null) {
			throw new KettleException(
					"Not existed: JobName:" + jobEntry.getJobName() + " EntryName:" + jobEntry.getEntryName());
		}

		EntryDetailsDto details = new EntryDetailsDto();
		details.setJobName(jobEntry.getJobName());
		details.setEntryName(jobEntry.getEntryName());
		details.setNewName(""); // New entry name is empty

		details.setDescription(entryMeta.getDescription());
		details.setType(entryMeta.getEntry().getPluginId());
		details.setParallel(entryMeta.isLaunchingInParallel());
		
		// Load entry parameters per type
		details.encodeEntryParams(entryMeta);
		
		int count = jobMeta.findNrPrevJobEntries(entryMeta);
		String[] prev = new String[count];
		for (int i = 0; i < count; i++) {
			prev[i] = jobMeta.findPrevJobEntry(entryMeta, i).getName();
		}

		count = jobMeta.findNrNextJobEntries(entryMeta);
		String[] next = new String[count];
		for (int i = 0; i < count; i++) {
			next[i] = jobMeta.findNextJobEntry(entryMeta, i).getName();
		}

		details.setNextEntryNames(next);
		details.setPrevEntryNames(prev);

		return details;
	}

	@Override
	public ReturnCodeDto saveEntry(EntryDetailsDto entryDetails) throws Exception {
		
		JobMeta jobMeta = CloudRepository.loadJobByName(entryDetails.getOwner() , entryDetails.getJobName(),entryDetails.getGroup());
		JobEntryCopy entryMeta = jobMeta.findJobEntry(entryDetails.getEntryName());
		if (entryMeta == null) { // Not existed. Add it ??
			ReturnCodeDto r = addEntry(new EntryHeaderDto(entryDetails.getOwner(),entryDetails.getJobName(), entryDetails.getEntryName(), entryDetails.getType()));
			if( !r.isSuccess() ) {
				return r ;
			}
			jobMeta = CloudRepository.loadJobByName(entryDetails.getOwner() , entryDetails.getJobName(),entryDetails.getGroup());
			entryMeta = jobMeta.findJobEntry(entryDetails.getEntryName());
		}

		entryDetails.decodeParameterObject(entryMeta, jobMeta);
		entryMeta.setLaunchingInParallel(entryDetails.isParallel());//是否并行执行
		// Change entry name if new name given
		entryMeta.setName(Const.NVL(entryDetails.getNewName(), entryDetails.getEntryName()));

		// jobMeta.addOrReplaceEntry(entryMeta);
		CloudRepository.saveJob(jobMeta,entryDetails.getOwner() ,entryDetails.getGroup());
		return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public ReturnCodeDto deleteEntry(JobEntryDto jobEntry) throws Exception {
		JobMeta jobMeta = CloudRepository.loadJobByName(jobEntry.getOwner() , jobEntry.getJobName(),jobEntry.getGroup());

		JobEntryCopy entryMeta = jobMeta.findJobEntry(jobEntry.getEntryName());
		if (entryMeta == null) {
			return new ReturnCodeDto(1, "Entry not existed");
		}

		// !!! Here we should remove all hops connected to the entry !!!
		JobHopMeta fromHop = jobMeta.findJobHopFrom(entryMeta);
		while (fromHop != null) {
			jobMeta.removeJobHop(fromHop);
			fromHop = jobMeta.findJobHopFrom(entryMeta);
		}

		JobHopMeta toHop = jobMeta.findJobHopTo(entryMeta);
		while (toHop != null) {
			jobMeta.removeJobHop(toHop);
			toHop = jobMeta.findJobHopTo(entryMeta);
		}

		jobMeta.removeJobEntry(jobMeta.indexOfJobEntry(entryMeta));

		CloudRepository.saveJob(jobMeta,jobEntry.getOwner() ,jobEntry.getGroup());
		return new ReturnCodeDto(0, "Succeeded");
	}

	@Override
	public ReturnCodeDto moveEntry(EntryPositionDto entryPosition) throws Exception {
		JobMeta jobMeta = CloudRepository.loadJobByName(entryPosition.getOwner() , entryPosition.getJobName(),entryPosition.getGroup());

		JobEntryCopy entryMeta = jobMeta.findJobEntry(entryPosition.getEntryName());
		if (entryMeta == null) {
			return new ReturnCodeDto(1, "Entry not existed");
		}

		entryMeta.setLocation(entryPosition.getXloc(), entryPosition.getYloc());
		entryMeta.setNr(entryPosition.getNr());
		CloudRepository.saveJob(jobMeta,entryPosition.getOwner() ,entryPosition.getGroup());
		return new ReturnCodeDto(0, "Succeeded");
	}

	 @Override
	public Object getDetails(JobEntryDto jobEntry) throws Exception {
		JobMeta jobMeta = CloudRepository.loadJobByName(jobEntry.getOwner() , jobEntry.getJobName(),jobEntry.getGroup());
		JobEntryCopy entryMeta = jobMeta.findJobEntry(jobEntry.getEntryName());
		if (entryMeta == null) {
			throw new KettleException(jobEntry.getEntryName() + " is not exist!");
		}
		if (StringUtils.isEmpty(jobEntry.getDetailType())) {
			throw new KettleException(jobEntry.getDetailType() + " is not exist!");
		}
		if (entryDetailService == null || entryDetailService.size() == 0) {
			throw new KettleException(jobEntry.getDetailType() + " detail service is not exist!");
		}

		Optional<EntryDetailService> stepDetailServiceOpt = entryDetailService.stream()
				.filter(service -> jobEntry.getDetailType().equals(service.getEntryDetailType())).findFirst();
		if (stepDetailServiceOpt.isPresent()) {
			Map<String, Object> param = jobEntry.getDetailParam();
			return stepDetailServiceOpt.get().dealEntryDetailByflag(param.get("flag").toString(), param);
		} else {
			throw new KettleException(jobEntry.getDetailType() + " detail service is not exist!");
		}

	}

}
