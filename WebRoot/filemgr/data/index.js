function PageLogic() {
    var _this = this;
    this.files_checked = [];
    this.pathCur = { f_id: "", f_pid: "", f_pidRoot: "", f_nameLoc: "根目录", f_pathRel: "/" };//
    this.data = {
        downPath: "", up6: null, panel: { up: null, down: null }, down2: null,up_inited:false,down_inited:false
    };
    this.ui = {
        ico: [
            {name:"up", url:page.path.res + "imgs/16/upload.png"}
            ,{name:"up-fd", url:page.path.res + "imgs/16/folder.png"}
            ,{ name: "paste", url:page.path.res + "imgs/16/paste.png" }
            , { name: "del", url:page.path.res + "imgs/16/del.png" }
            , { name: "down", url:page.path.res + "imgs/16/down.png" }
            , { name: "up-panel", url:page.path.res + "imgs/16/up-panel.png" }
            , { name: "down-panel", url:page.path.res + "imgs/16/down-panel.png" }
        ]
    };
    this.init_up6 = function () {
        if (this.data.up6 != null) return;
        this.data.up6 = new HttpUploaderMgr();
        this.data.up6.event.fileAppend = function () {
            _this.open_upload_panel();
        };
        this.data.up6.event.md5Complete = function (obj, md5) { /*alert(md5);*/ };
        this.data.up6.event.fdComplete = function (obj) {
            _this.attr.event.file_post_complete();
        };
            this.data.up6.event.fileComplete = function (obj) {
            _this.attr.event.file_post_complete();
        };
        this.data.up6.event.loadComplete = function () {
            _this.data.up_inited = true;
            setTimeout(function () {
                _this.load_uncomp();
            }, 300);
        };
        this.data.up6.load_to(this.attr.ui.up6);
    };
    this.init_down2 = function () {
        if (this.data.down2 != null) return;
        this.data.down2 = new DownloaderMgr();
        this.data.down2.event.loadComplete = function () {
            _this.data.down_inited = true;
            setTimeout(function () {
                _this.load_uncmp_down();
            }, 300);
        };
        this.data.down2.event.sameFileExist = function (name) {
            layer.alert('相同下载项已存在：'+name, { icon: 2 });
        };
        this.data.down2.loadTo(this.attr.ui.down2);
    };
    this.init_imgs = function () {
        $.each(this.ui.ico, function (i, n) {
            var img = $("img[name='" + n.name + "']");
            img.attr("src", n.url);
            if (typeof (n.w) != undefined) {
                img.css("width", n.w);
            }
        });
    };

    //加载未完成列表
    this.load_uncomp= function () {
        var param = jQuery.extend({}, { time: new Date().getTime() });
        $.ajax({
            type: "GET"
            , dataType: "json"
            , url: "index.jsp?op=uncomp"
            , data: param
            , success: function (res) {
                if (res.length>0) _this.open_upload_panel();

                $.each(res, function (i, n) {
                    if (n.fdTask) {
                        var f = _this.data.up6.addFolderLoc(n);
                        f.folderInit = true;
                        f.folderScan = true;
                        f.ui.btn.post.show();
                        f.ui.btn.del.show();
                        f.ui.btn.cancel.hide();
                    }
                    else {
                        var f = _this.data.up6.addFileLoc(n);
                        f.ui.percent.text("("+n.perSvr+")");
                        f.ui.process.css("width",n.perSvr);
                        f.ui.btn.post.show();
                        f.ui.btn.del.show();
                        f.ui.btn.cancel.hide();
                    }
                });                
            }
            , error: function (req, txt, err) { }
            , complete: function (req, sta) { req = null; }
        });
    };
    this.load_uncmp_down = function () {
        var param = $.extend({}, this.data.down2.Config.Fields);
	    $.ajax({
	        type: "GET"
            , dataType: 'json'
            , jsonp: "callback" //自定义的jsonp回调函数名称，默认为jQuery自动生成的随机函数名
            , url: "index.jsp?op=uncmp-down"
            , data: param
            , success: function (files)
            {
                if (files.length > 0) { _this.open_down_panel();}
                $.each(files, function (i, n) {
                    var dt = {
                        svrInit: true
                        , id: n.f_id
                        , lenLoc: n.f_lenLoc
                        , pathLoc: n.f_pathLoc
                        , nameLoc: n.f_nameLoc
                        , sizeSvr: n.f_sizeSvr
                        , perLoc: n.f_perLoc
                        , fdTask: n.f_fdTask
                    };
                    _this.data.down2.resume_file(dt);
                });
            }
            , error: function (req, txt, err) { alert("加载文件列表失败！" +req.responseText); }
            , complete: function (req, sta) {req = null;}
	    });
    };

    this.open_upload_panel = function () {
        layer.open({
            type: 1
            , maxmin: true
            , shade: 0//不显示遮罩
            , title: '上传文件'
            , offset: 'rb'//右下角
            //, btn: ['确定', '取消']
            , content: _this.data.panel.up
            , area: ['452px', '562px']
            , success: function (layero, index) {
                _this.data.panel.up.show();
            }
            , btn1: function (index, layero) {
                layer.close(index);//关闭窗口
                _this.data.panel.up.hide();
            }
            , btn2: function (index, layero) {
                _this.data.panel.up.hide();
            }
        });
        _this.data.panel.up.show();
    };

    this.open_down_panel = function () {
        layer.open({
            type: 1
            , maxmin: true
            , shade: 0//不显示遮罩
            , title: '文件下载'
            , offset: 'rb'//右下角
            //, btn: ['确定', '取消']
            , content: _this.data.panel.down
            , area: ['452px', '562px']
            , success: function (layero, index) {
                _this.data.panel.down.show();
            }
            , btn1: function (index, layero) {
                layer.close(index);//关闭窗口
                _this.data.panel.down.hide();
            }
            , btn2: function (index, layero) {
                _this.data.panel.down.hide();
            }
        });
        _this.data.panel.down.show();
    };

    this.upload_file = function () {
        if (!this.data.up_inited) {
            layer.alert('控件没有初始化成功', { icon: 2 });
            return;
        }
        this.data.up6.openFile();
    };
    this.upload_folder = function () {
        if (!this.data.up_inited) {
            layer.alert('控件没有初始化成功', { icon: 2 });
            return;
        }
        this.data.up6.openFolder();
    };
    this.upload_paste = function () {
        if (!this.data.up_inited) {
            layer.alert('控件没有初始化成功', { icon: 2 });
            return;
        }
        this.data.up6.pasteFiles();
    };
    this.page_close = function () {
        this.data.up6.page_close();
        this.data.down2.page_close();
    };

    this.attr = {
        ui: {
            table: null, btnDown: "#btn-down", key: "#search-key"
            , up6: "#up6-panel"
            , down2: "#down2-panel"
            , btnUp: "#btn-up"
            , btnUpPaste: "#btn-up-paste"
            , btnDel: "#btn-del"
            , btnMkFolder: "#btn-mk-folder"
        }
        , nav_path: null
        , ui_ents: [
            {
                id: "#btn-up", e: "click", n: function () {
                    _this.upload_file();
                }
            },
            {
                id: "#btn-up-fd", e: "click", n: function () {
                    _this.upload_folder();
                }
            },
            {
                id: "#btn-up-paste", e: "click", n: function () {
                    _this.upload_paste();
                }
            },
            {
                id: "#btn-open-up", e: "click", n: function () {
                    _this.open_upload_panel();
                }
            },
            {
                id: "#btn-mk-folder", e: "click", n: function () {
                    _this.attr.event.btn_mk_folder_click();
                }
            },
            {
                id: "#btn-down", e: "click", n: function () {
                    _this.attr.event.btn_down_click();
                }
            },
            {
                id: "#btn-open-down", e: "click", n: function () {
                    _this.open_down_panel();
                }
            },
            {
                id: "#btn-del", e: "click", n: function () {
                    _this.attr.event.btn_del_click();
                }
            },
            {
                id: "#btn-search", e: "click", n: function () {
                    _this.attr.event.btn_search_click();
                }
            }
        ]
        , app: null
        , event: {
            file_post_complete: function () {
                _this.attr.event.btn_refresh_click();
            }
            , folder_post_complete: function () {
                _this.attr.event.btn_refresh_click();
            }
            , file_append: function (f) {
                f.ui.path.text(_this.pathCur.f_nameLoc);
            }
            , folder_append: function (f) {
                f.ui.path.text(_this.pathCur.f_nameLoc);
            }
            , file_md5_complete: function (obj) {
                obj.fileSvr.pid = _this.pathCur.f_id;
                obj.fileSvr.pidRoot = _this.pathCur.f_pidRoot;
            }
            , scan_complete: function (f) {
                f.folderSvr.pid = _this.pathCur.f_id;
                f.folderSvr.pidRoot = _this.pathCur.f_pidRoot;
            }
            , btn_up_click: function () {
                //上传
                layer.open({
                    type: 2
                    , title: '上传文件'
                    , btn: ['确定', '取消']
                    , content: 'up.jsp'
                    , area: ['454px', '592px']
                    , success: function (layero, index) {
                        var body = layer.getChildFrame('body', index);
                        var ifm = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                        //ifm.method();//调用子页面方法
                        ifm.ent_post_complete = _this.attr.event.file_post_complete;
                    }
                    , btn1: function (index, layero) {
                        var ifm = window[layero.find('iframe')[0]['name']];
                        var ret = ifm.toObj();//
                        layer.close(index);//关闭窗口
                    }
                    , btn2: function (index, layero) { }
                });
            }
            , btn_up_paste_click: function () {
                _this.up6.pasteFiles();
            }
            , btn_mk_folder_click: function () {
                new LayerWindow({
                    title: "新建文件夹"
                    , w: "589px"
                    , h: "167px"
                    , url: "biz/form.jsp"
                    , btn_ok: "确定"
                    , load_complete: function (ifm) {
                        ifm.initUI({
                            ui: [{ id: "f_nameLoc", txt: "名称" }]
                        });
                    }
                    , btn_ok_click: function (ifm) {
                        var newData = ifm.toObj();
                        var pidRoot = _this.pathCur.f_pidRoot;
                        pidRoot = pidRoot.replace(/\s+/g, "");
                        if (pidRoot == "") pidRoot = _this.pathCur.f_id;

                        var data = $.extend({}, newData, {
                            f_pid: _this.pathCur.f_id
                            , f_pidRoot: pidRoot
                        });

                        var param = { data: encodeURIComponent(JSON.stringify(data)) };
                        $.ajax({
                            type: "GET"
                            , dataType: "json"
                            , url: "index.jsp?op=mk-folder"
                            , data: param
                            , success: function (res) {
                                _this.attr.event.btn_refresh_click();
                            }
                            , error: function (req, txt, err) { }
                            , complete: function (req, sta) { req = null; }
                        });

                    }
                });
            }
            , up6_sel_file: function () {
                layer.open({
                    type: 1
                    , maxmin: true
                    , shade: 0//不显示遮罩
                    , title: '上传文件'
                    , offset: 'rb'//右下角
                    , btn: ['确定', '取消']
                    , content: _this.attr.ui.up6
                    , area: ['635px', '454px']
                    , success: function (layero, index) {
                        _this.data.panel.up.show();
                    }
                    , btn1: function (index, layero) {
                        layer.close(index);//关闭窗口
                        _this.data.panel.up.hide();
                    }
                    , btn2: function (index, layero) {
                        _this.data.panel.up.hide();
                    }
                });
            }
            , btn_down_click: function () {

                if (_this.data.down2.Config["Folder"] == "") {
                    _this.data.down2.open_folder();
                }
                else {
                    _this.open_down_panel();
                    $.each(_this.files_checked, function (i, f) {
                        if (_this.data.down2.exist_url(f.f_nameLoc)) {
                            layer.alert('相同下载项已存在：' + f.f_nameLoc, { icon: 2 });
                            return;
                        }
                        //文件夹
                        if (f.f_fdTask) {
                            var dt = { f_id: f.f_id, lenSvr: f.f_lenLoc, pathSvr: f.f_pathSvr, nameLoc: f.f_nameLoc, fileUrl: _this.data.down2.Config["UrlDown"] };
                            _this.data.down2.app.addFolder(dt);
                        }
                        else {
                            //下载数据转换：lenSvr,pathSvr,nameLoc,fileUrl
                            var dt = { f_id: f.f_id, lenSvr: f.f_lenLoc, pathSvr: f.f_pathSvr, nameLoc: f.f_nameLoc, fileUrl: _this.data.down2.Config["UrlDown"] };
                            _this.data.down2.app.addFile(dt);

                        }
                    });
                }
            }
            , btn_search_click: function () {
                layui.use(['table'], function () {
                    var key = $(_this.attr.ui.key).val();
                    var table = layui.table;
                    table.reload('files', {
                        url: 'index.jsp?op=data&key=' + key //
                        , page: { curr: 1 }//第一页
                    });

                    //_this.attr.event.path_changed(data);
                });
            }
            , btn_del_click: function () {
                layer.msg('确实要删除选中文件？', {
                    time: 0 //不自动关闭
                    , btn: ['确定', '取消']
                    , yes: function (index) {
                        layer.close(index);

                        var ids = [];
                        $.each(_this.files_checked, function (i, n) {
                            ids.push({ f_id: n.f_id, f_fdTask: n.f_fdTask });
                        });
                        var str = JSON.stringify(ids);
                        str = encodeURIComponent(str);
                        var param = jQuery.extend({}, { data: str, time: new Date().getTime() });
                        $.ajax({
                            type: "POST"
                            , dataType: "json"
                            , url: "index.jsp?op=del-batch"
                            , data: param
                            , success: function (res) {
                                _this.attr.event.btn_refresh_click();
                                $(_this.attr.ui.btnDel).addClass("hide");
                            }
                            , error: function (req, txt, err) { }
                            , complete: function (req, sta) { req = null; }
                        });
                    }
                });
            }
            , btn_refresh_click: function () {
                _this.attr.ui.table.reload('files', {
                    url: 'index.jsp?op=data&pid=' + _this.pathCur.f_id + '&tm=' + new Date().getTime()
                    , page: { curr: 1 }//第一页
                });
            }
            , table_tool_click: function (obj, table) {
                _this.attr.table_events[obj.event](obj, table);
            }
            , table_check_change: function (obj, table) {
                var cs = table.checkStatus('files');
                //未选中
                if (cs.data.length < 1) {
                    $(_this.attr.ui.btnDown).addClass("hide");
                    $(_this.attr.ui.btnDel).addClass("hide");
                }
                else {
                    $(_this.attr.ui.btnDown).removeClass("hide");
                    $(_this.attr.ui.btnDel).removeClass("hide");
                    _this.files_checked = cs.data;
                }
            }
            , table_edit: function (obj, table) {

                var param = jQuery.extend({}, obj.data, { f_nameLoc: obj.value });
                $.ajax({
                    type: "GET"
                    , dataType: "json"
                    , url: "index.jsp?op=rename"
                    , data: { data: JSON.stringify(param) }
                    , success: function (res) {
                        obj.update({ f_nameLoc: obj.value });
                    }
                    , error: function (req, txt, err) { }
                    , complete: function (req, sta) { req = null; }
                });
            }
            , table_rename: function (obj, table) {
                new LayerWindow({
                    title: "重命名"
                    , w: "589px"
                    , h: "167px"
                    , url: "biz/form.jsp"
                    , btn_ok: "确定"
                    , load_complete: function (ifm) {
                        ifm.initUI({
                            ui: [{ id: "f_nameLoc", txt: "文件名称" }]
                            , data: obj.data
                        });
                    }
                    , btn_ok_click: function (ifm) {
                        var newData = ifm.toObj();
                        var data = $.extend({}, obj.data, newData);

                        var param = { data: encodeURIComponent(JSON.stringify(data)) };
                        $.ajax({
                            type: "GET"
                            , dataType: "json"
                            , url: "index.jsp?op=rename"
                            , data: param
                            , success: function (res) {
                                obj.update({ "f_nameLoc": newData.f_nameLoc });
                            }
                            , error: function (req, txt, err) { }
                            , complete: function (req, sta) { req = null; }
                        });

                    }
                });
            }
            , table_del: function (obj, table) {
                var msg = "确定要删除文件：" + obj.data.f_nameLoc + " ？";
                if (obj.data.f_fdTask) msg = "确定要删除文件夹：" + obj.data.f_nameLoc + " ？";

                layer.msg(msg, {
                    time: 0 //不自动关闭
                    , icon: 3
                    , btn: ['确定', '取消']
                    , yes: function (index) {
                        layer.close(index);

                        var param = { id: obj.data.f_id };
                        $.ajax({
                            type: "GET"
                            , dataType: "json"
                            , url: "index.jsp?op=del"
                            , data: param
                            , success: function (res) {
                                obj.del();
                            }
                            , error: function (req, txt, err) { }
                            , complete: function (req, sta) { req = null; }
                        });

                    }
                });

            }
            , table_file_click: function (obj, table) {
                if (obj.data.f_fdTask) _this.attr.open_folder(obj.data, table);
            }
            , path_changed: function (data) {
                return;
                _this.pathCur = data;
                $.ajax({
                    type: "GET"
                    , dataType: "json"
                    , url: "index.jsp?op=path"
                    , data: { data: encodeURIComponent(JSON.stringify(data)) }
                    , success: function (res) {
                        _this.attr.nav_path.folders = res;
                        _this.attr.nav_path.folderCur = data.f_id;
                    }
                    , error: function (req, txt, err) { }
                    , complete: function (req, sta) { req = null; }
                });
            }
        }
        , table_events: {
            "up": function (obj, table) {
            }
            , "mkFolder": function (obj, table) { _this.attr.event.table_file_click(obj, table); }
            , "delete": function (obj, table) { _this.attr.event.table_del(obj, table); }
            , "rename": function (obj, table) { _this.attr.event.table_rename(obj, table); }
            , "file": function (obj, table) { _this.attr.event.table_file_click(obj, table); }
        }
        , open_folder: function (data, table) {
            return;
            layui.use(['table'], function () {
                var table = layui.table;
                table.reload('files', {
                    url: 'index.jsp?op=data&pid=' + data.f_id //
                    , page: { curr: 1 }//第一页
                });

                _this.attr.event.path_changed(data);
            });
        }
        
        , search: function (sql) {
            layui.use(['table'], function () {
                var table = layui.table;
                table.reload('files', {
                    url: 'index.jsp?op=search&where=' + encodeURIComponent(sql)
                    , page: { curr: 1 }//
                });
            });
        }
    };

    this.init = function () {
        this.data.panel.up = $("#up6-panel");
        this.data.panel.down = $("#down2-panel");

        $.each(_this.attr.ui_ents, function (i, n) {
            $(n.id).bind(n.e, n.n);
        });
        this.init_imgs();
    };
    //
}

$(function () {
    pageApp = new PageLogic();
    pageApp.init();
    pageApp.init_up6();
    pageApp.init_down2();
});