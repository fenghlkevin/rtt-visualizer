package cn.com.cennavi.tpeg.decoder.parser.impl;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;

import cn.com.cennavi.tpeg.decoder.parser.ITPEGDeocder;
import cn.com.cennavi.tpeg.exception.TPEGDecodeException;
import cn.com.cennavi.tpeg.item.ComponentFrame;
import cn.com.cennavi.tpeg.item.TECFrame;
import cn.com.cennavi.tpeg.item.TFPFrame;
import cn.com.cennavi.tpeg.item.TPEG;
import cn.com.cennavi.tpeg.item.TransportFrame;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;

public class TPEGDecoderImpl implements ITPEGDeocder {

	@Override
	public void execute(byte[] item, List<TFPMessage> tfps, List<TECMessage> tecs) throws Throwable {

		if (item == null || item.length <= 0) {
			throw new ParseException("No Enough byte length", 0);
		}
		ByteArrayInputStream stream = null;
		stream = new ByteArrayInputStream(item);
		TPEG tpeg = new TPEG();
		tpeg.setEncodedStream(stream);
		tpeg.decoding();
		if (tpeg.getFrames() == null || tpeg.getFrames().size() <= 0) {
			throw new TPEGDecodeException("No TPEG Frame");
		}

		for (TransportFrame tf : tpeg.getFrames()) {
			if (tf == null) {
				continue;
			}
			List<ComponentFrame> cfs = tf.getServiceFrame().getServices();
			for (ComponentFrame cf : cfs) {
				if (cf instanceof TFPFrame) {
					TFPFrame tfp = (TFPFrame) cf;
					for (TFPMessage msg : tfp.getServiceComponent()) {
						tfps.add(msg);
					}

				} else if (cf instanceof TECFrame) {
					TECFrame tec = (TECFrame) cf;
					for (TECMessage msg : tec.getServiceComponent()) {
						tecs.add(msg);
					}
				} else {
					continue;
				}
			}
		}

	}

}
