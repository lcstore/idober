<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>乐助猫,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
				<#list model.qResponse as oDoc>
					  <#if (oDoc_index%4==0) >
					<div class="row">
					   </#if>
						   <div class="col-md-3">
						      <div class="list-col-box">
						        <div class="list-pic">
						        	<a href="/item/${oDoc.matchCode}" target="_blank"><img alt="${oDoc.productName}" src="${((oDoc.imgUrl)?length>0)?string((oDoc.imgUrl),'/assets/img/noimg220x220.jpg')}" /></a>
						        </div>
						        <div class="list-txt">
						        	<a href="/item/${oDoc.matchCode}" target="_blank" title="${oDoc.productName}"> <span>${unifyOf(oDoc.productName)}</span></a>
						        </div>
						        <div class="list-price">
							        <#if (oDoc.minPrice) && (oDoc.maxPrice) >
							             <strong class="list_price"><span class="zm-coin">¥</span>
								           <#if (oDoc.minPrice ==oDoc.maxPrice) >
								             ${oDoc.minPrice}
										   <#else>
								             ${oDoc.minPrice}-${oDoc.maxPrice}
										   </#if>
							             </strong>
									<#else>
							          <strong class="list_price">暂无报价</strong>
									</#if>
						        </div>
						     </div>
					      </div>
					 <#if ((oDoc_index+1)%4==0) || (!oDoc_has_next)>
				      </div>
					</#if>
				</#list>
				</div>
			</div>
		</div>
	</div>
</body>
</html>