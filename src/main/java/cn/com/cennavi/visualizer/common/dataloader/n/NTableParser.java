package cn.com.cennavi.visualizer.common.dataloader.n;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.khelekore.prtree.PRTree;

import com.BaiduMerc;
import com.GoogleMerc;

import cn.com.cennavi.kfgis.framework.file.impl.NAbstractFileDataParser;
import cn.com.cennavi.spatial.service.geo.Rectangle2DConverter;
import cn.com.cennavi.tool.file.midmifRec;
import cn.com.cennavi.tool.file.midmiffileReader;

/**
 * parser lpinfo file
 * 
 * @author fengheliang
 * 
 */
public class NTableParser extends NAbstractFileDataParser<NTable> {

	public NTableParser(String sysKey, String dataFlag) {
		super(sysKey, dataFlag);
	}

	@Override
	protected FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().toLowerCase().equalsIgnoreCase(".svn") && (pathname.getName().toLowerCase().endsWith(".mid"));
			}
		};
	}

	@Override
	protected NTable createContainer() {
		return new NTable();
	}

	@Override
	protected void putData2Container(NTable table, FileInputStream fis, File f) throws Exception {
		fis.close();
		if (f.getPath().toLowerCase().endsWith(".mif")) {
			return;
		}

		String mid = f.getPath();
		String mif = mid.substring(0, mid.length() - 3) + "mif";

		midmiffileReader mr = new midmiffileReader(mid, mif, "Symbol");
		mr.openfile();
		mr.readmifhead();
		midmifRec values = null;
		try {
			while ((values = mr.readmidmifOneRec()) != null) {
				NInfo info = new NInfo();

				String tempstr = values.strmidRec.replaceAll("\\\"", "");
				String[] columns = tempstr.split(",");
				info.setCross_flag(new Integer(columns[4]));
				info.setCross_lid(columns[6]);
				info.setId(new Long(columns[1]));
				table.getNinfo().put(info.getId(), info);
				// values.vecmifRec.remove(0);
				for (String temp : values.vecmifRec) {
					if (temp.trim().equalsIgnoreCase("") || temp.trim().toLowerCase().startsWith("pen") || temp.trim().toLowerCase().startsWith("pline") || temp.trim().toLowerCase().startsWith("symbol")) {
						continue;
					}

					String[] tempa = temp.split(" ");
					if (temp.trim().toLowerCase().startsWith("point")) {
						Point2D.Double lonlat = BaiduMerc.bd_encrypt(new Double(tempa[2]), new Double(tempa[1]));
						//info.getShape().add(lonlat);
						//Point2D.Double xy = GoogleMerc.latlon2xy(lonlat);
						//info.setRect(xy.getX(),xy.getY());
					}
				}
			}
		} catch (Exception e) {
			logger.error("构建RTable失败 File [" + f.getPath() + "]", e);
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
	protected void afterLoaded(NTable table) {
		PRTree<Rectangle2D.Double> ptree = new PRTree<Rectangle2D.Double>(new Rectangle2DConverter(), 30);
		ptree.load(table.getNinfo().values());
		table.setPrtree_n(ptree);
	}

}
