package cn.com.cennavi.tpeg.map.dao;

import java.util.List;

import cn.com.cennavi.transform.bean.LPInfo;

public class CSVFileDataDAOImpl implements IDataDAO {

	@Override
	public List<LPInfo> readPosLPsInGrids(int lpLocationTableNumber) {
//		LPInfoContainer container=LPInfoContainer.getInstance();
//		return container.readPosLPsInGrids(lpLocationTableNumber);
		return readPosLPsInGrids(lpLocationTableNumber,null);
	}

	@Override
	public int getGroupid(Integer lpLocationTableNumber, Integer locid) {
		return this.getGroupid(lpLocationTableNumber, locid,null);
	}

	@Override
	public List<LPInfo> readPosLPsInGrids(int lpLocationTableNumber,
			String lpinfoversion) {
		LPInfoContainer container=LPInfoContainer.getInstance(lpinfoversion);
		return container.readPosLPsInGrids(lpLocationTableNumber);
	}

	@Override
	public int getGroupid(Integer lpLocationTableNumber, Integer locid,
			String lpinfoversion) {
		LPInfoContainer container=LPInfoContainer.getInstance(lpinfoversion);
		return container.getGroupid(lpLocationTableNumber, locid);
	}

	
}
