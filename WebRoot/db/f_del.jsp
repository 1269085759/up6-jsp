<%@ page language="java" import="up6.DBFile" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="up6.biz.*" %>
<%@ page import="up6.DBConfig" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
out.clear();
/*
	此页面主要用来向数据库添加一条记录。
	一般在 HttpUploader.js HttpUploader_MD5_Complete(obj) 中调用
	更新记录：
		2012-05-24 完善
		2012-06-29 增加创建文件逻辑，
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String fid = request.getParameter("id");
String uid = request.getParameter("uid");
String callback = request.getParameter("callback");//jsonp
int ret = 0;

if (	!StringUtils.isBlank(fid)
	&&	!StringUtils.isBlank(uid))
{
	DBConfig cfg = new DBConfig();
	DBFile db = cfg.db();
	db.Delete(Integer.parseInt(uid),fid);
	up6_biz_event.file_del(fid,Integer.parseInt(uid));
	ret = 1;
}
%><%= callback + "(" + ret + ")" %>