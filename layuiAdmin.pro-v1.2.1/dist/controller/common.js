/** layuiAdmin.pro-v1.2.1 LPPL License By http://www.layui.com/admin/ */
 ;layui.define(function(e){var i=(layui.$,layui.layer,layui.laytpl,layui.setter,layui.view,layui.admin);i.events.logout=function(){i.req({url:"./json/user/logout.json",type:"get",data:{},done:function(e){i.exit()}})},e("common",{})});
