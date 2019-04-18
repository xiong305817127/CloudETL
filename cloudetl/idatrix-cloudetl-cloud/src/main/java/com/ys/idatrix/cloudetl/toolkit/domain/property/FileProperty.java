/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * FileProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class FileProperty  extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = -6433462242049997391L;

	private String type;
	
	private String protocol;
	
	private String root;
	
	private String path;
	
	private String exts;

	private String charset;

	private long size;

	private String compress;

	private String author;

	public FileProperty(String name) {
		super(name);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExts() {
		return exts;
	}

	public void setExts(String exts) {
		this.exts = exts;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getCompress() {
		return compress;
	}

	public void setCompress(String compress) {
		this.compress = compress;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
