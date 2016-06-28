<!DOCTYPE html>
<html lang="en">
  <head>
    <title>500页面</title>
    <link rel="stylesheet" href="${static_host}/assets/css/error.css?v=${version}">
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="span6 text-center">
          <h1>
            小猫累趴了,<span id="clock">5</span>秒后自动跳到首页
          </h1>
          <input type="hidden" name="${error.name}" value="${error.message}">
         </div>
      </div>
      <div class="span1"></div>
    </div>
   <script src="${static_host}/assets/js/jquery.countdown.min.js?v=${version}"></script>
    
   <script type="text/javascript">
	    $(document).ready(function() {
		 var $clock = $('#clock')
		    .on('update.countdown', function(event) {
		      var format = '%S';
		      $(this).html(event.strftime(format));
		    })
		    .on('finish.countdown', function(event) {
		      window.location.href="http://www.lezomao.com";
		    });
		    var val = 5*1000;
		    var endDate = new Date().valueOf() + val;
            $clock.countdown(endDate.toString());
		});
    </script>
  </body>

</html>