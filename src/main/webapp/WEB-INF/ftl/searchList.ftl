<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<meta name="Description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
					<div class="row">
					  <#list model["indexHotList"] as pVo>
						<div class="col-md-4 act-col">
							<div class="act-col-box">
								<div class="list-pic">
									<a href="${pVo.productUrl}" target="_blank">
										<img
										alt="${pVo.productName}"
										src="${pVo.imgUrl}" />
									</a>
								</div>
								<div class="act-txt">
									<a href="${pVo.productUrl}" target="_blank">
										<span>${pVo.productName}</span>
									</a>
								</div>
								<div class="act-price">
									<del>
										<span class="zm-coin">¥</span>${pVo.marketPrice}
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>${pVo.productPrice}</strong>
								</div>
								<div class="act-shop shop-pic">
									<img alt="${pVo.siteName}" src="/img/${pVo.siteId}.png" />
								</div>
							</div>
						</div>
						 </#list>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	$(document).ready(function() {
        function Queryer(){
             this._maxCount = 5;
             this._count = 0;
             this._period = 1000;
        };
        Queryer.prototype.start = function(){
        	this._sid = $("input[name=sid][value]").val();
            this.query();
        };
        Queryer.prototype.stop = function(){
           if(this._myChecker){
              clearInterval(this._myChecker);
           }
        };
        Queryer.prototype.check = function(oData){
           this._count++;
           if(oData && oData.code==2){
              this.stop();
              this.addResult(oData);
              return;
           }
           var _this = this;
           if(!this._myChecker){
               this._myChecker= setInterval(function(){
                  _this.query();
               },this._period) ; 
           }
           if(this._count==this._maxCount) {
             this.stop();
           }
        };
        Queryer.prototype.query = function(){
            var _this_ =this;
            $.ajax({  
                type: "GET",  
                url: "/search/query?sid="+ _this_._sid,  
                dataType: 'json',  
                success: function(data){ 
                	_this_.check(data);
                },  
                error:function(data){  
                    _this_.stop();
                }  
            });
        };
        Queryer.prototype.addResult=function(oCallBack){
			var oData = eval('('+oCallBack.data+')');
			var oDocs = oData.docs;
			if(!oDocs){
				return;
			}
			var searchHtml = "";
			for(var i=0;i<oDocs.length;i++){
				var oDoc = oDocs[i];
				searchHtml+='<div class="col-md-4"><div class="list-col-box">\n';
				searchHtml+='<div class="list-pic">\n';
				searchHtml+='<a href="'+(oDoc.unionUrl?oDoc.unionUrl:oDoc.productUrl)+'" target="_blank"><img alt="'+oDoc.productName+'" src="'+oDoc.imgUrl+'" /></a>\n';
				searchHtml+='</div>\n';
				searchHtml+='<div class="list-txt">\n';
				searchHtml+='<a href="'+oDoc.productUrl+'" target="_blank"> <span>'+oDoc.productName+'</span></a>\n';
				searchHtml+='</div>\n';
				searchHtml+='<div class="list-price">\n';
				searchHtml+='<del><span class="zm-coin">¥</span>'+oDoc.marketPrice+'</del>\n';
				if(oDoc.productPrice && oDoc.productPrice>=0){
					searchHtml+='<strong class="list_price"><span class="zm-coin">¥</span>'+oDoc.productPrice+'</strong>\n';
				}else {
					searchHtml+='<strong class="list_price">暂无报价</strong>\n';
				}
				searchHtml+='</div>\n';
				searchHtml+='<div class="shop-pic"><img alt="'+oDoc.siteName+'" src="/img/'+oDoc.siteId+'.png" /></div>\n';
				searchHtml+='</div></div>\n';
			}
			 $("#sContainer").html(searchHtml);
			 return false;
		};
		var oQueryer = new Queryer();
		oQueryer.start();
	});
	</script>
</body>
</html>