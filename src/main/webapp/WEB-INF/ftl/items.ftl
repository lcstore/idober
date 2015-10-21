<!DOCTYPE html>
<html lang="en">
<head>
<meta name="description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>

</head>

<body class="home">
<!-- start items -->

 <#list model["tagRectList"] as tagRect>
	<!-- Highlights - jumbotron -->
	<#if tagRect_index%2==0>
	<div class="jumbotron no-padding ">
	<#else>
	<div class="container">
	</#if>
		<div class="container">
		<div class="tagRect">${unifyOf(tagRect.tagName,8,'')}</div>
		    <#list tagRect.dataList as pVo>
				<#if (pVo_index%4==0) >
			<div class="row centered">
				</#if>
				<div class="col-lg-3">
				   <div class="list-col-box item">
					    <div class="list-pic">
						    <a href="/sku/${pVo.skuCode}" target="blank">
							  <img alt="${pVo.productName}" src="${((pVo.imgUrl)?length>0)?string((pVo.imgUrl),'/assets/img/noimg220x220.jpg')}">
							</a>
						</div>
						<div class="list-txt">
						    <a href="/sku/${pVo.skuCode}" target="_blank">${unifyOf(pVo.productName)}</a>
						</div>
						<div class="list-price">
					        <del><span class="zm-coin">¥</span>${pVo.marketPrice}</del>
							<article class="article-top">
					        <#if (pVo.productPrice) && (pVo.productPrice>=0) >
					          <strong class="list_price"><span class="zm-coin">¥</span>${pVo.productPrice}</strong>
							<#else>
					          <strong class="list_price">暂无报价</strong>
							</#if>
							</article>
				        </div>
					</div>
				</div>
				<#if ((pVo_index+1)%4==0) || (!pVo_has_next)>
			      </div>
				</#if>
			</#list>
		</div>
	</div>
 </#list>
</body>
</html>