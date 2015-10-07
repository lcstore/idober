<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<sitemesh:write property='head'/>
	<title><sitemesh:write property='title'/></title>

<link rel="shortcut icon" href="/assets/img/gt_favicon.png">

<link rel="stylesheet" href="/assets/css/bootstrap.min.css?v=${version}">

<!-- Custom styles for our template -->
<link rel="stylesheet" href="/assets/css/bootstrap-theme.css?v=${version}"
	media="screen">
<link rel="stylesheet" href="/assets/css/main.css?v=${version}">

	<script src="/assets/js/jquery-1.11.3.min.js?v=${version}"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$('img').error(function(){
		       $(this).attr('src', '/assets/img/noimg220x220.jpg');
		       $(this).error = null;
		});
	});
	</script>
</head>

<body class="home">
    <!-- for next decorator.header -->
    <sitemesh:write property='body'/>
    
    
	<!-- JavaScript libs are placed at the end of the document so the pages load faster -->
	<script src="/assets/js/bootstrap-3.2.0.min.js?v=${version}"></script>
	<script src="/assets/js/headroom.min.js?v=${version}"></script>
	<script src="/assets/js/jQuery.headroom.min.js?v=${version}"></script>
	<script src="/assets/js/template.js?v=${version}"></script>
</body>
</html>