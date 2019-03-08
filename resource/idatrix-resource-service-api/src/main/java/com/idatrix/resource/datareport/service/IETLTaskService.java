package com.idatrix.resource.datareport.service;

import com.idatrix.resource.datareport.dto.ETLTaskResultDto;
import com.idatrix.resource.datareport.dto.StatusFeedbackDto;

public interface IETLTaskService {
	StatusFeedbackDto updateETLTaskProcessResults(ETLTaskResultDto results);
}
