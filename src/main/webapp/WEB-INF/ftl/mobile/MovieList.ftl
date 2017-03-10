<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta name="description" content="狸猫资讯精心收集整理的，正在热播高清电影下载地址列表，为你准备！"/>
<meta name="keywords" content="狸猫资讯、lezomao,正在热播,高清电影,种子下载,迅雷下载,高清下载" />
<meta name="title" content="最新正在热播高清电影下载地址列表 - 狸猫资讯(LezoMao.com)" />
<title>最新正在热播高清电影下载地址列表 - 狸猫资讯(LezoMao.com)</title>
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
					    <a class="item-wp"  href="/movie/detail/${oDoc.id}.html">
					      <div class="cover-wp" >
					      <div class="img-thumbnail">
					        <img alt="${oDoc.name}" src="${oDoc.image}" width="162" height="225" >
					      </div>
					      <p>${oDoc.name}
					        <strong class="rate">${oDoc.rate?string("0.0")}</strong>
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
				       <#if (prevNum=1)>
			           <a href="${curPath}.html" class="newer-posts"><span aria-hidden="true">&larr;</span>上一页</a>
				       <#else>
			           <a href="${curPath}/${prevNum}.html" class="newer-posts"><span aria-hidden="true">&larr;</span>上一页</a>
				       </#if>
					 </#if>
					 <span class="page-number">第 ${curPage} 页/共 ${totalPage} 页</span>
					 <#if (curPage<totalPage)>
					    <#assign nextNum=curPage +1>
					    <a href="${curPath}/${nextNum}.html" class="older-posts">下一页<span aria-hidden="true">&rarr;</span></a>
					</#if>
					</nav>
				</div>
				</div>
		    </div>
		    <div class="col-md-3">
		        <article class="hot-movie">
                    <h3 class="text-center"> 正在热播 </h3>
                    <div class="hot-list">
                       <#list oStarList as oStar>
                       <a  href="/movie/detail/${oStar.id}.html">
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