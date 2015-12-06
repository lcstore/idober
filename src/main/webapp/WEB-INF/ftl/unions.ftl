<!DOCTYPE html>
<html lang="en">
<head>
</head>

<body>
	<div class="container text-center">
		<div class="row centered">
			<div class="col-lg-3">
				<#if model["union"].unionUrl != null> 
					<#if model["union"].siteId == 1013> 
						<a biz-itemid="${model["union"].productCode}" isconvert=1 href="${model["union"].unionUrl}" target="_blank" >
						 恭喜您，获得宝贝(${model["union"].productCode})
						</a>
					<#else> 
					<a href="${model["union"].unionUrl}" target="_blank">
				       恭喜您，获得宝物(${model["union"].productCode})
					</a>
					</#if>
				<#else> 
				    <a href="${model["union"].productUrl}" target="_blank">
				       继续，寻找宝物(${model["union"].productCode})
					</a>
				</#if>
			</div>
		</div>
	</div>
</body>
</html>