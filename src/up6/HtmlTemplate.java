package up6;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HtmlTemplate {

    private JSONArray m_params;
    private String m_html;
    private PathTool m_pt;

    public HtmlTemplate() {
    	this.m_pt = new PathTool();
        this.m_params = new JSONArray();

        //添加系统路径变量
        this.add_param_AppPath();
    }

    void add_param_AppPath()
    {    	
        ConfigReader cr = new ConfigReader();
        this.m_params.add(cr.m_files);
    }


    public void addParam(JSONObject o)
    {
        this.m_params.add(o);
    }

    public void addParam(Object o)
    {
        if (o == null) return;

        if (o instanceof JSONArray)
        {
        	JSONArray arr = (JSONArray)o;
        	for(int i = 0 ; i < arr.size();++i)
        	{
        		this.m_params.add(arr.get(i));
        	}
        }
        else {
            this.m_params.add(o);
        }
    }

    public void setHtml(String v) { this.m_html = v; }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="f">相对路径,/data/test.html</param>
    public void setFile(String f) {
    	String path = this.m_pt.MapPath(f);    	
        this.m_html = FileTool.readAll(path);
    }

    public String toString()
    {
        //解析所有标签
        this.parse_tags();

        return this.m_html;
    }

    /// <summary>
    /// 提取所有标签，
    /// </summary>
    /// <param name="tags">标签，标签名称。{name},name</param>
    public Map extractTag() throws IOException
    {
        int tagBegin = -1;//左括号开始位置
        //提取的标签 {tag}
        Map tags = new HashMap();

        int charStr = 0;
        int index = 0;
        StringReader sr = new StringReader(this.m_html);
        int s = 0;
        while ( (s= sr.read()) != -1)
        {
            charStr = (char)s;
            if (charStr == '{') tagBegin = index;
            else if (charStr == '}' 
                && (-1!= tagBegin)
                )
            {
                int tagLen = index - tagBegin + 1;
                String tag = this.m_html.substring(tagBegin, tagLen);

                //过滤非标准标签名称,防止重复添加
                if ( !tags.containsKey(tag))
                {
                    String tagName = tag.substring(1, tag.length() - 2);
                    tags.put(tag, tagName);
                }

                tagBegin = -1;
            }
            else
            {
                //跳过非标签字符串
                char c = (char)charStr;
                if (!Character.isLetterOrDigit(c) && c != '-' && c!='_')
                {
                    tagBegin = -1;
                }
            }
            ++index;
        }
        return tags;
    }

    public void parse_tags()
    {
        //提取标签,(标签值，标签名称)
        Map tags=new HashMap();
		try {
			tags = this.extractTag();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for(int i = 0 ; i < this.m_params.size();++i)        
        {
            for(Object obj : tags.keySet())
            {
            	Object o = this.m_params.get(i);
            	Object t_v = tags.get(obj);
            	
            	String v = JsonPath.parse(o).read(t_v.toString());
                if (null == v) continue;
                String val = v;
                this.m_html = this.m_html.replace(obj.toString(), val);
            }
        }
    }
}
