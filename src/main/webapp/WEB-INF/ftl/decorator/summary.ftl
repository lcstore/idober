<!DOCTYPE html>
<html lang="en">
<head>
    <sitemesh:write property='head'/>
    <title><sitemesh:write property='title'/></title>
</head>
<body>
	<!-- start summary -->
	<div class="container text-center">
		<!-- START TIMER -->
		<div id="timer" data-animated="FadeIn" class="animated FadeIn">
			<p id="message"></p>
			<div id="days" class="timer_box">
				<h1>46</h1>
				<p>Days</p>
			</div>
			<div id="hours" class="timer_box">
				<h1>20</h1>
				<p>Hours</p>
			</div>
			<div id="minutes" class="timer_box">
				<h1>46</h1>
				<p>Minutes</p>
			</div>
			<div id="seconds" class="timer_box">
				<h1>28</h1>
				<p>Seconds</p>
			</div>
		</div>
		<!-- END TIMER -->
	</div>
	<!-- end summary -->
	
	
	<!-- for next decorator.summary -->
    <sitemesh:write property='body'/>
</body>
</html>