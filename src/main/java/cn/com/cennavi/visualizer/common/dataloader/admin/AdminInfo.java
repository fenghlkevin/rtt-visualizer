package cn.com.cennavi.visualizer.common.dataloader.admin;

import java.io.Serializable;

public class AdminInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5760032247796780323L;
	private String id;
	private String value;
	private String pyname;
	
	public String getPyname() {
		return pyname;
	}
	public void setPyname(String pyname) {
		this.pyname = pyname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
