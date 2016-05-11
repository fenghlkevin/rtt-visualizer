package cn.com.cennavi.tpeg.map.dao;

import java.util.List;

import cn.com.cennavi.transform.bean.LPInfo;

/**
 * 读取
 * @author Kevin Feng
 *
 */
public interface IDataDAO {
    /**
     * 取得所有位置点信息
     * @param gridBlock
     * @return
     * @deprecated
     */
    public List<LPInfo> readPosLPsInGrids(int lpLocationTableNumber);
    
    /**
     * 
     * @param lpLocationTableNumber
     * @param locid
     * @return
     * @deprecated
     */
    public int getGroupid(Integer lpLocationTableNumber,Integer locid);
    
    /**
     * 取得所有位置点信息
     * @param gridBlock
     * @return
     */
    public List<LPInfo> readPosLPsInGrids(int lpLocationTableNumber,String lpinfoversion);
    
    public int getGroupid(Integer lpLocationTableNumber,Integer locid,String lpinfoversion);
}
