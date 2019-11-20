package up6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.model.FileInf;
import up6.sql.SqlExec;
import up6.sql.SqlParam;
import up6.sql.SqlWhereMerge;

public class DbFolder 
{
	JSONObject root;
	
	public DbFolder() 
	{
		this.root = new JSONObject();
		this.root.put("f_id", "");
		this.root.put("f_nameLoc", "根目录");
		this.root.put("f_pid", "");
		this.root.put("f_pidRoot", "");
	}
	
	/**
	 * 将JSONArray转换成map
	 * @param folders
	 * @return
	 */
	public Map<String, JSONObject> toDic(JSONArray folders)
    {
        Map<String, JSONObject> dt = new HashMap<String, JSONObject>();
        for(int i = 0 , l = folders.size();i<l;++i)
        {
        	JSONObject o = folders.getJSONObject(i);
        	String id = o.getString("f_id");
        	dt.put(id, o);
        }
        return dt;
    }
	
    public Map<String, JSONObject> foldersToDic(String pidRoot)
    {
        //默认加载根目录
    	String sql = String.format("select f_id,f_nameLoc,f_pid,f_pidRoot from up6_folders where f_pidRoot='%s'", pidRoot);

        SqlExec se = new SqlExec();
        JSONArray folders = se.exec("up6_folders", sql, "f_id,f_nameLoc,f_pid,f_pidRoot","");
        return this.toDic(folders);
    }
    
    public ArrayList<JSONObject> sortByPid( Map<String, JSONObject> dt, String idCur, ArrayList<JSONObject> psort) {

    	String cur = idCur;
        while (true)
        {
            //key不存在
            if (!dt.containsKey(cur)) break;

            JSONObject d = dt.get(cur);//查父ID
            psort.add(0, d);//将父节点排在前面            
            cur = d.getString("f_pid").trim();//取父级ID

            if (cur.trim() == "0") break;
            if ( StringUtils.isBlank(cur) ) break;
        }
        return psort;
    }
    

    public JSONArray build_path_by_id(JSONObject fdCur) {

        String id = fdCur.getString("f_id").trim();//
        String pidRoot = fdCur.getString("f_pidRoot").trim();//

        //根目录
        ArrayList<JSONObject> psort = new ArrayList<JSONObject>();
        if (StringUtils.isBlank(id))
        {
            psort.add(0, this.root);

            return JSONArray.fromObject(psort);
        }

        //构建目录映射表(id,folder)
        Map<String, JSONObject> dt = this.foldersToDic(pidRoot);

        //按层级顺序排列目录
        psort = this.sortByPid(dt, id, psort);

        SqlExec se = new SqlExec();
        //是子目录->添加根目录
        if (!StringUtils.isBlank(pidRoot))
        {
            JSONObject root = se.read("up6_files"
            		, "f_id,f_nameLoc,f_pid,f_pidRoot"
            		, new SqlParam[] { new SqlParam("f_id", pidRoot) });
            psort.add(0, root);
        }//是根目录->添加根目录
        else if (!StringUtils.isBlank(id) && StringUtils.isBlank(pidRoot))
        {
            JSONObject root = se.read("up6_files"
            		, "f_id,f_nameLoc,f_pid,f_pidRoot"
            		, new SqlParam[] { new SqlParam("f_id", id) });
            psort.add(0, root);
        }
        psort.add(0, this.root);

        return JSONArray.fromObject(psort);
    }
    
    public JSONArray build_path(JSONObject fdCur) {
    	
    	//查询文件表目录数据
    	SqlExec se = new SqlExec();
        JSONArray files = se.select("up6_files", "f_id,f_pid,f_nameLoc,f_pathRel", new SqlParam[] { new SqlParam("f_fdTask",true) },"");
        JSONArray folders = se.select("up6_folders", "f_id,f_pid,f_nameLoc,f_pathRel", new SqlParam[] { },"");
        String id = fdCur.getString("f_id").trim();//

        //根目录
        ArrayList<JSONObject> psort = new ArrayList<JSONObject>();
        if (StringUtils.isBlank(id))
        {
            psort.add(0, this.root);

            return JSONArray.fromObject(psort);
        }

        //构建目录映射表(id,folder)
        Map<String, JSONObject> dtFiles = this.toDic(files);
        Map<String, JSONObject> dtFolders = this.toDic(folders);
        for(String key : dtFolders.keySet())
        {
        	if(!dtFiles.containsKey(key)) dtFiles.put(key, dtFolders.get(key));
        }
        
        //按层级顺序排列目录
        psort = this.sortByPid(dtFiles, id, psort);

        
        psort.add(0, this.root);

        return JSONArray.fromObject(psort);
    }
	
	public FileInf read(String id) {
        SqlExec se = new SqlExec();
        String sql = String.format("select f_pid,f_pidRoot,f_pathSvr,f_pathRel from up6_files where f_id='%s' union select f_pid,f_pidRoot,f_pathSvr,f_pathRel from up6_folders where f_id='%s'", id,id);
        JSONArray data = se.exec("up6_files", sql, "f_pid,f_pidRoot,f_pathSvr,f_pathRel","");
        JSONObject o = (JSONObject)data.get(0);

        FileInf file = new FileInf();
        file.id = id;
        file.pid = o.getString("f_pid").trim();
        file.pidRoot = o.getString("f_pidRoot").trim();
        file.pathSvr = o.getString("f_pathSvr").trim();
        file.pathRel = o.getString("f_pathRel").trim();
        return file;
    }
	
	public Boolean exist_same_file(String name,String pid)
    {
        SqlWhereMerge swm = new SqlWhereMerge();
        swm.equal("f_nameLoc", name.trim());
        swm.equal("f_pid", pid.trim());
        swm.equal("f_deleted", 0);

        String sql = String.format("select f_id from up6_files where %s ", swm.to_sql());

        SqlExec se = new SqlExec();
        JSONArray arr = se.exec("up6_files", sql, "f_id", "");
        return arr.size() > 0;
    }
	
	/**
	 * 检查是否存在同名目录
	 * @param name
	 * @param pid
	 * @return
	 */
	public Boolean exist_same_folder(String name,String pid) 
	{
        SqlWhereMerge swm = new SqlWhereMerge();
        swm.equal("f_nameLoc", name.trim());
        swm.equal("f_deleted", 0);
        swm.equal("f_pid", pid.trim());
        String where = swm.to_sql();

        String sql = String.format("(select f_id from up6_files where %s ) union (select f_id from up6_folders where %s)", where,where);

        SqlExec se = new SqlExec();
        JSONArray fid = se.exec("up6_files", sql, "f_id", "");
        return fid.size() > 0;		
	}
	
    public Boolean rename_file_check(String newName,String pid)
    {
        SqlExec se = new SqlExec();            
        JSONArray res = se.select("up6_files"
            , "f_id"
            ,new SqlParam[] {
                new SqlParam("f_nameLoc",newName)
                ,new SqlParam("f_pid",pid)
            },"");
        return res.size() > 0;
    }
    
    public Boolean rename_folder_check(String newName, String pid)
    {
        SqlExec se = new SqlExec();
        JSONArray res = se.select("up6_folders"
            , "f_id"
            , new SqlParam[] {
                new SqlParam("f_nameLoc",newName)
                ,new SqlParam("f_pid",pid)
            },"");
        return res.size() > 0;
    }

    public void rename_file(String name,String id) {
        SqlExec se = new SqlExec();
        se.update("up6_files"
            , new SqlParam[] { new SqlParam("f_nameLoc", name) }
            , new SqlParam[] { new SqlParam("f_id", id) });
    }
    
    public void rename_folder(String name, String id, String pid) {
        SqlExec se = new SqlExec();
        se.update("up6_folders"
            , new SqlParam[] { new SqlParam("f_nameLoc", name) }
            , new SqlParam[] { new SqlParam("f_id", id) });
    }
}
