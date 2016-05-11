package cn.com.cennavi.visualizer.common.dataloader.mt;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.com.cennavi.kfgis.framework.file.impl.CSVDataAbstractParser;
import cn.com.cennavi.kfgis.util.ObjectUtil;

/**
 * parser lpinfo file
 * 
 * @author fengheliang
 * 
 */
public class MTTableParser extends CSVDataAbstractParser<MTTable> {

	public MTTableParser(String sysKey, String dataFlag) {
		super(sysKey, dataFlag);
	}
	
	@Override
	protected FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && !pathname.getName().toLowerCase().equalsIgnoreCase(".svn")&&pathname.getName().toLowerCase().endsWith(".csv");
			}
		};
	}

	@Override
	protected MTTable createContainer() {
		return new MTTable();
	}

	@Override
	protected boolean ignore(String[] tokens) {
		return false;// TODO
	}

	@Override
	protected void putValue2Container(MTTable tmcTable, String[] tokens) {
	}

	private String[] title = null;

	@Override
	protected void putValue2Container(MTTable table, String[] tokens, File f, int count) {
		if (count == 0) {
			title = tokens;
			return;
		}
		Object obj = null;

		try {
			obj = MTInfo.class.newInstance();
			for (int i = 0; i < tokens.length; i++) {
				String fieldName = title[i];
				String fieldValue = tokens[i];
				Method method = ObjectUtil.getMethodByName(obj, "set"+fieldName, true);
				method.invoke(obj, fieldValue);
			}
			final MTInfo t = (MTInfo) obj;
			Integer lc=new Integer(t.getLocationCode());
			Integer dir="+".equalsIgnoreCase(t.getLocationDirection())?0:1;
			Integer ltn=new Integer(t.getLtn());
			Long lpkey=getLPKey(lc,dir,ltn);//(new Long(t.getLocationCode())*10L+new Integer(t.getLink_direction()))*100+new Long(t.getLtn());
			
			if(table.getMtLinksMap().containsKey(lpkey)){
				table.getMtLinksMap().get(lpkey).add(t);
			}else{
				List<MTInfo> temp=new ArrayList<MTInfo>();
				temp.add(t);
				table.getMtLinksMap().put(lpkey, temp);
			}
			
		} catch (Exception e) {
			logger.error("构建MT失败 File ["+f.getPath()+"], row number is ["+count+"]", e);
		}

	}
	
	public static Long getLPKey(int locid,int dir,int ltn){
		return (locid*10L+dir)*100L+ltn;
	}
}
