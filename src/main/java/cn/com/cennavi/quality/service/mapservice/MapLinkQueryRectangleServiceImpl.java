package cn.com.cennavi.quality.service.mapservice;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.com.cennavi.common.param.RectangleParams;
import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.nspatial.INSpatialSearcher.DataFilter;
import cn.com.cennavi.visualizer.common.dataloader.n.NTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RInfo;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;

@Service("queryLinkByRectangle")
public class MapLinkQueryRectangleServiceImpl extends AbstractMapQueryService implements IMapQueryServiceInf{

	public JsonResult query(AbstractParams p, HttpServletRequest request, HttpServletResponse response) {

		RectangleParams params=(RectangleParams)p;
		
		NTable n = (NTable) CommonFileMapContainer.getInstance().getFileMap("map.n." + params.getMapversion());
		RTable r = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + params.getMapversion());

		List<MapItem> revalues = new ArrayList<MapLinkQueryRectangleServiceImpl.MapItem>();
		Collection<Rectangle2D.Double> all = spatialSearcher.bbox(params.getMin_lng(), params.getMin_lat(), params.getMax_lng(), params.getMax_lat(), new DataFilter() {
			@Override
			public boolean accept(java.awt.geom.Rectangle2D.Double bean) {
				RInfo rinfo = (RInfo) bean;
				return rinfo.getKind() == 0 || rinfo.getKind() == 1 || rinfo.getKind() == 2 || rinfo.getKind() == 3 || rinfo.getKind() == 4 || rinfo.getKind() == 6;
			}
		}, r.getPrtree_r());
		for (Rectangle2D.Double _bean : all) {
			RInfo rinfo = (RInfo) _bean;
			revalues.add(getLinkInfo(rinfo.getLinkid(), r, n, false));
		}

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(revalues);
		return jsonResult;
	}


}
