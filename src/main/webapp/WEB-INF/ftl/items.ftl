<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏<sitemesh:write property='title'/></title>

</head>

<body class="home">

 <#list model["tagRectList"] as tagRect>
	<!-- Highlights - jumbotron -->
	<#if tagRect_index%2==0>
	<div class="jumbotron no-padding ">
	<#else>
	<div class="container">
	</#if>
		<div class="container text-center">
		<div class="tagRect">${tagRect.tagName}</div>
			<div class="row centered">
		  <#list tagRect.dataList as pVo>
				<div class="col-lg-3">
				    <a href="/cluster/${pVo.matchCode}" target="blank">
					  <img src="${pVo.imgUrl}" alt="${pVo.productName}">
					</a>
					<a href="/cluster/${pVo.matchCode}" target="blank">
					   <h3>${pVo.productName}</h3>
					</a>
				</div>
			</#list>
			</div>
		</div>
	</div>
 </#list>
</body>
</html>