--创建游标,分页存储过程返回的数据保存在游标中
create or replace package zysoft is
  type t_cursor is ref cursor;
end;


--分页存储过程
create or replace procedure sppage_cursor
(
    tablename 	in varchar2,
    primarykey 	in varchar2,
    pagesize	in integer,
    pageindex 	in integer,
    field_where in varchar2,
    fields 		in varchar2,
    fields_sort in varchar2,
    ret 		out zysoft.t_cursor
) is
    --定义需要的变量
    v_sql varchar2(4000);    
    v_sort varchar2(4000);
    v_where varchar2(4000);
    v_start integer;
    v_end integer;
begin
    
    v_sort := fields_sort;
    v_where := field_where;
    
	--排序
    if length(v_sort) < 1 then
    	begin
	    	v_sort := primarykey || 'desc';
        end;
    end if;
    
    --条件
    if length(v_where) <1 then
    	begin
	    	v_where := '1=1';
        end;
    end if;
    
    --计算v_start和v_end是多少
    v_start := (pageindex - 1) * (pagesize-1);
    v_end := (pageindex - 1) * pagesize + pagesize;
    
    v_sql := 'select t2.* from (select rownum rn,t1.* from (select '|| fields || ' from '|| tablename  || ' where ' || v_where || ' order by ' || v_sort || ') t1 where rownum<=' || v_end || ') t2 where rn>=' || v_start;

    --打开游标，让游标指向结果集
    open ret for v_sql;    
    
    --关闭游标
--    close v_out_result;
end sppage_cursor;