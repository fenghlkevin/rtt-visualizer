package cn.com.cennavi.rtic.decoder.match;

import cn.com.cennavi.rtic.decoder.bean.RTICMesage;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData;


public interface IRTICMatcher {
	
	public RTICMesage match(CommRttData item);
	
}
