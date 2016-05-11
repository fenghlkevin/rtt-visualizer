package cn.com.cennavi.visualizer.common.dataloader.corresponding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CorrespondingTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7793489473486596362L;
	
	private Map<Long,CorrespondingInfo> correspondingMap;
	
	{
		correspondingMap=new HashMap<Long, CorrespondingInfo>();
	}

	public Map<Long, CorrespondingInfo> getCorrespondingMap() {
		return correspondingMap;
	}

	public void setCorrespondingMap(Map<Long, CorrespondingInfo> correspondingMap) {
		this.correspondingMap = correspondingMap;
	}
	
}
