<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="up6.*" %>
<%@ page import="up6.biz.*" %>
<%
out.clear();
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
            , wb.m_path.get("root")+"/filemgr/data/vue-up6.js"
            , wb.m_path.get("root")+"/filemgr/data/vue-down2.js"
            , wb.m_path.get("root")+"/filemgr/data/vue-index.js"
    		) %>
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-md-12">说明：使用了vue,boostrap,layer组件，支持浏览器：ie9+,firefox,chrome</div>
            </div>
        <div class="row" id="app">
            <div class="col-md-12">
                <div class="m-t-md row">
                    <div class="col-md-12">
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="btnUp_click">
                        <img :src="ico.file" />
                        上传文件</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="btnUpFolder_click">
                        <img :src="ico.btnUpFd" />
                        上传目录</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="btnPaste_click">
                        <img :src="ico.btnPaste" />
                        粘贴上传</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="btnMkFolder_click">
                        <img :src="ico.btnEdit" />
                        新建文件夹</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="openUp_click">
                        <img :src="ico.btnPnlUp" />
                        打开上传面板</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="openDown_click">
                        <img :src="ico.btnPnlDown" />
                        打开下载面板</button>
                    <button class="btn btn-default btn-sm m-r-xs" role="button" @click="btnDowns_click" v-show="idSels.length>0">
                        <img :src="ico.btnDown" />
                        批量下载</button>
                    <button class="btn btn-default btn-sm hide" role="button" @click="">
                        <img :src="ico.btnDel" />
                        删除</button>
                        </div>
                </div>
                <!--上传面板-->
                <up6 id="pnl-up" ref="up6" style="display: none;" 
                    :fd_create="url.fd_create"
                    :f_create="url.f_create"
                    :license="license.up6"
                    @load_complete="up6_loadComplete"
                    @item_selected="up6_itemSelected"
                    @file_append="up6_fileAppend"
                    @file_complete="up6_fileComplete"
                    @folder_complete="up6_folderComplete"></up6>
                <!--下载面板-->
                <down2 id="pnl-down" ref="down" style="display: none;"
                    :fd_data="url.fd_data"
                    :license="license.down2"
                    @load_complete="down_loadComplete"
                    @same_file_exist="down_sameFileExist"></down2>
                <!--路径导航-->
                <ol class="breadcrumb  m-t-xs" style="margin-bottom: -5px;">
                    <template v-for="p in pathNav">
                    <li>
                        <a class="link" @click="nav_click(p)">{{p.f_nameLoc}}</a>
                    </li>
                </template>
                </ol>
                <!--文件列表-->
                <table class="table table-hover table-condensed">
                    <thead>
                        <tr>
                            <th style="width:20px"></th>
                            <th style="width:50%;"><input type="checkbox" @change="selAll_click" v-model="idSelAll" />名称</th>
                            <th>编辑</th>
                            <th>大小</th>
                            <th>上传时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-show="folderMker.edit">
                            <td></td>
                            <td>
                                <input class="form-control input-sm" style="width:80%;float:left;" v-model="folderMker.name" ref="tbFdName" @keyup.enter="btnMkFdOk_click"/>
                                <a class="btn btn-default btn-sm m-l-xs" @click="btnMkFdOk_click"><img :src="ico.ok"/></a>
                                <a class="btn btn-default btn-sm" @click="btnMkFdCancel_click"><img :src="ico.cancel"/></a>
                                    </td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr v-for="(f,index) in items">
                            <td>{{index+1}}</td>
                            <td>
                                <div :name="'v'+index">
                                <input type="checkbox" :value="f.f_id" v-model="idSels" :name="'ckb'+index"/>
                                <img :src="ico.file" v-show="!f.f_fdTask" :name="'name'+index"/>
                                <img :src="ico.folder" v-show="f.f_fdTask"/>
                                <a @click="open_folder(f)" class="link m-l-xs" :name="'name'+index">{{f.f_nameLoc}}</a>
                                    </div>
                                <div :name="'edit'+index" style="display:none;">
                                <input class="form-control input-sm" style="width:80%;float:left;" :value="f.f_nameLoc" :name="'name'+index" @keyup.enter="btnRename_ok(f,index)"/>
                                <a class="btn btn-default btn-sm m-l-xs" @click="btnRename_ok(f,index)"><img :src="ico.ok"/></a>
                                <a class="btn btn-default btn-sm"  @click="btnRename_cancel(f,index)"><img :src="ico.cancel"/></a>
                                    </div>
                            </td>
                            <td>
                                <a class="m-r-md link" @click="itemRename_click(f,index)"><img :src="ico.btnEdit"/>重命名</a>
                                <a class="m-r-md link" @click="itemDown_click(f)"><img :src="ico.btnDown"/>下载</a>
                                <a class=" link" @click="btnDel_click(f)"><img :src="ico.btnDel"/>删除</a>
                            </td>
                            <td>{{f.f_fdTask?"":f.f_sizeLoc}}</td>
                            <td>{{tm_format(f.f_time)}}</td>
                        </tr>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="5">
                                <div id="pager"></div>
                            </td>
                        </tr>
                    </tfoot>
                </table>
                <script type="text/javascript">
                    layui.use(['layer'], function () {
                        window.layer = layui.layer;
                    });

                    window.onbeforeunload = function (event) {  }
                    window.unload = function (event) { pageApp.page_close(); };
                </script>
                </div>
        </div>
    </div>
</body>
</html>