package up6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileTool {
	public static String readAll(String path)
	{
		File f = new File(path);
		char[] data = new char[(int)f.length()];
		BufferedReader br;
		try {
			br = new BufferedReader( new InputStreamReader(new FileInputStream(f),"UTF-8"));
			br.read(data);
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(data);		
	}
}