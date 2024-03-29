package up6.biz;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import up6.DbHelper;
import up6.model.FileInf;


public class fd_scan 
{
	DbHelper db;
	Connection con;
	PreparedStatement cmd_add_f = null;
	PreparedStatement cmd_add_fd = null;
	public FileInf root = null;//根节点
	
	public fd_scan()
	{
		this.db = new DbHelper();
		this.con = this.db.GetCon();		
	}
	
	public void makeCmdF()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up6_files (");
		sb.append(" f_id");//1
		sb.append(",f_pid");//2
		sb.append(",f_pidRoot");//3
		sb.append(",f_fdTask");//4
		sb.append(",f_fdChild");//5
		sb.append(",f_uid");//6
		sb.append(",f_nameLoc");//7
		sb.append(",f_nameSvr");//8
		sb.append(",f_pathLoc");//9
		sb.append(",f_pathSvr");//10
		sb.append(",f_pathRel");//11
		sb.append(",f_md5");//12
		sb.append(",f_lenLoc");//13
		sb.append(",f_sizeLoc");//14
		sb.append(",f_lenSvr");//15
		sb.append(",f_perSvr");//16
		sb.append(",f_complete");//17
		
		sb.append(") values(");
		
		sb.append(" ?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(")");

        try {
        	this.cmd_add_f = this.con.prepareStatement(sb.toString());
			this.cmd_add_f.setString(1, "");//id
	        this.cmd_add_f.setString(2, "");//pid
	        this.cmd_add_f.setString(3, "");//pidRoot
	        this.cmd_add_f.setBoolean(4, true);//fdTask
	        this.cmd_add_f.setBoolean(5, false);//f_fdChild
	        this.cmd_add_f.setInt(6, 0);//f_uid
	        this.cmd_add_f.setString(7, "");//f_nameLoc
	        this.cmd_add_f.setString(8, "");//f_nameSvr
	        this.cmd_add_f.setString(9, "");//f_pathLoc
	        this.cmd_add_f.setString(10, "");//f_pathSvr
	        this.cmd_add_f.setString(11, "");//f_pathRel
	        this.cmd_add_f.setString(12, "");//f_md5
	        this.cmd_add_f.setLong(13, 0);//f_lenLoc
	        this.cmd_add_f.setString(14, "");//f_sizeLoc
	        this.cmd_add_f.setLong(15, 0);//f_lenSvr	        
	        this.cmd_add_f.setString(16, "");//f_perSvr
	        this.cmd_add_f.setBoolean(17, true);//f_complete
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void makeCmdFD()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up6_folders (");
		sb.append(" f_id");//1
		sb.append(",f_pid");//2
		sb.append(",f_pidRoot");//3
		sb.append(",f_nameLoc");//4
		sb.append(",f_uid");//5
		sb.append(",f_pathLoc");//6
		sb.append(",f_pathSvr");//7
		sb.append(",f_pathRel");//8
		sb.append(",f_complete");//9
		sb.append(") values(");//
		sb.append(" ?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(",?");
		sb.append(")");

        try {
        	this.cmd_add_fd = this.con.prepareStatement(sb.toString());
			this.cmd_add_fd.setString(1, "");//id
	        this.cmd_add_fd.setString(2, "");//pid
	        this.cmd_add_fd.setString(3, "");//pidRoot
	        this.cmd_add_fd.setString(4, "");//name
	        this.cmd_add_fd.setInt(5, 0);//f_uid
	        this.cmd_add_fd.setString(6, "");//pathLoc
	        this.cmd_add_fd.setString(7, "");//pathSvr
	        this.cmd_add_fd.setString(8, "");//pathRel
	        this.cmd_add_fd.setBoolean(9, true);//complete
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void GetAllFiles(FileInf inf,String root)
	{
		File dir = new File(inf.pathSvr); 
		File [] allFile = dir.listFiles();
		for(int i = 0; i < allFile.length; i++)
		{
			if(allFile[i].isDirectory())
			{
				FileInf fd = new FileInf();
				String uuid = UUID.randomUUID().toString();
				uuid = uuid.replace("-", "");
				fd.id = uuid;
				fd.pid = inf.id;
				fd.pidRoot = this.root.id;
				fd.nameSvr = allFile[i].getName();
				fd.nameLoc = fd.nameSvr;
				fd.pathSvr = allFile[i].getPath();
				fd.pathSvr = fd.pathSvr.replace("\\", "/");
				fd.pathRel = fd.pathSvr.substring(root.length() + 1);
				fd.perSvr = "100%";
				fd.complete = true;
				this.save_folder(fd);
				
				this.GetAllFiles(fd, root);
			}
			else
			{
				FileInf fl = new FileInf();
				String uuid = UUID.randomUUID().toString();
				uuid = uuid.replace("-", "");
				fl.id = uuid;
				fl.pid = inf.id;
				fl.pidRoot = this.root.id;
				fl.nameSvr = allFile[i].getName();
				fl.nameLoc = fl.nameSvr;
				fl.pathSvr = allFile[i].getPath();
				fl.pathSvr = fl.pathSvr.replace("\\", "/");
				fl.pathRel = fl.pathSvr.substring(root.length() + 1);
				fl.lenSvr = allFile[i].length();
				fl.lenLoc = fl.lenSvr;
				fl.perSvr = "100%";
				fl.complete = true;
				this.save_file(fl);
			}
		}
	}
	
	protected void save_file(FileInf f)
	{		
        try {
			this.cmd_add_f.setString(1, f.id);//id
	        this.cmd_add_f.setString(2, f.pid);//pid
	        this.cmd_add_f.setString(3, f.pidRoot);//pidRoot
	        this.cmd_add_f.setBoolean(4, f.fdTask);//fdTask
	        this.cmd_add_f.setBoolean(5, true);//f_fdChild
	        this.cmd_add_f.setInt(6, f.uid);//f_uid
	        this.cmd_add_f.setString(7, f.nameLoc);//f_nameLoc
	        this.cmd_add_f.setString(8, f.nameSvr);//f_nameSvr
	        this.cmd_add_f.setString(9, f.pathLoc);//f_pathLoc
	        this.cmd_add_f.setString(10, f.pathSvr);//f_pathSvr
	        this.cmd_add_f.setString(11, f.pathRel);//f_pathRel
	        this.cmd_add_f.setString(12, f.md5);//f_md5
	        this.cmd_add_f.setLong(13, f.lenLoc);//f_lenLoc
	        this.cmd_add_f.setString(14, f.sizeLoc);//f_sizeLoc
	        this.cmd_add_f.setLong(15, f.lenSvr);//f_lenSvr	        
	        this.cmd_add_f.setString(16, f.perSvr);//f_perSvr
	        this.cmd_add_f.setBoolean(17, f.complete);//f_complete
	        this.cmd_add_f.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
	}
	
	protected void save_folder(FileInf f)
	{
		try {
			this.cmd_add_fd.setString(1, f.id);//id
	        this.cmd_add_fd.setString(2, f.pid);//pid
	        this.cmd_add_fd.setString(3, f.pidRoot);//pidRoot
	        this.cmd_add_fd.setString(4, f.nameSvr);//name
	        this.cmd_add_fd.setInt(5, f.uid);//f_uid
	        this.cmd_add_fd.setString(6, f.pathLoc);//pathLoc
	        this.cmd_add_fd.setString(7, f.pathSvr);//pathSvr
	        this.cmd_add_fd.setString(8, f.pathRel);//pathRel
	        this.cmd_add_fd.setBoolean(9, f.complete);//complete
	        this.cmd_add_fd.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void scan(FileInf inf, String root) throws IOException, SQLException
	{
		this.makeCmdF();
		this.makeCmdFD();
		this.GetAllFiles(inf, root);
		this.cmd_add_f.close();
        this.cmd_add_fd.close();
        this.con.close();
	}
}