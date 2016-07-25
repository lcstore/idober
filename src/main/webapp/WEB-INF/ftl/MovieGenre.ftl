<!DOCTYPE html>
<html lang="zh-cn">
<head>

<title>狸猫资讯,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<#assign oDocList = (oDocList![])>
<#assign oCrumbList = (oCrumbList![])>
<#assign oStarList = (oStarList![])>
<body>
	<div class="container">
		<div class="row">
		    <div class="col-md-9">
		        <ol class="breadcrumb">
		          <#list oCrumbList as oCrumb>
				    <#if (!oCrumb_has_next)>
				     <li class="active"><a href="${oCrumb.link}">${oCrumb.title}</a></li>
				    <#else>
				     <li><a href="${oCrumb.link}">${oCrumb.title}</a></li>
					</#if>
		          </#list>
				</ol>
				<#--
		        <div class="tags bottom-border">
			        <div class="tag-list">
			        <a class="tag-btn activate" href="/register/step1a">悬疑</a>
		            <a class="tag-btn" href="/register/step1">热门</a>
		            <a class="tag-btn" href="/register/step1">经典</a>
			        </div>
		        </div>
		        -->
		        <div class="items">
				<div class="item-list">
				<#list oDocList as oDoc>
					<#if (oDoc_index%4==0) >
					<div class="row row-wp">
					</#if>
					    <div class="col-md-3 col-wp">
					    <a class="item-wp" target="_blank" href="/movie/detail/${oDoc.id}">
					      <div class="cover-wp" >
					      <div class="img-thumbnail">
					        <img alt="${oDoc.name}" src="${oDoc.image}" width="162" height="225" >
					      </div>
					      <p>${oDoc.name}
					        <strong class="rate">${oDoc.rate}</strong>
					      </p>
					      </div>
					 
					    </a>
						</div>
					<#if ((oDoc_index+1)%4==0) || (!oDoc_has_next)>
				    </div>
					</#if>
				</#list>
				</div>
				<div class="pageContainer">
				   <nav class="pagination">
				     <#if (curPage>1)>
				       <#assign prevNum=curPage -1>
			           <a href="${curPath}/${prevNum}/" class="newer-posts"><span aria-hidden="true">&larr;</span>上一页</a>
					 </#if>
					 <span class="page-number">第 ${curPage} 页/共 ${totalPage} 页</span>
					 <#if (curPage<totalPage)>
					    <#assign nextNum=curPage +1>
					    <a href="${curPath}/${nextNum}/" class="older-posts">下一页<span aria-hidden="true">&rarr;</span></a>
					</#if>
					</nav>
				</div>
				</div>
		    </div>
		    <div class="col-md-3">
		        <article class="hot-movie">
                    <h3 class="text-center"> 高分电影 </h3>
                    <div class="hot-list">
                       <#list oStarList as oStar>
                       <a target="_blank" href="/movie/detail/${oStar.id}">
                         <div class="star-wp">
                         <img src="${oStar.cover}" alt="${oStar.name}" class="img-circle star-img" width="60" height="60">
                         <div class="star-txt">${oStar.name}</div>
                         <div class="star-main bigstar${oStar.star} star-loc"></div>
                         </div>
                       </a>
                       </#list>
                    </div>
                </article>
		    </div>
		</div>
	</div>
</body>
</html>