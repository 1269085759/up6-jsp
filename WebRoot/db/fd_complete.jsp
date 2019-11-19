<%@ page language="java" import="up6.DBFile" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="up6.model.FileInf" %>
<%@ page import="up6.DBConfig" %>
<%@ page import="up6.*" %>
<%@ page import="up6.model.*" %>
<%@ page import="up6.biz.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
out.clear();
/*
	此页面主要更新文件夹数据表。已上传字段
	更新记录：
		2014-07-23 创建
		2019-05-29 增加对子目录上传处理
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String id	= request.getParameter("id");
String uid	= request.getParameter("uid");
String cbk 	= request.getParameter("callback");//jsonp
int ret = 0;

//参数为空
if (	!StringUtils.isBlank(uid)
	||	!StringUtils.isBlank(id))
{
	//取当前节点信息
	DbFolder db = new DbFolder();
	FileInf folder = db.read(id);
	
	//根节点		
	FileInf root = new FileInf();
	root.id = folder.pidRoot;
	
	//当前节点是根节点
	if( StringUtils.isBlank(root.id)) root.id = folder.id;
	

	//上传完毕
	DBConfig cfg = new DBConfig();
	DBFile db2 = cfg.db();
	db2.fd_complete(id,uid);
	
	//扫描文件夹结构
	fd_scan sa = new fd_scan();
	sa.root = root;
	sa.scan(folder,folder.pathSvr);
	
	cfg.db().fd_scan(id,uid);
	
	up6_biz_event.folder_post_complete(id);
	
	ret = 1;
}
out.write(cbk + "(" + ret + ")");
%>