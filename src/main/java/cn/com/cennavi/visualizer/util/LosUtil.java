package cn.com.cennavi.visualizer.util;

public class LosUtil {
	public static int rtic2tpeg(int los){
		if(los==2){
			return 3;
		}else if(los==3){
			return 4;
		}else if(los==4){
			return 5;
		}else{
			return los;
		}
	}
}
