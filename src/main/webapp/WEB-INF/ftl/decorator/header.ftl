<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><sitemesh:write property='title' /></title>
<sitemesh:write property='head'/>

<link rel="stylesheet" href="${static_host}/assets/css/bootstrap.min.css?v=${version}">

<!-- Custom styles for our template -->
<link rel="stylesheet" href="${static_host}/assets/css/bootstrap-theme.css?v=${version}"
	media="screen">
<link rel="stylesheet" href="${static_host}/assets/css/main.css?v=${version}">

	<script src="${static_host}/assets/js/jquery-1.11.3.min.js?v=${version}"></script>
	<script src="${static_host}/assets/js/custom/common-tools.js?v=${version}"></script>
</head>

<body class="home">
    <!-- for next decorator.header -->
    <sitemesh:write property='body'/>
    
	<script src="${static_host}/assets/js/bootstrap-3.2.0.min.js?v=${version}"></script>
</body>
</html>