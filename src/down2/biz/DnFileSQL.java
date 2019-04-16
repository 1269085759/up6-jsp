package down2.biz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import down2.model.DnFileInf;
import up6.DbHelper;

public class DnFileSQL extends DnFile
{	

    public void update(String fid,int uid,String lenLoc,String perLoc,String nameLoc,String pathLoc,String lenSvr)
    {
        String sql = "update down_files set f_lenLoc=?,f_lenSvr=?,f_perLoc=?,f_nameLoc=?,f_pathLoc=? where f_id=? and f_uid=?";
        DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try
		{
			cmd.setLong(1,Long.parseLong(lenLoc));
			cmd.setLong(2,Long.parseLong(lenSvr));
			cmd.setString(3,perLoc);
			cmd.setString(4,nameLoc);
			cmd.setString(5,pathLoc);
			cmd.setString(6,fid);
			cmd.setInt(7,uid);
			db.ExecuteNonQuery(cmd);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
    }

    /// <summary>
    /// 获取所有未完成的文件列表
    /// </summary>
    /// <returns></returns>
    public String GetAll(int uid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(" f_id");
        sb.append(",f_nameLoc");
        sb.append(",f_pathLoc");
        sb.append(",f_fileUrl");
        sb.append(",f_perLoc");
        sb.append(",f_lenLoc");
        sb.append(",f_lenSvr");
        sb.append(",f_sizeSvr");
        sb.append(",f_fdTask");
        sb.append(" from down_files");
        sb.append(" where f_uid=?");

        ArrayList<DnFileInf> files = new ArrayList<DnFileInf>();
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try
		{
			cmd.setInt(1,uid);
			ResultSet r = db.ExecuteDataSet(cmd);
			while (r.next())
			{
				DnFileInf f		= new DnFileInf();
				f.id			= r.getString(1);
				f.nameLoc		= r.getString(2);
			    f.pathLoc		= r.getString(3);
				f.fileUrl		= r.getString(4);
			    f.perLoc		= r.getString(5);
			    f.lenLoc		= r.getLong(6);
			    f.lenSvr		= r.getLong(7);
			    f.sizeSvr		= r.getString(8);
			    f.fdTask		= r.getBoolean(9);
				files.add(f);
			}
			r.close();
			cmd.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

        Gson g = new Gson();
	    return g.toJson( files );
	}

	public void Clear()
	{
        String sql = "delete from down_files ";
        DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		db.ExecuteNonQuery(cmd);    
	}
}