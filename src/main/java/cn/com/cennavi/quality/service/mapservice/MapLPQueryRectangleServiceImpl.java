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
import cn.com.cennavi.tpeg.map.dao.LPInfoContainer;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.visualizer.common.dataloader.admin.AdminTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;

@Service("queryLPByRectangle")
public class MapLPQueryRectangleServiceImpl extends AbstractMapQueryService implements IMapQueryServiceInf{

	public JsonResult query(AbstractParams p, HttpServletRequest request, HttpServletResponse response) {

		RectangleParams params=(RectangleParams)p;
		
		AdminTable a = (AdminTable) CommonFileMapContainer.getInstance().getFileMap("admin.all");
		MTTable mt = (MTTable) CommonFileMapContainer.getInstance().getFileMap("mt." + params.getMapversion());
		RTable r = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + params.getMapversion());
		LPInfoContainer container = LPInfoContainer.getInstance(params.getMapversion());

		List<MapItem> revalues = new ArrayList<MapLPQueryRectangleServiceImpl.MapItem>();
		Collection<Rectangle2D.Double> all = spatialSearcher.bbox(params.getMin_lng(), params.getMin_lat(), params.getMax_lng(), params.getMax_lat(),null, container.getPrtree_nocity());
		for (Rectangle2D.Double _bean : all) {
			LPInfo lp = (LPInfo) _bean;
			
			revalues.add(getLinkInfo(lp,0, r, mt,a));
			revalues.add(getLinkInfo(lp,1, r, mt,a));
		}

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(revalues);
		return jsonResult;
	}
}
