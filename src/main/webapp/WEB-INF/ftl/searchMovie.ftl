<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>乐助猫,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<div class="container-fluid">
				<#list model.qResponse as oDoc>
					  <#if (oDoc_index%4==0) >
					<div class="row">
					   </#if>
						   <div class="col-md-3">
						      <div class="list-col-box">
						        <div class="list-txt">
						        	<a href="${oDoc.url}" target="_blank">${oDoc.title}</a>
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