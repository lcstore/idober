searchList.ftl<!DOCTYPE html>
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
 .input-group-price{
   width:60%
 }
</style>
</head>
<body>
	<header id="header">
		<nav role="navigation"
			class="navbar navbar-inverse navbar-default navbar-fixed-top"
			id="header_outer">
			<div class="navbar-header">
				<button data-target=".navbar-header-collapse" data-toggle="collapse"
					class="navbar-toggle" type="button">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a title="Lezomao" href="/" class="navbar-brand" id="header_logo">Lezomao</a>
			</div>
			<div class="collapse navbar-collapse navbar-header-collapse">
				<div class="row">
					<div class="col-md-3">
						<ul class="nav navbar-nav" id="header_menu">
							<li class="active"><a href="/">首页</a></li>
							<li><a href="#">爱抢眼</a></li>
							<li><a href="#">购实惠</a></li>
						</ul>
					</div>
					<div class="col-md-4">
						<form class="navbar-form navbar-left" role="search">
							<div class="form-group">
						       <#if model["qWord"] = null> 
								  <input type="text" size="40" class="form-control"
									placeholder="Search" value="牛奶" name="q">
								<#else> 
								  <input type="text" size="40" class="form-control"
									placeholder="Search" value="${model["qWord"]}" name="q">
								</#if>
								<button type="submit" class="btn btn-success">Go</button>
							</div>
						</form>
					</div>
					<div class="col-md-3">
						<ul id="header_me" class="nav navbar-nav navbar-right ng-scope">
							<li class="divider-vertical hidden-xs"></li>
							<li><a href="#" class="mr_15">登录</a></li>
							<li><a href="#">注册</a></li>
						</ul>
					</div>
				</div>
			</div>

		</nav>
	</header>
	<div class="main data-box">
	    <input type="hidden" name="codeList" value="${model["codeList"]}"/>
		<div class="act">
			<div class="jumbotron actbox-padding">
				<form class="form-inline" role="form" id="queryFrm">
				  <div class="form-group">
				     <div class="input-group">
				     	<span class="input-group-addon">相似编码:</span>
				      	<input type="text" class="form-control" name="sCode" value="1404568054795">
				 	 </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <span class="input-group-addon">商品编码:</span>
				      <input type="text" class="form-control" name="pCode">
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <span class="input-group-addon">价格范围:[</span>
				      <input type="text" class="form-control" name="fromPrice">
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <input type="text" class="form-control" name="toPrice">
				      <span class="input-group-addon">]</span>
				    </div>
				  </div>
				  <button type="button" class="btn btn-default" id="queryBtn">查询</button>
				</form>
				<div class="container-fluid">
					<div class="row">
						<table class='table table-bordered table-striped' id="similarTb">
						 <thead>
							<tr>
								<th>ID</th>
								<th>SIMILAR_CODE</th>
								<th>SITE_ID</th>
								<th>PRODUCT_CODE</th>
								<th>PRODUCT_NAME</th>
								<th>IMG_URL</th>
								<th>PRODUCT_PRICE</th>
								<th>BAR_CODE</th>
								<th>CREATE_TIME</th>
								<th>UPDATE_TIME</th>
								<th>操作</th>
							</tr>
						 </thead>
						 <tbody id="similarData">
						 </tbody>
						</table>
					</div>
					<div id="pageNav"></div>
				</div>
			</div>
		</div>
	</div>
	<footer class="hidden-xs" id="footer-outer">
		<div class="columns">
		  <script type="text/javascript">
		var _bdhmProtocol = (("https:" == document.location.protocol) ? " https://" : " http://");
		document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3Fa9ae2d488204441bbdd903e86109496b' type='text/javascript'%3E%3C/script%3E"));
		</script>
		</div>
		<div class="text-center">
			Copyright &copy; 2014 LEZOMAO.COM 版权所有
			<p>鄂ICP备14009865号</p>
		</div>
	</footer>
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script
		src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
		
		<script type="text/javascript">
		// 对Date的扩展，将 Date 转化为指定格式的String   
		    // 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
		    // 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
		    // 例子：   
		    // (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
		    // (new Date()).format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
		    Date.prototype.format = function(fmt)   
		    { //author: meizz   
		      var o = {   
		        "M+" : this.getMonth()+1,                 //月份   
		        "d+" : this.getDate(),                    //日   
		        "h+" : this.getHours(),                   //小时   
		        "m+" : this.getMinutes(),                 //分   
		        "s+" : this.getSeconds(),                 //秒   
		        "q+" : Math.floor((this.getMonth()+3)/3), //季度   
		        "S"  : this.getMilliseconds()             //毫秒   
		      };   
		      if(/(y+)/.test(fmt))   
		        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
		      for(var k in o)   
		        if(new RegExp("("+ k +")").test(fmt))   
		      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
		      return fmt;   
		    }  
		    
		$(document).ready(function() {
		    $("#pageNav").on('click', "li a[href][page]", function () {
		         var _this_ = $(this);
		         var iPage = _this_.attr('page');
		         var oParam = {};
		         $('#queryFrm input[name]').each(function(index,element){
		              oParam[$(element).attr('name')]=$(element).val();
				  });
				  oParam['pageSize']=36;
				  oParam['page']=iPage;
				  querySimilars(oParam);
			});
		    $("#queryBtn").click(function () {
		         var oParam = {};
		         $('#queryFrm input[name]').each(function(index,element){
		              oParam[$(element).attr('name')]=$(element).val();
				  });
		         console.log('oParam:'+JSON.stringify(oParam));
		         querySimilars(oParam);
			});
			
			 function querySimilars(oParam){
	            $.ajax({  
	                type: "POST",  
	                url: "/similar/query",  
	                data: oParam,
	                data: oParam,  
	                dataType: 'json',  
	                success: function(data){ 
	                   addSimilarDataList(data);
	                   addPageNav(data);
	                },  
	                error:function(data){  
	                }  
	            });
		    }
		    function addPageNav(oData){
		        var curPage = oData.curPage;
		        var html ='<nav><lable>总数：'+oData.totalRow+'</lable><ul class="pagination pagination-lg">';
		        if(!oData.prevPage){
		          html+='<li class="disabled"><span>&laquo;</span></li>';
		        }else {
		          html+='<li><a href="#" page="1">&laquo;</a></li>';
		        }
		        var fromPage = curPage-3;
		        fromPage = fromPage<1?1:fromPage;
		        var toPage = curPage+2;
		        toPage = toPage>oData.totalPage?oData.totalPage:toPage;
		        if(fromPage>2){
		           html+='<li><a href="#" page="1">1</a></li>';
		           html+='<li><span>…</span></li>';
		        }
		        for(var i=fromPage;i<=toPage;i++){
		          if(i==curPage){
		            html+='<li class="active"><span>'+curPage+'</span></li>';
		          }else {
		            html+='<li><a href="#" page='+i+'>'+i+'</a></li>';
		          }
		        }
		        if(toPage+1<oData.totalPage){
		           html+='<li><span>…</span></li>';
		           html+='<li><a href="#" page='+oData.totalPage+'>'+oData.totalPage+'</a></li>';
		        }
		        if(!oData.nextPage){
		          html+='<li class="disabled"><span>&raquo;</span></li>';
		        }else {
		          html+='<li><a href="#" page='+oData.totalPage+'>&raquo;</a></li>';
		        }
		        html +='</ul></nav>';
		        $('#pageNav').html(html);
		    }
		    function addSimilarDataList(oData){
		        oData = oData.data;
		        var html ='';
		        for(var i=0;i<oData.length;i++){
		            var pVo  = oData[i];
		            html +='<tr id="rid_'+pVo.id+'">';
					html +='<td name="id">'+pVo.id+'</td>';
					html +='<td name="similarCode">'+pVo.similarCode+'</td>';
					html +='<td name="siteId">'+pVo.siteId+'</td>';
					html +='<td name="productCode">'+pVo.productCode+'</td>';
					html +='<td name="productName"><a titile='+pVo.productName+' href='+pVo.productUrl+' target="_blank">'+pVo.productName+'</a></td>';
					html +='<td name="imgUrl"><img height="200px" width="200px" src='+(pVo.imgUrl?pVo.imgUrl:'#')+'></td>';
					html +='<td name="productPrice">'+pVo.productPrice+'</td>';
					html +='<td name="barCode">'+(pVo.barCode?pVo.barCode:'')+'</td>';
					html +='<td name="createTime">'+new Date(pVo.createTime).format("yyyy-MM-dd hh:mm:ss")+'</td>';
					html +='<td name="updateTime">'+new Date(pVo.updateTime).format("yyyy-MM-dd hh:mm:ss")+'</td>';
					html +='<td class="callBtn">';
					html +='<button id="updateBtn_'+pVo.id+'" class="btn btn-success"  type="button" >修改</button>';
					html +='<button id="saveBtn_'+pVo.id+'" class="btn btn-success"  type="button" style="display: none;">保存</button>';
					html +='</td>';
					html +='</tr>\n';
		        }
		        $('#similarData').html(html);
		    }
		    
		    (function(){
		       var sCodeString = $('input[name=codeList]').val();
		       var oCodeArr = eval("(" + sCodeString + ")");
		       $('input[name=sCode]').val(oCodeArr[0]);
		       $("#queryBtn").click()
		    }())
		});
	</script>
</body>
</html>