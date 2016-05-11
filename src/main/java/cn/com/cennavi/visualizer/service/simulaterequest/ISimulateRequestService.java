package cn.com.cennavi.visualizer.service.simulaterequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.GetMessageParams;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.SimulateReqMIB2Params;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.SimulateReqParams;

public interface ISimulateRequestService {
	
	public JsonResult initSessionDailmer(SimulateReqParams params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult initSessionMIB2(SimulateReqMIB2Params params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult initSessionRenault(SimulateReqMIB2Params params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult getMessageDailmer(GetMessageParams params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult getMessageRenault(GetMessageParams params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult getMessageBmw(GetMessageParams params,HttpServletRequest request, HttpServletResponse response);
	
	public JsonResult getMessageMib2(GetMessageParams params,HttpServletRequest request, HttpServletResponse response);

}
