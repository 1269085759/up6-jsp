/*
版权所有(C) 2009-2019 荆门泽优软件有限公司
保留所有权利
产品网站：http://www.ncmem.com/webapp/down2/index.aspx
控件下载：http://www.ncmem.com/webapp/down2/pack.aspx
示例下载：http://www.ncmem.com/webapp/down2/versions.aspx
联系邮箱：1085617561@qq.com
版本：2.4.13
更新记录：
    2009-11-05 创建
	2014-02-27 优化版本号。
    2015-08-13 优化
    2017-06-08 增加对edge的支持，完善逻辑。
    2017-07-22 优化文件夹下载，优化文件下载。
*/
function DownloaderMgr()
{
	var _this = this;
	this.Config = {
		  "Folder"		: ""
		, "Debug"		: false//调试模式
		, "LogFile"		: "f:\\log.txt"//日志文件路径。
		, "Company"		: "荆门泽优软件有限公司"
		, "Version"		: "1,2,72,60650"
		, "License"		: ""//
		, "Cookie"		: ""//
		, "ThreadCount"	: 3//并发数
        , "ThreadBlock"	: 3//文件块线程数，每个文件使用多少线程下载数据。3~10
        , "ThreadChild" : 3//子文件线程数，提供给文件夹使用。3~10
		, "FilePart"	: 5242880//文件块大小，计算器：http://www.beesky.com/newsite/bit_byte.htm
        , "FolderClear"	: true//下载前是否清空目录
        //file
        , "UrlCreate"   : page.path.root + "down2/db/f_create.jsp"
        , "UrlDel"      : page.path.root + "down2/db/f_del.jsp"
        , "UrlList"     : page.path.root + "down2/db/f_list.jsp"
        , "UrlListCmp"  : page.path.root + "down2/db/f_list_cmp.jsp"
        , "UrlUpdate"   : page.path.root + "down2/db/f_update.jsp"
        , "UrlDown"     : page.path.root + "down2/db/f_down.jsp"
	    //folder
        , "UrlFdData"   : page.path.root + "down2/db/fd_data.jsp"
        //x86
        , ie: {
              part: { clsid: "6528602B-7DF7-445A-8BA0-F6F996472569", name: "Xproer.DownloaderPartition" }
            , path: "http://www.ncmem.com/download/down2/2.4/down2.cab"
        }
        //x64
        , ie64: {
            part: { clsid: "19799DD1-7357-49de-AE5D-E7A010A3172C", name: "Xproer.DownloaderPartition64" }
            , path: "http://www.ncmem.com/download/down2/2.4/down64.cab"
        }
        , firefox: { name: "", type: "application/npHttpDown", path: "http://www.ncmem.com/download/down2/2.4/down2.xpi" }
        , chrome: { name: "npHttpDown", type: "application/npHttpDown", path: "http://www.ncmem.com/download/down2/2.4/down2.crx" }
	    //Chrome 45
        , chrome45: { name: "com.xproer.down2", path: "http://www.ncmem.com/download/down2/2.4/down2.nat.crx" }
        , exe: { path: "http://www.ncmem.com/download/down2/2.4/down2.exe" }
        , edge: {protocol:"down2",port:9700,visible:false}
        , "Fields": { "uname": "test", "upass": "test", "uid": "0" }
        , errCode: {
            "0": "发送数据错误"
            , "1": "接收数据错误"
            , "2": "访问本地文件错误"
            , "3": "域名未授权"
            , "4": "文件大小超过限制"
            , "5": "地址为空"
            , "6": "配置文件不存在"
            , "7": "本地目录不存在"
            , "8": "查询文件信息失败"
            , "9": "子文件大小超过限制"
            , "10": "子文件数量超过限制"
            , "11":"连接服务器失败"
            , "12":"地址错误"
            , "13":"服务器错误"}
        , state: {
            Ready: 0,
            Posting: 1,
            Stop: 2,
            Error: 3,
            GetNewID: 4,
            Complete: 5,
            WaitContinueUpload: 6,
            None: 7,
            Waiting: 8
        }
        , ui: {
            file: 'div[name="file"]'
            ,panel: 'div[name="down_panel"]'
            , list: 'div[name="down_body"]'
            , ico: {
                file: page.path.res + "imgs/32/file.png"
                , folder: page.path.res + "imgs/32/folder.png"
                , stop: page.path.res + "imgs/16/stop.png"
                , del: page.path.res + "imgs/16/del.png"
                , post: page.path.res + "imgs/16/post.png"
                , postF: page.path.res + "imgs/16/file.png"
                , postFd: page.path.res + "imgs/16/folder.png"
                , paste: page.path.res + "imgs/16/paste.png"
                , clear: page.path.res + "imgs/16/clear.png"
                , config: page.path.res + "imgs/16/config.png"
                , "start-all": page.path.res + "imgs/16/start.png"
                , "stop-all": page.path.res + "imgs/16/stop.png"
            }
            , header: 'div[name="down_header"]'
            , toolbar: 'div[name="down_toolbar"]'
            , footer: 'div[name="down_footer"]'
            , btn: {
                setup: 'span[name="btnSetup"]'
                , setFolder: "span[name='btnSetFolder']"
                , clear: 'span[name="btnClear"]'
            }
            , ele: {
                ico: {
                    file: 'img[name="file"]'
                    , fd: 'img[name="folder"]'
                }
                ,name: 'div[name="name"]'
                ,size: 'div[name="size"]'
                , process: 'div[name="process"]'
                , percent: 'div[name="percent"]'
                , msg: 'div[name="msg"]'
                , btn: {
                    cancel: 'span[name="cancel"]'
                    , stop: 'span[name="stop"]'
                    , down: 'span[name="down"]'
                    , del: 'span[name="del"]'
                    , open: 'span[name="open"]'
                    ,openFd:'span[name="open-fd"]'
                }
            }
        }
	};

    this.event = {
          downComplete: function (obj) { }
        , downError: function (obj, err) { }
        , queueComplete: function () { }
        , loadComplete: function () { }
	};

    this.websocketInited = false;
	var browserName = navigator.userAgent.toLowerCase();
	this.ie = browserName.indexOf("msie") > 0;
	this.ie = this.ie ? this.ie : browserName.search(/(msie\s|trident.*rv:)([\w.]+)/) != -1;
	this.firefox = browserName.indexOf("firefox") > 0;
	this.chrome = browserName.indexOf("chrome") > 0;
	this.chrome45 = false;
	this.nat_load = false;
    this.chrVer = navigator.appVersion.match(/Chrome\/(\d+)/);
    this.edge = navigator.userAgent.indexOf("Edge") > 0;
    this.edgeApp = new WebServerDown2(this);
    this.edgeApp.ent.on_close = function () { _this.socket_close(); };
    this.app = down2_app;
    this.app.edgeApp = this.edgeApp;
    this.app.Config = this.Config;
    this.app.ins = this;
    if (this.edge) { this.ie = this.firefox = this.chrome = this.chrome45 = false; }
	
	this.idCount = 1; 	//上传项总数，只累加
	this.queueCount = 0;//队列总数
	this.filesMap = new Object(); //本地文件列表映射表
	this.filesCmp = new Array();//已完成列表
	this.filesUrl = new Array();
    this.queueWait = new Array(); //等待队列，数据:id1,id2,id3
    this.queueWork = new Array(); //正在上传的队列，数据:id1,id2,id3
	this.parter = null;
	this.btnSetup = null;//安装控件的按钮
    this.working = false;
    this.allStoped = false;//
    this.ui = { file: null ,list:null,panel:null,header:null,footer:null};

	this.getHtml = function()
	{ 
	    //自动安装CAB
        var html = '<embed name="ffParter" type="' + this.Config.firefox.type + '" pluginspage="' + this.Config.firefox.path + '" width="1" height="1"/>';
        if (this.chrome45) html = "";
		//var acx = '<div style="display:none">';
		/*
			IE静态加载代码：
			<object id="objDownloader" classid="clsid:E94D2BA0-37F4-4978-B9B9-A4F548300E48" codebase="http://www.qq.com/HttpDownloader.cab#version=1,2,22,65068" width="1" height="1" ></object>
			<object id="objPartition" classid="clsid:6528602B-7DF7-445A-8BA0-F6F996472569" codebase="http://www.qq.com/HttpDownloader.cab#version=1,2,22,65068" width="1" height="1" ></object>
		*/
        html += '<object name="parter" classid="clsid:' + this.Config.ie.part.clsid + '"';
        html += ' codebase="' + this.Config.ie.path + '#version=' + _this.Config["Version"] + '" width="1" height="1" ></object>';
        if (this.edge) html = '';
	    return html;
	};

    this.to_params= function (param, key) {
        var paramStr = "";
        if (param instanceof String || param instanceof Number || param instanceof Boolean) {
            paramStr += "&" + key + "=" + encodeURIComponent(param);
        } else {
            $.each(param, function (i) {
                var k = key == null ? i : key + (param instanceof Array ? "[" + i + "]" : "." + i);
                paramStr += '&' + _this.to_params(this, k);
            });
        }
        return paramStr.substr(1);
    };
	this.set_config = function (v) { jQuery.extend(this.Config, v); };
	this.clearComplete = function ()
	{
	    $.each(this.filesCmp, function (i,n)
	    {
	        n.remove();
	    });
	    this.filesCmp.length = 0;
    };
    this.find_ui = function (o) {
        var tmp = {
            ico: {
                file:o.find(this.Config.ui.ele.ico.file)
                ,fd: o.find(this.Config.ui.ele.ico.fd)
            }
            ,name:o.find(this.Config.ui.ele.name)
            ,size:o.find(this.Config.ui.ele.size)
            , process: o.find(this.Config.ui.ele.process)
            , percent: o.find(this.Config.ui.ele.percent)
            , msg: o.find(this.Config.ui.ele.msg)
            , btn: {
                cancel:o.find(this.Config.ui.ele.btn.cancel)
                ,stop: o.find(this.Config.ui.ele.btn.stop)
                ,down: o.find(this.Config.ui.ele.btn.down)
                ,del: o.find(this.Config.ui.ele.btn.del)
                ,open: o.find(this.Config.ui.ele.btn.open)
                , openFd: o.find(this.Config.ui.ele.btn.openFd)
            }
            ,div:o
        };
        $.each(tmp.btn, function (i, n) {
            $(n).hover(function () {
                $(this).addClass("bk-hover");
            }, function () {
                $(this).removeClass("bk-hover");
            });
        });
        return tmp;
    };
	this.add_ui = function (f)
	{
	    //存在相同项
        if (this.exist_url(f.nameLoc)) {
            alert("已存在相同名称的任务：" + f.nameLoc);
            return null;
        }
        this.filesUrl.push(f.nameLoc);

	    var _this = this;

        var tmp = this.ui.file.clone();
	    tmp.css("display", "block");
	    this.ui.list.append(tmp);
        var ui = this.find_ui(tmp);

        var downer;
        if (f.fdTask) { downer = new FdDownloader(f, this); }
	    else { downer = new FileDownloader(f,this);}
	    this.filesMap[f.id] = downer;//
        downer.ui = ui;

	    ui.name.text(f.nameLoc);
	    ui.name.attr("title", f.nameLoc);
	    ui.msg.text("");
	    ui.size.text(f.sizeSvr);
	    ui.percent.text("("+f.perLoc+")");
        ui.process.width(f.perLoc);

        downer.ready(); //准备
    };
	this.resume_folder = function (fdSvr)
    {
        var fd = jQuery.extend({}, fdSvr, { svrInit: true });
	    this.add_ui(fd);
	    //if (null == obj) return;
        //obj.svr_inited = true;

	    //return obj;
    };
    this.resume_file = function (fSvr) {
        var f = jQuery.extend({}, fSvr, { svrInit: true });
        this.add_ui(f);
        //if (null == obj) return;
        //obj.svr_inited = true;

        //return obj;
    };
	this.init_file = function (f)
    {
        this.app.initFile(f);
    };
    this.init_folder = function (f) {
        this.app.initFolder(jQuery.extend({},this.Config,f));
    };
    this.init_file_cmp = function (json)
    {
        var p = this.filesMap[json.id];
        p.init_complete(json);
    };
    this.add_file = function (f) {
        var obj = this.add_ui(f);
    };
    this.add_folder = function (f)
	{
	    var obj = this.add_ui(f);
	};
	this.exist_url = function (url)
	{
	    var v = false;
	    for (var i = 0, l = this.filesUrl.length; i < l; ++i)
	    {
	        v = this.filesUrl[i] == url;
	        if (v) break;
	    }
	    return v;
	};
    this.remove_url = function (url) {
        this.filesUrl = $.grep(this.filesUrl, function (n, i) {
            return n == url;
        },true);
    };
    this.remove_wait = function (id) {
        if (this.queueWait.length == 0) return;
        this.queueWait = $.grep(this.queueWait, function (n, i){
            return n == id;
        }, true);
    };
	this.open_folder = function (json)
	{
	    this.app.openFolder();
	};
    this.down_file = function (json) { };
    //队列控制
    this.work_full = function () { return (this.queueWork.length + 1) > this.Config.ThreadCount; };
    this.add_wait = function (id) { this.queueWait.push(id); };
    this.add_work = function (id) { this.queueWork.push(id); };
    this.del_work = function (id) {
        if (_this.queueWork.length < 1) return;
        this.queueWork = $.grep(this.queueWork, function (n, i){
            return n = id;
        }, true);
    };
    this.down_next = function () {
        if (_this.allStoped) return;
        if (_this.work_full()) return;
        if (_this.queueWait.length < 1) return;
        var f_id = _this.queueWait.shift();
        var f = _this.filesMap[f_id];
        f.down();
    };

	this.init_end = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.init_end(json);
	};
	this.down_begin = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_begin(json);
	};
	this.down_process = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_process(json);
	};
	this.down_error = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_error(json);
    };
    this.down_open_folder = function (json) {
        //用户选择的路径
        //json.path
        this.Config["Folder"] = json.path;
    };
	this.down_recv_size = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_recv_size(json);
	};
	this.down_recv_name = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_recv_name(json);
	};
	this.down_complete = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_complete(json);
	};
	this.down_stoped = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.down_stoped(json);
	};
    this.start_queue = function ()
    {
        this.allStoped = false;
        this.down_next();
        this.down_next();
        this.down_next();
    };
	this.stop_queue = function (json)
    {
        this.allStoped = true;
        $.each(this.queueWork, function (i, n) {
            _this.filesMap[n].stop();
        });        
	};
	this.queue_begin = function (json) { this.working = true;};
	this.queue_end = function (json) { this.working = false;};
    this.load_complete = function (json) {
        if (this.websocketInited) return;
        this.websocketInited = true;

        this.btnSetup.hide();
        var needUpdate = true;
        if (typeof (json.version) != "undefined") {
            if (json.version == this.Config.Version) {
                needUpdate = false;
            }
        }
        if (needUpdate) this.update_notice();
        else { this.btnSetup.hide(); }
        this.event.loadComplete();
    };
    this.load_complete_edge = function (json) {
        this.edge_load = true;
        this.btnSetup.hide();
        _this.app.init();
    };
    this.socket_close = function () {
        while (_this.QueuePost.length > 0)
        {
            _this.filesMap[_this.QueuePost[0]].post_stoped(null);
        }
		_this.QueuePost.length = 0;
    };
	this.recvMessage = function (str)
	{
	    var json = JSON.parse(str);
	         if (json.name == "init_file_cmp") { _this.init_file_cmp(json); }
	    else if (json.name == "open_folder") { _this.down_open_folder(json); }
	    else if (json.name == "down_recv_size") { _this.down_recv_size(json); }
	    else if (json.name == "down_recv_name") { _this.down_recv_name(json); }
	    else if (json.name == "init_end") { _this.init_end(json); }
	    else if (json.name == "add_file") { _this.add_file(json); }
	    else if (json.name == "add_folder") { _this.add_folder(json); }
	    else if (json.name == "down_begin") { _this.down_begin(json); }
	    else if (json.name == "down_process") { _this.down_process(json); }
	    else if (json.name == "down_error") { _this.down_error(json); }
	    else if (json.name == "down_complete") { _this.down_complete(json); }
	    else if (json.name == "down_stoped") { _this.down_stoped(json); }
	    else if (json.name == "queue_complete") { _this.event.queueComplete(); }
	    else if (json.name == "queue_begin") { _this.queue_begin(json); }
	    else if (json.name == "queue_end") { _this.queue_end(json); }
	    else if (json.name == "load_complete") { _this.load_complete(json); }
	    else if (json.name == "load_complete_edge") { _this.load_complete_edge(json); }
    };

    this.checkVersion = function ()
	{
	    //Win64
	    if (window.navigator.platform == "Win64")
	    {
	        jQuery.extend(this.Config.ie, this.Config.ie64);
	    }
	    if (this.firefox)
        {
            if (!this.app.checkFF())//仍然支持npapi
            {
                this.edge = true;
                this.app.postMessage = this.app.postMessageEdge;
                this.edgeApp.run = this.edgeApp.runChr;
            }
	    }
	    else if (this.chrome)
	    {
	        this.app.check = this.app.checkFF;
	        jQuery.extend(this.Config.firefox, this.Config.chrome);
	        //44+版本使用Native Message
	        if (parseInt(this.chrVer[1]) >= 44)
	        {
                _this.firefox = true;
                if (!this.app.checkFF())//仍然支持npapi
                {
                    this.edge = true;
                    this.app.postMessage = this.app.postMessageEdge;
                    this.edgeApp.run = this.edgeApp.runChr;
                }
	        }
        }
        else if (this.edge) {
            this.app.postMessage = this.app.postMessageEdge;
        }
	};
    this.checkVersion();

    //升级通知
    this.update_notice = function () {
        this.btnSetup.text("升级控件");
        this.btnSetup.css("color", "red");
        this.btnSetup.show();
    };

	//安全检查，在用户关闭网页时自动停止所有上传任务。
	this.safeCheck = function()
    {
        $(window).bind("beforeunload", function (event)
        {
            if (_this.queueWork.length > 0)
            {
				event.returnValue = "您还有程序正在运行，确定关闭？";
            }
        });
        
		$(window).bind("unload", function()
        {
            if(this.edge) _this.edgeApp.close();
            if (_this.queueWork.length > 0)
            {
                _this.stop_queue();
            }
        });
    };
    this.page_close = function () {
        if (this.edge) _this.edgeApp.close();
        if (_this.queueWork.length > 0) {
            _this.stop_queue();
        }
    };
	
	this.loadAuto = function()
	{
	    var html = this.getHtml();
	    var ui = $(document.body).append(html);
	    this.initUI(ui);
	};
	//加截到指定dom
	this.loadTo = function(id)
	{
	    var obj = $(id);
	    var html = this.getHtml();
	    var ui = obj.append(html);
	    this.initUI(obj);
	};
	this.initUI = function (ui/*jquery obj*/)
	{
	    this.down_panel = ui.find(this.Config.ui.panel);
	    this.btnSetup = ui.find(this.Config.ui.btn.setup);
        this.ui.file = ui.find(this.Config.ui.file);
        this.parter = ui.find('embed[name="ffParter"]').get(0);
        this.ieParter = ui.find('object[name="parter"]').get(0);

	    var down_body = ui.find(this.Config.ui.list);
	    var down_head = ui.find(this.Config.ui.header);
	    var post_bar = ui.find(this.Config.ui.toolbar);
	    var post_foot = ui.find(this.Config.ui.footer);
	    down_body.height(this.down_panel.height() - post_bar.height() - down_head.height() - post_foot.outerHeight() - 1);

	    var btnSetFolder = ui.find(this.Config.ui.btn.setFolder);
        this.ui.list = down_body;
        //设置图标
        $.each(this.Config.ui.ico, function (i, n) {
            ui.find('img[name="' + i + '"]').attr("src", n);
        });

	    //设置下载文件夹
        btnSetFolder.click(function () { _this.open_folder(); });
        this.btnSetup.click(function () { window.open(_this.Config.exe.path); });
		//清除已完成
        ui.find(this.Config.ui.btn.clear).click(function () { _this.clearComplete(); });
		ui.find('span[name="btnStart"]').click(function () { _this.start_queue(); });
        ui.find('span[name="btnStop"]').click(function () { _this.stop_queue(); });

        this.safeCheck();//

        $(function () {
            if (!_this.edge) {
                if (_this.ie) {
                    _this.parter = _this.ieParter;
                }
                _this.parter.recvMessage = _this.recvMessage;
            }

            if (_this.edge) {
                _this.edgeApp.connect();
            }
            else {
                _this.app.init();
            }
        });
	};

    //加载未未完成列表
	this.loadFiles = function ()
	{
	    //var param = jQuery.extend({}, this.Config.Fields, { time: new Date().getTime()});
	    //$.ajax({
	    //    type: "GET"
     //       , dataType: 'jsonp'
     //       , jsonp: "callback" //自定义的jsonp回调函数名称，默认为jQuery自动生成的随机函数名
     //       , url: _this.Config["UrlList"]
     //       , data: param
     //       , success: function (msg)
     //       {
     //           if (msg.value == null) return;
     //           var files = JSON.parse(decodeURIComponent(msg.value));

     //           for (var i = 0, l = files.length; i < l; ++i)
     //           {
     //               if (files[i].fdTask)
     //               { _this.resume_folder(files[i]); }
     //               else { _this.resume_file(files[i]); }
     //           }
     //       }
     //       , error: function (req, txt, err) { alert("加载文件列表失败！" +req.responseText); }
     //       , complete: function (req, sta) {req = null;}
	    //});
	};
}
