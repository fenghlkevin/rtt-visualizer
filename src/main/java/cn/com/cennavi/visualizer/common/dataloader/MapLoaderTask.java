package cn.com.cennavi.visualizer.common.dataloader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.cennavi.framework.httpput.dataloader.AbstractLoaderTask;
import cn.com.cennavi.kfgis.framework.file.impl.NAbstractFileDataParser;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingTableParser;
import cn.com.cennavi.visualizer.common.dataloader.lp.LPTableParser;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.n.NTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RTableParser;

@Component("mapdataLoaderTask")
public class MapLoaderTask extends AbstractLoaderTask{

	private static Logger logger = LoggerFactory.getLogger("task");
	
	
	private static List<NAbstractFileDataParser<?>> parserList;
	

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 *  
	 * @author:fengheliang
	 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
	 */
	public void load() {
		if (logger.isDebugEnabled()) {
			logger.debug("Start check new Data dataLoaderTask");
		}
		
		String[] syskeys=System.getProperties().keySet().toArray(new String[0]);
		if(parserList==null){
			parserList=new ArrayList<NAbstractFileDataParser<?>>();
			for(String tempKey:syskeys){
				if(tempKey.toLowerCase().startsWith("lp.")){
					parserList.add(new LPTableParser(tempKey,tempKey));
				}else if(tempKey.toLowerCase().startsWith("mt.")){
					parserList.add(new MTTableParser(tempKey,tempKey));
				}else if(tempKey.toLowerCase().startsWith("ct.")){
					parserList.add(new CorrespondingTableParser(tempKey,tempKey));
				}else if(tempKey.toLowerCase().startsWith("map.n.")){
					parserList.add(new NTableParser(tempKey,tempKey));
				}else if(tempKey.toLowerCase().startsWith("map.")){
					parserList.add(new RTableParser(tempKey,tempKey));
				}else if(tempKey.toLowerCase().startsWith("admin.")){
					parserList.add(new RTableParser(tempKey,tempKey));
				}
			}
		}
		for(NAbstractFileDataParser<?> parse:parserList){
			this.read(parse.getSysKey(), null,parse, false);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Ene Load dataLoaderTask");
		}
	}

}