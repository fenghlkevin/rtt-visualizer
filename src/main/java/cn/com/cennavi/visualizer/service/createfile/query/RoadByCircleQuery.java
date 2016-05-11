package cn.com.cennavi.visualizer.service.createfile.query;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.ObjectUtil;
import cn.com.cennavi.nspatial.INSpatialSearcher;
import cn.com.cennavi.nspatial.NSpatialSearcherImpl;
import cn.com.cennavi.tpeg.map.dao.CSVFileDataDAOImpl;
import cn.com.cennavi.tpeg.map.dao.IDataDAO;
import cn.com.cennavi.tpeg.map.dao.LPInfoContainer;
import cn.com.cennavi.tpeg.map.dao.NRoadLoader;
import cn.com.cennavi.transform.bean.City;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.CircleParams;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.Road;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItem;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItemType;
import cn.com.cennavi.visualizer.util.MapKUtil;

@Component("circle")
public class RoadByCircleQuery implements IRoadQueryInf {
	
	private static Projection proj;
	private static Map<Long, Road> cacheRoad;

	static {
		proj = ProjectionFactory.fromPROJ4Specification(new String[]{"+init=3785"});
		cacheRoad = new HashMap<Long, IGetRoadService.Road>();
	}

	INSpatialSearcher spatialSearcher = new NSpatialSearcherImpl(proj);

	IDataDAO dao = new CSVFileDataDAOImpl();

	@Override
	public JsonResult query(AbstractParams ap, HttpServletRequest request, HttpServletResponse response) {

		CircleParams params=(CircleParams)ap;

		MTTable mt = (MTTable) CommonFileMapContainer.getInstance().getFileMap("mt." + params.getLpinfo_version());
		RTable r = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + params.getTmc_map_version());

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");

		if ( params.getLrctype().equalsIgnoreCase("TMC")) {
			LPInfoContainer container = LPInfoContainer.getInstance(params.getLpinfo_version());
			// Collection<Rectangle2D.Double> all =
			// spatialSearcher.buffer(pline, null, null,
			// container.getPrtree_nocity());

			Double center_lng = params.getCenter_lng();
			Double center_lat = params.getCenter_lat();
			int radius = new Double(params.getRadius()).intValue();
			Collection<Rectangle2D.Double> all = spatialSearcher.round(center_lng, center_lat, radius, null, container.getPrtree_nocity());

			@SuppressWarnings("unchecked")
			Collection<Rectangle2D.Double> useAll=(Collection<java.awt.geom.Rectangle2D.Double>) ObjectUtil.clone(all); 
			Map<Long, Road> result = new HashMap<Long, Road>();
			for (Rectangle2D.Double one : useAll) {
				LPInfo lp = (LPInfo) one;
				City city = NRoadLoader.instance().execute(lp.getLpLocationTableNumber(), null, params.getLpinfo_version());
				Long messageKey = (lp.getGroupID() * 100L + lp.getLpLocationTableNumber()) * 10L;

				Long posMessageId = messageKey;
				Long negMessageId = messageKey + 1L;
				newRoad(lp, city, r, mt, posMessageId, result);
				newRoad(lp, city, r, mt, negMessageId, result);
			}
			jsonResult.setJsonObj(result.values());
		}
		return jsonResult;
	
	}

	private boolean newRoad(LPInfo now, City city, RTable r, MTTable mt, Long messageid, Map<Long, Road> result) {

		if (!result.containsKey(messageid)) {
			if (!cacheRoad.containsKey(messageid)) {
				cn.com.cennavi.transform.bean.Road road = city.getCityTMCRoad().get(new Long(messageid).intValue());

				int direction = road.getDirection();

				Road reRoad = new Road();
				

				reRoad.setRoadid(messageid);
				reRoad.setDirection(direction);
				// reRoad.setRoadname(roadname);

//				boolean isfirst=true;
				for (LPInfo lp : road.getTmcs()) {
//					if(isfirst){
//						continue;
//					}
					RoadItem ri = new RoadItem();
					reRoad.getRoaditems().put(new Long(lp.getLocId()), ri);

					ri.setId(lp.getLocId());
					ri.setType(RoadItemType.TMC);
					ri.setNextItemId(direction == 0 ? lp.getNextPt() : lp.getPrevPt());
					ri.setPrevItemId(direction == 0 ? lp.getPrevPt() : lp.getNextPt());

					Long lpkey = MTTableParser.getLPKey(lp.getLocId(), road.getDirection(), lp.getLpLocationTableNumber());
					List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
					List<Point2D.Double> temp = MapKUtil.getDrawDirectionShape(linkids, mt, r);
					ri.setShape(temp);
					ri.setLength(direction==1?lp.getPosLen():lp.getNegLen());
					
					ri.setGroupid(lp.getGroupID());
					ri.setDirection(direction);
					ri.setAreacode(lp.getLpLocationTableNumber());
				}
				cacheRoad.put(messageid, reRoad);
			}
			result.put(messageid, (Road) ObjectUtil.clone(cacheRoad.get(messageid)));
		}

		Road reRoad = result.get(messageid);
		RoadItem ri = reRoad.getRoaditems().get(new Long(now.getLocId()));
		if (ri != null) {
			ri.setInOverlay(true);
		} else {
			System.err.println("item error :" + now.getLocId());
		}
		return true;
	}
}
