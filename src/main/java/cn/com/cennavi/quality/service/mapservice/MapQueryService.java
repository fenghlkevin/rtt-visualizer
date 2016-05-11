//package cn.com.cennavi.quality.service.mapservice;
//
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Service;
//
//import com.jhlabs.map.proj.Projection;
//import com.jhlabs.map.proj.ProjectionFactory;
//
//import cn.com.cennavi.common.param.AbstractReqParams;
//import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
//import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
//import cn.com.cennavi.kfgis.util.ObjectUtil;
//import cn.com.cennavi.nspatial.INSpatialSearcher;
//import cn.com.cennavi.nspatial.INSpatialSearcher.DataFilter;
//import cn.com.cennavi.nspatial.NSpatialSearcherImpl;
//import cn.com.cennavi.quality.service.mapservice.MapController.CircleParams;
//import cn.com.cennavi.visualizer.common.dataloader.n.NInfo;
//import cn.com.cennavi.visualizer.common.dataloader.n.NTable;
//import cn.com.cennavi.visualizer.common.dataloader.r.RInfo;
//import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
//
//@Service
//public class MapQueryService extends AbstractMapQueryService implements IMapQueryServiceInf{
//
//	private static Projection proj;
//
//	static {
//		proj = ProjectionFactory.fromPROJ4Specification(new String[]{"+init=3785"});
//	}
//
//	INSpatialSearcher spatialSearcher = new NSpatialSearcherImpl(proj);
//
//	public JsonResult querylinks(CircleParams params, HttpServletRequest request, HttpServletResponse response) {
//
//		NTable n = (NTable) CommonFileMapContainer.getInstance().getFileMap("map.n." + params.getMapversion());
//		RTable r = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + params.getMapversion());
//
//		Double center_lng = params.getCenter_lng();
//		Double center_lat = params.getCenter_lat();
//		int radius = new Double(params.getRadius()).intValue();
//		// Collection<Rectangle2D.Double> all =
//		// spatialSearcher.round(center_lng, center_lat, radius, new
//		// DataFilter() {
//		//
//		// @Override
//		// public boolean accept(java.awt.geom.Rectangle2D.Double bean) {
//		// NInfo ninfo = (NInfo) bean;
//		// // 路口主点号码 （路口标识为 1、 2、 3 时有效）
//		// return ninfo.getCross_flag() == 2 || ninfo.getCross_flag() == 3;
//		// }
//		// }, n.getPrtree_n());
//		//
//		// @SuppressWarnings("unchecked")
//		// Collection<Rectangle2D.Double> useAll =
//		// (Collection<java.awt.geom.Rectangle2D.Double>) ObjectUtil.clone(all);
//		//
//		// HashSet<Link> revalues = new HashSet<MapQueryService.Link>();
//		// for (Rectangle2D.Double _bean : useAll) {
//		// NInfo ninfo = (NInfo) _bean;
//		// List<Link> revalue=new ArrayList<MapQueryService.Link>();
//		// getLinkInfoFromNode(revalue,ninfo.getId(),r,n,true);
//		// revalues.addAll(revalue);
//		// }
//
//		List<Link> revalues = new ArrayList<MapQueryService.Link>();
//		Collection<Rectangle2D.Double> all = spatialSearcher.round(center_lng, center_lat, radius, new DataFilter() {
//			@Override
//			public boolean accept(java.awt.geom.Rectangle2D.Double bean) {
//				RInfo rinfo = (RInfo) bean;
//				return rinfo.getKind() == 0 || rinfo.getKind() == 1 || rinfo.getKind() == 2 || rinfo.getKind() == 3 || rinfo.getKind() == 4 || rinfo.getKind() == 6;
//			}
//		}, r.getPrtree_r());
//		for (Rectangle2D.Double _bean : all) {
//			RInfo rinfo = (RInfo) _bean;
//			revalues.add(getLinkInfo(rinfo.getLinkid(), r, n, false));
//		}
//
//		JsonResult jsonResult = new JsonResult();
//
//		jsonResult.setCallback(params.getCallback());
//		jsonResult.setContent_type("application/json");
//		jsonResult.setEncoding("utf-8");
//		jsonResult.setJsonObj(revalues);
//		return jsonResult;
//	}
//
//	public static class Link {
//
//		public Link(Long linkid) {
//			this.linkid = linkid;
//		}
//
//		private List<Point2D.Double> shape;
//
//		private Long linkid;
//
//		private List<Link> nextLinks;
//
//		private List<Link> lastLinks;
//
//		public List<Point2D.Double> getShape() {
//			return shape;
//		}
//
//		public void setShape(List<Point2D.Double> shape) {
//			this.shape = shape;
//		}
//
//		public List<Link> getNextLinks() {
//			return nextLinks;
//		}
//
//		public void setNextLinks(List<Link> nextLinks) {
//			this.nextLinks = nextLinks;
//		}
//
//		public List<Link> getLastLinks() {
//			return lastLinks;
//		}
//
//		public void setLastLinks(List<Link> lastLinks) {
//			this.lastLinks = lastLinks;
//		}
//
//		public Long getLinkid() {
//			return linkid;
//		}
//
//		public void setLinkid(Long linkid) {
//			this.linkid = linkid;
//		}
//	}
//	
//	public static class CircleParams extends AbstractReqParams {
//
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 8466985134330584341L;
//
//		private Double center_lng;
//
//		private Double center_lat;
//
//		private Double radius;
//		
//		private String mapversion;
//
//		public Double getCenter_lng() {
//			return center_lng;
//		}
//
//		public void setCenter_lng(Double center_lng) {
//			this.center_lng = center_lng;
//		}
//
//		public Double getCenter_lat() {
//			return center_lat;
//		}
//
//		public void setCenter_lat(Double center_lat) {
//			this.center_lat = center_lat;
//		}
//
//		public Double getRadius() {
//			return radius;
//		}
//
//		public void setRadius(Double radius) {
//			this.radius = radius;
//		}
//
//		public String getMapversion() {
//			return mapversion;
//		}
//
//		public void setMapversion(String mapversion) {
//			this.mapversion = mapversion;
//		}
//	}
//
//}
