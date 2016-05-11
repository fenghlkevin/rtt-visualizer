package cn.com.cennavi.visualizer.common.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ProjectContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

//	private void addMapData() {
//		String[] syskeys = System.getProperties().keySet().toArray(new String[0]);
//		LPTableParser lp=new LPTableParser(null, null);
//		FileFilter lpFileFilter=lp.createFileFilter();
//		for (String tempKey : syskeys) {
//			File[] files=null;
//			if (tempKey.toLowerCase().startsWith("map.")) {
//				File path=new File(System.getProperty(tempKey));
//				if(lpFileFilter==null){
//					files=path.listFiles();
//				}else{
//					files=path.listFiles(lpFileFilter);
//				}
//				
//			} else if (tempKey.toLowerCase().startsWith("lp.")) {
//				
//			} else if (tempKey.toLowerCase().startsWith("mt.")) {
//				
//			}
//		}
//	}
}
