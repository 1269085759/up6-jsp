<%@ page language="java" import="up6.*" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@	page import="com.google.gson.Gson" %>
<%@ page import="up6.*" %>
<%@ page import="up6.model.*" %>
<%@ page import="up6.biz.*" %>
<%@	page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%
out.clear();
/*
	所有单个文件均以md5模式存储。
	更新记录：
		2012-05-24 完善
		2012-06-29 增加创建文件逻辑，
		2015-07-30 取消文件夹层级结构存储规则，改为使用日期存储规则，文件夹层级结构仅保存在数据库中。
		2016-01-07 返回值改为JSON
		2016-04-09 完善逻辑。
		2017-07-13 取消生成id操作
*/
WebBase web = new WebBase(pageContext);
String id		= web.queryString("id");
String md5 		= web.queryString("md5");
String uid 		= web.queryString("uid");
String lenLoc 	= web.queryString("lenLoc");//数字化的文件大小。12021
String sizeLoc 	= web.queryString("sizeLoc");//格式化的文件大小。10MB
String callback = web.queryString("callback");
String pathLoc	= web.queryString("pathLoc");
pathLoc			= PathTool.url_decode(pathLoc);

//参数为空
if (	StringUtils.isBlank(md5)
	&& StringUtils.isBlank(uid)
	&& StringUtils.isBlank(sizeLoc))
{
	out.write(callback + "({\"value\":null})");
	return;
}

FileInf fileSvr= new FileInf();
fileSvr.id = id;
fileSvr.fdChild = false;
fileSvr.uid = Integer.parseInt(uid);
fileSvr.nameLoc = PathTool.getName(pathLoc);
fileSvr.pathLoc = pathLoc;
fileSvr.lenLoc = Long.parseLong(lenLoc);
fileSvr.sizeLoc = sizeLoc;
fileSvr.deleted = false;
fileSvr.md5 = md5;
fileSvr.nameSvr = fileSvr.nameLoc;

//所有单个文件均以uuid/file方式存储
PathBuilderUuid pb = new PathBuilderUuid();
fileSvr.pathSvr = pb.genFile(fileSvr.uid,fileSvr);
fileSvr.pathSvr = fileSvr.pathSvr.replace("\\","/");

DBConfig cfg = new DBConfig();
DBFile db = cfg.db();
FileInf fileExist = new FileInf();
	
boolean exist = db.exist_file(md5,fileExist);
//数据库已存在相同文件，且有上传进度，则直接使用此信息
if(exist && fileExist.lenSvr > 1)
{
	fileSvr.nameSvr			= fileExist.nameSvr;
	fileSvr.pathSvr 		= fileExist.pathSvr;
	fileSvr.perSvr 			= fileExist.perSvr;
	fileSvr.lenSvr 			= fileExist.lenSvr;
	fileSvr.complete		= fileExist.complete;
	db.Add(fileSvr);
	
	//触发事件
    up6_biz_event.file_create_same(fileSvr);
}//此文件不存在
else
{
	db.Add(fileSvr);
	//触发事件
    up6_biz_event.file_create(fileSvr);
	
	FileBlockWriter fr = new FileBlockWriter();
	fr.CreateFile(fileSvr.pathSvr,fileSvr.lenLoc);
}

Gson gson = new Gson();
String json = gson.toJson(fileSvr);

json = URLEncoder.encode(json,"UTF-8");//编码，防止中文乱码
json = json.replace("+","%20");
json = callback + "({\"value\":\"" + json + "\"})";//返回jsonp格式数据。
out.write(json);%>