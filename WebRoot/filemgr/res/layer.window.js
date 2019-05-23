/**
 * layer弹出窗口
 * 用法：
 * LayerWindow({
 * title:""
 * ,w:""
 * ,h:""
 * ,url:""
 * ,load_complete:function(ifm){}}
 * ,btn_ok_click:function(ifm){}
 * )
 * @param {json} obj
 */
function LayerWindow(obj)
{
    var _this = this;
    this.sett = {
        title: ""
        , w: "600px"
        , h: "500px"
        , url: ""
        , load_complete: function (ifm) { }
        , btn_ok_click: function (ifm) { }
        , btn_ok: "添加"
    };

    $.extend(this.sett, obj);

    layer.open({
        type: 2,
        title: _this.sett.title,
        area: [_this.sett.w, _this.sett.h],
        btn: [_this.sett.btn_ok, '关闭'],
        content: _this.sett.url,
        success: function (layero, index) {
            var body = layer.getChildFrame('body', index);
            var ifm = window[layero.find('iframe')[0]['name']];
            _this.sett.load_complete(ifm);
        },
        yes: function (index, layero) {
            var ifm = window[layero.find('iframe')[0]['name']];
            var body = layer.getChildFrame('body', index);
            _this.sett.btn_ok_click(ifm);
            layer.close(index);//关闭窗口
        },
        btn2: function (index, layero) { }
    });
}