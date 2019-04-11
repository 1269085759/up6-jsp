package up6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

/**
 * 数据库配置类
 * 
 * @author Administrator
 *
 */
public class DBConfig {
	String db="sql";//sql,oracle,mysql
	
	String driver = "";
	String url = "";
	String name = "";
	String pass = "";
	
	//sql
	String sql_driver= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String sql_url = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=up6";
	String sql_name = "sa";
	String sql_pass = "123456";
	
	//mysql
	String mysql_driver = "com.mysql.jdbc.Driver";
	String mysql_url = "jdbc:mysql://127.0.0.1:3306/up6?user=root&password=123456&characterEncoding=UTF-8";
	
	//oracle数据库配置
	String oracle_driver = "oracle.jdbc.driver.OracleDriver";
	String oracle_url = "jdbc:oracle:thin:@localhost:1521:orcl";
	String oracle_name = "system";
	String oracle_pass = "123456";
	
	public DBConfig() {
				
		if( StringUtils.equals(this.db, "sql") )
		{
			this.driver = this.sql_driver;
			this.url = this.sql_url;
			this.name = this.sql_name;
			this.pass = this.sql_pass;
		}
		else if( StringUtils.equals(this.db, "mysql") )
		{
			this.driver = this.mysql_driver;
			this.url = this.mysql_url;
		}
		else if( StringUtils.equals(this.db, "oracle") )
		{
			this.driver = this.oracle_driver;
			this.url = this.oracle_url;
			this.name = this.oracle_name;
			this.pass = this.oracle_pass;
		}
	}
	
	public DBFile db() {		
		if( StringUtils.equals(this.db, "sql") ) return new DBFileSQL();
		else if( StringUtils.equals(this.db, "mysql") ) return new DBFileMySQL();
		else if( StringUtils.equals(this.db, "oracle") ) return new DBFileOracle();
		else return new DBFile();
	}
	
	public Connection getCon() 
	{
		Connection con = null;
		
		try 
		{
			Class.forName(this.driver).newInstance();//加载驱动。
			if (StringUtils.equals(this.db, "mysql")) con = DriverManager.getConnection(this.url);
			else con = DriverManager.getConnection(this.url,this.name,this.pass);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;		
	}
}
