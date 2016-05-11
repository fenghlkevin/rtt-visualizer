package cn.com.cennavi.visualizer.common.dataloader.lp;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.khelekore.prtree.PRTree;

import cn.com.cennavi.kfgis.framework.file.impl.NAbstractFileDataParser;
import cn.com.cennavi.spatial.service.geo.Rectangle2DConverter;
import cn.com.cennavi.tpeg.map.dao.LPInfoContainer;

/**
 * parser lpinfo file
 * 
 * @author fengheliang
 * 
 */
public class LPTableParser extends NAbstractFileDataParser<Integer> {

	@Override
	protected void afterLoaded(Integer table) {
		String[] keys = LPInfoContainer.getInstanceMapKeys();
		for (String key : keys) {
			LPInfoContainer container = LPInfoContainer.getInstance(key);
			PRTree<Rectangle2D.Double> ptree = new PRTree<Rectangle2D.Double>(new Rectangle2DConverter(), 30);
			ptree.load(container.getAlllp_nocity());
			container.setPrtree_nocity(ptree);
		}
	}

	@Override
	public FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().toLowerCase().equalsIgnoreCase(".svn") && pathname.getName().toLowerCase().endsWith(".csv");
			}
		};
	}

	public LPTableParser(String sysKey, String dataFlag) {
		super(sysKey, dataFlag);
	}

	@Override
	protected Integer createContainer() {
		return 1;
	}

	@Override
	protected void putData2Container(Integer table, FileInputStream fis, File f) throws Exception {
		fis.close();
		try{
			String fileName = f.getParentFile().getName();
			// String nameFields[]=fileName.split("[.]")[0].split("_");
			LPInfoContainer.getInstance(fileName).loadData(f.getPath());
		}catch(Exception e){
			logger.error("构建lpinfo失败 File ["+f.getPath()+"]", e);
		}
		

	}

}
