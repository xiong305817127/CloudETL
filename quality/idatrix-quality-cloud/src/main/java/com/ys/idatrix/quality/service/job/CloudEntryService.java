/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.job;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.entry.EntryCopyDto;
import com.ys.idatrix.quality.dto.entry.EntryDetailsDto;
import com.ys.idatrix.quality.dto.entry.EntryHeaderDto;
import com.ys.idatrix.quality.dto.entry.EntryNameCheckResultDto;
import com.ys.idatrix.quality.dto.entry.EntryPositionDto;
import com.ys.idatrix.quality.dto.entry.JobEntryDto;

/**
 * Access to entry related information services
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudEntryService {
	
	public ReturnCodeDto addEntry(EntryHeaderDto entryHeader) throws Exception;
	
	public EntryNameCheckResultDto checkEntryName(JobEntryDto jobEntry) throws Exception;
	
	public EntryDetailsDto editEntry(JobEntryDto jobEntry) throws Exception;
	
	public ReturnCodeDto saveEntry(EntryDetailsDto entryDetails) throws Exception;
	
	public ReturnCodeDto deleteEntry(JobEntryDto jobEntry) throws Exception;
	
	public ReturnCodeDto moveEntry(EntryPositionDto entryPosition) throws Exception;

	/**
	 * @param jobEntry
	 * @return
	 * @throws Exception
	 */
	Object getDetails(JobEntryDto jobEntry) throws Exception;

	ReturnCodeDto copyEntry(EntryCopyDto entrycopy) throws Exception;
	
}
