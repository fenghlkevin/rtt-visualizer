package cn.com.cennavi.tool.file;

public class Test {

	public static void main(String[] args) {
		String path="/home/fengheliang/tools/workbench/workspace/ws-web/rtt-visualizer/src/main/resources/data/map/";
		midmiffileReader mr=new midmiffileReader(path+"Rbeijing.mid",path+"Rbeijing.mif","PEN");
		mr.openfile();
		midmifRec head=mr.readmifhead();
		midmifRec values=mr.readmidmifOneRec();
		mr.closefile();
	}
	
}
