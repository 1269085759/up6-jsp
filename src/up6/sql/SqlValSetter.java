package up6.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SqlValSetter {
	
	/**
	 * 设置值
	 * @param cmd
	 * @param fields
	 * @param val
	 */
	public void set(PreparedStatement cmd,JSONArray fields,JSONObject val)
	{
		if(null==fields) return;
		if(null==val) return;
		
		for(int index = 0 ; index < fields.size(); ++index)
		{
			JSONObject o = (JSONObject)fields.get(index);
			String type = o.getString("type").toLowerCase();
			String name = o.getString("name");
			int i = index+1;
			try {
				     if(StringUtils.equals("string", type)) cmd.setString(i, val.getString(name) );
				else if(StringUtils.equals("int", type)) cmd.setInt(i, val.getInt(name) );
				else if(StringUtils.equals("datetime", type)) 
					{
					SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss" );
					
					cmd.setDate( i, (Date) df.parse(val.getString(name)) );
					
					}
				else if(StringUtils.equals("long", type)) cmd.setLong( i, val.getLong( name) );
				else if(StringUtils.equals("smallint", type)) cmd.setInt( i, val.getInt(name) );
				else if(StringUtils.equals("tinyint", type)) cmd.setInt( i, val.getInt( name) );
				else if(StringUtils.equals("short", type)) cmd.setShort( i, (short)val.getInt( name ) );
				else if(StringUtils.equals("byte", type)) cmd.setByte( i, (byte)val.getInt( name) );
				else if(StringUtils.equals("bool", type)) cmd.setBoolean(i, val.getBoolean( name ) );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param cmd
	 * @param fields
	 * @param val
	 * @param prevIndex
	 */
	public int set(PreparedStatement cmd,JSONArray fields,JSONObject val,int prevIndex)
	{
		if(null==fields) return 0;
		if(null==val) return 0;		
		
		for(int index = 0 ; index < fields.size(); ++index)
		{
			JSONObject o = (JSONObject)fields.get(index);
			String type = o.getString("type").toLowerCase();
			String name = o.getString("name");
			int i = index+1 + prevIndex;//跳过上一个参数的位置
			try {
				     if(StringUtils.equals("string", type)) cmd.setString(i, val.getString(name) );
				else if(StringUtils.equals("int", type)) cmd.setInt(i, val.getInt(name) );
				else if(StringUtils.equals("datetime", type)) 
					{
					SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss" );
					
					cmd.setDate( i, (Date) df.parse(val.getString(name)) );
					
					}
				else if(StringUtils.equals("long", type)) cmd.setLong( i, val.getLong( name) );
				else if(StringUtils.equals("smallint", type)) cmd.setInt( i, val.getInt(name) );
				else if(StringUtils.equals("tinyint", type)) cmd.setInt( i, val.getInt( name) );
				else if(StringUtils.equals("short", type)) cmd.setShort( i, (short)val.getInt( name ) );
				else if(StringUtils.equals("byte", type)) cmd.setByte( i, (byte)val.getInt( name) );
				else if(StringUtils.equals("bool", type)) cmd.setBoolean(i, val.getBoolean( name ) );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prevIndex + fields.size();
	}
	
	/**
	 * 
	 * @param cmd
	 * @param fields
	 * @param vals
	 * @param prevColumnIndex 上一个字段索引。在同时设置多组值时使用。
	 */
	public void set(PreparedStatement cmd,JSONArray fields,SqlParam[] vals)
	{
		if(null == fields) return;
		if(null == vals) return;
		if(vals.length == 0) return;
		
		for(int index = 0 ; index < fields.size(); ++index)
		{
			JSONObject o = (JSONObject)fields.get(index);
			String type = o.getString("type").toLowerCase();
			int i = index+1;
			try {
				     if(StringUtils.equals("string", type)) cmd.setString(i, vals[index].m_valStr );
				else if(StringUtils.equals("int", type)) cmd.setInt(i, vals[index].m_valInt );
				else if(StringUtils.equals("datetime", type)) cmd.setDate( i, (Date) vals[index].m_valTm );
				else if(StringUtils.equals("long", type)) cmd.setLong( i, vals[index].m_valLong );
				else if(StringUtils.equals("smallint", type)) cmd.setInt( i, vals[index].m_valInt );
				else if(StringUtils.equals("tinyint", type)) cmd.setInt( i, vals[index].m_valInt );
				else if(StringUtils.equals("short", type)) cmd.setShort( i, vals[index].m_valShort );
				else if(StringUtils.equals("byte", type)) cmd.setByte( i, vals[index].m_valByte );
				else if(StringUtils.equals("bool", type)) cmd.setBoolean(i, vals[index].m_valBool);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param cmd
	 * @param fields
	 * @param vals
	 * @param prevOffset 上一个变量参数的索引，基于0
	 */
	public int set(PreparedStatement cmd,JSONArray fields,SqlParam[] vals,int prevOffset)
	{
		if(null == fields) return 0;
		if(null == vals) return 0;
		if(vals.length == 0) return 0;
		
		for(int index = 0 ; index < fields.size(); ++index)
		{
			JSONObject o = (JSONObject)fields.get(index);
			String type = o.getString("type").toLowerCase();
			int i = index+1+prevOffset;//路过上一个参数
			try {
				     if(StringUtils.equals("string", type)) cmd.setString(i, vals[index].m_valStr );
				else if(StringUtils.equals("int", type)) cmd.setInt(i, vals[index].m_valInt );
				else if(StringUtils.equals("datetime", type)) cmd.setDate( i, (Date) vals[index].m_valTm );
				else if(StringUtils.equals("long", type)) cmd.setLong( i, vals[index].m_valLong );
				else if(StringUtils.equals("smallint", type)) cmd.setInt( i, vals[index].m_valInt );
				else if(StringUtils.equals("tinyint", type)) cmd.setInt( i, vals[index].m_valInt );
				else if(StringUtils.equals("short", type)) cmd.setShort( i, vals[index].m_valShort );
				else if(StringUtils.equals("byte", type)) cmd.setByte( i, vals[index].m_valByte );
				else if(StringUtils.equals("bool", type)) cmd.setBoolean(i, vals[index].m_valBool);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prevOffset + fields.size();
	}
}