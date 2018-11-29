



    (function(){
        var InterValObj; //timer变量，控制时间
        var count = 30; //间隔函数，1秒执行
        var curCount;//当前剩余秒数
        $("#getCode").on('click', getsms);

        function getsms() {
            var phone  = $("#tel_1").val();
            $.post("http://114.55.93.111:8080/jfinalshop-4.0-api/api/account/sendSMS",{"username":phone},function (data) {
                if (data.code == "1"){
                    alert("发送短信成功！");
                    curCount = count;
                    $("#getCode").unbind("click",getsms)
                    $("#getCode").html(curCount + "秒后可重新发送");
                   InterValObj = window.setInterval(SetRemainTime, 1000);
                }
            });
        }
        function SetRemainTime() {
            if (curCount == 0) {
                window.clearInterval(InterValObj);//停止计时器
                /* $("#getCode").removeAttr("disabled");//启用按钮*/
                $("#getCode").bind("click",getsms);
                $("#getCode").html("重新发送验证码");
            }
            else {
                curCount--;
                $("#getCode").html(curCount + "秒后可重新发送");
            }
        }

    })();







