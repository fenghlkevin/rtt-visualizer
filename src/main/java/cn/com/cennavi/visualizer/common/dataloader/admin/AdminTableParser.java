package cn.com.cennavi.visualizer.common.dataloader.admin;

import java.io.File;
import java.io.FileFilter;

import cn.com.cennavi.kfgis.framework.file.impl.CSVDataAbstractParser;

/**
 * parser lpinfo file
 * 
 * @author fengheliang
 * 
 */
public class AdminTableParser extends CSVDataAbstractParser<AdminTable> {

	public AdminTableParser(String sysKey, String dataFlag) {
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
	protected AdminTable createContainer() {
		return new AdminTable();
	}

	@Override
	protected boolean ignore(String[] tokens) {
		return false;// TODO
	}

	@Override
	protected void putValue2Container(AdminTable tmcTable, String[] tokens) {
	}

	@Override
	protected void putValue2Container(AdminTable table, String[] tokens, File f, int count) {
		try {
			String code=tokens[0].split("\"")[3];
			String name=tokens[1].split("\"")[3];
			String py=tokens[1].split("\"")[3].replaceAll(" ", "");
			AdminInfo ai=new AdminInfo();
			ai.setId(code);
			ai.setValue(name);
			ai.setPyname(py);
			table.getAdminsByCode().put(code, ai);
			table.getAdminsByPYName().put(py.toLowerCase(), ai);
		} catch (Exception e) {
			logger.error("构建Admin失败 File [" + f.getPath() + "], row number is [" + count + "]", e);
		}

	}
	
	public static void main(String[] args){
		String ct[]="\"code\":\"340000\"".split("\"");
		System.out.println(ct);
	}
}
