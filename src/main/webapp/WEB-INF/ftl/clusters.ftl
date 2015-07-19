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
	<div class="container">
		  <#list model["clusterList"] as pVo>
		     <div class="list-group">
	          <a href="${pVo.productUrl}" class="list-group-item list-group-item-success">
	            <span class="badge" >12</span>
	            <span>${pVo.tokenBrand}</span>
				<span>${pVo.tokenCategory}</span>
	            ${pVo.productName}
	          </a>
	        </div>
		</#list>
	</div>
</body>
</html>