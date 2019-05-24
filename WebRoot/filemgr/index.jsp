<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@
	page import="up6.*" %><%@
	page import="up6.biz.*" %><%
	PageFileMgr pfm = new PageFileMgr(pageContext);
	
	WebBase wb = new WebBase(pageContext);
%><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>文件管理器</title>
    <%= wb.paramPage() %>
    <%= wb.require(    		
            wb.m_path.get("jquery")
            , wb.m_path.get("res")+"filemgr.css"
            , wb.m_path.get("bootstrap")
            , wb.m_path.get("layerui")
            , wb.m_path.get("moment")
            , wb.m_path.get("vue")
            , wb.m_path.get("up6")
            , wb.m_path.get("down2")
            , wb.m_path.get("root")+"/filemgr/data/index.js"
            , wb.m_path.get("res")+"layer.window.js"
    		) %>
</head>
<body>
    <div class="container-fluid">
        <div class="m-t-md clearfix">
            <button class="btn btn-default btn-sm pull-left m-r-xs" role="button" id="btn-up"><img name="up"/> 上传文件</button>
            <button class="btn btn-default btn-sm pull-left m-r-xs" role="button" id="btn-up-fd"><img name="up-fd"/> 上传目录</button>
            <button class="btn btn-default btn-sm pull-left m-r-xs" role="button" id="btn-up-paste"><img name="paste"/> 粘贴上传</button>
            <button class="btn btn-default btn-sm pull-left m-r-xs" role="button" id="btn-open-up"><img name="up-panel"/> 打开上传面板</button>
            <button class="btn btn-default btn-sm pull-left m-r-xs" role="button" id="btn-open-down"><img name="down-panel"/> 打开下载面板</button>
            <button class="btn btn-default btn-sm pull-left hide m-r-xs" role="button" id="btn-down"><img name="down"/> 下载</button>
            <button class="btn btn-default btn-sm pull-left hide" role="button" id="btn-del"><img name="del"/> 删除</button>
        </div>
        <div id="up6-panel" style="display:none;">
            <%= wb.template(wb.path("rootAbs")+"filemgr/data/ui-up.html") %>            
        </div>
        <div id="down2-panel" style="display:none;"><%=wb.template(wb.path("rootAbs")+"filemgr/data/ui-down.html") %></div>
        
        <!--路径导航-->
        <ol class="breadcrumb  m-t-xs" style="margin-bottom:-10px;" id="path">
            <li>
                <a class="link" >根目录</a>
            </li>
        </ol>
        <table class="layui-hide" lay-size="sm" id="files" lay-filter="files" lay-skin="nob"></table>
        <script type="text/javascript">
        	var pageApp = null;				
        	//JavaScript代码区域
            layui.use(['element', 'table','laytpl'], function () {
                var element = layui.element, table = layui.table,laytpl = layui.laytpl;

                //js-module
                table.render({
                    elem: '#files'
                    , id: 'files'
                    , defaultToolbar: []//关闭过滤，打印按钮
                    , size: 'sm'
                    , height: 'full'//高度,full, 
                    , url: 'index.jsp?op=data&time='+(new Date().getTime())
                    , limit: 20//每页显示的条数，默认10
                    , page: true //开启分页
                    , cols: [[ //表头
                        { width: 50, sort: false, type: 'numbers' }
                        , { field: 'f_id', title: '', width: 50, sort: false, type: 'checkbox' }
                        , {
                            field: 'f_nameLoc', title: '名称', width: 500, sort: false, templet: function (d) {
                                var tmp = '<img src="{{d.img}}"/> <a class="link" lay-event="file">{{d.f_nameLoc}}</a>';
                                var par = $.extend(d, { img: page.path.res + "imgs/16/file.png" });
                                if (d.f_fdTask) par.img = page.path.res + "imgs/16/folder.png";
                                var str = laytpl(tmp).render(par);
                                return str;
                            }
                        }
                        , { field: 'f_sizeLoc', title: '大小', width: 80, sort: false, }
                        , { field: 'f_time', title: '上传时间', width:150, templet: function (d) { return moment(d.f_time).format('YYYY-MM-DD HH:mm:ss') } }
                        , { title: '编辑', templet: function (d) { return '<a class="layui-table-link link" lay-event="delete"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 删除</a>';} }
                    ]],
                    done: function (res, curr, count) { pageApp.attr.ui.table = table;}
                });

                table.on('tool(files)', function (obj) { pageApp.attr.event.table_tool_click(obj, table);});

                //工具栏
                table.on('toolbar(files)', function (obj) { pageApp.attr.event.table_tool_click(obj, table); });

                //复选框
                table.on('checkbox(files)', function (obj) { pageApp.attr.event.table_check_change(obj, table); });
                table.on('edit(files)', function (obj) { pageApp.attr.event.table_edit(obj); });
                pageApp.attr.ui.table = table;
            });

            window.onbeforeunload = function (event) { return confirm("确定离开此页面吗？"); }
            window.unload = function (event) { pageApp.page_close(); };
        </script>
    </div>
</body>
</html>