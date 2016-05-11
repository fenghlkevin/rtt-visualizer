package cn.com.cennavi.visualizer.service.parsedata.translate;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.com.cennavi.kfgis.framework.exception.NServiceInternalException;
import cn.com.cennavi.kfgis.util.SBase64;
import cn.com.cennavi.kfgis.util.csv.CSVReader;
import cn.com.cennavi.kfgis.util.csv.CSVReaderImpl;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData.CNRTICRttData;


public class CNRticRttDataTranslateImpl implements IRttDataTranslate {

	@Override
	public List<CommRttData> translate(String database64) {

		List<CommRttData> all=new ArrayList<CommRttData>();
		
		String data = database64;
		byte[] item = null;
		try {
			item = SBase64.decode(data);
		} catch (UnsupportedEncodingException e) {
			throw new NServiceInternalException("Base64解析失败", e);
		}
		String rticstr = new String(item);
		CSVReader rticcsv = new CSVReaderImpl(new StringReader(rticstr), CSVReader.DEFAULT_SEPARATOR, CSVReader.DEFAULT_QUOTE_CHARACTER, 1);
		
		String[] line;
		try {
			while((line=rticcsv.readNext())!=null){
				CNRTICRttData rtic=new CNRTICRttData();
				rtic.setMeshid(Long.valueOf(line[0]));
				rtic.setKind(Integer.valueOf(line[1]));
				rtic.setId(Long.valueOf(line[2]));
				rtic.setLos(Integer.valueOf(line[5]));
				all.add(rtic);
			}
		} catch (IOException e) {
			throw new NServiceInternalException("解析CNRTIC失败", e);
		}
		return all;
	}


}
