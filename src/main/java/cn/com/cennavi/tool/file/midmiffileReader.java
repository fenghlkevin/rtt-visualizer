package cn.com.cennavi.tool.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class midmiffileReader implements Imidmiffile {

	/*midmif文件路径*/
	private String strmidfile;
	private String strmiffile;
	private String strKeyWord;

	
	public BufferedReader bfRmid = null;
	public BufferedReader bfRmif = null;

	
	public midmiffileReader(String strmid, String strmif, String strKeyWord) {
		// TODO Auto-generated constructor stub
		this.strmidfile = strmid;
		this.strmiffile = strmif;
		this.strKeyWord = strKeyWord;
	}
	
	public boolean openfile()
	{
		try {
			bfRmid = new BufferedReader(new FileReader(new File(this.strmidfile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}
		
		try {
			bfRmif = new BufferedReader(new FileReader(new File(this.strmiffile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.closefile();
			return false;
		}

		return true;
	}
	
	public void closefile()
	{
		if (this.bfRmid != null)
		{
			try {
				this.bfRmid.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.bfRmid = null;
		}
		
		if (this.bfRmif != null)
		{
			try {
				this.bfRmif.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.bfRmif = null;
		}
	
	}
	
	public midmifRec readmifhead()
	{
		midmifRec rec = new midmifRec();
		rec.vecmifRec = new Vector<String>();
		rec.strmidRec = "";

		String strline;
		try {
			
			/*读取mif数据*/
			while((strline = this.bfRmif.readLine()) != null)
			{
				if (strline.startsWith("Data"))
				{
					rec.vecmifRec.add(strline);
					break;
				}
				else
				{
					rec.vecmifRec.add(strline);					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return rec;
		
	}


	public midmifRec readmidmifOneRec()
	{
		midmifRec rec = new midmifRec();
		rec.vecmifRec = new Vector<String>();

		String strline;
		try {
			
			/*读取mid数据*/
			strline = bfRmid.readLine();
			if (strline != null)
			{
				rec.strmidRec = strline;
			}
			else
			{
				return null;
			}
			
			/*读取mif数据*/
			while((strline = this.bfRmif.readLine()) != null)
			{
				if (strline.trim().toLowerCase().startsWith(strKeyWord.toLowerCase()))
				{
					rec.vecmifRec.add(strline);
					break;
				}
				else
				{
					rec.vecmifRec.add(strline);					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return rec;
		
	}

	public midmifRec readmidmifOneRec_noPen()
	{
		midmifRec rec = new midmifRec();
		rec.vecmifRec = new Vector<String>();

		String strline;
		try {
			
			/*读取mid数据*/
			strline = bfRmid.readLine();
			if (strline != null)
			{
				rec.strmidRec = strline;
			}
			else
			{
				return null;
			}
			
			/*读取mif数据*/
			while((strline = this.bfRmif.readLine()) != null)
			{
				if (strline.trim().startsWith(strKeyWord))
				{
//					rec.vecmifRec.add(strline2);
					break;
				}
				else
				{
					rec.vecmifRec.add(strline);					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return rec;
		
	}

}
