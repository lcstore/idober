<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="description" content="狸猫资讯,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,狸猫资讯。">
<title>狸猫资讯,购实惠,购正品,快捷购物不吃亏<sitemesh:write property='title'/></title>

<link rel="stylesheet" href="/assets/css/clusters.css?v=${version}">


</head>

<body class="home">
	<div class="container">
		 <div class="row article-padding">
		      <div class="article-list col-md-9">
		        <#list model["clusterList"] as cArticle>
			        <#if cArticle_index==0>
					  <article class="article-top">
					<#else>
					   <article class="article-node">
					</#if>
			          <div class="col-md-3">
			            <a href="${cArticle.productUrl}" >
			              <img src="${cArticle.imgUrl}" class="leftbar"/>
			            </a>
			          </div>
		             <div class="desc col-md-9">
		             	<a target="_blank" href="${cArticle.productUrl}">${cArticle.productName}</a>
		             	<div class="curPrice">${cArticle.currentPrice}</div>
		             	<div class="brand">${cArticle.tokenBrand}</div>
		             	<div class="brand">${cArticle.tokenCategory}</div>
		             	<div class="brand">${cArticle.tokenVary}</div>
		             	<div class="shop">${cArticle.shopId}</div>
		             	
                     </div>
			       </article>
		        </#list> 
		        <article class="article-node">
		          <div class="col-md-3">
		            <a href="" >
		              <img src="http://img10.360buyimg.com/n0/jfs/t595/197/509204436/102860/1bd3c1b/546e9e81N8f2d62eb.jpg" class="leftbar"/>
		            </a>
		          </div>
		            <div class="desc col-md-9"><a target="_blank" href="http://item.jd.com/1192475.html" class="title info_flow_news_title">完美芦荟胶40g 3支装</a><div class="author"><a href="http://item.jd.com/1192475.html"><span class="name">thethief</span></a><span class="time">&nbsp;•&nbsp;<abbr title="2015-08-01 14:28:26 +0800" class="timeago">2015/08/01 14:28</abbr></span></div><div class="brief">【京东自营 正品速达】多效完美芦荟胶正品保证！</div></div>
		       </article>
		      </div>
		      <div class="sidebar-list col-md-3" role="complementary">
		           <div class="thumbnail" style="height: 336px;">
		            <a onclick="_hmt.push(['_trackEvent', 'tile', 'click', 'bootcdn'])" target="_blank" title="Bootstrap中文网开放CDN服务" href="http://www.bootcdn.cn/"><img width="300" height="150" alt="Open CDN" data-src="http://static.bootcss.com/www/assets/img/opencdn.png" src="http://static.bootcss.com/www/assets/img/opencdn.png" class="lazy"></a>
		            <div class="caption">
		              <h3> 
		                <a onclick="_hmt.push(['_trackEvent', 'tile', 'click', 'bootcdn'])" target="_blank" title="Bootstrap中文网开放CDN服务" href="http://www.bootcdn.cn/">Open CDN<br><small>开放CDN服务</small></a>
		              </h3>
		              <p>Bootstrap中文网联合又拍云存储共同推出了开放CDN服务，我们对广泛的前端开源库提供了稳定的存储和带宽的支持，例如Bootstrap、jQuery等。
		              </p>
		            </div>
		          </div>
		          
		          <div class="thumbnail" style="height: 336px;">
					<a onclick="_hmt.push(['_trackEvent', 'tile', 'click', 'fontawesome'])" target="_blank" title="Font Awesome" href="/p/font-awesome/"><img width="300" height="150" alt="Font Awesome" data-src="http://static.bootcss.com/www/assets/img/font-awesome.png" src="http://static.bootcss.com/www/assets/img/font-awesome.png" class="lazy"></a>
					<div class="caption">
					<h3> 
					<a onclick="_hmt.push(['_trackEvent', 'tile', 'click', 'fontawesome'])" target="_blank" title="Font Awesome" href="/p/font-awesome/">Font Awesome <br><small>Bootstrap专用图标字体</small></a>
					</h3>
					<p>
					Font Awesome 中包含的所有图标都是矢量的，也就可以任意缩放，避免了一个图标做多种尺寸的麻烦。CSS对字体可以设置的样式也同样能够运用到这些图标上了。
					</p>
					</div>
					</div>
		      </div>
		</div>
	</div>
</body>
</html>