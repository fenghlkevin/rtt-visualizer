package cn.com.cennavi.visualizer.common.dataloader.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AdminTable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3773270614446286349L;
	
	private Map<String, AdminInfo> adminsByCode=new HashMap<String, AdminInfo>();
	
	private Map<String, AdminInfo> adminsByPYName=new HashMap<String, AdminInfo>();

	public Map<String, AdminInfo> getAdminsByCode() {
		return adminsByCode;
	}

	public void setAdminsByCode(Map<String, AdminInfo> adminsByCode) {
		this.adminsByCode = adminsByCode;
	}

	public Map<String, AdminInfo> getAdminsByPYName() {
		return adminsByPYName;
	}

	public void setAdminsByPYName(Map<String, AdminInfo> adminsByPYName) {
		this.adminsByPYName = adminsByPYName;
	}


	
}
