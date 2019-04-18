/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.hop;

import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.stereotype.Service;

import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.hop.HopDto;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.CloudRepository;

/**
 * Entry hop service implementation.
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudHopServiceImpl implements CloudHopService {

	@Override
	public ReturnCodeDto addHop(HopDto hop) {
		try {

			if (hop.isJob()) {
				JobMeta jobMeta = CloudRepository.loadJobByName(hop.getOwner() , hop.getName(),hop.getGroup());

				JobEntryCopy from = jobMeta.findJobEntry(hop.getFrom());
				JobEntryCopy to = jobMeta.findJobEntry(hop.getTo());
				JobHopMeta hopMeta = jobMeta.findJobHop(from, to);
				if (jobMeta.findJobHop(from, to) != null) {
					// Existed
					hopMeta.setEnabled(hop.getEnabled());
					hopMeta.setUnconditional(hop.isUnconditional());
					hopMeta.setEvaluation(hop.isEvaluation());
					hopMeta.setChanged(true);
					int oldIndex = jobMeta.indexOfJobHop(hopMeta);
					jobMeta.removeJobHop(oldIndex);
					jobMeta.addJobHop(oldIndex, hopMeta);
					
				} else {
					hopMeta = new JobHopMeta();
					hopMeta.setFromEntry(jobMeta.findJobEntry(hop.getFrom()));
					hopMeta.setToEntry(jobMeta.findJobEntry(hop.getTo()));
					hopMeta.setEnabled(hop.getEnabled());
					hopMeta.setUnconditional(hop.isUnconditional());
					hopMeta.setEvaluation(hop.isEvaluation());
					jobMeta.addJobHop(hopMeta);
				}

				CloudRepository.saveJob(jobMeta,hop.getOwner() ,hop.getGroup());
			} else {
				TransMeta transMeta = CloudRepository.loadTransByName(hop.getOwner() ,hop.getName(),hop.getGroup());

				StepMeta from = transMeta.findStep(hop.getFrom());
				StepMeta to = transMeta.findStep(hop.getTo());
				TransHopMeta hopMeta = transMeta.findTransHop(from, to);
				if (transMeta.findTransHop(from, to) != null) {
					// Existed
					hopMeta.setEnabled(hop.getEnabled());
					hopMeta.setChanged(true);
					transMeta.setTransHop(transMeta.indexOfTransHop(hopMeta), hopMeta);
				} else {
					hopMeta = new TransHopMeta();
					hopMeta.setFromStep(transMeta.findStep(hop.getFrom()));
					hopMeta.setToStep(transMeta.findStep(hop.getTo()));
					hopMeta.setEnabled(hop.getEnabled());
					transMeta.addTransHop(hopMeta);
				}
				
				//增加步骤错误处理
				if(from.supportsErrorHandling() && !hop.isUnconditional()) {
					StepErrorMeta stepErrorMeta = new StepErrorMeta( transMeta, from, to, "errorSize", "errorDescriptions", "errorFieldName","errorCodes");
					stepErrorMeta.setEnabled(true);
					from.setStepErrorMeta(stepErrorMeta);
				}

				CloudRepository.saveTrans(transMeta,hop.getOwner() ,hop.getGroup());
			}

			return new ReturnCodeDto(0, "Succeeded");
		} catch (Exception e) {
			return new ReturnCodeDto(-1, CloudLogger.getExceptionMessage(e));
			// e.printStackTrace();
		}
	}

	@Override
	public ReturnCodeDto editHop(HopDto hop) {
		try {

			if (hop.isJob()) {
				JobMeta jobMeta = CloudRepository.loadJobByName(hop.getOwner() , hop.getName(),hop.getGroup());

				JobEntryCopy from = jobMeta.findJobEntry(hop.getFrom());
				JobEntryCopy to = jobMeta.findJobEntry(hop.getTo());
				JobHopMeta hopMeta = jobMeta.findJobHop(from, to);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Not found");
				}

				hopMeta.setEnabled(hop.getEnabled());
				hopMeta.setUnconditional(hop.isUnconditional());
				hopMeta.setEvaluation(hop.isEvaluation());
				hopMeta.setChanged(true);

				jobMeta.addJobHop(jobMeta.indexOfJobHop(hopMeta), hopMeta);
				CloudRepository.saveJob(jobMeta,hop.getOwner() ,hop.getGroup());
			} else {
				TransMeta transMeta = CloudRepository.loadTransByName(hop.getOwner() ,hop.getName(),hop.getGroup());

				StepMeta from = transMeta.findStep(hop.getFrom());
				StepMeta to = transMeta.findStep(hop.getTo());
				TransHopMeta hopMeta = transMeta.findTransHop(from, to);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Not found");
				}

				hopMeta.setEnabled(hop.getEnabled());
				hopMeta.setChanged(true);
				
				//增加步骤错误处理
				if(from.supportsErrorHandling() && !hop.isUnconditional()) {
					StepErrorMeta stepErrorMeta = new StepErrorMeta(transMeta, from, to) ;
					stepErrorMeta.setEnabled(true);
					from.setStepErrorMeta(stepErrorMeta);
				}

				transMeta.setTransHop(transMeta.indexOfTransHop(hopMeta), hopMeta);
				CloudRepository.saveTrans(transMeta,hop.getOwner() ,hop.getGroup());
			}

			return new ReturnCodeDto(0, "Succeeded");
		} catch (Exception e) {
			return new ReturnCodeDto(-1, CloudLogger.getExceptionMessage(e));
			// e.printStackTrace();
		}
	}

	@Override
	public ReturnCodeDto invertHop(HopDto hop) {
		try {
			
			if (hop.isJob()) {
				JobMeta jobMeta = CloudRepository.loadJobByName(hop.getOwner() ,hop.getName(),hop.getGroup());

				JobEntryCopy from = jobMeta.findJobEntry(hop.getFrom());
				JobEntryCopy to = jobMeta.findJobEntry(hop.getTo());
				JobHopMeta hopMeta = jobMeta.findJobHop(from, to);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Not found");
				}

				hopMeta.setFromEntry(to);
				hopMeta.setToEntry(from);
				hopMeta.setEnabled(hop.getEnabled());
				hopMeta.setUnconditional(hop.isUnconditional());
				hopMeta.setEvaluation(hop.isEvaluation());
				jobMeta.addJobHop(jobMeta.indexOfJobHop(hopMeta), hopMeta);
				CloudRepository.saveJob(jobMeta,hop.getOwner() ,hop.getGroup());
			}else{
				TransMeta transMeta = CloudRepository.loadTransByName(hop.getOwner() ,hop.getName(),hop.getGroup());
				
				StepMeta from = transMeta.findStep(hop.getFrom());
				StepMeta to = transMeta.findStep(hop.getTo());
				TransHopMeta hopMeta = transMeta.findTransHop(from, to);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Not found");
				}
				
				hopMeta.setFromStep(to);
				hopMeta.setToStep(from);
				hopMeta.setEnabled(hop.getEnabled());
				
				transMeta.setTransHop(transMeta.indexOfTransHop(hopMeta), hopMeta);
				CloudRepository.saveTrans(transMeta,hop.getOwner() ,hop.getGroup());
			}
			
			
			return new ReturnCodeDto(0, "Succeeded");
		} catch (Exception e) {
			return new ReturnCodeDto(-1, CloudLogger.getExceptionMessage(e));
			// e.printStackTrace();
		}
	}

	@Override
	public ReturnCodeDto deleteHop(HopDto hop) {
		try {
			if (hop.isJob()) {
				JobMeta jobMeta = CloudRepository.loadJobByName(hop.getOwner() ,hop.getName(),hop.getGroup());

				JobEntryCopy from = jobMeta.findJobEntry(hop.getFrom());
				JobEntryCopy to = jobMeta.findJobEntry(hop.getTo());
				JobHopMeta hopMeta = jobMeta.findJobHop(from, to,true);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Hop Not found");
				}

				jobMeta.removeJobHop(hopMeta);
				CloudRepository.saveJob(jobMeta,hop.getOwner() ,hop.getGroup());
			}else{
				TransMeta transMeta = CloudRepository.loadTransByName(hop.getOwner() ,hop.getName(),hop.getGroup());
				
				StepMeta from = transMeta.findStep(hop.getFrom());
				StepMeta to = transMeta.findStep(hop.getTo());
				TransHopMeta hopMeta = transMeta.findTransHop(from, to,true);
				if (hopMeta == null) {
					return new ReturnCodeDto(1, "Hod Not found");
				}
				
				transMeta.removeTransHop(hopMeta);
				
				//删除步骤错误处理
				if(from.supportsErrorHandling() && from.getStepErrorMeta() != null &&  to.getName().equals(from.getStepErrorMeta().getTargetStep().getName())) {
					from.setStepErrorMeta(null);
				}
				
				CloudRepository.saveTrans(transMeta,hop.getOwner() ,hop.getGroup());
			}
			
			return new ReturnCodeDto(0, "Succeeded");
		} catch (Exception e) {
			return new ReturnCodeDto(-1, CloudLogger.getExceptionMessage(e));
			// e.printStackTrace();
		}
	}

}
