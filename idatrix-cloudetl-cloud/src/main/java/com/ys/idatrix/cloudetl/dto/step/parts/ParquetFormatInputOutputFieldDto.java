package com.ys.idatrix.cloudetl.dto.step.parts;

public class ParquetFormatInputOutputFieldDto {

	protected String path;
	protected String name;
	protected String type;
	protected boolean nullable;
	protected String ifNullValue;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getIfNullValue() {
		return ifNullValue;
	}

	public void setIfNullValue(String ifNullValue) {
		this.ifNullValue = ifNullValue;
	}

}
