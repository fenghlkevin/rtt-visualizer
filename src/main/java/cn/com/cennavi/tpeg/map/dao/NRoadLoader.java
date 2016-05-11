package cn.com.cennavi.tpeg.map.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.cennavi.kfgis.util.ObjectUtil;
import cn.com.cennavi.transform.bean.City;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.transform.bean.Road;

/**
 * 道路对象读取器
 * 
 * @author Kevin Feng
 * 
 */
public class NRoadLoader {

	private Logger logger = LoggerFactory.getLogger(NRoadLoader.class);

	private static NRoadLoader instance;

	private static Object lock = new Object();

	/**
	 * 保证单例运行
	 * 
	 * @return
	 */
	public static NRoadLoader instance() {
		if (instance == null) {
			synchronized (lock) {
				instance = new NRoadLoader();
			}
		}
		return instance;
	}

	/**
	 * 城市道路数据
	 */
//	public Map<Integer, City> citys;
	
	/**
	 * key:lpinfoversion_ltn
	 */
	private Map<String,City> citys;

	/**
	 * 执行读取TMC并排序
	 * 
	 * @param lpLocationTableNumber
	 * @return
	 */
	public City execute(int lpLocationTableNumber,IDataDAO dao, String lpinfoversion) {
		if (citys == null || citys.isEmpty()) {
			citys = new HashMap<String, City>();
		}
		String citykey=lpinfoversion+"_"+lpLocationTableNumber;

		City city = citys.get(citykey);

		if (city == null || city.getCityTMCRoad() == null || city.getCityTMCRoad().isEmpty() || city.getCityGroupIDs() == null
				|| city.getCityTMCRoad().isEmpty()) {
			city = new City();
			Map<Integer, Road> roads = new HashMap<Integer, Road>();
			Map<Integer, Integer> groupIds = new HashMap<Integer, Integer>();

			List<LPInfo> posLpinfoList = readPosLPsInGrids(lpLocationTableNumber,lpinfoversion);
			if (posLpinfoList == null||posLpinfoList.size()<=0) {
				logger.debug("LTN为[" + lpLocationTableNumber + "].读取位置点数据失败");
				return null;
			}
			/**
			 * 根据道路正方向与负方向进行排序
			 */
			roads.putAll(getSortedLPMap(posLpinfoList, true));
			roads.putAll(getSortedLPMap(posLpinfoList, false));
			Integer[] keys = roads.keySet().toArray(new Integer[roads.size()]);
			for (Integer key : keys) {
				Road road = roads.get(key);
				if (road == null || road.getTmcs() == null || road.getTmcs().isEmpty()) {
					logger.debug("道路无位置点.Message为[" + key + "]");
					return null;
				}
				/**
				 * 删除只有一个位置的道路
				 */
				if (road.getTmcs().size() == 1) {
					logger.debug("道路位置点个数为1，不能处理，删掉该道路数据.Message为[" + key + "]");
					roads.remove(road);

				}
				/**
				 * 根据位置点数据，生成所有道路的groupid<locid+ltn,groupid>
				 */
				for (LPInfo tmc : road.getTmcs()) {
					Integer groupidKey = tmc.getLocId() * 100 + tmc.getLpLocationTableNumber();
					groupIds.put(groupidKey, tmc.getGroupID());
				}
			}
			city.setCityGroupIDs(groupIds);
			city.setCityTMCRoad(roads);
			citys.put(citykey, city);
		}
		// 为了保证数据使用不会冲突，每次使用内存中数据时进行先克隆后使用
		return (City) ObjectUtil.clone(city);

	}

	// 位置点的分组排序(posDir=true时，按正方向排序；posDir=false时，按反方向排序)
	private Map<Integer, Road> getSortedLPMap(List<LPInfo> lpinfoList, boolean posDir) {
		// 参数检查
		if ((lpinfoList == null) || (lpinfoList.size() == 0)) {
			logger.error("位置点列表为空!");
			return null;
		}

		Map<Integer, Road> retMap = new HashMap<Integer, Road>();
		// 以groupID为key的位置点组
		Map<Integer, Map<Integer, LPInfo>> groupedLpMap = new HashMap<Integer, Map<Integer, LPInfo>>();
		// 道路正方向的首位置点表(以groupID+ltn为key)
		Map<Integer, LPInfo> groupFirstLPMap = new HashMap<Integer, LPInfo>();
		
		// 道路正方向的首位置点表(以groupID+ltn为key)
		Map<Integer, Boolean> isLoopRoadMap = new HashMap<Integer, Boolean>();
		// 位置点分组，并且取得在检索范围内的道路正方向首位置点
		for (LPInfo lp : lpinfoList) {
			Integer groupIDLTN = getGroupIDLTN(lp.getGroupID(), lp.getLpLocationTableNumber());
			Map<Integer, LPInfo> lpSet = groupedLpMap.get(groupIDLTN);
			if (lpSet == null) {
				lpSet = new HashMap<Integer, LPInfo>();
				groupedLpMap.put(groupIDLTN, lpSet);
			}
			lpSet.put(lp.getLocId(), lp);
			// 为正方向上第一个点时，则保存
			if (lp.getNextPt() == 0) {
				groupFirstLPMap.put(groupIDLTN, lp);
				isLoopRoadMap.put(groupIDLTN, false);
			}
		}
			
		// 道路正方向首位置点不在检索范围内时，以检索范围内正方向上的第一个位置点为首位置点
		for (Integer groupIDLTN : groupedLpMap.keySet()) {
			if (groupFirstLPMap.get(groupIDLTN) == null) {
				getPosDirFirstLP(groupIDLTN,groupFirstLPMap,isLoopRoadMap,groupedLpMap.get(groupIDLTN));
			}
		}
		// 道路正负方向上位置点排序
		for (Integer groupIDLTN : groupFirstLPMap.keySet()) {
			// messageID
			Integer messageID = getMessageID(groupIDLTN, posDir);
			// 正方向的有序位置点列表
			List<LPInfo> sortedList = new ArrayList<LPInfo>();
			// 头结点插入
			LPInfo head = groupFirstLPMap.get(groupIDLTN);
			LPInfo oriHead = groupFirstLPMap.get(groupIDLTN);
			/**
			 * 如果第一位的nextPt不为空，则证明该路是环路，首位置点需要两个。
			 */
			Road road = new Road();
			sortedList.add(head);
			if(isLoopRoadMap.get(groupIDLTN)!=null&&isLoopRoadMap.get(groupIDLTN)){
				road.setLoopRoad(true);
			}
			// 道路所属位置点组取得
			Map<Integer, LPInfo> lps = groupedLpMap.get(groupIDLTN);
			while ((head.getPrevPt() != 0) && (head.getPrevPt() != oriHead.getLocId())) {
				// 前一节点取得
				LPInfo preLP = lps.get(head.getPrevPt());
				if (preLP != null) {
					
					if (posDir) {
						// 正方向上头插入
						sortedList.add(0, preLP);
					} else {
						// 负方向上顺序插入
						sortedList.add(preLP);
					}
					// 处理下一个结点
					head = preLP;
				} else {
					break;
				}
			}
			if(road.isLoopRoad()){
				sortedList.add(0,sortedList.get(0));
			}
			
			road.setDirection(posDir?0:1);
			road.setTmcs(sortedList);
			road.setMessageID(messageID);
			// 道路指定方向上的位置点列表设定
			retMap.put(messageID, road);
		}

		return retMap;
	}

	// 根据groupID和方向取得messageID
	private Integer getMessageID(Integer groupIDLTN, boolean posDir) {
		return (posDir) ? (groupIDLTN * 10) : (groupIDLTN * 10 + 1);
	}

	// 根据groupID和ltn获取道路ID
	private Integer getGroupIDLTN(Integer groupID, Integer ltn) {
		return (groupID * 100 + ltn);
	}

	// 取得道路位置点序列中正方向上的首个位置点
	private void getPosDirFirstLP(Integer groupIDLTN,Map<Integer, LPInfo> groupFirstLPMap,Map<Integer, Boolean> isLoopRoadMap,Map<Integer, LPInfo> lpMap) {
		
		// 道路正方向的首位置点表(以groupID+ltn为key)
		if ((lpMap == null) || (lpMap.isEmpty())) {
			logger.error("道路位置点序列为空!");
			return;
		}
		for (LPInfo lp : lpMap.values()) {
			// 如果位置点的nextPt不存在或者不在位置点列表内，则为首位置点
			if ((lp.getNextPt() == 0) || (lpMap.get(lp.getNextPt()) == null)) {
				groupFirstLPMap.put(groupIDLTN, lp);
				isLoopRoadMap.put(groupIDLTN, false);
				return;
			}
		}
		// 道路为环状时，任取一点为起点
		groupFirstLPMap.put(groupIDLTN, lpMap.values().iterator().next());
		isLoopRoadMap.put(groupIDLTN, true);
	}
	
	private List<LPInfo> readPosLPsInGrids(int lpLocationTableNumber,
			String lpinfoversion) {
		LPInfoContainer container=LPInfoContainer.getInstance(lpinfoversion);
		return container.readPosLPsInGrids(lpLocationTableNumber);
	}

	private int getGroupid(Integer lpLocationTableNumber, Integer locid,
			String lpinfoversion) {
		LPInfoContainer container=LPInfoContainer.getInstance(lpinfoversion);
		return container.getGroupid(lpLocationTableNumber, locid);
	}
}
