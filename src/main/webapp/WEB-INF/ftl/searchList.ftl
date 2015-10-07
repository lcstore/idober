<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<meta name="Description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
					<div class="row">
					  <#assign oDocs=model["qResponse"]?eval />
					  <#list oDocs as oDoc>
						    <div class="col-md-3"><div class="list-col-box">
					        <div class="list-pic">
					        <a href="${oDoc.productUrl}" target="_blank"><img alt="${oDoc.productName}" src="${(oDoc.imgUrl)???string(oDoc.imgUrl,'/assets/img/noimg220x220.jpg')}" /></a>
					        </div>
					        <div class="list-txt">
					        <a href="${oDoc.productUrl}" target="_blank"> <span>${oDoc.productName}</span></a>
					        </div>
					        <div class="list-price">
					        <del><span class="zm-coin">¥</span>${oDoc.marketPrice}</del>
					        <#if oDoc.productPrice && oDoc.productPrice>=0>
							  <article class="article-top">
					          <strong class="list_price"><span class="zm-coin">¥</span>${oDoc.productPrice}</strong>
							<#else>
					          <strong class="list_price">暂无报价</strong>
							</#if>
					        </div>
					        </div></div>
					   </#list>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>