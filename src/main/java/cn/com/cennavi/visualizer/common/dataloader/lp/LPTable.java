package cn.com.cennavi.visualizer.common.dataloader.lp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.com.cennavi.transform.bean.LPInfo;

/**
 * save all tmctable Maps
 * @author fengheliang
 *
 */
public class LPTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8631323093240588295L;

	/**
	 */
	private Map<Long,LPInfo> lpMap=new HashMap<Long, LPInfo>();

	public Map<Long, LPInfo> getLpMap() {
		return lpMap;
	}

	public void setLpMap(Map<Long, LPInfo> lpMap) {
		this.lpMap = lpMap;
	}


	
	
}
