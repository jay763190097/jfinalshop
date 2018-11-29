/*搜索记录相关*/
//从localStorage获取搜索时间的数组
var hisTime;
//从localStorage获取搜索内容的数组
var hisItem;
//从localStorage获取最早的1个搜索时间
var firstKey;
$("#search_1").on('keypress',function(e) {
    var keycode = e.keyCode;
    alert(keycode);
    if (keycode == "13"){
        alert(keycode);
        var value = $(this).val();
        var time = (new Date()).getTime();
        //输入的内容localStorage有记录
        if($.inArray(value, hisItem) >= 0) {
            for(var j = 0; j < localStorage.length; j++) {
                if(value == localStorage.getItem(localStorage.key(j))) {
                    localStorage.removeItem(localStorage.key(j));
                }
            }
            localStorage.setItem(time, value);
        }
        //输入的内容localStorage没有记录
        else {
            //由于限制了只能有6条记录，所以这里进行判断
            if(hisItem.length > 10) {
                firstKey = hisTime[0]
                localStorage.removeItem(firstKey);
                localStorage.setItem(time, value);
            } else {
                localStorage.setItem(time, value)
            }
        }
        init();
    }
})




function init() {
    //每次执行都把2个数组置空
    hisTime = [];
    hisItem = [];
    //模拟localStorage本来有的记录
    //localStorage.setItem("a",12333);
    //本函数内的两个for循环用到的变量
    var i = 0
    for(; i < localStorage.length; i++) {
        if(!isNaN(localStorage.key(i))) {
            hisItem.push(localStorage.getItem(localStorage.key(i)));
            hisTime.push(localStorage.key(i));
        }
    }
    i = 0;
    //执行init(),每次清空之前添加的节点
    $(".history").html("");
    for(; i < hisItem.length; i++) {
        //alert(hisItem);
        $(".history").prepend("<span  onclick='hisword()'>"+hisItem[i]+"</span>")
    }
}
init();


function hisword() {
    var searchname = $(this).val();
}

function delehistory() {
    var f = 0;
    for(; f < hisTime.length; f++) {
        localStorage.removeItem(hisTime[f]);
    }
    init();
}