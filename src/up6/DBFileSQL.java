package up6;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import up6.model.FileInf;

public class DBFileSQL extends DBFile
{	
	
	public boolean read(String f_id, FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select top 1 ");
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
		sb.append(" from up6_files where f_id=?");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, f_id);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 				= f_id;
				fileSvr.uid 			= r.getInt(1);
				fileSvr.nameLoc 		= r.getString(2);
				fileSvr.nameSvr 		= r.getString(3);
				fileSvr.pathLoc 		= r.getString(4);
				fileSvr.pathSvr 		= r.getString(5);
				fileSvr.pathRel 		= r.getString(6);
				fileSvr.md5 			= r.getString(7);
				fileSvr.lenLoc 			= r.getLong(8);
				fileSvr.sizeLoc 		= r.getString(9);
				fileSvr.offset 			= r.getLong(10);
				fileSvr.lenSvr 			= r.getLong(11);
				fileSvr.perSvr 			= r.getString(12);
				fileSvr.complete 		= r.getBoolean(13);
				fileSvr.PostedTime 		= r.getDate(14);
				fileSvr.deleted 		= r.getBoolean(15);
				ret = true;
			}
			r.close();
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
	public boolean exist_file(String md5, FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select top 1 ");
		sb.append("f_id");
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
		sb.append(" from up6_files where f_md5=? and f_complete=1 order by f_perSvr DESC");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, md5);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 				= r.getString(1);
				fileSvr.uid 			= r.getInt(2);
				fileSvr.nameLoc 		= r.getString(3);
				fileSvr.nameSvr 		= r.getString(4);
				fileSvr.pathLoc 		= r.getString(5);
				fileSvr.pathSvr 		= r.getString(6);
				fileSvr.pathRel 		= r.getString(7);
				fileSvr.md5 			= r.getString(8);
				fileSvr.lenLoc 			= r.getLong(9);
				fileSvr.sizeLoc 		= r.getString(10);
				fileSvr.offset 			= r.getLong(11);
				fileSvr.lenSvr 			= r.getLong(12);
				fileSvr.perSvr 			= r.getString(13);
				fileSvr.complete 		= r.getBoolean(14);
				fileSvr.PostedTime 		= r.getDate(15);
				fileSvr.deleted 		= r.getBoolean(16);
				ret = true;
			}
			r.close();
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	
	/**
	 * @param f_uid
	 * @param f_id
	 */
	public void fd_complete(String fid, String uid)
	{
		String sql = "update up6_files set f_perSvr='100%',f_lenSvr=f_lenLoc,f_complete=1 where f_id=? and f_uid=?;";
		sql += "update up6_folders set f_complete=1 where f_id=? and f_uid=?;";
		sql += "update up6_files set f_perSvr='100%',f_lenSvr=f_lenLoc,f_complete=1 where f_pidRoot=?;";
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setString(1, fid);
			cmd.setInt(2, Integer.parseInt(uid));
			cmd.setString(3, fid);
			cmd.setInt(4, Integer.parseInt(uid));
			cmd.setString(5, fid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}


	public void delFolder(String id,int uid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up6_files set f_deleted=1 where f_id=? and f_uid=?;");
		sb.append("update up6_files set f_deleted=1 where f_pidRoot=? and f_uid=?;");
		sb.append("update up6_folders set f_deleted=1 where f_id=? and f_uid=?;");
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try 
		{
			cmd.setString(1, id);
			cmd.setInt(2, uid);
			cmd.setString(3, id);
			cmd.setInt(4, uid);
			cmd.setString(5, id);
			cmd.setInt(6, uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}
}