package up6.sql;

import java.util.Date;

public class SqlParam {
	public String m_name="";
	public Boolean m_valBool = false;
	public byte m_valByte=0;
	public String m_valStr="";
	public int m_valInt=0;
	public short m_valShort=0;
	public long m_valLong=0;
	public Date m_valTm=new Date();
	
	public SqlParam(String name,byte v) {
		this.m_name=name;
		this.m_valByte = v;
	}
	
	public SqlParam(String name,Boolean v) {
		this.m_name=name;
		this.m_valBool = v;
	}
	
	public SqlParam(String name,String v)
	{
		this.m_name=name;
		this.m_valStr = v;		
	}
	
	public SqlParam(String name,short v)
	{
		this.m_name=name;
		this.m_valShort = v;
	}
	
	public SqlParam(String name,int v)
	{
		this.m_name=name;
		this.m_valInt = v;
	}
	
	public SqlParam(String name,long v)
	{
		this.m_name=name;
		this.m_valLong = v;
	}
	
	public SqlParam(String name,Date v)
	{
		this.m_name=name;
		this.m_valTm = v;
	}
}
