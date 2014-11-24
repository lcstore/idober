<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<meta name="Description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<link rel="stylesheet" href="../css/bootstrap.css">
<link rel="stylesheet" href="../css/bootstrap-theme.min.css">
<link rel="stylesheet" href="../css/home.css">
<!-- 添加标签图标16x16 -->
<!-- <link rel="icon" href="/favicon.ico" type="image/x-icon"> -->
<style>
</style>
</head>
<body>
   <span>${.now}</span>
   <span>sid:${model["sid"]}</span>
   <span>result:${model["qResult"]}</span>
  	<input type="hidden" name="sid" value='${model["sid"]}'/>
	<span id="testSpan"></span>
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script
		src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script type="text/javascript">
$(document).ready(function() {
        var oQueryer = function(){
             this._maxCount = 5;
             this._count = 0;
        };
        oQueryer.start = function(){
        	this._sid = $("input[name=sid][value]").val();
            this.check();
        };
        oQueryer.stop = function(){
           if(this._myChecker){
              clearInterval(this._myChecker);
           }
        },
        oQueryer.check = function(){
           this.query();
           this._count++;
           if(this._data && this._data.code==2){
              this.stop();
              this.addResult(this._data);
              return;
           }else if(!this._myChecker){
               this._myChecker= setInterval(this.check,3000) ; 
           }
           if(this._count<this._maxCount) {
             this.stop();
           }
        };
        oQueryer.query = function(){
            $.ajax({  
                type: "GET",  
                url: "/search/query?sid="+ this._sid,  
                dataType: 'json',  
                success: function(data){ 
                	this._data = data;
                },  
                error:function(data){  
                    this.stop();
                }  
            });
        };
		oQueryer.addResult=function(data){
		    alert(data);
			var oDocs = data.docs;
			if(!oDocs){
				return;
			}
			var searchHtml = "";
			for(var i=0;i<oDocs.length;i++){
				var oDoc = oDocs[i];
				searchHtml+='<div class="col-md-4">\n';
				searchHtml+='<div class="list-pic">\n';
				searchHtml+='<a href="'+oDoc.productUrl+'" target="_blank">\n';
				searchHtml+='<img alt="'+oDoc.productName+'" src="'+oDoc.imgUrl+'" />\n';
				searchHtml+='</a></div>\n';
				searchHtml+='<div class="list-txt">\n';
				searchHtml+='<a href="'+oDoc.productUrl+'" target="_blank">\n';
				searchHtml+='<span>'+oDoc.productName+'</span>\n';
				searchHtml+='</a></div>\n';
				searchHtml+='<div class="list-price">\n';
				searchHtml+='<del><span class="zm-coin">¥</span>'+oDoc.marketPrice+'</del>\n';
				searchHtml+='<strong class="list_price"><span class="zm-coin">¥</span>'+oDoc.marketPrice+'</strong>\n';
				searchHtml+='</div>\n';
				searchHtml+='<div class="shop-pic">\n';
				searchHtml+='<img alt="siteName" src="/img/'+oDoc.productUrl+'.png" />\n';
				searchHtml+='</div></div>\n';
			}
			 $("#testSpan").html(searchHtml);
			console.warn(searchHtml);
			alert(searchHtml);
		};
		oQueryer.start();
	});
	</script>
</body>
</html>