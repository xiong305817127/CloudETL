package com.ys.idatrix.quality.dubbo.reference.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idatrix.resource.datareport.service.IETLTaskService;

@Service
public class SampleServiceImpl  {

	public static final Log logger = LogFactory.getLog("订阅任务状态推送");

	@Reference(check = false)
	private IETLTaskService taskService;
	



}
