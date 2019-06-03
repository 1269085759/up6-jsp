package down2.biz;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.biz.PathRelBuilder;
import up6.sql.SqlExec;
import up6.sql.SqlParam;

/**
 * 文件夹下载处理
 * @author zysoft 2019-05-30
 *
 */
public class DnFolder {
	
	public String files(String id) 
	{
		SqlExec se = new SqlExec();
		JSONObject fd = se.read("up6_folders", "f_pidRoot", new SqlParam[] {new SqlParam("f_id",id)});
		String pidRoot = "";
		if(fd == null) pidRoot = id;
		else pidRoot = fd.getString("f_pidRoot");
		
		return this.filesChild(id, pidRoot);
		
	}
	
	public String filesChild(String id,String pidRoot)
	{
        //构建子目录路径
        PathRelBuilder prb = new PathRelBuilder();
        JSONArray fs = prb.build(id,pidRoot);
        

        return fs.toString();		
	}

}
