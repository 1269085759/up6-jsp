package up6.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SqlCmdReader {
	
	public SqlCmdReader() {}
	
	public Object read(ResultSet r,String type,int index)
	{
		Object o = null;
		type = type.toLowerCase();
		
		try {			
			     if(StringUtils.equals("string", type)) o = r.getString(index)==null ? "" : r.getString(index);
			else if(StringUtils.equals("int", type)) o = r.getInt(index);
			else if(StringUtils.equals("datetime", type)) o = r.getDate(index).toString();
			else if(StringUtils.equals("long", type)) o = r.getLong(index);
			else if(StringUtils.equals("smallint", type)) o = r.getInt(index);
			else if(StringUtils.equals("tinyint", type)) o = r.getByte(index);
			else if(StringUtils.equals("short", type)) o = r.getShort(index);
			else if(StringUtils.equals("byte", type)) o = r.getByte(index);
			else if(StringUtils.equals("bool", type)) o = r.getBoolean(index);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return o;
	}
	
	public Object read(ResultSet r,JSONObject field,int index)
	{
		Object o = null;
		String type = field.getString("type").toLowerCase();
		
		try {			
			     if(StringUtils.equals("string", type)) o = r.getString(index)==null ? "" : r.getString(index);
			else if(StringUtils.equals("int", type)) o = r.getInt(index);
			else if(StringUtils.equals("datetime", type)) o = r.getDate(index).toString();
			else if(StringUtils.equals("long", type)) o = r.getLong(index);
			else if(StringUtils.equals("smallint", type)) o = r.getInt(index);
			else if(StringUtils.equals("tinyint", type)) o = r.getByte(index);
			else if(StringUtils.equals("short", type)) o = r.getShort(index);
			else if(StringUtils.equals("byte", type)) o = r.getByte(index);
			else if(StringUtils.equals("bool", type)) o = r.getBoolean(index);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return o;
	}
	
	/**
	 * 读取所有字段数据
	 * @param r
	 * @param fields
	 * @return
	 */
	public JSONObject read(ResultSet r,JSONArray fields)
	{
		JSONObject o = new JSONObject();
		for(int i = 0 ; i < fields.size() ; ++i)
		{
			JSONObject field = (JSONObject) fields.get(i);
			o.put(field.getString("name"), this.read(r, field.getString("type"),i+1) );
		}
		return o;
	}
}