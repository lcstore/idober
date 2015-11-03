<!DOCTYPE html>
<html lang="en">
<head>
    <sitemesh:write property='head'/>
</head>
<body>
	<!-- start activity -->
	<header id="head">
		<div class="container">
			<div class="row mt centered">
				<div class="col-lg-6 col-lg-offset-3">
					<div id="carousel-example-generic" class="carousel slide"
						data-ride="carousel">
						<!-- Indicators -->
						<ol class="carousel-indicators">
							<li data-target="#carousel-example-generic" data-slide-to="0"
								class="active"></li>
							<li data-target="#carousel-example-generic" data-slide-to="1"></li>
							<li data-target="#carousel-example-generic" data-slide-to="2"></li>
						</ol>

						<!-- Wrapper for slides -->
						<div class="carousel-inner">
							<div class="item active">
								<img src="/assets/img/p01.png" alt="">
							</div>
							<div class="item">
								<img src="/assets/img/p02.png" alt="">
							</div>
							<div class="item">
								<img src="/assets/img/p03.png" alt="">
							</div>
						</div>
					</div>
				</div>
				<!-- /col-lg-8 -->
			</div>
			<!-- /row -->
		</div>
		<! -- /container -->
	</header>
	<!-- end activity -->
	
	
	<!-- for next decorator.activity -->
    <sitemesh:write property='body'/>
</body>
</html>