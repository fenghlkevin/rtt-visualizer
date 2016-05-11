package cn.com.cennavi.tpeg.decoder.parser;

import java.util.List;

import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;

public interface ITPEGDeocder {
	public void execute(byte[] item, List<TFPMessage> tfps, List<TECMessage> tecs) throws Throwable;
}
