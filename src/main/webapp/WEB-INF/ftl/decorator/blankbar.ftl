<!DOCTYPE html>
<html lang="en">
<head>
    <title><sitemesh:write property='title' /></title>
    <sitemesh:write property='head'/>
</head>
<body>
	<div class="blankbar"></div>
    <sitemesh:write property='body'/>
  	<script type="text/javascript">
	$(document).ready(function() {
		var height = $("#top-navbar").height();
		var blank = 40;
		var bHeight = height+blank;
		console.log('bHeight:'+bHeight)
       $(".blankbar").css("height",bHeight);
	});
	</script>
</body>
</html>