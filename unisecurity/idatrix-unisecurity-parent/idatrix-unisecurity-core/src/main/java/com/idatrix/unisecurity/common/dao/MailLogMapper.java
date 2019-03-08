package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.MailLog;

/**
 * Created by huangyi on 2017/7/22.
 */
public interface MailLogMapper {

	void insert(MailLog mailLog);

	void update(MailLog mailLog);

	int findById(MailLog mailLog);

	void updateStatus(String id, String status);

	Integer getMaxId();
	
}
