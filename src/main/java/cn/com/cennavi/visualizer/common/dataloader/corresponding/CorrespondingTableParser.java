package cn.com.cennavi.visualizer.common.dataloader.corresponding;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.com.cennavi.kfgis.framework.file.impl.CSVDataAbstractParser;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingInfo.NILink;

public class CorrespondingTableParser extends CSVDataAbstractParser<CorrespondingTable> {
	public CorrespondingTableParser(String sysKey, String dataFlag) {
		super(sysKey, dataFlag);
	}

	@Override
	protected FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().toLowerCase().equalsIgnoreCase(".svn") && pathname.getName().toLowerCase().endsWith(".csv");
			}
		};
	}

	@Override
	protected CorrespondingTable createContainer() {
		return new CorrespondingTable();
	}

	@Override
	protected boolean ignore(String[] tokens) {
		return false;// TODO
	}

	@Override
	protected void putValue2Container(CorrespondingTable tmcTable, String[] tokens) {
	}

	@Override
	protected void putValue2Container(CorrespondingTable table, String[] tokens, File f, int count) {
		if (count == 0) {
			return;
		}

		try {
			CorrespondingInfo ci = new CorrespondingInfo();
			ci.setMeshNo(Long.valueOf(tokens[0]));
			ci.setRticLinkKind(Integer.valueOf(tokens[1]));
			ci.setRticLinkNo(Long.valueOf(tokens[2] + tokens[1]));
			ci.setTrafficClass(Integer.valueOf(tokens[3]));
			ci.setProductClass(Integer.valueOf(tokens[4]));
			ci.setLength(Integer.valueOf(tokens[5]));
			List<NILink> links = new ArrayList<CorrespondingInfo.NILink>();
			ci.setLinks(links);
			for (int i = 6; i < tokens.length;) {
				NILink link = new NILink();
				links.add(link);
				Long linkid = Long.valueOf(tokens[i++]);
				link.setDirection(linkid < 0 ? 1 : 0);
				link.setLinkid(Math.abs(linkid));
				link.setLength(Integer.valueOf(tokens[i++]));
			}
			table.getCorrespondingMap().put(ci.getRticLinkNo(), ci);
		} catch (Exception e) {
			logger.error("构建MT失败 File [" + f.getPath() + "], row number is [" + count + "]", e);
		}

	}

	public static Long getRticKey(long meshid, long number, int kind) {
		DecimalFormat df1 = new DecimalFormat("00000");
		String num = df1.format(number);
		String rtickey = meshid + num + kind;
		return Long.valueOf(rtickey);
	}
}
