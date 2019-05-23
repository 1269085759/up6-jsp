package up6;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import net.sf.json.JSONObject;

public class ConfigReader {
	public JSONObject m_files;
	public PathTool m_pt;
	public ReadContext m_jp;
	
	public ConfigReader()
	{
		this.m_pt = new PathTool();
		//自动加载/config.json
		String path = this.m_pt.MapPath("/filemgr/data/config/config.json");
		String json = FileTool.readAll(path);		
		this.m_files = JSONObject.fromObject(json);
		this.m_jp = JsonPath.parse(json);
	}
	
	/**
	 * 将配置加载成一个json对象
	 * @param name
	 * @return
	 */
	public JSONObject module(String name)
	{
		String path = this.m_jp.read(name);
		XDebug.Output(path);
		path = this.m_pt.MapPath(path);
		String data = FileTool.readAll(path);
		return JSONObject.fromObject(data);
	}
}