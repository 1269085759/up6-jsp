<%@ page language="java" import="up6.DBFile" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="up6.DBConfig" %>
<%
out.clear();
/*
	清空数据库记录
	更新记录：
		2014-07-21 创建
*/
	DBConfig cfg = new DBConfig();
	cfg.db().Clear();
%>