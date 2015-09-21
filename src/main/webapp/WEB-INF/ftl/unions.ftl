<!DOCTYPE html>
<html lang="en">
<head>
<meta name="description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>

</head>

<body>
	<div class="container text-center">
		<div class="row centered">
			<div class="col-lg-3">
				<#if model["union"].unionUrl != null> 
				    <a href="${model["union"].unionUrl}" target="blank">
				       恭喜您，获得宝物(${model["union"].productCode})
					</a>
				<#else> 
				    <a href="${model["union"].productUrl}" target="blank">
				       继续，寻找宝物(${model["union"].productCode})
					</a>
				</#if>
			</div>
		</div>
	</div>
</body>
</html>