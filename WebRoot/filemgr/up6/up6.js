/*
	版权所有 2009-2019 荆门泽优软件有限公司
	保留所有权利
	产品首页：http://www.ncmem.com/webapp/up6/index.aspx
	联系信箱：1085617561@qq.com
	联系QQ：1085617561
    版本：2.3.7
	更新记录：
		2009-11-05 创建。
		2015-07-31 优化更新进度逻辑
        2019-03-18 完善文件夹粘帖功能，完善文件夹初始化逻辑。
*/

function getRoot()
{
    for (var i = 0, l = document.scripts.length; i < l; ++i)
    {
        var src = document.scripts[i].src;
        if (src.lastIndexOf("/up6.js")!=-1)
        {
            src = src.replace("/up6.js", "/");
            return src;
        }
    }
}
var root = getRoot();
//jQuery.getScript(root+"up6.edge.js", function (data, status, xhr) { console.log("加载完毕");});

function debugMsg(m) { $("#msg").append(m); }
function HttpUploaderMgr()
{
	var _this = this;
	this.Config = {
		  "EncodeType"		: "utf-8"
		, "Company"			: "荆门泽优软件有限公司"
		, "Version"			: "2,7,118,5338"
		, "License"			: ""//
		, "Authenticate"	: ""//域验证方式：basic,ntlm
		, "AuthName"		: ""//域帐号
		, "AuthPass"		: ""//域密码
        , "CryptoType"      : "md5"//验证方式：md5,sha1,crc
		, "FileFilter"		: "*"//文件类型。所有类型：*。自定义类型：jpg,bmp,png,gif,rar,zip,7z,doc
		, "FileSizeLimit"	: "0"//自定义允许上传的文件大小，以字节为单位。0表示不限制。字节计算工具：http://www.beesky.com/newsite/bit_byte.htm
		, "FilesLimit"		: "0"//文件选择数限制。0表示不限制
		, "AllowMultiSelect": true//多选开关。1:开启多选。0:关闭多选
		, "RangeSize"		: "2097152"//文件块大小，以字节为单位。必须为64KB的倍数。推荐大小：2MB。
        , "BlockMd5"		: false//开启文件块MD5验证
		, "Debug"			: false//是否打开调式模式。true,false
		, "LogFile"			: "F:\\log.txt"//日志文件路径。需要先打开调试模式。
		, "InitDir"			: ""//初始化路径。示例：D:\\Soft
		, "AppPath"			: ""//网站虚拟目录名称。子文件夹 web
        , "Cookie"			: ""//服务器cookie
        , "Md5Folder"       : false//上传文件夹时是否计算子文件md5
        , "QueueCount"      : 3//同时上传的任务数
        , "Md5Thread"       : 10//最大为10
        , "FolderThread"    : 3//最大为10
        , "FdSizeLimit"     : 0//文件夹大小限制。0表示不限制
        , "FdChildLimit"    : 0//文件夹子元素数量限制（子文件+子文件夹）。0表示不限制
        , "ProcSaveTm"      : 60//定时保存进度。单位：秒，默认：1分钟
        , "AutoConnect"     : {opened:false,time:3000}//启动错误自动重传
		//文件夹操作相关
		, "UrlFdCreate"		: page.path.root + "db/fd_create.jsp"
		, "UrlFdComplete"	: page.path.root + "db/fd_complete.jsp"
		, "UrlFdDel"	    : page.path.root + "db/fd_del.jsp"
		//文件操作相关
		, "UrlCreate"		: page.path.root + "db/f_create.jsp"
		, "UrlPost"			: page.path.root + "db/f_post.jsp"
        , "UrlProcess"		: page.path.root + "db/f_process.jsp"
		, "UrlComplete"		: page.path.root + "db/f_complete.jsp"
		, "UrlList"			: page.path.root + "db/f_list.jsp"
		, "UrlDel"			: page.path.root + "db/f_del.jsp"
	    //x86
        , ie: {
              drop: { clsid: "0868BADD-C17E-4819-81DE-1D60E5E734A6", name: "Xproer.HttpDroper6" }
            , part: { clsid: "BA0B719E-F4B7-464b-A664-6FC02126B652", name: "Xproer.HttpPartition6" }
            , path: "http://www.ncmem.com/download/up6.3/up6.cab"
        }
	    //x64
        , ie64: {
              drop: { clsid: "7B9F1B50-A7B9-4665-A6D1-0406E643A856", name: "Xproer.HttpDroper6x64" }
            , part: { clsid: "307DE0A1-5384-4CD0-8FA8-500F0FFEA388", name: "Xproer.HttpPartition6x64" }
            , path: "http://www.ncmem.com/download/up6.3/up64.cab"
        }
        , firefox: { name: "", type: "application/npHttpUploader6", path: "http://www.ncmem.com/download/up6.3/up6.xpi" }
        , chrome: { name: "npHttpUploader6", type: "application/npHttpUploader6", path: "http://www.ncmem.com/download/up6.3/up6.crx" }
        , edge: {protocol:"up6",port:9100,visible:false}
        , exe: { path: "http://www.ncmem.com/download/up6.3/up6.exe" }
		, "SetupPath": "http://localhost:4955/demoAccess/js/setup.htm"
        , "Fields": {"uname": "test","upass": "test","uid":"0"}
        , bizData: {pid:"",pidRoot:""}
        , errCode: {
            "0": "发送数据错误"
            , "1": "接收数据错误"
            , "2": "访问本地文件错误"
            , "3": "域名未授权"
            , "4": "文件大小超过限制"
            , "5": "文件大小为0"
            , "6": "文件被占用"
            , "7": "文件夹子元素数量超过限制"
            , "8": "文件夹大小超过限制"
            , "9": "子文件大小超过限制"
            , "10": "文件夹数量超过限制"
            , "11": "服务器返回数据错误"
            , "12": "连接服务器失败"
            , "13": "请求超时"
            , "14": "上传地址错误"
            , "15": "文件块MD5不匹配"
            , "16": "读取文件夹配置信息失败"
            , "100": "服务器错误"
        }
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
            , MD5Working: 9
            , scan: 10
        }
        , ui: {
            file: "div[name='file']"
            ,folder: "div[name='folder']"
            , panel:"div[name='post_panel']"
            ,header:"div[name='post_head']"
            , list:"div[name='post_body']"
            ,icon: {
                file: page.path.res +"imgs/32/file.png"
                , folder: page.path.res +"imgs/32/folder.png"
                , stop: page.path.res +"imgs/32/stop.png"
                , del: page.path.res +"imgs/32/del.png"
                , post: page.path.res +"imgs/32/post.png"
                , postF: page.path.res +"imgs/16/file.png"
                , postFd: page.path.res +"imgs/16/folder.png"
                , paste: page.path.res +"imgs/16/paste.png"
                , clear: page.path.res +"imgs/16/clear.png"
            }
            , ele: {
                name: 'div[name="name"]'
                , ico:{
                    file: 'img[name="file"]'
                    , folder: 'img[name="folder"]'
                    , post: 'img[name="post"]'
                    , del: 'img[name="del"]'
                    , stop: 'img[name="stop"]'
                }
                , size: 'div[name="size"]'
                , path: 'div[name="path"]'
                , state: 'div[name="msg"]'
                , process: 'div[name="process"]'
                , percent: 'div[name="percent"]'
                , msg: 'div[name="msg"]'
                , btn:{
                    post: 'span[name="post"]'
                    , stop: 'span[name="stop"]'
                    , del: 'span[name="del"]'
                    , cancel: 'span[name="cancel"]'
                }
            }
        }
	};

    //biz event
	this.event = {
	      "md5Complete": function (obj/*HttpUploader对象*/, md5) { }
        , "scanComplete": function (obj/*文件夹扫描完毕，参考：FolderUploader*/) { }
        , "fileComplete": function (obj/*文件上传完毕，参考：FileUploader*/) { }
        , "fileAppend": function (/*文件和目录添加事件*/) { }
        , "fdComplete": function (obj/*文件夹上传完毕，参考：FolderUploader*/) { }
        , "queueComplete": function () {/*队列上传完毕*/ }
        , "loadComplete": function () {/*队列上传完毕*/ }
        , "addFdError": function (json) {/*添加文件夹失败*/ }
	};

	//http://www.ncmem.com/
	this.Domain = "http://" + document.location.host;
    this.working = false;
    this.websocketInited = false;

    this.FileFilter = this.Config.FileFilter.split(","); //文件过滤器
	this.filesMap = new Object(); //本地文件列表映射表
	this.QueueFiles = new Array();//文件队列，数据:id1,id2,id3
	this.QueueWait = new Array(); //等待队列，数据:id1,id2,id3
	this.QueuePost = new Array(); //上传队列，数据:id1,id2,id3
	this.arrFilesComplete = new Array(); //已上传完的文件列表
    this.filesUI = null;//上传列表面板
    this.ieParter = null;
	this.parter = null;
	this.Droper = null;
	this.uiSetupTip = null;
	this.btnSetup = null;
    //检查版本 Win32/Win64/Firefox/Chrome
	var browserName = navigator.userAgent.toLowerCase();
	this.ie = browserName.indexOf("msie") > 0;
    //IE11检查
	this.ie = this.ie ? this.ie : browserName.search(/(msie\s|trident.*rv:)([\w.]+)/) != -1;
	this.firefox = browserName.indexOf("firefox") > 0;
	this.chrome = browserName.indexOf("chrome") > 0;
	this.chrome45 = false;
	this.nat_load = false;
	this.edge_load = false;
	this.chrVer = navigator.appVersion.match(/Chrome\/(\d+)/);
	this.ffVer = navigator.userAgent.match(/Firefox\/(\d+)/);
	this.edge = navigator.userAgent.indexOf("Edge") > 0;
    this.edgeApp = new WebServerUp6(this);
    this.edgeApp.ent.on_close = function () { _this.socket_close(); };
    this.app = up6_app;
    this.app.edgeApp = this.edgeApp;
    this.app.Config = this.Config;
    this.app.ins = this;
    if (this.edge) { this.ie = this.firefox = this.chrome = this.chrome45 = false; }
    this.ui = {
        list: null
        , file: null
        , folder: null
    };

	//容器的HTML代码
	this.getHtml = function()
	{
	    //npapi
	    var com = '<embed name="ffParter" type="' + this.Config.firefox.type + '" pluginspage="' + this.Config.firefox.path + '" width="1" height="1"/>';
	    if (this.chrome45) com = "";
	    if (this.ie)
	    {
	        //拖拽组件
	        //com += '<object name="droper" classid="clsid:' + this.Config.ie.drop.clsid + '"';
	        //com += ' codebase="' + this.Config.ie.path + '#version=' + this.Config.Version + '" width="192" height="192" >';
	        //com += '</object>';
	    }
	    if (this.edge) com = '';
	    //文件夹选择控件
	    com += '<object name="parter" classid="clsid:' + this.Config.ie.part.clsid + '"';
	    com += ' codebase="' + this.Config.ie.path + '#version=' + this.Config.Version + '" width="1" height="1" ></object>';

	    return com;
	};
	
	//打开上传面板
	this.OpenPnlUpload = function()
	{
		$("#liPnlUploader").click();
	};
	//打开文件列表面板
	this.OpenPnlFiles = function()
	{
		$("#liPnlFiles").click();
	};

    //删除文件对象
    this.del_file = function (id) {
        this.filesMap = $.grep(this.filesMap, function (i, n) {
            return i == id;
        },true);
    };
	this.set_config = function (v) { jQuery.extend(this.Config, v);};
	this.open_files = function (json)
	{
	    for (var i = 0, l = json.files.length; i < l; ++i)
        {
	        this.addFileLoc(json.files[i]);
        }
        this.event.fileAppend();
	    setTimeout(function () { _this.PostFirst(); },500);
	};
	this.open_folders = function (json)
    {
        for (var i = 0, l = json.folders.length; i < l; ++i) {
            this.addFolderLoc(json.folders[i]);
        }
        this.event.fileAppend();
	    setTimeout(function () { _this.PostFirst(); }, 500);
	};
	this.paste_files = function (json)
	{
	    for (var i = 0, l = json.files.length; i < l; ++i)
	    {
	        this.addFileLoc(json.files[i]);
	    }
	};
	this.post_process = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.post_process(json);
	};
	this.post_error = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.post_error(json);
	};
	this.post_complete = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.post_complete(json);
    };
    this.post_stoped = function (json) {
        var p = this.filesMap[json.id];
        p.post_stoped(json);
    };
    this.scan_process = function (json) {
        var p = this.filesMap[json.id];
        p.scan_process(json);
    };
    this.scan_complete = function (json) {
        var p = this.filesMap[json.id];
        p.scan_complete(json);
    };
	this.md5_process = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.md5_process(json);
	};
	this.md5_complete = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.md5_complete(json);
	};
	this.md5_error = function (json)
	{
	    var p = this.filesMap[json.id];
	    p.md5_error(json);
    };
    this.load_complete = function (json)
    {
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
	this.load_complete_edge = function (json)
    {
	    this.edge_load = true;
        this.btnSetup.hide();
        _this.app.init();
    };
    this.add_folder_error = function (json) {
        this.event.addFdError(json);
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
	         if (json.name == "open_files") { _this.open_files(json); }
	    else if (json.name == "open_folders") { _this.open_folders(json); }
	    else if (json.name == "paste_files") { _this.paste_files(json); }
	    else if (json.name == "post_process") { _this.post_process(json); }
	    else if (json.name == "post_error") { _this.post_error(json); }
	    else if (json.name == "post_complete") { _this.post_complete(json); }
	    else if (json.name == "post_stoped") { _this.post_stoped(json); }
	    else if (json.name == "scan_process") { _this.scan_process(json); }
	    else if (json.name == "scan_complete") { _this.scan_complete(json); }
	    else if (json.name == "md5_process") { _this.md5_process(json); }
	    else if (json.name == "md5_complete") { _this.md5_complete(json); }
	    else if (json.name == "md5_error") { _this.md5_error(json); }
	    else if (json.name == "add_folder_error") { _this.add_folder_error(json); }
	    else if (json.name == "load_complete") { _this.load_complete(json); }
	    else if (json.name == "load_complete_edge") { _this.load_complete_edge(json); }
	    else if (json.name == "extension_complete")
        {
            setTimeout(function () {
                var param = { name: "init", config: _this.Config };
                _this.app.postMessage(param);
                 }, 1000);
        }
	};

	this.checkBrowser = function ()
	{
	    //Win64
	    if (window.navigator.platform == "Win64")
	    {
	        jQuery.extend(this.Config.ie, this.Config.ie64);
	    }
	    if (this.firefox)
	    {
	        if (!this.app.checkFF() || parseInt(this.ffVer[1]) >= 50)//仍然支持npapi
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
	    else if (this.edge)
	    {
            this.app.postMessage = this.app.postMessageEdge;
	    }
	};
    this.checkBrowser();
    //升级通知
    this.update_notice = function () {
        this.btnSetup.text("升级控件");
        this.btnSetup.css("color", "red");
        this.btnSetup.show();
    };
	//安装控件
	this.Install = function ()
	{
        if (!_this.app.Check())
		{
            _this.app.Setup();
		}
		else
		{
			$("body").empty();
			$("body").append("插件安装成功");
		}
	};

	//安全检查，在用户关闭网页时自动停止所有上传任务。
	this.SafeCheck = function(event)
	{
		$(window).bind("beforeunload", function(event)
		{
			if (_this.QueuePost.length > 0)
			{
				event.returnValue = "您还有程序正在运行，确定关闭？";
			}
		});

		$(window).bind("unload", function()
		{
            if(this.edge) _this.edgeApp.close();
			if (_this.QueuePost.length > 0)
            {
				_this.StopAll();
            }
		});
	};

    this.page_close = function () {
        if (this.edge) _this.edgeApp.close();
        if (_this.QueuePost.length > 0) {
            _this.StopAll();
        }
    };

	this.loadAuto = function ()
	{
	    var dom = $(document.body).append(this.getHtml());
	    this.initUI(dom);
	};

	//加载容器，上传面板，文件列表面板
	this.load_to = function(oid)
    {
        var tp = $(oid);
	    var dom = tp.append(this.getHtml());
	    this.initUI(tp);
	};

	this.initUI = function (dom)
	{
        var panel = dom.find(this.Config.ui.panel);
        this.ui.list = dom.find(this.Config.ui.list);
        this.ui.file = dom.find(this.Config.ui.file);
        this.ui.folder = dom.find(this.Config.ui.folder);
        this.parter  = dom.find('embed[name="ffParter"]').get(0);
        this.ieParter= dom.find('object[name="parter"]').get(0);
	    this.Droper  = dom.find('object[name="droper"]').get(0);

        //更新图标
        $.each(this.Config.ui.icon, function (i, n) {
            dom.find("img[name=\"" + i + "\"]").attr("src",n);
        });

        this.btnSetup = dom.find('span[name="btnSetup"]').click(function () {
            window.open(_this.Config.exe.path);
        });//("href",this.Config.exe.path);
	    //drag files

        panel.find('.btn-t').each(function ()
        {
            $(this).hover(function () {
                $(this).addClass("bk-hover");
            }, function () {
                $(this).removeClass("bk-hover");
            });
        });
	    //添加多个文件
	    panel.find('span[name="btnAddFiles"]').click(function () { _this.openFile(); });
	    //添加文件夹
        panel.find('span[name="btnAddFolder"]').click(function () { _this.openFolder(); });
	    //粘贴文件
        panel.find('span[name="btnPasteFile"]').click(function () { _this.pasteFiles(); });
	    //清空已完成文件
        panel.find('span[name="btnClear"]').click(function () { _this.ClearComplete(); })
            .hover(function () {
                $(this).addClass("bk-hover");
            }, function () {
                $(this).removeClass("bk-hover");
            });

	    this.SafeCheck();

        $(function ()
        {
            if (!_this.edge) {
                if (_this.ie) {
                    _this.parter = _this.ieParter;
                    if (null != _this.Droper) _this.Droper.recvMessage = _this.recvMessage;
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
	
    //清除已完成文件
	this.ClearComplete = function()
	{
	    $.each(this.arrFilesComplete, function (i, n) { n.remove(); });
	    this.arrFilesComplete.length = 0;
	};

	//上传队列是否已满
	this.IsPostQueueFull = function()
	{
		//目前只支持同时上传三个文件
	    return (_this.QueuePost.length + 1) > this.Config.QueueCount;
	};

	//添加到上传队列
	this.AppendQueuePost = function(fid)
	{
	    _this.QueuePost.push(fid);
	};

    //从上传队列删除
	this.RemoveQueuePost = function (fid) {
	    if (_this.QueuePost.length < 1) return;
	    this.QueuePost = $.grep(this.QueuePost, function (n, i) {
            return n == fid;
        }, true);
	};
	
	//添加到上传队列
	this.AppendQueue = function(fid)
	{
		_this.QueueFiles.push(fid);
	};

	//从队列中删除
	this.RemoveQueue = function(fid)
	{ 
	    if (this.QueueFiles.length < 1) return;
	    this.QueueFiles = $.grep(this.QueueFiles, function (n, i) {
            return n == fid;
        }, true);
	};
	
	//添加到未上传ID列表，(停止，出错)
	this.AppendQueueWait = function(fid)
	{
		_this.QueueWait.push(fid);
	};
	
	//从未上传ID列表删除，(上传完成)
	this.RemoveQueueWait = function(fid)
	{ 
	    if (this.QueueWait.length < 1) return;
	    this.QueueWait = $.grep(this.QueueWait, function (n, i) {
            return n == fid;
        }, true);
	};

	//停止所有上传项
	this.StopAll = function()
	{
		for (var i = 0, l = _this.QueuePost.length; i < l; ++i)
		{
			_this.filesMap[_this.QueuePost[i]].stop_manual();
		}
		_this.QueuePost.length = 0;
	};

	//传送当前队列的第一个文件
	this.PostFirst = function ()
	{
		//上传列表不为空
		if (_this.QueueFiles.length > 0)
		{
		    while (_this.QueueFiles.length > 0)
			{
				//上传队列已满
				if (_this.IsPostQueueFull()) return;
				var index = _this.QueueFiles.shift();
				_this.filesMap[index].post();
				_this.working = true;//
			}
		}
	};
	
	//启动下一个传输
	this.PostNext = function()
	{
		if (this.IsPostQueueFull()) return; //上传队列已满

		if (this.QueueFiles.length > 0)
		{
		    var index = this.QueueFiles.shift();
			var obj = this.filesMap[index];

			//空闲状态
			if (this.Config.state.Ready == obj.State)
			{
				obj.post();
			}
		} //全部上传完成
		else
		{
		    if (this.QueueFiles.length == 0//文件队列为空
                && this.QueuePost.length == 0//上传队列为空
                && this.QueueWait.length == 0)//等待队列为空
			{
		        if (this.working)
		        {
		            this.event.queueComplete();
		            this.working = false;
		        }
			}
		}
	};

	/*
	验证文件名是否存在
	参数:
	[0]:文件名称
	*/
	this.Exist = function(fn)
	{
		for (a in _this.filesMap)
		{
		    var fileSvr = _this.filesMap[a].fileSvr;
		    if (fileSvr.pathLoc == fn)
		    {
		        return true;
		    }
		}
		return false;
	};

	/*
	根据ID删除上传任务
	参数:
		fid 上传项ID。唯一标识
	*/
	this.Delete = function(fid)
	{
		//_this.RemoveQueue(fid); //从队列中删除
		_this.RemoveQueueWait(fid);//从未上传列表中删除
	};

	/*
	判断文件类型是否需要过滤
	根据文件后缀名称来判断。
	*/
	this.NeedFilter = function(fname)
    {
        if (this.Config.FileFilter == "*") return false;
        var exArr = fname.split(".");
        var ext = exArr[exArr.length - 1].toLowerCase();//扩展名
        var allowExt = this.Config.FileFilter.split(",");
        for (var i = 0, l = allowExt.length; i < l; ++i)
        {
            if (allowExt[i].toLowerCase() == ext) return false;
        }
        return true;
	};
	
	//打开文件选择对话框
	this.openFile = function()
	{
        _this.app.openFiles();
	};
	
	//打开文件夹选择对话框
	this.openFolder = function()
	{
        _this.app.openFolders();
	};

	//粘贴文件
	this.pasteFiles = function()
	{
        _this.app.pasteFiles();
	};

	this.ResumeFile = function (fileSvr)
	{
	    //本地文件名称存在
	    if (_this.Exist(fileSvr.pathLoc)) return;
	    var uper = this.addFileLoc(fileSvr);

	    setTimeout(function () { _this.PostFirst();},500);
	};

    this.find_ui = function (ui) {
        var tmp = {
            ico: {
                file: ui.find(this.Config.ui.ele.ico.file)
                , folder: ui.find(this.Config.ui.ele.ico.folder)
            }
            ,name:ui.find(this.Config.ui.ele.name)
            ,size: ui.find(this.Config.ui.ele.size)
            , path: ui.find(this.Config.ui.ele.path)
            , state: ui.find(this.Config.ui.ele.state)
            , process: ui.find(this.Config.ui.ele.process)
            , percent: ui.find(this.Config.ui.ele.percent)
            , msg: ui.find(this.Config.ui.ele.msg)
            , btn: {
                post:ui.find(this.Config.ui.ele.btn.post)
                ,stop: ui.find(this.Config.ui.ele.btn.stop)
                , del: ui.find(this.Config.ui.ele.btn.del)
                , cancel: ui.find(this.Config.ui.ele.btn.cancel)
            }
            ,div:ui
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
	//fileLoc:name,id,ext,size,length,pathLoc,md5,lenSvr,id
	this.addFileLoc = function(fileLoc)
	{
		//本地文件名称存在
        if (_this.Exist(fileLoc.pathLoc)) {
            alert("队列中已存在相同文件，请重新选择。");
            return;
        }
		//此类型为过滤类型
		if (_this.NeedFilter(fileLoc.ext)) return;

		var nameLoc = fileLoc.nameLoc;
		_this.AppendQueue(fileLoc.id);//添加到队列

		var tmp = this.ui.file.clone();//文件信息
		tmp.css("display", "block");
        this.ui.list.append(tmp);//添加文件信息
		var ui = this.find_ui(tmp);

		var upFile = new FileUploader(fileLoc, _this);
		this.filesMap[fileLoc.id] = upFile;//添加到映射表		
		
        upFile.ui = ui;

		ui.name.text(nameLoc).attr("title", nameLoc);
		ui.size.text(fileLoc.sizeLoc);
		ui.msg.text("");
		ui.percent.text("(0%)");
		ui.btn.cancel.click(function()
		{
			upFile.stop();
			upFile.remove();
			_this.PostFirst();//
		});
		ui.btn.post.click(function ()
		{
		    ui.btn.post.hide();
		    ui.btn.del.hide();
		    ui.btn.cancel.hide();
		    ui.btn.stop.show();
		    if (!_this.IsPostQueueFull())
		    {
		        upFile.post();
		    }
		    else
		    {
		        upFile.Ready();
		        //添加到队列
                _this.AppendQueue(fileLoc.id);
		    }
		});
		ui.btn.stop.click(function ()
		{
		    upFile.stop();
		});
		ui.btn.del.click(function () { upFile.remove(); });
		
		upFile.Ready(); //准备
		return upFile;
	};

	//添加文件夹,json为文件夹信息字符串
	this.addFolderLoc = function (json)
	{
	    var fdLoc = json;
		//本地文件夹存在
        if (this.Exist(fdLoc.pathLoc)) {
            alert("队列中已存在相同文件，请重新选择。");
            return;
        }
        //针对空文件夹的处理
	    if (json.files == null) jQuery.extend(fdLoc,{files:[]});
	    //if (json.lenLoc == 0) return;

		this.AppendQueue(json.id);//添加到队列

        var tmp = this.ui.file.clone();//文件夹信息
		tmp.css("display", "block");
		this.ui.list.append(tmp);//添加到上传列表面板
        var ui = this.find_ui(tmp);
        ui.ico.file.hide();
        ui.ico.folder.show();

        if (typeof (fdLoc.perSvr) != "undefined") {
            ui.percent.text("(" + fdLoc.perSvr + ")");
            ui.process.css("width", fdLoc.perSvr);
        }
		ui.msg.text("");
		//if(fdLoc.fdName != null) fdLoc.name = fdLoc.fdName;
		ui.name.text(fdLoc.nameLoc);
		ui.name.attr("title", fdLoc.nameLoc + "\n文件：" + fdLoc.files.length + "\n文件夹：" + fdLoc.foldersCount + "\n大小：" + fdLoc.sizeLoc);
		ui.size.text(fdLoc.sizeLoc);

		var fdTask = new FolderUploader( fdLoc, this);
		this.filesMap[fdLoc.id] = fdTask;//添加到映射表
		fdTask.ui = ui;
	    ui.btn.cancel.click(function()
		{
			fdTask.stop();
			fdTask.remove();
							
	    });
	    ui.btn.post.click(function ()
	    {
	        ui.btn.post.hide();
	        ui.btn.del.hide();
	        ui.btn.cancel.hide();
	        ui.btn.stop.show();

	        if (!_this.IsPostQueueFull())
	        {
	            fdTask.post();
	        }
	        else
	        {
	            fdTask.Ready();
	            _this.AppendQueue(fdTask.id);
	        }
	    });
	    ui.btn.stop.click(function ()
	    {
	        fdTask.stop();
	    });
		ui.btn.del.click(function(){fdTask.remove();});
		fdTask.Ready(); //准备
		return fdTask;
	};

	this.ResumeFolder = function (fileSvr)
	{
	    var fd = this.addFolderLoc(fileSvr);
        fd.folderInit = true;
        fd.Scaned = true;
        fd.ui.size.text(fileSvr.sizeLoc);
        fd.ui.percent.text(fileSvr.perSvr);
	    //
		if (null == fileSvr.files)
		{
		    alert("文件为空");
		    return;
		}
	};
}