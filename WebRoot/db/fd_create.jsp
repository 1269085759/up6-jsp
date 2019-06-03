<%@ page language="java" import="up6.*" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.google.gson.*" %>
<%@ page import="up6.*" %>
<%@ page import="up6.model.*" %>
<%@ page import="up6.biz.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="net.sf.json.*" %>
<%
out.clear();
/*
	更新记录：
		2014-07-23 创建
		2014-08-05 修复BUG，上传文件夹如果没有子文件夹时报错的问题。
		2015-07-30 将子文件命名方式改为 md5 方式，不再使用原文件名称存储，防止冲突。
		2016-04-09 完善存储逻辑。

	JSON格式化工具：http://tool.oschina.net/codeformat/json
	POST数据过大导致接收到的参数为空解决方法：http://sishuok.com/forum/posts/list/2048.html
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

WebBase web     = new WebBase(pageContext);
String id       = web.queryString("id");
String uid      = web.queryString("uid");
String lenLoc   = web.queryString("lenLoc");
String sizeLoc  = web.queryString("sizeLoc");
String pathLoc  = web.queryString("pathLoc");
pathLoc         = URLDecoder.decode(pathLoc,"UTF-8");//utf-8解码
String callback = web.queryString("callback");//jsonp


//参数为空
if (StringUtils.isBlank(id)
	|| StringUtils.isBlank(uid)
	|| StringUtils.isBlank(pathLoc))
{
	out.write(callback + "({\"value\":null})");
	return;
}

FileInf fileSvr = new FileInf();
fileSvr.id      = id;
fileSvr.fdChild = false;
fileSvr.fdTask  = true;
fileSvr.uid     = Integer.parseInt(uid);
fileSvr.nameLoc = PathTool.getName(pathLoc);
fileSvr.pathLoc = pathLoc;
fileSvr.lenLoc  = Long.parseLong(lenLoc);
fileSvr.sizeLoc = sizeLoc;
fileSvr.deleted = false;
fileSvr.nameSvr = fileSvr.nameLoc;

//生成存储路径
PathBuilderUuid pb = new PathBuilderUuid();
fileSvr.pathSvr = pb.genFolder(fileSvr);
fileSvr.pathSvr = fileSvr.pathSvr.replace("\\","/");
PathTool.createDirectory(fileSvr.pathSvr);

//添加到数据表
DBConfig cfg = new DBConfig();
DBFile db = cfg.db();
db.Add(fileSvr);
up6_biz_event.folder_create(fileSvr);

Gson g = new Gson();
String json = g.toJson(fileSvr);
json = URLEncoder.encode(json,"utf-8");
json = json.replace("+","%20");

JSONObject ret = new JSONObject();
ret.put("value",json);
json = callback + String.format("(%s)",ret.toString());//返回jsonp格式数据。
out.write(json);%>