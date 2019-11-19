package up6.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import up6.ConfigReader;
import up6.DBConfig;
import up6.DbHelper;

public class SqlExec {
	ReadContext m_table;
	SqlValSetter m_svs;
	SqlCmdReader m_scr;
    
    public SqlExec() {
    	this.m_svs = new SqlValSetter();
    	this.m_scr = new SqlCmdReader();
    }
    
    public ReadContext table(String tableName)
    {
    	ConfigReader cr = new ConfigReader();
    	JSONObject o = cr.module(String.format("database.%s", tableName));
    	return JsonPath.parse(o);
    }
    
    public void exec(String table,String sql,String fields,String where,JSONObject o)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
   	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd= db.GetCommand(sql);
    	this.m_svs.set(cmd, field_sel, o);
    	db.ExecuteNonQuery(cmd);
    }
    
    public void exec(String sql)
    {
    	DbHelper db = new DbHelper();
		db.ExecuteNonQuery(sql);
    }
    
    public JSONArray exec(String table,String sql,String fields,String newNames)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	String[] names = fields.split(",");
    	if( !StringUtils.isBlank(newNames) ) names = newNames.split(",");
   	
    	JSONArray arr = new JSONArray();
    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd= db.GetCommand(sql);    	
    	ResultSet r = db.ExecuteDataSet(cmd);
    	try {
			while(r.next())
			{
				JSONObject item = new JSONObject();
				for(int i = 0 ; i < field_sel.size();++i)
				{
					JSONObject field = (JSONObject) field_sel.get(i);					
					item.put(names[i], this.m_scr.read(r, field,i+1));
				}
				arr.add(item);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
    
    public void exec_batch(String table,String sql,String fields,String where,JSONArray values) 
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	JSONArray field_where = this.selFields(where);
    	

    	DbHelper db = new DbHelper();

		Connection con = db.GetCon();		
		try {
			PreparedStatement cmd = con.prepareStatement(sql);
			for(int i = 0 ; i < values.size(); ++i)
			{
				JSONObject val = (JSONObject)values.get(i);
				int parIndex = this.m_svs.set(cmd, field_sel, val,0);
				this.m_svs.set(cmd, field_where, val,parIndex );
				cmd.execute();
			}
			con.close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //从字段表中选择字段
    public JSONArray selFields(String fields)
    {
    	if( StringUtils.isBlank(fields)) return null;
    	JSONArray arr = new JSONArray();
    	
    	//所有字段
    	if(StringUtils.equals(fields, "*")) 
    	{
    		JSONArray o = this.m_table.read("$.fields");
    		
    		return JSONArray.fromObject(o);
    	}
    		
    	String[] fs = fields.split(",");
    	for(String f : fs)
    	{
    		String q = String.format("$.fields[?(@.name=='%s')]",f);    		
    		net.minidev.json.JSONArray o = this.m_table.read(q);    		 
    		
    		arr.add( o.get(0) );
    	}
    	return arr;
    }
    
    public JSONObject selPK()
    {
    	JSONArray fs = this.m_table.read("fields[?(@.identity==true && @.primary==true)]");
    	return JSONObject.fromObject( fs.get(0) );
    }
    
    /**
     * 从变量中选择字段信息
     * @param ps
     * @return
     */
    public JSONArray selFields(SqlParam[] ps)
    {
    	if(null == ps ) return null;
    	if(ps.length < 1) return null;
    	JSONArray arr = new JSONArray();
    	
    	for(SqlParam p : ps)
    	{
    		String q = String.format("$.fields[?(@.name=='%s')]",p.m_name);
    		net.minidev.json.JSONArray o = this.m_table.read(q);    		 
    		
    		arr.add( o.get(0) );
    	}
    	return arr;
    }
    
    public String toFields(SqlParam[] sp) 
    {
    	if(null == sp) return "";
    	ArrayList<String> arr = new ArrayList<String>();
    	for(SqlParam p : sp)
    	{
    		arr.add(p.m_name);
    	}
    	String[] arrs = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(arrs,",");
    }
    
    /**
     * 转换成SQL条件语句
     * @param sp
     * @return name=? and age=?
     */
    public String toSqlConditions(SqlParam[] sp) {
    	if(sp.length == 0) return "1=1";
    	
    	ArrayList<String> arr = new ArrayList<String>();
    	for(SqlParam p : sp)
    	{
    		String v = String.format("%s=?", p.m_name);
    		arr.add(v);    		
    	}
    	String[] vs = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(vs, " and ");
    }
    
    /**
     * 转换成值设置语句 name=?,age=?,type=?
     * @param fields
     * @return
     */
    public String toSqlSet(String fields) {
    	String[] field_arr = StringUtils.split(fields, ",");
    	ArrayList<String> arr = new ArrayList<String>();
    	for(String f : field_arr)
    	{
    		arr.add( String.format("%s=?", f) );
    	}    	
    	String[] s = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(s, ",");
    }    

    public String toSqlSet(SqlParam[] fields) {
    	ArrayList<String> arr = new ArrayList<String>();
    	for(SqlParam p : fields)
    	{
    		String v = String.format("%s=?", p.m_name);
    		arr.add(v);    		
    	}
    	String[] vs = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(vs, ",");
    }
    
    /**
     * 转换成条件变量语句 id=? and age=?
     * @param names
     * @return
     */
    public String toSqlWhere(String names)
    {
    	String[] field_arr = StringUtils.split(names, ",");
    	
    	
    	ArrayList<String> arr = new ArrayList<String>();
    	for(String f : field_arr)
    	{
    		arr.add( String.format("%s=?", f) );
    	}    	
    	String[] s = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(s, " and ");    	
    }
    public String toSqlWhere(SqlParam[] ps)
    {	
    	ArrayList<String> arr = new ArrayList<String>();
    	for(SqlParam f : ps)
    	{
    		arr.add( String.format("%s=?", f.m_name) );
    	}    	
    	String[] s = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(s, " and ");    	
    }
    
    /**
     * 转换成条件变量语句 ?,?,?
     * @param names
     * @return
     */
    public String toParam(String names)
    {
    	String[] field_arr = StringUtils.split(names, ",");
    	
    	
    	ArrayList<String> arr = new ArrayList<String>();
    	for(String f : field_arr)
    	{
    		arr.add( "?" );
    	}    	
    	String[] s = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(s, ",");    	
    }
    public String toParam(SqlParam[] ps)
    {	
    	ArrayList<String> arr = new ArrayList<String>();
    	for(int i = 0 ; i < ps.length ; ++i)
    	{
    		arr.add( "?" );
    	}    	
    	String[] s = arr.toArray(new String[arr.size()]);
    	return StringUtils.join(s, ",");    	
    }
    
    public JSONObject read(String table,String fields,SqlParam[] ps) 
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	JSONArray field_cdt = this.selFields(ps);
    	String sql = String.format("select %s from %s where %s", fields,table,this.toSqlConditions(ps));

    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	this.m_svs.set(cmd, field_cdt, ps);
    	ResultSet r = db.ExecuteDataSet(cmd);
    	JSONObject o = null;    	
    	try {
			if(r.next())
			{
				o = new JSONObject();
		    	SqlCmdReader scr = new SqlCmdReader();
		    	o = scr.read(r, field_sel);
			}
	    	r.close();
	    	cmd.getConnection().close();
	    	cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return o;
    }
    
    public void insert(String table,String fields,JSONObject o)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);

        String sql = String.format("insert into %s ( %s ) values( %s )"
            , table
            , fields
            , this.toParam(fields));

        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sql);
        this.m_svs.set(cmd, field_sel, o);
        db.ExecuteNonQuery( cmd );        
    }
    
    public void insert(String table, SqlParam[] pars) {
    	this.m_table = this.table(table);        
        JSONArray field_sel = this.selFields(pars);
        

        String sql = String.format("insert into %s ( %s ) values( %s )"
            , table
            , this.toFields(pars)
            , this.toParam(pars));

        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sql);
        this.m_svs.set(cmd, field_sel, pars);            
        db.ExecuteNonQuery(cmd);        
    }
    
    public void update(String table,String fields,String where,JSONObject obj)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	JSONArray field_were = this.selFields(where);
    	String sql = String.format("update %s set %s where %s", table,this.toSqlSet(fields),this.toSqlWhere(where));
    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	int prevIndex = this.m_svs.set(cmd, field_sel,obj,0);
    	this.m_svs.set(cmd, field_were,obj,prevIndex);
    	db.ExecuteNonQuery(cmd);
    }
    
    public void update(String table,SqlParam[] fields,String where)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	
    	String sql = String.format("update %s set %s where %s", table,this.toSqlSet(fields),where);
    	    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	this.m_svs.set(cmd, field_sel, fields);
    	db.ExecuteNonQuery(cmd);
    }
    
    public void update(String table,SqlParam[] fields,SqlParam[] where)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	JSONArray field_where = this.selFields(where);
    	
    	String sql = String.format("update %s set %s where %s", table,this.toSqlSet(fields),this.toSqlWhere(where));
    	    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	int prevIndex = this.m_svs.set(cmd, field_sel, fields,0);
    	this.m_svs.set(cmd, field_where, where,prevIndex);
    	db.ExecuteNonQuery(cmd);
    }
    
    public void delete(String table,SqlParam[] where)
    {
    	this.m_table = this.table(table);
    	JSONArray field_where = this.selFields(where);
    	
    	String sql = String.format("delete from %s where %s", table,this.toSqlWhere(where));
    	    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	this.m_svs.set(cmd, field_where, where);
    	db.ExecuteNonQuery(cmd);
    }
    
    public int count(String table,SqlParam[] where)
    {
    	this.m_table = this.table(table);
    	String ws = this.toSqlConditions(where);
    	JSONArray field_where = this.selFields(where);
    	String sql = String.format("select count(*) from %s where %s", table,ws);
    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	this.m_svs.set(cmd,field_where,where);
    	Object v = db.ExecuteScalar(cmd);
    	return Integer.parseInt(v.toString());
    }
    
    public int count(String table,String where)
    {
    	this.m_table = this.table(table);    	
    	
    	String sql = String.format("select count(*) from %s where %s", table,where);
    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);    	
    	Object v = db.ExecuteScalar(cmd);
    	return Integer.parseInt(v.toString());
    }
    
    public JSONArray select(String table,String fields,SqlParam[] where,String sort)
    {
    	SqlCmdReader scr = new SqlCmdReader();
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	JSONArray field_cdt = this.selFields(where);
    	
    	String sql_where = "";
    	if(where.length>0) sql_where = String.format("where %s", this.toSqlConditions(where));    	
    	
    	String sql_sort="";
    	if(!StringUtils.isBlank(sort)) sql_sort = String.format("order by %s", sort);
    	
    	String sql = String.format("select %s from %s %s %s", fields,table,sql_where,sql_sort);    	
    	    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);
    	this.m_svs.set(cmd, field_cdt, where);//设置条件参数
    	ResultSet r = db.ExecuteDataSet(cmd);
    	JSONArray arr = new JSONArray();
    	
    	try {
			while(r.next())
			{
				JSONObject o = scr.read(r, field_sel);
				arr.add(o);
			}
			r.close();
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
    
    public JSONArray select(String table,String fields,String where,String sort)
    {
    	SqlCmdReader scr = new SqlCmdReader();
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	
    	if( !StringUtils.isBlank(where) ) where = String.format("where %s", where);
    	if( !StringUtils.isBlank(sort)) sort = String.format(" order by %s", sort);
    	
    	String sql = String.format("select %s from %s %s %s", fields,table,where,sort);  	
    	
    	DbHelper db = new DbHelper();
    	PreparedStatement cmd = db.GetCommand(sql);    	
    	ResultSet r = db.ExecuteDataSet(cmd);
    	JSONArray arr = new JSONArray();
    	
    	try {
			while(r.next())
			{
				JSONObject o = scr.read(r, field_sel);
				arr.add(o);
			}
			r.close();
			cmd.getConnection().close();
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
    
    public JSONArray page(String table,String primaryKey,String fields,int pageSize,int pageIndex,String where,String sort)
    {
    	DBConfig cfg = new DBConfig();
    	if(StringUtils.equals(cfg.m_db, "oracle")) return this.page_oracle(table, primaryKey, fields, pageSize, pageIndex, where, sort);
    	if(StringUtils.equals(cfg.m_db, "mysql")) return this.page_mysql(table, primaryKey, fields, pageSize, pageIndex, where, sort);
    	
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	
    	JSONArray arr = new JSONArray();
    	try {

        	DbHelper db = new DbHelper();
        	CallableStatement cmd = db.GetCommandStored("{call spPager(?,?,?,?,?,?,?,?)}");
			cmd.setString(1, table);
            cmd.setString(2, primaryKey);
            cmd.setInt(3, pageSize);
            cmd.setInt(4, pageIndex);
            cmd.setBoolean(5, false);
            cmd.setString(6, where);
            cmd.setString(7, sort);
            cmd.setString(8, fields);
            ResultSet r = db.ExecuteDataSet(cmd);
            while(r.next())
            {
            	JSONObject item = new JSONObject();
            	for(int i = 0 ; i < field_sel.size();++i)
            	{
            		JSONObject column = (JSONObject) field_sel.get(i);
            		Object v = this.m_scr.read(r, column.getString("type"), i+2);//第一列是行号
            		item.put(column.getString("name"),v);
            	}
        		arr.add(item);            	
            }
            r.close();
            cmd.getConnection().close();
            cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
    
    public JSONArray page_mysql(String table,String primaryKey,String fields,int pageSize,int pageIndex,String where,String sort)
    {
    	int rowStart = pageSize *(pageIndex-1);
    	if( !StringUtils.isBlank(where)) where = "where " + where;
    	if( !StringUtils.isBlank(sort)) sort = "order by " + sort;
    	String sql = String.format("select %s from %s %s %s limit %d,%d"
    			,fields,table,where,sort,rowStart,pageSize);    	
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	
    	JSONArray arr = new JSONArray();
    	try {

        	DbHelper db = new DbHelper();
        	PreparedStatement cmd = db.GetCommand(sql);
            ResultSet r = db.ExecuteDataSet(cmd);
            while(r.next())
            {
            	JSONObject item = new JSONObject();
            	for(int i = 0 ; i < field_sel.size();++i)
            	{
            		JSONObject column = (JSONObject) field_sel.get(i);
            		Object v = this.m_scr.read(r, column.getString("type"), i+1);
            		item.put(column.getString("name"),v);
            	}
        		arr.add(item);            	
            }
            r.close();
            cmd.getConnection().close();
            cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
    
    public JSONArray page_oracle(String table,String primaryKey,String fields,int pageSize,int pageIndex,String where,String sort)
    {
    	this.m_table = this.table(table);
    	JSONArray field_sel = this.selFields(fields);
    	
    	JSONArray arr = new JSONArray();
    	try {
    		int pageStart = (pageIndex - 1)*(pageSize - 1);
    		int pageEnd = (pageIndex - 1) * pageSize + pageSize;
    		String sql = String.format("select * from (select rownum rn ,a.* from(select %s from %s where %s) a where rownum <= %d ) where rn >= %d",fields,table,where,pageEnd,pageStart);

        	DbHelper db = new DbHelper();
        	PreparedStatement cmd = db.GetCommand(sql);            
            ResultSet r = db.ExecuteDataSet(cmd);
            while(r.next())
            {
            	JSONObject item = new JSONObject();
            	for(int i = 0 ; i < field_sel.size();++i)
            	{
            		JSONObject column = field_sel.getJSONObject(i);
            		Object v = this.m_scr.read(r, column.getString("type"), i+2);//第一列是行号
            		item.put(column.getString("name"),v);
            	}
        		arr.add(item);
            }            
            r.close();
            cmd.getConnection().close();
            cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return arr;
    }
}