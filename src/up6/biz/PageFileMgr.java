package up6.biz;

import java.util.UUID;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.DbFolder;
import up6.PathTool;
import up6.WebBase;
import up6.sql.SqlExec;
import up6.sql.SqlParam;
import up6.sql.SqlWhereMerge;

/**
 * 文件管理器页面逻辑
 * @author zysoft
 *
 */
public class PageFileMgr {
	PageContext m_pc;
	WebBase m_wb;
	
	public PageFileMgr(PageContext pc) 
	{
		this.m_pc = pc;
		this.m_wb = new WebBase(pc);
		
		String op = pc.getRequest().getParameter("op");
		if(StringUtils.equals("data", op)) this.load_data();
		else if(StringUtils.equals("data", op)) this.load_data();
		else if(StringUtils.equals("search", op)) this.load_data();
		else if(StringUtils.equals("rename", op)) this.rename();
		else if(StringUtils.equals("del", op)) this.del();
		else if(StringUtils.equals("del-batch", op)) this.del_batch();
		else if(StringUtils.equals("path", op)) this.build_path();
		else if(StringUtils.equals("mk-folder", op)) this.mk_folder();
		else if(StringUtils.equals("uncomp", op)) this.uncomp();
		else if(StringUtils.equals("uncmp-down", op)) this.uncmp_down();
		else if(StringUtils.equals("tree", op)) this.load_tree();
	}


    void mk_folder()
    {
        String data = this.m_wb.queryString("data");
        data = PathTool.url_decode(data);
        JSONObject obj = JSONObject.fromObject( data );
        String name = obj.getString("f_nameLoc").trim();
        String pid = obj.getString("f_pid".trim());
        String pidRoot = obj.getString("f_pidRoot").trim();
        obj.put("f_nameLoc", name);
        obj.put("f_pid", pid);
        obj.put("f_pidRoot", pidRoot);

        DbFolder df = new DbFolder();
        if (df.exist_same_folder(name, pid))
        {
        	JSONObject ret = new JSONObject();
        	ret.put("ret", false);
        	ret.put("msg", "已存在同名目录");            
            this.m_wb.toContent(ret);
            return;
        }

        SqlExec se = new SqlExec();
    	String guid = UUID.randomUUID().toString().replace("-","");
    	
        //根目录
        if ( StringUtils.isBlank(pid) )
        {             
            obj.put("f_id", guid);

            se.insert("up6_files", new SqlParam[] {
                new SqlParam("f_id",guid)
                ,new SqlParam("f_pid",obj.getString("f_pid") )
                ,new SqlParam("f_pidRoot",obj.getString("f_pidRoot") )
                ,new SqlParam("f_nameLoc",obj.getString("f_nameLoc") )
                ,new SqlParam("f_complete",true)
                ,new SqlParam("f_fdTask",true)
            });
        }//子目录
        else
        {
            obj.put("f_id",guid);
            se.insert("up6_folders"
                , new SqlParam[] {
                new SqlParam("f_id",guid)
                ,new SqlParam("f_pid",obj.getString( "f_pid" ))
                ,new SqlParam("f_pidRoot",obj.getString( "f_pidRoot" ) )
                ,new SqlParam("f_nameLoc",obj.getString( "f_nameLoc" ) )
                ,new SqlParam("f_complete",true)
                });
        }

        obj.put("ret", true);
        this.m_wb.toContent(obj);
    }
    
    void build_path()
    {
        String data = this.m_wb.queryString("data");
        data = PathTool.url_decode(data);
        JSONObject fd = JSONObject.fromObject(data);

        DbFolder df = new DbFolder();

        this.m_wb.toContent( df.build_path_by_id(fd) );
    }
    
    void load_tree() {
        String pid = this.m_pc.getRequest().getParameter("pid");
        SqlWhereMerge swm = new SqlWhereMerge();
        swm.equal("f_fdChild", 0);
        swm.equal("f_fdTask", 1);
        swm.equal("f_deleted", 0);
        if (!StringUtils.isBlank(pid)) swm.equal("f_pid", pid);

        SqlExec se = new SqlExec();
        JSONArray arr = new JSONArray();
        JSONArray data = se.select("up6_files"
            , "f_id,f_pid,f_pidRoot,f_nameLoc"
            , swm.to_sql()
            ,"");

        //查子目录
        if (!StringUtils.isBlank(pid))
        {
            data = se.select("up6_folders"
                , "f_id,f_pid,f_pidRoot,f_nameLoc"
                , new SqlParam[] {
                    new SqlParam("f_pid", pid)
                    ,new SqlParam("f_deleted", false)
                },"");
        }

        for(int i = 0 , l = data.size() ; i<l;++i)
        {
            JSONObject item = new JSONObject();
            JSONObject f = (JSONObject)data.get(i);
            item.put("id", f.getString("f_id") );
            item.put("text", f.getString("f_nameLoc"));
            item.put("parent", "#");
            item.put("nodeSvr", f);
            arr.add(item);
        }
        this.m_wb.toContent(arr);
    }
	
	public void load_data() {
		String pid = this.m_wb.queryString("pid");
		String pageSize = this.m_wb.queryString("pageSize");
		String pageIndex = this.m_wb.queryString("pageIndex");
		if(StringUtils.isBlank(pageSize)) pageSize = "20";
		if(StringUtils.isBlank(pageIndex)) pageIndex = "1";
		
		SqlWhereMerge swm = new SqlWhereMerge();
		swm.equal("f_pid", pid);
		swm.equal("f_complete", 1);
		swm.equal("f_deleted", 0);
		swm.equal("f_fdChild", 1);
		
		Boolean isRoot = StringUtils.isBlank(pid);
		if(isRoot) swm.equal("f_fdChild", 0);		
		
		String where = swm.to_sql();
		
		//加载文件列表
		SqlExec se = new SqlExec();
		JSONArray files = se.page("up6_files"
				,"f_id"
				, "f_id,f_pid,f_nameLoc,f_sizeLoc,f_lenLoc,f_time,f_pidRoot,f_fdTask,f_pathSvr,f_pathRel"
				, Integer.parseInt( pageSize )
				, Integer.parseInt( pageIndex )
				, where
				, "f_fdTask desc,f_time desc");
		
		//根目录不加载up6_folders表
		JSONArray folders = new JSONArray();
		if(!isRoot)
		{
			swm.del("f_fdChild");
            where = swm.to_sql();
            folders = se.page("up6_folders"
                , "f_id"
                , "f_id,f_nameLoc,f_pid,f_sizeLoc,f_time,f_pidRoot"
                ,Integer.parseInt(pageSize)
                ,Integer.parseInt(pageIndex)
                , where
                , "f_time desc");

            for(int i = 0 , l = folders.size();i<l;++i)
            {
            	JSONObject o = folders.getJSONObject(i);
            	o.put("f_fdTask", true);
            	o.put("f_fdChild", false);
            	o.put("f_pathSvr", "");
            	o.put("f_pathRel", "");
            }
		}
		
		//添加文件
		for(int i = 0 , l = files.size();i<l;++i) folders.add(files.getJSONObject(i));
		
		JSONObject o = new JSONObject();
		o.put("count", se.count("up6_files", where));
		o.put("code", 0);
		o.put("msg", "");
		o.put("data", folders);
		
		this.m_wb.toContent(o);
	}
	public void search() {}
	public void rename() {

        String data = this.m_wb.queryString("data");
        data = PathTool.url_decode(data);
        JSONObject o = JSONObject.fromObject(data);

        DbFolder db = new DbFolder();
        Boolean fdTask = o.getBoolean("f_fdTask");
        String pid = o.getString("f_pid").trim();
        String id = o.getString("f_id").trim();
        String nameNew = o.getString("f_nameLoc").trim();

        Boolean exist = false;
        if (!fdTask || StringUtils.isBlank(pid)) exist = db.rename_file_check(nameNew, pid);
        else exist = db.rename_folder_check(nameNew, pid);

        //存在同名项
        if (exist)
        {
        	JSONObject res = new JSONObject();
        	res.put("state", false);
        	res.put("msg", "存在同名荐");            
            this.m_wb.toContent(res);
            return;
        }

        //是文件或根目录
        if (!fdTask || StringUtils.isBlank(pid)) db.rename_file(nameNew, id);
        else db.rename_folder(nameNew, id, pid);

        JSONObject ret = new JSONObject();
        ret.put("state", true);
        this.m_wb.toContent(ret);           
	}
	public void del() {
		String id = this.m_pc.getRequest().getParameter("id");
		
		SqlWhereMerge swm = new SqlWhereMerge();
		swm.equal("f_id", id);
		String where = swm.to_sql();
		
		SqlExec se = new SqlExec();
		se.update("up6_files", new SqlParam[] {new SqlParam("f_deleted",true)}, where);
		se.update("up6_folders", new SqlParam[] {new SqlParam("f_deleted",true)}, where);
		
		JSONObject ret = new JSONObject();
		ret.put("ret", 1);
		this.m_wb.toContent(ret);		
		
	}
	public void del_batch() {
		
		String data = this.m_pc.getRequest().getParameter("data");
		data = PathTool.url_decode(data);
		JSONArray o = JSONArray.fromObject(data);
		
		SqlExec se = new SqlExec();
		se.exec_batch("up6_files", "update up6_files set f_deleted=1 where f_id=?", "", "f_id", o);
		se.exec_batch("up6_folders", "update up6_folders set f_deleted=1 where f_id=?", "", "f_id", o);
		
		JSONObject ret = new JSONObject();
		ret.put("ret", 1);
		this.m_wb.toContent(ret);
		
	}
	public void uncomp() {

        SqlExec se = new SqlExec();
        JSONArray files = se.exec("up6_files"
            , "select f_id,f_nameLoc,f_pathLoc ,f_sizeLoc,f_lenSvr,f_perSvr,f_fdTask from up6_files where f_complete=0 and f_fdChild=0 and f_deleted=0"
            , "f_id,f_nameLoc,f_pathLoc,f_sizeLoc,f_lenSvr,f_perSvr,f_fdTask"
            , "id,nameLoc,pathLoc,sizeLoc,lenSvr,perSvr,fdTask");
        this.m_wb.toContent( files );
	}
	public void uncmp_down() {

		String uid = this.m_pc.getRequest().getParameter("uid");
        SqlExec se = new SqlExec();
        JSONArray files = se.select("down_files"
            , "f_id,f_nameLoc,f_pathLoc,f_perLoc,f_sizeSvr,f_fdTask"
            , new SqlParam[] {new SqlParam("f_uid",Integer.parseInt(uid))}
            , ""
            );
        this.m_wb.toContent( files );
	}
}