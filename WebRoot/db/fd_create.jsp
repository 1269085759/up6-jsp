<%@ page language="java" import="up6.*" pageEncoding="UTF-8"%><%@
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="com.google.gson.*" %><%@
	page import="up6.*" %><%@
	page import="up6.model.*" %><%@
	page import="up6.biz.*" %><%@
	page import="org.apache.commons.lang.StringUtils" %><%@
	page import="java.net.URLDecoder" %><%@
	page import="java.net.URLEncoder" %><%/*
	以md5模式上传文件夹，不在服务端创建层级结构，在数据库中保存层级信息。
	客户端上传的文件夹JSON格式：
    [
	     [name:"soft"		    //文件夹名称
	     ,pid:0                //父级ID
	     ,idLoc:0              //文件夹ID，客户端定义
	     ,idSvr:0              //文件夹ID，与数据库中的xdb_folder.fd_id对应。
	     ,length:"102032"      //数字化的文件夹大小，以字节为单位
	     ,size:"10G"           //格式化的文件夹大小
	     ,pathLoc:"d:\\soft"   //文件夹在客户端的路径
	     ,pathSvr:"e:\\web"    //文件夹在服务端的路径
	     ,foldersCount:0       //子文件夹总数
	     ,filesCount:0         //子文件总数
	     ,filesComplete:0      //已上传完成的子文件总数
	     ,folders:[
	           {name:"img1",pidLoc:0,pidSvr:10,idLoc:1,idSvr:0,pathLoc:"D:\\Soft\\img1",pathSvr:"E:\\Web"}
	          ,{name:"img2",pidLoc:1,pidSvr:10,idLoc:2,idSvr:0,pathLoc:"D:\\Soft\\image2",pathSvr:"E:\\Web"}
	          ,{name:"img3",pidLoc:2,pidSvr:10,idLoc:3,idSvr:0,pathLoc:"D:\\Soft\\image2\\img3",pathSvr:"E:\\Web"}
	          ]
	     ,files:[
	           {name:"f1.exe",idLoc:0,idSvr:0,pidRoot:0,pidLoc:1,pidSvr:0,length:"100",size:"100KB",pathLoc:"",pathSvr:""}
	          ,{name:"f2.exe",idLoc:0,idSvr:0,pidRoot:0,pidLoc:1,pidSvr:0,length:"100",size:"100KB",pathLoc:"",pathSvr:""}
	          ,{name:"f3.exe",idLoc:0,idSvr:0,pidRoot:0,pidLoc:1,pidSvr:0,length:"100",size:"100KB",pathLoc:"",pathSvr:""}
	          ,{name:"f4.rar",idLoc:0,idSvr:0,pidRoot:0,pidLoc:1,pidSvr:0,length:"100",size:"100KB",pathLoc:"",pathSvr:""}
	          ]
	]

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

String id = request.getParameter("id");
String uid = request.getParameter("uid");
String lenLoc = request.getParameter("lenLoc");
String sizeLoc = request.getParameter("sizeLoc");
String pathLoc = request.getParameter("pathLoc");
pathLoc = URLDecoder.decode(pathLoc,"UTF-8");//utf-8解码
String callback = request.getParameter("callback");//jsonp


//参数为空
if (StringUtils.isBlank(id)
	|| StringUtils.isBlank(uid)
	|| StringUtils.isBlank(pathLoc))
{
	out.write(callback + "({\"value\":null})");
	return;
}

FileInf fileSvr= new FileInf();
fileSvr.id = id;
fileSvr.fdChild = false;
fileSvr.fdTask = true;
fileSvr.uid = Integer.parseInt(uid);
fileSvr.nameLoc = PathTool.getName(pathLoc);
fileSvr.pathLoc = pathLoc;
fileSvr.lenLoc = Long.parseLong(lenLoc);
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
json = callback + "({\"value\":\"" + json + "\"})";//返回jsonp格式数据。
out.write(json);%>