package cn.com.cennavi.visualizer.service.createfile.query;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import com.BaiduMerc;
import com.jhlabs.map.util.ShapeUtil;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.HttpTookit;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.RouteParams;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.Road;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItem;
import cn.com.cennavi.visualizer.service.createfile.IGetRoadService.RoadItemType;

/**
 * Created on 2014-4-2
 * <p>
 * Title: WEB-T GIS核心系统_驾车服务服务_驾车规划接口功能模块
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: 沈阳世纪高通科技有限公司
 * </p>
 * <p>
 * Department: 技术开发部
 * </p>
 * 
 * @author "liuyang" "liuyang"@cennavi.com.cn
 * @version 1.0
 * @update 2014-4-2修改日期 上午11:19:20修改描述22 ${date}上午11:19:20
 */
@Component("route")
public class RouteQuery implements IRoadQueryInf{

	public JsonResult query(AbstractParams params, HttpServletRequest request, HttpServletResponse response) {

		RouteParams rp=(RouteParams)params;
		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(rp.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");

		String[] sp = rp.getSpoint().split(" ");
		String[] ep = rp.getEpoint().split(" ");
		Point2D.Double st = new Point2D.Double(new Double(sp[0]), new Double(sp[1]));
		Point2D.Double et = new Point2D.Double(new Double(ep[0]), new Double(ep[1]));

		Point2D.Double spoint = BaiduMerc.bd_decrypt(st.getY(), st.getX());
		Point2D.Double epoint = BaiduMerc.bd_decrypt(et.getY(), et.getX());

		RouteResult rr = null;
		String resStr;
		try {
			resStr = getRoute(spoint, epoint);
			rr = this.structReturnInfoNew(resStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Road road = new Road();
		road.setDirection(-1);
		road.setRoadid(0);
		RoadItem ri = new RoadItem();

		ri.setAreacode(-1);
		ri.setDirection(-1);
		ri.setGroupid(-1);
		ri.setId(100001);
		ri.setInOverlay(false);
		ri.setLength(Math.round(ShapeUtil.distance(rr.getShape())));
		ri.setNextItemId(0);
		ri.setPrevItemId(0);
		ri.setShape(rr.getShape());
		ri.setItemids(rr.getLinkArray());
		ri.setType(RoadItemType.LINK);

		road.getRoaditems().put(ri.getId(), ri);
		jsonResult.setJsonObj(new ArrayList<Road>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1072824648878922989L;

			{
				this.add(new Road());//为了适应前台对tmc的处理
				this.add(road);
			}
		});

		return jsonResult;
	}

	/**
	 * <p>
	 * Discription:[通过引擎获取路径信息]
	 * </p>
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * @author:"liuyang"
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	private String getRoute(Point2D.Double spoint, Point2D.Double epoint) throws Exception {
		// http://192.168.63.111:8000/rpe/?bypass=0,55929121,14382185,0,55907906,14374688&retflag=5
		String url = System.getProperty("RPE_URL") + "/rpe/?";
		String[] sp = ShapeUtil.to128S(spoint.getX(), spoint.getY());
		String[] ep = ShapeUtil.to128S(epoint.getX(), epoint.getY());
		String rpeurl = url + "bypass=0," + sp[0] + "," + sp[1] + ",0," + ep[0] + "," + ep[1] + "&retflag=4101";

		System.out.println(rpeurl);
		HttpTookit ht = new HttpTookit();
		byte[] bs = ht.doGet(rpeurl, "UTF-8");
		String result = new String(bs, "UTF-8");
		ht.close();

		return result;
	}

	/**
	 * <p>
	 * Discription:[根据GIS返回的数据，构建路线形状]
	 * </p>
	 * 
	 * @param gisResult
	 * @param result
	 * @throws Exception
	 * @author:""
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	private RouteResult structReturnInfoNew(String gisResult) throws Exception {
		Document doc = DocumentHelper.parseText(gisResult);
		Element doc_element = doc.getRootElement();

		/**
		 * 获取route列表信息
		 */
		List<Element> retElementList = doc_element.elements("route");
		RouteResult routeResult = new RouteResult();
		/**
		 * 循环route信息，将其中的标签解析并封装到相应的bean中
		 */
		for (Iterator<Element> iter = retElementList.iterator(); iter.hasNext();) {
			Element routeElement = iter.next();

			/**
			 * 对分段信息进行循环，并将其中的信息存储到bean中
			 */
			Iterator iterSegment = routeElement.elementIterator("segmt");
			while (iterSegment.hasNext()) {
				Element segmentEle = (Element) iterSegment.next();

				/**
				 * 坐标点串
				 */
				String clistValue = segmentEle.elementTextTrim("clist");
				if (clistValue != null) {
					String[] clists = clistValue.split(",");
					for (int i = 0; i < clists.length - 1; i += 2) {
						String[] p = ShapeUtil.toWGS84(clists[i], clists[i + 1]);
						Point2D.Double point = BaiduMerc.bd_encrypt(new Double(p[1]), new Double(p[0]));
						routeResult.getShape().add(point);
					}
				}

				/**
				 * link序列
				 */
				String listValue = segmentEle.elementTextTrim("pidlist");
				if (listValue != null) {
					String[] lists = listValue.split(",");
					for (String link : lists) {
						Long linkid = new Long(link);
						routeResult.getLinkArray().add(linkid);
					}
				}
			}
		}
		return routeResult;
	}

	public static class RouteResult {

		private List<Long> linkArray = new ArrayList<Long>();

		private List<Point2D.Double> shape = new ArrayList<Point2D.Double>();

		public List<Long> getLinkArray() {
			return linkArray;
		}

		public List<Point2D.Double> getShape() {
			return shape;
		}
	}

}