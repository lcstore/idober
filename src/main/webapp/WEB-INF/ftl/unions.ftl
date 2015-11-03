<!DOCTYPE html>
<html lang="en">
<head>
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