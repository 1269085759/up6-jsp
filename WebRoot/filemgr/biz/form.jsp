<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="up6.*" %>
<%@ page import="up6.biz.*" %>
<%
out.clear();

WebBase wb = new WebBase(pageContext);
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>表单</title>
    <%= wb.paramPage() %>
    <%= wb.require(    		
            wb.m_path.get("jquery")
            , wb.m_path.get("res")+"filemgr.css"
            , wb.m_path.get("bootstrap")
            , wb.m_path.get("layerui")
            , wb.m_path.get("moment")
            , wb.m_path.get("vue")
    		) %>
    <script type="text/javascript">
        function toObj()
        {
            return $("#form1").serializeJSON();
        }
        
        function fromObj(o)
        {
            $("#form1").initForm({ jsonValue:o});
        }

        /**
         * 参数：
         *  ui      表单元素数组
         *          下拉框：{id:"",txt:"",type:"select",data:jsonobject}
         *  data    绑定到表单的值
         * 示例：
         *  {ui:[{id:"",txt:"",type:"text"}],data:{}}
         * @param obj
         */
        function initUI(obj) {
            var fm = new Vue({
                el: '#form1',
                data: {
                    ui: obj.ui
                    , time: moment().format('YYYY-MM-DD HH:mm:ss')
                }
                , methods: {
                    getVal: function (id) {
                        if (typeof (obj.data) == "undefined") return '';
                        return obj.data[id];
                    }
                }
                , created: function () {
                }
            });
        }

        $(function ()
        {
            //$("#cid").dropdown({ source: page.app, name: "name", value: "id" });
        });
    </script>
</head>
<body>
	<div class="container-fluid">
        <form id="form1" class="layui-form m-t-sm">
            <div class="form-group" v-for="i in ui">
                <label for="" class="control-label text-size-xs">{{i.txt}}</label>
                <div class="">
                    <input v-if="i.type==='text'" type="text" class="form-control input-sm" :id="i.id" :name="i.id" placeholder="" v-bind:value="getVal(i.id)"/>
                    <input v-else-if="i.type==='time'" type="text" class="form-control input-sm" :id="i.id" :name="i.id" placeholder="" :value="time" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"/>
                    <select v-else-if="i.type==='select'" class="form-control input-sm" :id="i.id" :name="i.id">
                        <option v-for="(n,k) in i.data" :value="k">{{n}}</option>
                    </select>
                    <input v-else type="text" class="form-control input-sm" :id="i.id" :name="i.id" :value="getVal(i.id)" placeholder=""/>
                </div>
            </div>
        </form>
    </div>
</body>
</html>