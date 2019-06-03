package up6;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import up6.model.FileInf;
import up6.sql.SqlExec;
import up6.sql.SqlParam;

import com.google.gson.Gson;

/*
 * 原型
*/
public class DBFile {

	public DBFile()
	{
	}	

	public String GetAllUnComplete(int f_uid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_id");
		sb.append(",f_fdTask");		
		sb.append(",f_nameLoc");
		sb.append(",f_pathLoc");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_pathSvr");//fix(2015-03-16):修复无法续传文件的问题。
		sb.append(" from up6_files ");//change(2015-03-18):联合查询文件夹数据
		sb.append(" where f_uid=? and f_deleted=0 and f_fdChild=0 and f_complete=0 and f_scan=0");//fix(2015-03-18):只加载未完成列表

		ArrayList<FileInf> files = new ArrayList<FileInf>();
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, f_uid);
			ResultSet r = db.ExecuteDataSet(cmd);
			while(r.next())
			{
				FileInf f 		= new FileInf();
				f.uid			= f_uid;
				f.id 			= r.getString(1);
				f.fdTask 		= r.getBoolean(2);				
				f.nameLoc 		= r.getString(3);
				f.pathLoc 		= r.getString(4);
				f.md5 			= r.getString(5);
				f.lenLoc 		= r.getLong(6);
				f.sizeLoc 		= r.getString(7);
				f.offset 		= r.getLong(8);
				f.lenSvr 		= r.getLong(9);
				f.perSvr 		= r.getString(10);
				f.complete 		= r.getBoolean(11);
				f.pathSvr		= r.getString(12);//fix(2015-03-19):修复无法续传文件的问题。
				files.add(f);
				
			}
			r.close();
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(files.size() < 1) return null;
		
		Gson g = new Gson();
	    return g.toJson( files);//bug:arrFiles为空时，此行代码有异常	
	}
	
	public boolean read(String f_id,FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select");
		sb.append(" f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files where f_id=? limit 0,1");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, f_id);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 			= f_id;
				fileSvr.uid 		= r.getInt(1);
				fileSvr.nameLoc 	= r.getString(2);
				fileSvr.nameSvr 	= r.getString(3);
				fileSvr.pathLoc 	= r.getString(4);
				fileSvr.pathSvr 	= r.getString(5);
				fileSvr.pathRel 	= r.getString(6);
				fileSvr.md5 		= r.getString(7);
				fileSvr.lenLoc 		= r.getLong(8);
				fileSvr.sizeLoc 	= r.getString(9);
				fileSvr.offset 		= r.getLong(10);
				fileSvr.lenSvr 		= r.getLong(11);
				fileSvr.perSvr 		= r.getString(12);
				fileSvr.complete 	= r.getBoolean(13);
				fileSvr.PostedTime 	= r.getDate(14);
				fileSvr.deleted 	= r.getBoolean(15);
				ret = true;
			}
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	
	/// <summary>
	/// 根据文件MD5获取文件信息
	/// 取已上传完的文件
	/// </summary>
	/// <param name="md5"></param>
	/// <param name="inf"></param>
	/// <returns></returns>
	public boolean exist_file(String md5,/*out*/FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select");
		sb.append(" f_id");
		sb.append(",f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files where f_md5=? and f_complete=1 order by f_lenSvr DESC limit 0,1");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, md5);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 			= r.getString(1);
				fileSvr.uid 		= r.getInt(2);
				fileSvr.nameLoc 	= r.getString(3);
				fileSvr.nameSvr 	= r.getString(4);
				fileSvr.pathLoc 	= r.getString(5);
				fileSvr.pathSvr 	= r.getString(6);
				fileSvr.pathRel 	= r.getString(7);
				fileSvr.md5 		= r.getString(8);
				fileSvr.lenLoc 		= r.getLong(9);
				fileSvr.sizeLoc 	= r.getString(10);
				fileSvr.offset 		= r.getLong(11);
				fileSvr.lenSvr 		= r.getLong(12);
				fileSvr.perSvr 		= r.getString(13);
				fileSvr.complete 	= r.getBoolean(14);
				fileSvr.PostedTime 	= r.getDate(15);
				fileSvr.deleted 	= r.getBoolean(16);
				ret = true;
			}
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 增加一条数据，在f_create中调用。
	 * 文件名称，本地路径，远程路径，相对路径都使用原始字符串。
	 * d:\soft\QQ2012.exe
	 * @param model
	 * @return
	 */
	public void Add(FileInf f)
	{
		SqlExec se = new SqlExec();
		se.insert("up6_files", new SqlParam[] {
				new SqlParam("f_id",f.id)
				,new SqlParam("f_pid",f.pid)
				,new SqlParam("f_pidRoot",f.pidRoot)
				,new SqlParam("f_fdTask",f.fdTask)
				,new SqlParam("f_fdChild",f.fdChild)
				,new SqlParam("f_uid",f.uid)
				,new SqlParam("f_pos",f.offset)
				,new SqlParam("f_md5",f.md5)
				,new SqlParam("f_lenLoc",f.lenLoc)
				,new SqlParam("f_lenSvr",f.lenSvr)
				,new SqlParam("f_perSvr",f.perSvr)
				,new SqlParam("f_sizeLoc",f.sizeLoc)
				,new SqlParam("f_nameLoc",f.nameLoc)
				,new SqlParam("f_nameSvr",f.nameSvr)
				,new SqlParam("f_pathLoc",f.pathLoc)
				,new SqlParam("f_pathSvr",f.pathSvr)
				,new SqlParam("f_pathRel",f.pathRel)
				,new SqlParam("f_complete",f.complete)
		});
	}
	
	/**
	 * 添加子目录
	 * @param f
	 */
	public void addFolderChild(FileInf f) 
	{
		SqlExec se = new SqlExec();
		se.insert("up6_folders", new SqlParam[] {
				 new SqlParam("f_id",f.id)
				,new SqlParam("f_pid",f.pid)
				,new SqlParam("f_pidRoot",f.pidRoot)
				,new SqlParam("f_uid",f.uid)
				,new SqlParam("f_lenLoc",f.lenLoc)
				,new SqlParam("f_sizeLoc",f.sizeLoc)
				,new SqlParam("f_nameLoc",f.nameLoc)
				,new SqlParam("f_pathLoc",f.pathLoc)
				,new SqlParam("f_pathSvr",f.pathSvr)
				,new SqlParam("f_pathRel",f.pathRel)
		});
	}
	
	/**
	 * 清空文件表，文件夹表数据。
	 */
	public void Clear()
	{
		DbHelper db = new DbHelper();
		db.ExecuteNonQuery("delete from up6_files;");
		db.ExecuteNonQuery("delete from up6_folders;");
	}

	
	/**
	 * @param f_uid
	 * @param f_id
	 */
	public void fd_complete(String f_id, String uid)
	{
		DbHelper db = new DbHelper();
		Connection con = db.GetCon();
		
		try {
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.addBatch("update up6_files set f_perSvr='100%' ,f_lenSvr=f_lenLoc,f_complete=1 where f_id='" + f_id+"'");
			stmt.addBatch("update up6_files set f_perSvr='100%' ,f_lenSvr=f_lenLoc,f_complete=1 where f_pidRoot='" + f_id+"'");
			stmt.addBatch("update up6_folders set f_complete=1 where f_id='" + f_id + "' and f_uid=" + uid);
			stmt.executeBatch();
			con.commit();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void fd_scan(String id, String uid)
	{
		String sql = "update up6_files set f_scan=1 where f_id=? and f_uid=?";
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setString(1, id);
			cmd.setInt(2, Integer.parseInt(uid));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}

	/// <summary>
	/// 更新上传进度
	/// </summary>
	///<param name="f_uid">用户ID</param>
	///<param name="f_id">文件ID</param>
	///<param name="f_pos">文件位置，大小可能超过2G，所以需要使用long保存</param>
	///<param name="f_lenSvr">已上传长度，文件大小可能超过2G，所以需要使用long保存</param>
	///<param name="f_perSvr">已上传百分比</param>
	public boolean f_process(int uid,String f_id,long offset,long f_lenSvr,String f_perSvr)
	{
		SqlExec se = new SqlExec();
		se.update("up6_files"
				, new SqlParam[] {
						new SqlParam("f_pos",offset)
						,new SqlParam("f_lenSvr",f_lenSvr)
						,new SqlParam("f_perSvr",f_perSvr)
						}
				, new SqlParam[] {
						new SqlParam("f_uid",uid)
						,new SqlParam("f_id",f_id)
				});

		return true;
	}

	/// <summary>
	/// 上传完成。将所有相同MD5文件进度都设为100%
	/// </summary>
	public void UploadComplete(String md5)
	{
		String sql = "update up6_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1 where f_md5=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		
		try 
		{
			cmd.setString(1, md5);
			db.ExecuteNonQuery(cmd);//在部分环境中测试发现执行后没有效果。
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void complete(String id)
	{
		String sql = "update up6_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1,f_scan=1 where f_id=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		
		try {
			cmd.setString(1, id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}

	/// <summary>
	/// 删除一条数据，并不真正删除，只更新删除标识。
	/// </summary>
	/// <param name="f_uid"></param>
	/// <param name="f_id"></param>
	public void Delete(int f_uid,String f_id)
	{
		String sql = "update up6_files set f_deleted=1 where f_uid=? and f_id=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setInt(1, f_uid);
			cmd.setString(2, f_id);
			db.ExecuteNonQuery(cmd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 继承类重写
	 * @param id
	 * @param uid
	 */
	public void delFolder(String id,int uid) {}
}