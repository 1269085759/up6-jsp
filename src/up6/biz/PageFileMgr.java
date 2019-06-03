package up6.biz;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.PathTool;
import up6.WebBase;
import up6.sql.SqlExec;
import up6.sql.SqlParam;
import up6.sql.SqlWhereMerge;

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
		else if(StringUtils.equals("rename", op)) this.load_data();
		else if(StringUtils.equals("del", op)) this.del();
		else if(StringUtils.equals("del-batch", op)) this.del_batch();
		//else if(StringUtils.equals("path", op)) this.path();
		else if(StringUtils.equals("mk-folder", op)) this.del_batch();
		else if(StringUtils.equals("uncomp", op)) this.uncomp();
		else if(StringUtils.equals("uncmp-down", op)) this.uncmp_down();
	}
	
	public void load_data() {
		String pageSize = this.m_pc.getRequest().getParameter("pageSize");
		String pageIndex = this.m_pc.getRequest().getParameter("pageIndex");
		if(StringUtils.isBlank(pageSize)) pageSize = "20";
		if(StringUtils.isBlank(pageIndex)) pageIndex = "1";
		
		SqlWhereMerge swm = new SqlWhereMerge();
		swm.equal("f_complete", 1);
		swm.equal("f_deleted", 0);
		swm.equal("f_fdChild", 0);
		String where = swm.to_sql();
		
		SqlExec se = new SqlExec();
		JSONArray arr = se.page("up6_files"
				,"f_id"
				, "f_id,f_pid,f_nameLoc,f_sizeLoc,f_lenLoc,f_time,f_pidRoot,f_fdTask,f_pathSvr,f_pathRel"
				, Integer.parseInt( pageSize )
				, Integer.parseInt( pageIndex )
				, where
				, "f_fdTask desc,f_time desc");
		
		
		JSONObject o = new JSONObject();
		o.put("count", se.count("up6_files", where));
		o.put("code", 0);
		o.put("msg", "");
		o.put("data", arr);
		
		this.m_wb.toContent(o);
	}
	public void search() {}
	public void rename() {}
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