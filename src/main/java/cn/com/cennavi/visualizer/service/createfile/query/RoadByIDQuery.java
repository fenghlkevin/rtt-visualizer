package cn.com.cennavi.visualizer.service.createfile.query;

import java.awt.geom.Point2D;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.tpeg.map.dao.LPInfoContainer;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.QueryRoadItemReqParams;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItem;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItemType;
import cn.com.cennavi.visualizer.util.MapKUtil;

@Component("roadbyid")
public class RoadByIDQuery implements IRoadQueryInf {

	@Override
	public JsonResult query(AbstractParams ap, HttpServletRequest request, HttpServletResponse response) {
		QueryRoadItemReqParams params = (QueryRoadItemReqParams) ap;
		MTTable mt = (MTTable) CommonFileMapContainer.getInstance().getFileMap("mt." + params.getLpinfo_version());
		RTable r = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + params.getTmc_map_version());

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");

		if (params.getLrctype().equalsIgnoreCase("TMC")) {
			LPInfoContainer container = LPInfoContainer.getInstance(params.getLpinfo_version());
			LPInfo lp = container.getAlllp().get(new Integer(params.getRoaditem_areacode())).get(new Integer(params.getRoaditemid()));
			RoadItem ri = new RoadItem();

			ri.setId(lp.getLocId());
			ri.setType(RoadItemType.TMC);
			ri.setNextItemId(params.getDirection() == 0 ? lp.getNextPt() : lp.getPrevPt());
			ri.setPrevItemId(params.getDirection() == 0 ? lp.getPrevPt() : lp.getNextPt());

			Long lpkey = MTTableParser.getLPKey(lp.getLocId(), params.getDirection(), lp.getLpLocationTableNumber());
			List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
			List<Point2D.Double> temp = MapKUtil.getDrawDirectionShape(linkids, mt, r);
			ri.setShape(temp);
			ri.setLength(params.getDirection() == 1 ? lp.getPosLen() : lp.getNegLen());

			ri.setGroupid(lp.getGroupID());
			ri.setDirection(params.getDirection());
			ri.setAreacode(params.getRoaditem_areacode());
			jsonResult.setJsonObj(ri);
		}
		return jsonResult;

	}

}
