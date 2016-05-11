package cn.com.cennavi.quality.service.mapservice;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import cn.com.cennavi.kfgis.util.ObjectUtil;
import cn.com.cennavi.nspatial.INSpatialSearcher;
import cn.com.cennavi.nspatial.NSpatialSearcherImpl;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.visualizer.common.dataloader.admin.AdminInfo;
import cn.com.cennavi.visualizer.common.dataloader.admin.AdminTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.n.NInfo;
import cn.com.cennavi.visualizer.common.dataloader.n.NTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RInfo;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.util.MapKUtil;

public abstract class AbstractMapQueryService {
	
	private Logger logger=LoggerFactory.getLogger(AbstractMapQueryService.class);
	private static Projection proj;

	static {
		proj = ProjectionFactory.fromPROJ4Specification(new String[]{"+init=3785"});
	}

	INSpatialSearcher spatialSearcher = new NSpatialSearcherImpl(proj);
	
	protected void getLinkInfoFromNode(List<MapItem> revalue, long nid, RTable r, NTable n, boolean getLinkFromNode) {
		NInfo ninfo = n.getNinfo().get(nid);
		if (ninfo.getCross_flag() != 2 && ninfo.getCross_flag() != 3) {
			return;
		}
		String _linkstr = ninfo.getCross_lid();
		if (_linkstr == null || "".equalsIgnoreCase(_linkstr)) {
			return;
		}
		String[] tlinks = _linkstr.split("[|]");
		for (String _linkid : tlinks) {
			Long linkid = Long.valueOf(_linkid);
			MapItem link = getLinkInfo(linkid, r, n, getLinkFromNode);
			revalue.add(link);
		}
	}

	@SuppressWarnings("unchecked")
	protected MapItem getLinkInfo(Long linkid, RTable r, NTable n, boolean getLinkFromNode) {
		MapItem _tl = new MapItem(linkid);
		RInfo rinfo = r.getRinfo().get(linkid);
		_tl.setShape((List<Point2D.Double>) ObjectUtil.clone(rinfo.getShape()));
		if (getLinkFromNode) {
			NInfo _sn = n.getNinfo().get(rinfo.getSnodeID());
			List<MapItem> revalue = new ArrayList<MapItem>();
			getLinkInfoFromNode(revalue, _sn.getId(), r, n, false);
			_tl.setNextLinks(revalue);
			NInfo _en = n.getNinfo().get(rinfo.getSnodeID());
			revalue = new ArrayList<MapItem>();
			getLinkInfoFromNode(revalue, _en.getId(), r, n, false);
			_tl.setLastLinks(revalue);
		}
		return _tl;
	}
	
	@SuppressWarnings("unchecked")
	protected MapItem getLinkInfo(LPInfo lpinfo,int direction, RTable r, MTTable mt,AdminTable a) {
		AdminInfo ai=a.getAdminsByPYName().get(lpinfo.getCityname().toLowerCase());
		String admincode="0";
		if(ai==null){
			admincode="0";
			logger.error("city ["+lpinfo.getCityname().toLowerCase()+"] do not match in AdminTable");
		}else{
			admincode=a.getAdminsByPYName().get(lpinfo.getCityname().toLowerCase()).getId();
		}
		
		String id=lpinfo.getLocId()+"_"+admincode+"_"+direction;
		MapItem _tl = new MapItem(id);
		
		Long lpkey = MTTableParser.getLPKey(lpinfo.getLocId(), direction, lpinfo.getLpLocationTableNumber());
		List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
		List<Point2D.Double> temp = MapKUtil.getDrawDirectionShape(linkids, mt, r);
		
		_tl.setShape((List<Point2D.Double>) ObjectUtil.clone(temp));
		return _tl;
	}
	
	public static class MapItem {

		public MapItem(String linkid) {
			this.linkid = linkid;
		}
		
		public MapItem(Long linkid) {
			this.linkid = String.valueOf(linkid);
		}

		private List<Point2D.Double> shape;

		private String linkid;

		private List<MapItem> nextLinks;

		private List<MapItem> lastLinks;

		public List<Point2D.Double> getShape() {
			return shape;
		}

		public void setShape(List<Point2D.Double> shape) {
			this.shape = shape;
		}

		public List<MapItem> getNextLinks() {
			return nextLinks;
		}

		public void setNextLinks(List<MapItem> nextLinks) {
			this.nextLinks = nextLinks;
		}

		public List<MapItem> getLastLinks() {
			return lastLinks;
		}

		public void setLastLinks(List<MapItem> lastLinks) {
			this.lastLinks = lastLinks;
		}

		public String getLinkid() {
			return linkid;
		}

		public void setLinkid(String linkid) {
			this.linkid = linkid;
		}
	}

	
}
