Vue.component('down2', {
    props: ['fd_data','license'],
    data: function () {
        return {
            mgr: null
            , pluginInited: false
            , ico: { file: page.path.res + 'imgs/32/file.png', folder: page.path.res + 'imgs/32/folder.png' }
        }
    },
    methods: {
        check_path: function () {
            if (this.mgr.Config["Folder"] == "") {
                    this.mgr.open_folder();
                return false;
            }
            return true;
        }
    },
    mounted: function () {
        var _this = this;
        this.mgr = new DownloaderMgr();
        this.mgr.Config["UrlFdData"] = this.fd_data;
        this.mgr.Config["License"] = this.license;
        this.mgr.event.loadComplete = function () {
            _this.pluginInited = true;
            _this.$emit('load_complete');
        };
        this.mgr.event.sameFileExist = function (n) {
            _this.$emit('same_file_exist',n);
        };
        this.mgr.loadTo("#pnl-down");
    },
    template: '<div>\
        <div class="down-item d-hide" name="file" >\
            <div class="img-box">\
                <img name="file" :src="ico.file" /><img class="d-hide" name="folder" :src="ico.folder" />\
            </div>\
            <div class="area-l">\
                <div name="name" class="name">HttpUploader程序开发.pdf</div>\
                <div name="percent" class="percent">(35%)</div>\
                <div name="size" class="size" child="1">1000.23MB</div>\
                <div class="process-border"><div name="process" class="process"></div></div>\
                <div name="msg" class="msg top-space">15.3MB 20KB/S 10:02:00</div>\
            </div>\
            <div class="area-r">\
                <span class="btn-box d-hide" name="down" title="继续"><div>继续</div></span>\
                <span class="btn-box d-hide" name="stop" title="停止"><div>停止</div></span>\
                <span class="btn-box" name="cancel" title="取消">取消</span>\
                <span class="btn-box d-hide" name="del" title="删除"><div>删除</div></span>\
                <span class="btn-box d-hide" name="open" title="打开"><div>打开</div></span>\
                <span class="btn-box d-hide" name="open-fd" title="文件夹"><div>文件夹</div></span>\
            </div>\
        </div>\
        <div class="down-panel" name="down_panel">\
            <span class="btn-bk" name="btnSetFolder"><div><img name="config" />设置下载目录</div></span>\
            <span class="btn-bk" name="btnStart"><img name="start-all" />全部下载</span>\
            <span class="btn-bk" name="btnStop"><img name="stop-all" />全部停止</span>\
            <span class="btn-bk" name="btnClear"><img name="clear" />清除已完成文件</span>\
            <span class="btn-bk" name="btnSetup" v-show="!pluginInited">安装控件</span>\
            <div class="content" name="down_content">\
                <div name="down_body" class="down-view"></div>\
            </div>\
        </div>\
</div>'
});