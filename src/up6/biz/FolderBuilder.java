package up6.biz;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.sql.SqlExec;
import up6.sql.SqlParam;

/**
 * 构建文件夹下载数据
 * 格式：
 * [
 *   {f_id,nameLoc,pathSvr,pathRel,lenSvr,sizeSvr}
 *   {f_id,nameLoc,pathSvr,pathRel,lenSvr,sizeSvr}
 * ]
 * @author zysoft
 *
 */
public class FolderBuilder {
	
	/**
	 * 
	 * @param id 文件夹ID
	 * @return
	 */
	public JSONArray build(String id) 
	{
		SqlExec se = new SqlExec();
		JSONObject o = se.read("up6_files", "*", new SqlParam[] { new SqlParam("f_id", id) });
		//子目录
		if(o==null)
		{
			o = se.read("up6_folders", "*", new SqlParam[] { new SqlParam("f_id", id) });
		}
		
		String pathRoot = o.getString("f_pathRel");
		int index = pathRoot.length();

        
        //查询文件
        String where = String.format("CHARINDEX('%s',f_pathRel)>0 and f_fdTask=0", pathRoot+"/");
        JSONArray files = se.select("up6_files", "*", where,"");
        int count = files.size();
        for(int i = 0 ; i < count; i++)
        {
        	JSONObject f = (JSONObject)files.get(i);
            String pathRel = f.getString("f_pathRel");
            pathRel = pathRel.substring(index);
            
            f.put("f_id", f.getString("f_id"));
            f.put("nameLoc", f.getString("f_nameLoc"));
            f.put("pathSvr", f.getString("f_pathSvr"));
            f.put("lenSvr", f.getString("f_lenSvr"));                        
            f.put("pathRel", pathRel);
        }
        
        return files;
	}
}
