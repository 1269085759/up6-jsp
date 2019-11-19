package up6;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class PathTool {
	
	public static String getName(String n){
		File f = new File(n);
		return f.getName();
	}
	public static String getExtention(String n){
		String name = getName(n);

		int extIndex = name.lastIndexOf(".");
		//有扩展名
		if(-1 != extIndex)
		{
			String ext = name.substring(extIndex + 1);
			return ext;
		}
		return "";
	}
	
	public static Boolean exist(String v)
	{
		File f = new File(v);
		return f.exists();
	}
	
	public static void createDirectory(String v){

		File fd = new File(v);		
		//fix():不创建文件夹
		if(!fd.exists()) fd.mkdirs();
	}
	
	//规范化路径，与操作系统保持一致。
	public static String canonicalPath(String v) throws IOException{
		File f = new File(v);
		return f.getCanonicalPath();
	}
	
	public static String combine(String a,String b)
	{
		String path="";
		File ps;
		boolean split = a.endsWith("\\");
		if(!split) split = a.endsWith("/");		
		//没有斜杠
		if(!split)
		{
			ps = new File(a.concat("/").concat(b));
		}//有斜框
		else{
			ps = new File(a.concat(b));
		}
		try {
			path = ps.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
	
	public static String url_decode(String v)
	{
		v = v.replace("+","%20");	
		try {
			v = URLDecoder.decode(v,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//utf-8解码//客户端使用的是encodeURIComponent编码，
		return v;
	}
	
	/**
	 * 将相对路径转换成绝对路径
	 * @param ps res
	 * @return /up6/imgs
	 */
	public String MapPath(String ps)
	{		
		// /up6
		String root = this.getRoot();
		
		if(StringUtils.isBlank(ps)) return root;
		
		//传值： /imgs
		if(!StringUtils.equals("/", ps.substring(0, 1))) root.contains("/");
		
		root = root.concat(ps);
		
		File f = new File(root);
		String path="";
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
	
	/**
	 * 获取项目根目录
	 * @return C:\\java\\tomcat\\7.0.47\\webapps\\up6
	 */
	public String getRoot() 
	{
		//前面会多返回一个"/", 例如  /D:/test/WEB-INF/, 奇怪, 所以用 substring()
		String path = this.getClass().getResource("/").getPath().substring(1).replaceAll("//", "/");
		if ( !StringUtils.isBlank(path) && !path.endsWith("/"))
		{
			path = path.concat("/");    //避免 WebLogic 和 WebSphere 不一致
		}
		path = path.replace("classes/", "");
		//D:/apache-tomcat-6.0.29/webapps/up6/WEB-INF/
		path = path.replace("%20", " ");//fix(2016-02-29):如果路径中包含空格,getPath会自动转换成%20
		//D:/apache-tomcat-6.0.29/webapps/up6
		path = path.replace("WEB-INF/", "");
		//D:/apache-tomcat-6.0.29/webapps/up6/
		
		File f = new File(path);
		//D:/apache-tomcat-6.0.29/webapps/Uploader6.1MySQL
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//取规范化的路径。
		return path;
	}
}