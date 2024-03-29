package up6;
import java.sql.*;

/*
 * 原型
*/
public class DbHelper {

	public DbHelper()
	{
	}
	
	public Connection GetCon()
	{		
		DBConfig cfg = new DBConfig();
		return cfg.getCon();
	}
	
	/**
	 * 自动创建命令对象。
	 * @param sql
	 * @return
	 */
	public PreparedStatement GetCommand(String sql)
	{
		PreparedStatement cmd = null;
		try {
			cmd = this.GetCon().prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 与ExecuteGenKey配合使用。
	 * @param sql
	 * @param colIndex 自增ID列名称
	 * @return
	 */
	public PreparedStatement GetCommand(String sql,String colName)
	{
		PreparedStatement cmd = null;
		try {
			cmd = this.GetCon().prepareStatement(sql,new String[]{colName});
			//声明在执行时返回主键
			//this.GetCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 与ExecuteGenKey配合使用。
	 * @param sql
	 * @return
	 */
	public PreparedStatement GetCommandPK(String sql)
	{
		PreparedStatement cmd = null;
		try {
			//cmd = this.GetCon().prepareStatement(sql,new String[]{colName});
			//声明在执行时返回主键
			cmd = this.GetCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 执行存储过程
	 * @param sql
	 * @return
	 */
	public CallableStatement GetCommandStored(String sql)
	{
		CallableStatement cmd = null;
		try {
			cmd = this.GetCon().prepareCall(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(PreparedStatement cmd)
	{	
		this.ExecuteNonQuery(cmd,true);
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(PreparedStatement cmd,boolean autoClose)
	{		
		try 
		{
			cmd.executeUpdate();
			if(autoClose)
			{
				cmd.getConnection().close();//
				cmd.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(String sql)
	{		
		try 
		{
			PreparedStatement cmd = this.GetCommand(sql);
			//cmd.execute();
			cmd.executeUpdate();
			cmd.getConnection().close();
			cmd.close();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public boolean Execute(PreparedStatement cmd)
	{		
		boolean ret = false;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = true;
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(PreparedStatement cmd)
	{		
		int ret = 0;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(PreparedStatement cmd,String sql)
	{		
		int ret = 0;
		try {
			ResultSet rs = cmd.executeQuery(sql);
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(String sql)
	{		
		int ret = 0;
		try {
			PreparedStatement cmd = this.GetCommand(sql);
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public long ExecuteLong(PreparedStatement cmd)
	{		
		long ret = 0;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public long ExecuteLong(String sql)
	{		
		long ret = 0;
		try {
			PreparedStatement cmd = this.GetCommand(sql);
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public String ExecuteString(PreparedStatement cmd)
	{		
		String ret = null;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getString(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 注意：外部必须关闭ResultSet，connection,
	 * ResultSet索引基于1
	 * @param cmd
	 * @return
	 */
	public ResultSet ExecuteDataSet(PreparedStatement cmd)
	{
		ResultSet ret = null;
		try {
			ret = cmd.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,获取自动生成的key
	 * @param cmd
	 * @return
	 */
	public long ExecuteGenKey(PreparedStatement cmd)
	{		
		long ret = 0;
		try {
			cmd.executeUpdate();
			ResultSet rs = cmd.getGeneratedKeys();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public String GetStringSafe(String v,String def)
	{
		return v == null ? def : v;
	}	
}