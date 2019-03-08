package com.idatrix.unisecurity.user.bo;

import com.idatrix.unisecurity.common.domain.UUser;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class UserOnlineBo extends UUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String host;
    private Date startTime;
    private Date lastAccess;
    private long timeout;
    private boolean sessionStatus = Boolean.TRUE;

    public UserOnlineBo() {
    }

    public UserOnlineBo(UUser user) {
        super(user);
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getStartTime() {
        return new Date(this.startTime.getTime());
    }

    public void setStartTime(Date startTime) {
        this.startTime = new Date(startTime.getTime());
    }

    public Date getLastAccess() {
        return new Date(this.lastAccess.getTime());
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = new Date(lastAccess.getTime());
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(boolean sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
