package cn.com.cennavi.tool.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class filereader implements Imidmiffile {

	private String strfile;
	
	public BufferedReader bf = null;

	public filereader(String strfile) {
		// TODO Auto-generated constructor stub
		this.strfile = strfile;
	}
	
	@Override
	public boolean openfile() {
		// TODO Auto-generated method stub
		try {
			bf = new BufferedReader(new FileReader(new File(this.strfile)));
		} catch (FileNotFoundException e) {
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
		if (this.bf != null)
		{
			try {
				this.bf.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.bf = null;
		}
	}
	
	public String readLine()
	{
		try {
			return bf.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
