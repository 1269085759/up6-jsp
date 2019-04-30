<%@ page language="java" import="up6.DBFile" pageEncoding="UTF-8"%><%@
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="up6.FileBlockWriter" %><%@
	page import="up6.XDebug" %><%@
	page import="up6.*" %><%@
	page import="up6.biz.*" %><%@
	page import="org.apache.commons.fileupload.FileItem" %><%@
	page import="org.apache.commons.fileupload.FileItemFactory" %><%@
	page import="org.apache.commons.fileupload.FileUploadException" %><%@
	page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %><%@
	page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %><%@
	page import="org.apache.commons.lang.StringUtils" %><%@
	page import="java.net.URLDecoder"%><%@ 
	page import="java.util.Iterator"%><%@ 
	page import="net.sf.json.JSONObject"%><%@
	page import="java.util.List"%><%/*
	此页面负责将文件块数据写入文件中。
	此页面一般由控件负责调用
	参数：
		uid
		idSvr
		md5
		lenSvr
		pathSvr
		RangePos
		fd_idSvr
		fd_lenSvr
	更新记录：
		2012-04-12 更新文件大小变量类型，增加对2G以上文件的支持。
		2012-04-18 取消更新文件上传进度信息逻辑。
		2012-10-25 整合更新文件进度信息功能。减少客户端的AJAX调用。
		2014-07-23 优化代码。
		2015-03-19 客户端提供pathSvr，此页面减少一次访问数据库的操作。
		2016-04-09 优化文件存储逻辑，增加更新文件夹进度逻辑
		2017-07-13 取消数据库操作
		2017-10-23 增加删除文件块缓存操作
*/
//String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String uid 			= request.getHeader("uid");//
String id 			= request.getHeader("id");
String lenSvr		= request.getHeader("lenSvr");
String lenLoc		= request.getHeader("lenLoc");
String blockOffset	= request.getHeader("blockOffset");
String blockSize	= request.getHeader("blockSize");
String blockIndex	= request.getHeader("blockIndex");
String blockMd5		= request.getHeader("blockMd5");
String complete		= request.getHeader("complete");
String pathSvr		= request.getHeader("pathSvr");
pathSvr = PathTool.url_decode(pathSvr);

//参数为空 
if(	 StringUtils.isBlank( uid )
	|| StringUtils.isBlank( id )
	|| StringUtils.isBlank( blockOffset ) 
	|| StringUtils.isBlank(pathSvr))
{
	XDebug.Output("param is null");
	return;
}
 
// Check that we have a file upload request
boolean isMultipart = ServletFileUpload.isMultipartContent(request);
FileItemFactory factory = new DiskFileItemFactory();   
ServletFileUpload upload = new ServletFileUpload(factory);
List files = null;
try 
{
	files = upload.parseRequest(request);
} 
catch (FileUploadException e) 
{// 解析文件数据错误  
    out.println("read file data error:" + e.toString());
    return;
   
}

FileItem rangeFile = null;
// 得到所有上传的文件
Iterator fileItr = files.iterator();
// 循环处理所有文件
while (fileItr.hasNext()) 
{
	// 得到当前文件
	rangeFile = (FileItem) fileItr.next();	
}

boolean verify = false;
String msg = "";
String md5Svr = "";
long blockSizeSvr = rangeFile.getSize();
if(!StringUtils.isBlank(blockMd5))
{
	md5Svr = Md5Tool.fileToMD5(rangeFile.getInputStream());
}

verify = Integer.parseInt(blockSize) == blockSizeSvr;
if(!verify)
{
	msg = "block size error sizeSvr:" + blockSizeSvr + "sizeLoc:" + blockSize;
}

if(verify && !StringUtils.isBlank(blockMd5))
{
	verify = md5Svr.equals(blockMd5);
	if(!verify) msg = "block md5 error";
}

if(verify)
{
	//保存文件块数据
	FileBlockWriter res = new FileBlockWriter();
	//仅第一块创建
	if( Integer.parseInt(blockIndex)==1) res.CreateFile(pathSvr,Long.parseLong(lenLoc));
	res.write( Long.parseLong(blockOffset),pathSvr,rangeFile);
	up6_biz_event.file_post_block(id,Integer.parseInt(blockIndex));
	
	JSONObject o = new JSONObject();
	o.put("msg", "ok");
	o.put("md5", md5Svr);	
	o.put("offset", blockOffset);//基于文件的块偏移位置
	msg = o.toString();
}
rangeFile.delete();
out.write(msg);
%>