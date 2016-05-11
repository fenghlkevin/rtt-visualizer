package cn.com.cennavi.visualizer.common.dataloader.mt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * save all tmctable Maps
 * @author fengheliang
 *
 */
public class MTTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8631323093240588295L;

	/**
	 * 每个linkid对应的mt值
	 */
	private Map<Long,MTInfo> mtMap=new HashMap<Long, MTInfo>();
	
	/**
	 * 每个locid对应的MT序列
	 */
	private Map<Long,List<MTInfo>> mtLinksMap=new HashMap<Long, List<MTInfo>>();

	public Map<Long, MTInfo> getMtMap() {
		return mtMap;
	}

	public void setMtMap(Map<Long, MTInfo> mtMap) {
		this.mtMap = mtMap;
	}

	public Map<Long, List<MTInfo>> getMtLinksMap() {
		return mtLinksMap;
	}

	public void setMtLinksMap(Map<Long, List<MTInfo>> mtLinksMap) {
		this.mtLinksMap = mtLinksMap;
	}

	
	
}
