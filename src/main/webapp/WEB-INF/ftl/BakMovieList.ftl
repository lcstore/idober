<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>狸猫资讯,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<#assign oDocList = (oDocList![])>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<#list oDocList as oDoc>
					  <#if (oDoc_index%4==0) >
					<div class="row">
					   </#if>
						   <div class="col-md-3">
						      <div class="list-col-box">
						        <div class="list-pic">
						        	<a href="/movie/detail/${oDoc.id}" target="_blank"><img alt="${oDoc.name}" src="${((oDoc.cover)?length>0)?string((oDoc.cover),'/assets/img/noimg220x220.jpg')}" /></a>
						        </div>
						        <div class="list-txt">
						        	<a href="/movie/detail/${oDoc.id}" target="_blank" title="${oDoc.name}"> <span>${unifyOf(oDoc.name)}</span></a>
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
</body>
</html>