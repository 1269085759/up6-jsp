package up6.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SqlWhereMerge {
	Map<String, String> m_cds;
	
	public SqlWhereMerge() 
	{
		this.m_cds = new HashMap<String, String>();
	}
	
	public void equal(String n,String v)
	{
		this.m_cds.put(n, String.format("%s='%s'", n,v));
	}
	
	public void equal(String n,int v)
	{
		this.m_cds.put(n, String.format("%s=%d", n,v));
	}
	
	public void like(String n,String v)
	{
		this.m_cds.put(n, String.format("%s like '%%s%'", n,v));
	}
	
	public void clear() {this.m_cds.clear();}
	public void del(String n) { this.m_cds.remove(n); }
	
	public String to_sql()
    {
        ArrayList<String> cs = new ArrayList<String>();
		for(Object obj : this.m_cds.keySet())
		{
			Object value = this.m_cds.get(obj );
			cs.add(value.toString());
		}
		
		String[] s = cs.toArray(new String[cs.size()]);

		return StringUtils.join(s, " and ");        
    }
}
