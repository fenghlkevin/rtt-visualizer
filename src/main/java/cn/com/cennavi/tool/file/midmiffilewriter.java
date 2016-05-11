package cn.com.cennavi.tool.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class midmiffilewriter implements Imidmiffile {

	/*midmif文件路径*/
	private String strmidfile;
	private String strmiffile;

	public PrintWriter outRmid = null;
	public PrintWriter outRmif = null;
	
	public midmiffilewriter(String strmid, String strmif) {
		// TODO Auto-generated constructor stub
		this.strmidfile = strmid;
		this.strmiffile = strmif;
	}
	
	@Override
	public boolean openfile() {
		// TODO Auto-generated method stub
		try {
			this.outRmid = new PrintWriter(new BufferedWriter(new FileWriter(this.strmidfile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}

		try {
			this.outRmif = new PrintWriter(new BufferedWriter(new FileWriter(this.strmiffile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}

		return true;
	}

	public boolean openfile(boolean bAppend) {
		// TODO Auto-generated method stub
		try {
			this.outRmid = new PrintWriter(new BufferedWriter(new FileWriter(this.strmidfile, bAppend)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}

		try {
			this.outRmif = new PrintWriter(new BufferedWriter(new FileWriter(this.strmiffile, bAppend)));
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
		if (this.outRmid != null)
		{
			this.outRmid.close();
			this.outRmid = null;
		}
		
		if (this.outRmif != null)
		{
			this.outRmif.close();
			this.outRmif = null;
		}
		
	}
	
	public void writemidmifRec(midmifRec rec)
	{
		this.outRmid.println(rec.strmidRec);
		
		for (int i = 0; i < rec.vecmifRec.size(); i++)
		{
			this.outRmif.println(rec.vecmifRec.get(i));
		}
	}

	public void writemidmifHead(midmifRec rec)
	{
		for (int i = 0; i < rec.vecmifRec.size(); i++)
		{
			this.outRmif.println(rec.vecmifRec.get(i));
		}
	}

}
