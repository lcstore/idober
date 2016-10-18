<!DOCTYPE html>
<html xmlns:wb="http://open.weibo.com/wb" lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta property="qc:admins" content="42471075676452751763757" />
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<title>
     <sitemesh:write property='title' /></title>

     <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.0.3/css/bootstrap.min.css">
     <link rel="stylesheet" href="/assets/css/bootstrap.min.css" >
     <link rel="stylesheet" href="${static_host}/assets/css/bootstrap-theme.css?v=${version}" media="screen">
	
    <link rel="stylesheet" href="${static_host}/assets/css/main.css?v=${version}">
    
	<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
	<script src="/assets/js/jquery-1.11.3.min.js"></script>
	<script src="${static_host}/assets/js/jquery.cookie.js?v=${version}"></script>
	<script src="${static_host}/assets/js/custom/common-tools.js?v=${version}"></script>
	
	<sitemesh:write property='head'/>
	
</head>

<body class="home">
    <!-- for next decorator.header -->
    <sitemesh:write property='body'/>
    <input type="hidden" id="qqAppId" value="${qq_connect_appid}"/>
	<input type="hidden" id="qqRedirectUrl" value="${qq_redirect_url}"/>
    <input type="hidden" id="wbAppId" value="${wb_app_key}"/>
    <input type="hidden" id="wbRedirectUrl" value="${wb_redirect_url}"/>
	<script src="//cdn.bootcss.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<script src="/assets/js/bootstrap-3.2.0.min.js"></script>
</body>
</html>