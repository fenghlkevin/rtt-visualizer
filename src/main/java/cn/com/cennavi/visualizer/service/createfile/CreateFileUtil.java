package cn.com.cennavi.visualizer.service.createfile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.com.cennavi.tpeg.builder.TPEGBuilder;
import cn.com.cennavi.tpeg.item.TPEG;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;

public class CreateFileUtil {
	
	public static String saveTpegFile(List<TFPMessage> tfpList, List<TECMessage> tecList) {
		Long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.S");
		String fileDate = sdf.format(time);

		String filename = "rtt_test_" + fileDate + ".tpg";

		String url = System.getProperty("TEMP_TPG_DOWNLOAD_URL");
		String path = System.getProperty("TEMP_TPG_PATH");
		path += filename;
		url += filename;

		System.out.println(path);
		try {
			TPEGBuilder b = new TPEGBuilder();
			cn.com.cennavi.tpeg.builder.Configure c = new cn.com.cennavi.tpeg.builder.Configure();
			c.setSidA(10);
			c.setSidB(20);
			c.setSidC(30);
			b.setConfigure(c);

			TPEG tpeg = b.execute(tfpList, tecList);
			tpeg.encoding();

			File file = new File(path);
			// 如果文件夹不存在则创建
			if (!file.getParentFile().exists()) {
				file.mkdirs();
			}
			file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(tpeg.getEncodedStream());
			fos.flush();
			fos.close();
			System.out.println("tpeg file write over.");

		} catch (Throwable e) {
			e.printStackTrace();
			return "";
		}

		return url;
	}
}
