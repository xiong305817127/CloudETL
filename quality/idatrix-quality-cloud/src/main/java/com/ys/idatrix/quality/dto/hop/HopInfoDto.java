/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.hop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换连接线信息Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("线详细信息")
public class HopInfoDto {

	@ApiModelProperty("上一步骤/节点 名称")
	private String from;

	@ApiModelProperty("下一步骤/节点名称")
	private String to;

	private int fromNr;
	private int toNr;

	@ApiModelProperty("当 unconditional 为false(有条件)时,是否成功才执行")
	private boolean evaluation = true;

	@ApiModelProperty("是否无条件执行")
	private boolean unconditional = true;

	@ApiModelProperty("是否可用")
	private boolean enabled = true;

	public void setFrom(String from) {
		this.from = from;
	}
	public String getFrom() {
		return from;
	}

	public void setTo(String to) {
		this.to = to;
	}
	public String getTo() {
		return to;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * @return fromNr
	 */
	public int getFromNr() {
		return fromNr;
	}
	/**
	 * @param  设置 fromNr
	 */
	public void setFromNr(int fromNr) {
		this.fromNr = fromNr;
	}
	/**
	 * @param  设置 toNr
	 */
	public void setToNr(int toNr) {
		this.toNr = toNr;
	}

	/**
	 * @return toNr
	 */
	public int getToNr() {
		return toNr;
	}
	/**
	 * @return evaluation
	 */
	public boolean isEvaluation() {
		return evaluation;
	}
	/**
	 * @param  设置 evaluation
	 */
	public void setEvaluation(boolean evaluation) {
		this.evaluation = evaluation;
	}
	/**
	 * @return unconditional
	 */
	public boolean isUnconditional() {
		return unconditional;
	}
	/**
	 * @param  设置 unconditional
	 */
	public void setUnconditional(boolean unconditional) {
		this.unconditional = unconditional;
	}
	/* 
	 * 
	 */
	@Override
	public String toString() {
		return "HopInfoDto [from=" + from + ", to=" + to + ", fromNr=" + fromNr + ", toNr=" + toNr + ", evaluation="
				+ evaluation + ", unconditional=" + unconditional + ", enabled=" + enabled + "]";
	}


}
