package cn.com.cennavi.tool.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class filewriter implements Imidmiffile {

	/*文件路径*/
	private String strfile;

	public PrintWriter out = null;
	
	public filewriter(String strfile) {
		// TODO Auto-generated constructor stub
		this.strfile = strfile;
	}
	
	@Override
	public boolean openfile() {
		// TODO Auto-generated method stub
		return openfile(false);
	}

	public boolean openfile(boolean bAppend) {
		// TODO Auto-generated method stub
		try {
			this.out = new PrintWriter(new BufferedWriter(new FileWriter(this.strfile, bAppend)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}

		return true;
	}

	@Override
	public void closefile() {
		// TODO Auto-generated method stub
		if (this.out != null)
		{
			this.out.close();
			this.out = null;
		}
		
	}
	
	public void writeLine(String strLine)
	{
		this.out.println(strLine);
		
	}

}
