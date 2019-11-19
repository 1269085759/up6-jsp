Vue.component('up6', {
    props: ['f_create','fd_create','license'],
    data: function () {
        return {
            mgr: null
            , pluginInited: false
            , ico: {file:page.path.res+'imgs/32/file.png',folder:page.path.res+'imgs/32/folder.png'}
        }
    },//事件,entLoadComplete
    methods: {
        btnFile_click: function () {
            this.mgr.openFile();
        },
        btnFolder_click: function () {
            this.mgr.openFolder();
        },
        btnPaste_click: function () {
            this.mgr.pasteFiles();
        },
        btnClear_click: function () {
            this.mgr.ClearComplete();
        },
        btnSetup_click: function () { }
    },
    mounted: function () {
        var _this = this;
        this.mgr = new HttpUploaderMgr();
        this.mgr.Config["UrlCreate"] = this.f_create;
        this.mgr.Config["UrlFdCreate"] = this.fd_create;
        this.mgr.Config["License"] = this.license;
        this.mgr.event.loadComplete = function () {
            _this.pluginInited = true;
            
            _this.$emit('load_complete');
        };
        this.mgr.event.itemSelected = function () {
            _this.$emit('item_selected');
        };
        this.mgr.event.fileAppend = function (f) {
            _this.$emit('file_append',f);
        };
        this.mgr.event.fdComplete = function (fd) {
            _this.$emit('folder_complete');
        };
        this.mgr.event.fileComplete = function (f) {
            _this.$emit('file_complete');
        };
        this.mgr.load_to("#pnl-up");
    },
    template: '<div name="files-panel" class="post-container">\
        <div class="post-item" name="file" >\
        <div class="img-box"><img name="file" :src="ico.file" /><img name="folder" :src="ico.folder" class="hide1" /></div>\
        <div class="area-l">\
            <div class="file-head">\
                <div name="name" class="name">HttpUploader程序开发.pdf</div>\
                <div name="percent" class="percent">(0%)</div>\
                <div name="size" class="size" child="1">0byte</div>\
            </div>\
            <div class="process-border"><div name="process" class="process"></div></div>\
            <div name="msg" class="msg top-space">15.3MB 20KB/S 10:02:00</div>\
        </div>\
        <div class="area-r">\
            <span class="img-btn hide1" name="post" title="继续"><img name="post" /><div>继续</div></span>\
            <span class="img-btn" name="cancel" title="取消"><img name="del" /><div>取消</div></span>\
            <span class="img-btn hide1" name="stop" title="停止"><img name="stop" /><div>停止</div></span>\
            <span class="img-btn hide1" name="del" title="删除"><img name="del" /><div>删除</div></span>\
        </div>\
    </div>\
        <div name="post_panel" class="post-container">\
            <span class="btn-t" @click="btnFile_click"><img name="postF" />上传文件</span>\
            <span class="btn-t" @click="btnFolder_click"><img name="postFd" />上传文件夹</span>\
            <span class="btn-t" @click="btnPaste_click"><img name="paste" />粘贴上传</span>\
            <span class="btn-t" @click="btnClear_click"><img name="clear" />清除已完成</span>\
            <span class="btn-t" @click="btnSetup_click" v-show="!pluginInited">安装控件</span>\
            <div name="post_body" class="files-list"></div>\
        </div>\
</div>'
});