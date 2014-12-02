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
 .addClass{
   float:right;
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
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
					<div class="row">
						<table class='table table-bordered table-striped'>
						 <thead>
							<tr>
								<th>ID</th>
								<th>HOME</th>
								<th>CONFIG_PARSER</th>
								<th>MAX_PAGE</th>
								<th>DELETE</th>
								<th>STATUS</th>
								<th>CREATE_TIME</th>
								<th>UPDATE_TIME</th>
								<th>修改操作</th>
								<th><button id="addBtn"class="btn btn-success"  type="button" >添加</button></th>
							</tr>
						 </thead>
						 <tbody>
							<#if (model["proxyList"]??)>
							   <#if (model["proxyList"]?size>0)>
							      <#list model["proxyList"] as pVo>
									<tr id="rid_${pVo.id}">
										<td name="id">${pVo.id}</td>
										<td name="homeUrl">${pVo.homeUrl}</td>
										<td name="configParser">${pVo.configParser}</td>
										<td name="maxPage">${pVo.maxPage}</td>
										<td name="isDelete">${pVo.isDelete}</td>
										<td name="status">${pVo.status}</td>
										<td name="createTime">${pVo.createTime?datetime}</td>
										<td name="updateTime">${pVo.updateTime?datetime}</td>
										<td class="callBtn">
										<button id="updateBtn_${pVo.id}" class="btn btn-success"  type="button" >修改</button>
										<button id="saveBtn_${pVo.id}" class="btn btn-success"  type="button" style="display: none;">保存</button>
										</td>
										<#if (pVo.isDelete==1)>
										   <td class="callBtn"><button id="execBtn_${pVo.id}"class="btn btn-default"  type="button" disabled="disabled">执行</button></td>
										<#else>
										   <td class="callBtn"><button id="execBtn_${pVo.id}"class="btn btn-success"  type="button" >执行</button></td>
										</#if>
									</tr>
								 </#list>
							    <#else>
							      <tr ><td>无结果</td></tr>
							     </#if>
							</#if>
							 </tbody>
						</table>
					</div>
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
		$(document).ready(function() {
		    $("[id^=updateBtn_]").on('click',function(){
		         var oCurEle = $(this);
		         var oTdEles = oCurEle.parent().siblings('td:not(.callBtn)');
			     oTdEles.each(function(){
			        var sTxt = $(this).text();
			        var sName = $(this).attr('name');
			        if(sName.indexOf('Time')<0){
				        var sInput = '<input type="text" name="'+sName+'" value="'+sTxt+'"/>';
					    $(this).html(sInput);
			        }
				 });
				  var sId = $(this).attr('id');
			      sId = sId.replace('updateBtn_','saveBtn_');
			      $('#'+sId).show();
			      $(this).hide();
			});
		    $("[id^=saveBtn_]").on('click',function(){
		         var oCurEle = $(this);
		         var oTdEles = oCurEle.parent().siblings('td[name]');
		         var oParam = {};
			     oTdEles.each(function(){
			        var sTxt = $(this).find('input[name]').val();
			        var sName = $(this).attr('name');
			        if(sName.indexOf('Time')<0){
			          oParam[sName]=sTxt;
			        }
				    $(this).html(sTxt);
				 });
				var oSaveEls = $(this);
				$.post("/proxy/usource",oParam, function(data) {
				    var sId = oSaveEls.attr('id');
					var sUpdateId = sId.replace('saveBtn_','updateBtn_');
					$('#'+sUpdateId).show();
					oSaveEls.hide();
					var sExecId = sId.replace('saveBtn_','execBtn__');
					var oExecNode = $('#'+sExecId);
					if(oParam.isDelete==0){
					  oExecNode.removeAttr('disabled');
					  oExecNode.attr('class','btn btn-success');
					}else{
					  oExecNode.attr('disabled','disabled');
					  oExecNode.attr('class','btn btn-default');
					}
				});
			});
		    $("[id^=execBtn_]").on('click',function(){
		         var oCurEle = $(this);
		         var oTdEles = oCurEle.parent().siblings('td[name]');
		         var oParam = {};
			     oTdEles.each(function(){
			        var sName = $(this).attr('name');
			        var sTxt = $(this).text();
			        if(sName.indexOf('Time')<0){
			          oParam[sName]=sTxt;
			        }
				 });
				$.post("/proxy/execsrc",oParam, function(data) {
				    oCurEle.append('<span class="badge">'+data+'</span>');
				});
			});
		    $("[id=addBtn]").click(function(){
		            var oDate = new Date();
		            var sMonth = oDate.getMonth()<9?'0'+(oDate.getMonth()+1):oDate.getMonth()+1;
		            var sDate = oDate.getDate()<10?'0'+oDate.getDate():oDate.getDate();
		            var sCurDate =  oDate.getFullYear()+'-'+sMonth+'-'+sDate;
		            sCurDate +=' '+oDate.getHours()+':'+oDate.getMinutes()+':'+oDate.getSeconds();
		            var iMaxId=0;
		            $('tr[id^=rid_]').each(function(){
		                 var iCurId= $(this).attr('id').replace('rid_','') - 0;
		                 alert('iCurId:'+iCurId);
		                 if(iCurId > iMaxId){
		                    iMaxId = iCurId;
		                 }
					 });
					 alert(iMaxId);
					var iNextId = iMaxId +1;
		    		var sHtml = '';
					sHtml +='<tr id="rid_'+iNextId+'">';
					sHtml +='<td name="id"><input type="text" name="id"></td>';
					sHtml +='<td name="homeUrl"><input type="text" name="homeUrl" value=""></td>';
					sHtml +='<td name="configParser"><input type="text" name="configParser" value="ConfigProxyCollector"></td>';
					sHtml +='<td name="maxPage"><input type="text" name="maxPage" value="1"></td>';
					sHtml +='<td name="isDelete"><input type="text" name="isDelete" value="0"></td>';
					sHtml +='<td name="status"><input type="text" name="status" value="0"></td>';
					sHtml +='<td name="createTime">'+sCurDate+'</td>';
					sHtml +='<td name="updateTime">'+sCurDate+'</td>';
					sHtml +='<td class="callBtn">';
					sHtml +='<button id="updateBtn_'+iNextId+'" class="btn btn-success" type="button" style="display: none;">修改</button>';
					sHtml +='<button id="saveBtn_'+iNextId+'" class="btn btn-success" type="button" style="">保存</button>';
					sHtml +='</td>';
					sHtml +='<td class="callBtn"><button id="execBtn_'+iNextId+'" class="btn btn-success" type="button">执行</button></td>';
					sHtml +='</tr>';
					
					var oBodyNd = $('.row table tbody');
					oBodyNd.append(sHtml);
			});
		});
	</script>
</body>
</html>