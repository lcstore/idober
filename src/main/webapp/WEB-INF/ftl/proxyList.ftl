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
						<table  border="1">
							<tr>
								<th>ID</th>
								<th>HOME</th>
								<th>CONFIG_PARSER</th>
								<th>MAX_PAGE</th>
								<th>DELETE</th>
								<th>STATUS</th>
								<th>CREATE_TIME</th>
								<th>UPDATE_TIME</th>
								<th colspan="2">修改操作</th>
							</tr>
							<#if (model["proxyList"]??)>
							   <#if (model["proxyList"]?size>0)>
							      <#list model["proxyList"] as pVo>
									<tr>
										<td>${pVo.id}</td>
										<td>${pVo.homeUrl}</td>
										<td>${pVo.configParser}</td>
										<td>${pVo.maxPage}</td>
										<td>${pVo.isDelete}</td>
										<td>${pVo.status}</td>
										<td>${pVo.createTime?datetime}</td>
										<td>${pVo.updateTime?datetime}</td>
										<td><button id="updateBtn"class="btn btn-success"  type="button" >修改</button></td>
										<td><button type="button" class="btn btn-success" id="addBtn" style="margin-left: 10px;">添加</button></td>
									</tr>
								 </#list>
							    <#else>
							       	<tr colspan="10" ><button id="addBtn"class="btn btn-success"  type="button" >添加</button></tr>
							     </#if>
							</#if>
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
</body>
</html>