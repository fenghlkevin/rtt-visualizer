package cn.com.cennavi.quality.service.mapservice;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.com.cennavi.common.param.AbstractReqParams;
import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.common.dataloader.admin.AdminTable;

@Service("queryadmin")
public class MapQueryAdminServiceImpl extends AbstractMapQueryService implements IMapQueryServiceInf{

	public JsonResult query(AbstractParams p, HttpServletRequest request, HttpServletResponse response) {

		AdminParams params=(AdminParams)p;
		
		AdminTable at = (AdminTable) CommonFileMapContainer.getInstance().getFileMap("admin.all");

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(at.getAdminsByCode().values());
		return jsonResult;
	}

	public static class AdminParams extends AbstractReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		
	}

}
