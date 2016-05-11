package cn.com.cennavi.visualizer.common.dataloader.r;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.khelekore.prtree.PRTree;

import com.BaiduMerc;
import com.GoogleMerc;
import com.jhlabs.map.util.ShapeUtil;

import cn.com.cennavi.kfgis.framework.file.impl.NAbstractFileDataParser;
import cn.com.cennavi.kfgis.framework.util.ObjUtil;
import cn.com.cennavi.spatial.service.geo.Rectangle2DConverter;
import cn.com.cennavi.tool.file.midmifRec;
import cn.com.cennavi.tool.file.midmiffileReader;

/**
 * parser lpinfo file
 * 
 * @author fengheliang
 * 
 */
public class RTableParser extends NAbstractFileDataParser<RTable> {

	public RTableParser(String sysKey, String dataFlag) {
		super(sysKey, dataFlag);
	}

	@Override
	protected FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().toLowerCase().equalsIgnoreCase(".svn")
						&& (pathname.getName().toLowerCase().endsWith(".mid"));
			}
		};
	}

	@Override
	protected RTable createContainer() {
		return new RTable();
	}

	@Override
	protected void putData2Container(RTable table, FileInputStream fis, File f) throws Exception {
		fis.close();
		if (f.getPath().toLowerCase().endsWith(".mif")) {
			return;
		}

		String mid = f.getPath();
		String mif = mid.substring(0, mid.length() - 3) + "mif";

		midmiffileReader mr = new midmiffileReader(mid, mif, "PEN");
		mr.openfile();
		mr.readmifhead();
		midmifRec values = null;
		try {
			while ((values = mr.readmidmifOneRec()) != null) {
				RInfo info = new RInfo();

				String tempstr = values.strmidRec.replaceAll("\\\"", "");
				String[] columns = tempstr.split(",");
				//info.setMidvalues(columns);
				Long linkid = new Long(columns[1]);
				info.setDirection(Integer.valueOf(columns[5]));
				if(!ObjUtil.isEmpty(columns[9],true)&&ObjUtil.isNumber(columns[9])){
					info.setSnodeID(Long.valueOf(columns[9]));
				}
				
				if(!ObjUtil.isEmpty(columns[10],true)&&ObjUtil.isNumber(columns[10])){
					info.setEnodeID(Long.valueOf(columns[10]));
				}
				String _kind=columns[3].substring(0, 2);
				info.setKind(Integer.parseInt(_kind,16));
				
				info.setLinkid(Long.valueOf(columns[1]));
				
				table.getRinfo().put(linkid, info);
//				values.vecmifRec.remove(0);
				for (String temp : values.vecmifRec) {
					if (temp.trim().equalsIgnoreCase("")||temp.trim().toLowerCase().startsWith("pen")||temp.trim().toLowerCase().startsWith("pline")) {
						continue;
					}
					String[] tempa = temp.split(" ");
					if (temp.trim().toLowerCase().startsWith("line")) {
						Point2D.Double lonlat = BaiduMerc.bd_encrypt(new Double(tempa[2]), new Double(tempa[1]));
						info.getShape().add(lonlat);
						lonlat = BaiduMerc.bd_encrypt(new Double(tempa[4]), new Double(tempa[3]));
						info.getShape().add(lonlat);
					} else {
						Point2D.Double lonlat = BaiduMerc.bd_encrypt(new Double(tempa[1]), new Double(tempa[0]));
						info.getShape().add(lonlat);
					}
				}
				if(info.getShape().size()==1){
					Point2D.Double lonlat=GoogleMerc.latlon2xy(info.getShape().get(0));
					info.setRect(lonlat.getX(), lonlat.getY());
				} else {
					Point2D.Double[] ll = ShapeUtil.getBoxByPolygonr(info.getShape().toArray(new Point2D.Double[0]));
					Point2D.Double min = GoogleMerc.latlon2xy(ll[0]);
					Point2D.Double max = GoogleMerc.latlon2xy(ll[1]);
					info.setRect(min.getX(), min.getY(),max.getX(), max.getY());
				}
				
			}
		} catch (Exception e) {
			logger.error("构建RTable失败 File ["+f.getPath()+"]", e);
		}
		mr.closefile();
	}
	/**
	 * MapID char(8) ID char(13) Kind_num char(2) Kind char(23) Width char(3)
	 * Direction char(1) Toll char(1) Const_St char(1) PathName char(40) PathPY
	 * char(250) SnodeID char(13) EnodeID char(13) PathClass char(2) PathNo
	 * char(13) Length char(8) OnewayCRID char(13) WavName char(160) SlopeSN
	 * char(1) SlopeEN char(1) PID char(16)
	 */

	@Override
	protected void afterLoaded(RTable table) {
		PRTree<Rectangle2D.Double> ptree = new PRTree<Rectangle2D.Double>(new Rectangle2DConverter(), 30);
		ptree.load(table.getRinfo().values());
		table.setPrtree_r(ptree);
	}

}
