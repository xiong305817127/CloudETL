package com.ys.idatrix.cloudetl.recovery.trans.dto;

public class TransInfoDto {
	
	private String user;
	private String owner;
	private String transName;
	private Long startTime;
	
	
	public TransInfoDto() {
		super();
		this.startTime = System.currentTimeMillis();
	}
	
	public TransInfoDto(String user, String transName) {
		super();
		this.user = user;
		this.transName = transName;
	}
	
	/**
	 * @param user
	 * @param transName
	 * @param time
	 */
	public TransInfoDto(String user, String transName, Long time) {
		super();
		this.user = user;
		this.transName = transName;
		this.startTime = time;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param  设置 user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the transName
	 */
	public String getTransName() {
		return transName;
	}
	/**
	 * @param  设置 transName
	 */
	public void setTransName(String transName) {
		this.transName = transName;
	}

	/**
	 * @return the startTime
	 */
	public Long getStartTime() {
		return startTime;
	}

	/**
	 * @param  设置 startTime
	 */
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
	
	
}
