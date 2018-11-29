var page = 1;
var gid = 'a';
var pathName=window.document.location.pathname;
var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
//var resource="BW";
//var resource="fn";
//var resource="ft";
//var resource="bd";
//var resource="hc";
//var resource="bk";
//var resource="GL";
//var resource="bc";
//var resource="WC";
// var resource="SH";
//var resource="RD";
//var resource="AC";
//var resource="OA";
//var resource="CS";
// var resource="SHOE";
var resource="GM";
/*loadProperties();
function loadProperties(){
    $.i18n.properties({// 加载资浏览器语言对应的资源文件
        name:'i18n_zh_CN', // 资源文件名称
        path:'resources', // 资源文件路径
        mode:'both', // 用 Map 的方式使用资源文件中的值
        callback: function() {// 加载成功后设置显示内容
            //resource= ($.i18n.prop('resource'));
        }
    });
}
console.log(resource);*/
findGrade();
function findGrade(){
    $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/productCategory/findGrade",{"grade":"0","channel":resource },function (data) {
        $.each(data.datum,function(i, item){
            var html = "<a onclick='findCOGoods("+item.id+")'>"+item.name+"</a>";
            $("#menu_1").append(html);
        })
    });
}
findGoods();
function findGoods(){
    $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/index/hotGoods",{"channel":resource},function (data) {
        $.each(data.data,function(i, item){
            //alert(item);
            var html = "<li class='list-detail'><a href='"+projectName+"/goods/detail?id="+item.id+"'><img src='"+"http://114.55.93.111:8080/jfinalshop-4.0-web"+item.image+"' alt='"+item.name+"' class='shoes-img'></a>"
                +"<div class='shoes-name'> <a href='"+projectName+"/goods/detail?id="+item.id+"'>"+item.name+"</a></div>"
                +"<div class='shoes-price'>¥ "+item.price+"<span>¥ "+item.price+"</span></div></li>";
            $("#ul_1").append(html);
        })
    });
}

function findCOGoods(id){
    $("#gr").removeClass("active");
    $(".list-detail").remove();
    $(".dis-played").remove();
    var html ="<input class='dis-none dis-played' id='dis_1' value='"+id+"'>";
    $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/productCategory/findGoods",{"id":id,"pageNumber":1,"pageSize":"6","channel":resource},function (data) {
        $.each(data.datum.list,function(i, item){
            html+= "<li class='list-detail' id='list-1'><a href='"+projectName+"/goods/detail?id="+item.id+"'><img src='"+"http://114.55.93.111:8080/jfinalshop-4.0-web"+item.image+"' alt='"+item.name+"' class='shoes-img'></a>"
                +"<div class='shoes-name'> <a href='"+projectName+"/goods/detail?id="+item.id+"'>"+item.name+"</a></div>"
                +"<div class='shoes-price'>¥ "+item.price+"<span>¥ "+item.price+"</span></div></li>";
        })
        $("#ul_1").append(html);
        page = 1;
    });

}

function loadon(id,page) {
    page++;
    $("#dv3").remove();
    findCOGoods(id,page);
}

$(function() {
    var timer, str = "";
    $(window).on('touchmove', function() {
        clearTimeout(timer);
        timer = setTimeout(function() {
            if ($(window).height() + $(window).scrollTop() >= $("body").height()) {
                var gradeid = $("#dis_1");
                gid = gradeid.val();
                console.log(gid);
                if(gid !='a'){
                    page++;
                    $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/productCategory/findGoods",{"id":gid,"pageNumber":page,"pageSize":"6","channel":resource},function (data) {
                        attr = data.datum.list;
                        console.log(attr);
                        $.each(attr,function(i, item){
                            var html= "<li class='list-detail' id='list-1'><a href='"+projectName+"/goods/detail?id="+item.id+"'><img src='"+"http://114.55.93.111:8080/jfinalshop-4.0-web"+item.image+"' alt='"+item.name+"' class='shoes-img'></a>"
                                +"<div class='shoes-name'> <a href='"+projectName+"/goods/detail?id="+item.id+"'>"+item.name+"</a></div>"
                                +"<div class='shoes-price'>¥ "+item.price+"<span>¥ "+item.price+"</span></div></li>";
                            $("#ul_1").append(html);
                        })
                    });
                }

            }}, 300)
    })
});


/*
function scrollFn() {
    //真实内容的高度
    var pageHeight = Math.max(document.body.scrollHeight, document.body.offsetHeight);
    //视窗的高度
    var viewportHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 0;
    //隐藏的高度
    var scrollHeight = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    if (pageHeight - viewportHeight - scrollHeight < 10) {  //如果满足触发条件，执行
        var gradeid = $("#dis_1");
        var gid = gradeid.val();
        if(gid!=null){
            page++;
            $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/productCategory/findGoods",{"id":gid,"pageNumber":page,"pageSize":"6"},function (data) {
                attr = data.datum.list;
                console.log(attr);
                $.each(attr,function(i, item){
                    var html= "<li class='list-detail' id='list-1'><a href='http://localhost:8080/jfinalshop-4.0-h5/goods/detail?id="+item.id+"'><img src='"+"http://114.55.93.111:8080/jfinalshop-4.0-web"+item.image+"' alt='"+item.name+"' class='shoes-img'></a>"
                        +"<div class='shoes-name'> <a href='goods/detail?id="+item.id+"'>"+item.name+"</a></div>"
                        +"<div class='shoes-price'>¥ "+item.price+"<span>¥ "+item.price+"</span></div></li>";
                    $("#ul_1").append(html);
                })
            });
        }
    }
}*/

/*$(window).bind("scroll", scrollFn);*/
