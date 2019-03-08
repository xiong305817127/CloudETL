/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.hop;

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.hop.HopDto;

/**
 *
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudHopService {
	
	public ReturnCodeDto addHop(HopDto transHop);
	
	public ReturnCodeDto editHop(HopDto transHop);
	
	public ReturnCodeDto invertHop(HopDto transHop);
	
	public ReturnCodeDto deleteHop(HopDto transHop);

}
