package cn.com.cennavi.tpeg.map.dao;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.khelekore.prtree.PRTree;

import com.BaiduMerc;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import cn.com.cennavi.kfgis.util.csv.CSVReader;
import cn.com.cennavi.kfgis.util.csv.CSVReaderImpl;
import cn.com.cennavi.transform.bean.LPInfo;

public class LPInfoContainer {

	public static final String LPINFO_PATH = "lpinfo.data.path";

	private Map<Integer, Map<Integer, LPInfo>> alllp = null;

	private List<LPInfo> alllp_nocity = null;

	private PRTree<Rectangle2D.Double> prtree_nocity;

	private static Map<String, LPInfoContainer> instanceMap;
	
	static {
		instanceMap = new HashMap<String, LPInfoContainer>();
	}

	{

		alllp = new HashMap<Integer, Map<Integer, LPInfo>>();
		alllp_nocity = new ArrayList<LPInfo>();
	}
	public static LPInfoContainer getInstance(String lpinfoversion) {
		LPInfoContainer instance = instanceMap.get(lpinfoversion);
		if (instance == null) {
			instance = new LPInfoContainer();
			instanceMap.put(lpinfoversion, instance);
		}
		return instance;
	}

	public static String[] getInstanceMapKeys() {
		return instanceMap.keySet().toArray(new String[0]);
	}
	private static Projection proj;
	static {
		proj = ProjectionFactory.fromPROJ4Specification(new String[]{"+init=3785"});
	}

	public void loadData(String path) throws IOException {

		if (path == null || "".equalsIgnoreCase(path.trim())) {
			path = System.getProperty(LPINFO_PATH);
		}

		if (path == null || "".equalsIgnoreCase(path.trim())) {
			throw new RuntimeException("LPINFO文件没有配置");
		}

		File f = new File(path);
		if (!f.exists() || !f.isFile()) {
			throw new RuntimeException("[" + path + "] 不是LPINFO文件");
		}
		CSVReader reader = null;
		reader = new CSVReaderImpl(new BufferedReader(new FileReader(path)), CSVReaderImpl.DEFAULT_SEPARATOR, CSVReaderImpl.DEFAULT_QUOTE_CHARACTER, 1);
		String line[] = null;
		if (alllp == null) {
			alllp = new HashMap<Integer, Map<Integer, LPInfo>>();
		}
		while ((line = reader.readNext()) != null) {
			// locid,x,y,posFrc,negFrc,prevPt,nextPt,GroupId,posLen,negLen,lpCountryCode,lpExtendedCountryCode,lpLocationTableNumber,cityName,lpLocationTableVersion
			Point2D.Double lonlat = BaiduMerc.bd_encrypt(new Double(line[2]), new Double(line[1]));
			lonlat = this.latlon2xy(lonlat);
			LPInfo lp = new LPInfo(lonlat.getX(), lonlat.getY());

			lp.setLocId(Integer.parseInt(line[0]));
			lp.setXp(Double.parseDouble(line[1]));
			lp.setYp(Double.parseDouble(line[2]));

			lp.setPosFrc(Integer.parseInt(line[3]));
			lp.setNegFrc(Integer.parseInt(line[4]));

			lp.setPrevPt(Integer.parseInt(line[5]));
			lp.setNextPt(Integer.parseInt(line[6]));

			lp.setGroupID(Integer.parseInt(line[7]));

			lp.setNegLen(Integer.parseInt(line[8]));
			lp.setPosLen(Integer.parseInt(line[9]));

			lp.setLpCountryCode(Integer.parseInt(line[10]));
			lp.setLpExtendedCountryCode(Integer.parseInt(line[11]));
			lp.setLpLocationTableNumber(Integer.parseInt(line[12]));
			lp.setCityname(line[13]);
			lp.setLpLocationTableVersion(line[14]);

			/*
			 * lp.setPosSpeed(Integer.parseInt(line[0]));
			 * lp.setNegSpeed(Integer.parseInt(line[1]));
			 * 
			 * lp.setLocId(Integer.parseInt(line[2]));
			 * lp.setX(Double.parseDouble(line[3]));
			 * lp.setY(Double.parseDouble(line[4]));
			 * 
			 * lp.setPrevPt(Integer.parseInt(line[5]));
			 * lp.setNextPt(Integer.parseInt(line[6]));
			 * 
			 * lp.setPosFrc(Integer.parseInt(line[7]));
			 * lp.setNegFrc(Integer.parseInt(line[8]));
			 * 
			 * lp.setNegLen(Integer.parseInt(line[9]));
			 * lp.setPosLen(Integer.parseInt(line[10]));
			 * 
			 * lp.setGroupID(Integer.parseInt(line[11]));
			 * 
			 * lp.setLpCountryCode(Integer.parseInt(line[12]));
			 * lp.setLpExtendedCountryCode(Integer.parseInt(line[13]));
			 * lp.setLpLocationTableNumber(Integer.parseInt(line[14]));
			 * lp.setLpLocationTableVersion(line[15]);
			 */
			alllp_nocity.add(lp);
			Map<Integer, LPInfo> oneCity = alllp.get(lp.getLpLocationTableNumber());
			if (oneCity == null) {
				oneCity = new HashMap<Integer, LPInfo>();
				alllp.put(lp.getLpLocationTableNumber(), oneCity);
			}
			oneCity.put(lp.getLocId(), lp);
		}
	}

	public List<LPInfo> readPosLPsInGrids(final int lpLocationTableNumber) {
		Map<Integer, LPInfo> lps = alllp.get(lpLocationTableNumber);
		List<LPInfo> re = new ArrayList<LPInfo>();
		if (lps != null && lps.size() > 0) {
			for (LPInfo lp : lps.values()) {
				re.add(lp);
			}
		}
		return re;
	}

	public int getGroupid(Integer lpLocationTableNumber, Integer locid) {
		Map<Integer, LPInfo> lps = alllp.get(lpLocationTableNumber);
		if (lps != null) {
			LPInfo lp = lps.get(locid);
			if (lp != null) {
				return lp.getGroupID();
			}
		}
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<LPInfo> getAlllp_nocity() {
		return alllp_nocity;
	}

	public PRTree<Rectangle2D.Double> getPrtree_nocity() {
		return prtree_nocity;
	}

	public void setPrtree_nocity(PRTree<Rectangle2D.Double> prtree_nocity) {
		this.prtree_nocity = prtree_nocity;
	}

	private Point2D.Double latlon2xy(Point2D.Double src) {
		Point2D.Double dest = new Point2D.Double();
		proj.transform(src, dest);
		return dest;
	}

	public Map<Integer, Map<Integer, LPInfo>> getAlllp() {
		return alllp;
	}

}
