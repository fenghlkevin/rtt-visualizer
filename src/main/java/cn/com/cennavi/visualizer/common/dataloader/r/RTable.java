package cn.com.cennavi.visualizer.common.dataloader.r;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.khelekore.prtree.PRTree;

public class RTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5526283256338315116L;
	private Map<Long, RInfo> rinfo=new HashMap<Long, RInfo>();
	private PRTree<Rectangle2D.Double> prtree_r;
	public Map<Long, RInfo> getRinfo() {
		return rinfo;
	}
	public void setRinfo(Map<Long, RInfo> rinfo) {
		this.rinfo = rinfo;
	}
	public PRTree<Rectangle2D.Double> getPrtree_r() {
		return prtree_r;
	}
	public void setPrtree_r(PRTree<Rectangle2D.Double> prtree_r) {
		this.prtree_r = prtree_r;
	}
}
