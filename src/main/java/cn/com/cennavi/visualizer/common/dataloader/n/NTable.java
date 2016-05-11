package cn.com.cennavi.visualizer.common.dataloader.n;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.khelekore.prtree.PRTree;

public class NTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5526283256338315116L;
	
	private Map<Long, NInfo> ninfo = new HashMap<Long, NInfo>();
	
	private PRTree<Rectangle2D.Double> prtree_n;
	
	public Map<Long, NInfo> getNinfo() {
		return ninfo;
	}
	public void setNinfo(Map<Long, NInfo> ninfo) {
		this.ninfo = ninfo;
	}
	public PRTree<Rectangle2D.Double> getPrtree_n() {
		return prtree_n;
	}
	public void setPrtree_n(PRTree<Rectangle2D.Double> prtree_n) {
		this.prtree_n = prtree_n;
	}

}
