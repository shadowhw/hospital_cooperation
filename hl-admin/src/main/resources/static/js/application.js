
//次方法需要放在layui中才能使用
function showloading(t) {
    if (t) {//如果是true则显示loading
        loading = layer.load(0, {
            shade: [0.7, '#393D49'] //0.1透明度的白色背景
        });
    } else {//如果是false则关闭loading
        layer.closeAll('loading');
    }
}

//获取跳转过来的url中的参数
//name为参数名,注意name为字符
var LocString = String(window.document.location.href);
function GetQueryString(name) {
    var rs = new RegExp("(^|)" + name + "=([^&]*)(&|$)", "gi").exec(LocString), tmp;
    if (tmp = rs) {
        return decodeURI(tmp[2]);
    }
    // parameter cannot be found
    return "";
}

/**
 * 请求时同时发送当前登陆的账号id与sessionid进行校验
 * 不相同则需要重新登陆
 */
$.ajaxSetup({

    // 发送cookie
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    // 请求发送前
    beforeSend: function () {
        // 发送请求前，可以对data、url等处理
    },
    // 请求返回
    complete: function () {
        // 返回数据，根据数据调转页面等
    },
    headers: { // 默认添加请求头

    } ,
});
