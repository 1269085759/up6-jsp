package up6.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.DbFolder;
import up6.sql.SqlExec;
import up6.sql.SqlParam;

/**
 * 路径构建
 * @author zysoft
 *
 */
public class PathRelBuilder {
	Map<String,JSONObject> m_folders;
	JSONArray m_files;
	/**
	 * 需要查询的子目录列表
	 */
	ArrayList<String> m_fdQuerys;
	/**
	 * 子目录列表
	 */
	Map<String,String> m_childs;
	
	public PathRelBuilder() 
	{
		this.m_fdQuerys = new ArrayList<String>();
		this.m_childs = new HashMap<String, String>();
		this.m_files = null;
	}
	
	public JSONArray build(String id,String pidRoot) 
	{
        this.query(id, pidRoot);
        this.buildFolder(id);
        return this.buildFile();		
	}
	
	/**
	 * 查询目录和文件列表
	 * @param id
	 * @param pidRoot
	 */
    void query(String id,String pidRoot)
    {
        //查询目录
    	SqlExec se = new SqlExec();
        JSONArray folders = se.select("up6_folders"
            , "f_id,f_nameLoc,f_pid,f_pidRoot"
            , new SqlParam[] { new SqlParam("f_deleted", false) }
            , "");
       
        DbFolder df = new DbFolder();
        this.m_folders = df.toDic(folders);

        //是根节点
        if (id == pidRoot)
        {
            JSONObject root = se.read("up6_files", "f_nameLoc", new SqlParam[] { new SqlParam("f_id", id) });
            JSONObject cur = new JSONObject();
            cur.put("f_id", id);
            cur.put("f_pid", "");
            cur.put("f_pidRoot", "");
            cur.put("f_nameLoc", root.getString("f_nameLoc"));
            this.m_folders.put(id, cur);
        }

        //查询文件
        this.m_files = se.select("up6_files"
            , "f_id,f_pid,f_nameLoc,f_pathSvr,f_pathRel,f_lenSvr,f_sizeLoc"
            , new SqlParam[] {
                new SqlParam("f_pidRoot", pidRoot)
                ,new SqlParam("f_deleted", false)
                ,new SqlParam("f_complete", true)
            },""
        );       
    }
    
    void buildFolder(String id)
    {
    	String idCur = id;
        this.m_childs.put(id, id);
        JSONObject fdPrev = m_folders.get(id);
        fdPrev.put("f_pathRel","");
        while (true)
        {
            //更新所有子目录相对路径
            this.updateChilds(idCur, fdPrev.getString("f_pathRel"));
            if (this.m_fdQuerys.size() == 0) break;

            idCur = this.m_fdQuerys.get(0);
            this.m_fdQuerys.remove(0);

            JSONObject fdCur = this.m_folders.get(idCur);
            String pid = fdCur.getString("f_pid");
            JSONObject fdParent = this.m_folders.get(pid);
            fdCur.put("f_pathRel", fdParent.getString("f_pathRel") + "/" + fdCur.getString("f_nameLoc"));
        }

        //清除无关数据
        for(String k : this.m_childs.keySet())
        {
        	if(!this.m_folders.containsKey(k)) this.m_folders.remove(k);
        }
    }

    /**
     * 为所有文件构建路径
     * @return
     */
    JSONArray buildFile()
    {
    	DbFolder df = new DbFolder();
    	Map<String,JSONObject> fs = df.toDic(this.m_files);

        JSONArray arr = new JSONArray();
        //更新路径
    	for(Object k : fs.keySet())
    	{
    		JSONObject f = fs.get(k );

            if (!this.m_childs.containsKey(f.getString("f_pid"))) continue;

            JSONObject parent = this.m_folders.get( f.getString("f_pid") );
            f.put("f_pathRel", parent.getString("f_pathRel") + "/" + f.getString("f_nameLoc"));

            //更名
            f.put("nameLoc", f.getString("f_nameLoc"));
            f.put("pathSvr", f.getString("f_pathSvr"));
            f.put("pathRel", f.getString("f_pathRel"));
            f.put("lenSvr", f.getString("f_lenSvr"));
            f.put("sizeSvr",f.getString("f_sizeLoc") );
            arr.add(f);
        }

        return arr;
    }

    /**
     * 更新所有子目录路径
     * @param pid
     * @param parentRel
     * @return
     */
    Boolean updateChilds(String pid,String parentRel)
    {
    	JSONArray childs = new JSONArray();
    	for(String k : this.m_folders.keySet())
    	{
    		JSONObject f = this.m_folders.get(k );
    		if( StringUtils.equals(f.getString("f_pid"),pid)) childs.add(f);
    	}
    	
        for(int i = 0 , l = childs.size();i<l;++i)
        {
        	JSONObject c = childs.getJSONObject(i);
            this.m_fdQuerys.add(c.getString("f_id"));
            this.m_childs.put(c.getString("f_id"), c.getString("f_id"));
            c.put("f_pathRel", parentRel + "/" + c.getString("f_nameLoc"));
            if (StringUtils.isBlank(parentRel) )
                c.put("f_pathRel", c.getString("f_nameLoc"));
        }
        return childs.size() > 0;
    }
}
