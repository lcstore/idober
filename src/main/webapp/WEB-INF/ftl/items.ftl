<!DOCTYPE html>
<html lang="en">
<head>
<title>狸猫,识低价、辨真假,比价购物更放心！</title>

</head>

<body class="home">
<!-- start items -->

 <#list model["tagRectList"] as tagRect>
	<div class="container">
		<div class="container">
		<div class="tagRect">${unifyOf(tagRect.tagName,8,'')}</div>
		    <#list tagRect.dataList as pVo>
				<#if (pVo_index%4==0) >
			<div class="row centered">
				</#if>
				<div class="col-lg-3">
				   <div class="list-col-box item">
					    <div class="list-pic">
						    <a href="/sku/${pVo.skuCode}" target="_blank">
							  <img alt="${pVo.productName}" src="${((pVo.imgUrl)?length>0)?string((pVo.imgUrl),'/assets/img/noimg220x220.jpg')}">
							</a>
						</div>
						<div class="list-txt">
						    <a href="/sku/${pVo.skuCode}" target="_blank" title="${oDoc.productName}">${unifyOf(pVo.productName)}</a>
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