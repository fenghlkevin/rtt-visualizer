package cn.com.cennavi.tpeg.decoder.match;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;

public interface IMatcher {
	
	public TPEGMesage match(Item tfp);
	
}
