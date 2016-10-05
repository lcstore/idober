<!DOCTYPE html>
<html lang="zh-cn">
<head>
<#if (oDoc.id)?exists>
<link rel="canonical" href="http://www.lezomao.com/movie/album/${oDoc.id}.html"/>
</#if> 
<#assign oCrumbs = (oDoc.crumbs![])>
<meta name="description" content="狸猫资讯(LezoMao.com)，${oDoc.name}(${oDoc.year})，导演：${oDirectors?join('、')}，主演：${oActors?join('、')}"/>
<meta name="keywords" content="狸猫资讯、lezomao,导演：${oDirectors?join('、')}，主演：${oActors?join('、')},${oDoc.name}(${oDoc.year}),${oDoc.enname},种子下载,迅雷下载,高清下载" />
<meta name="title" content="${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载 - 狸猫资讯(LezoMao.com)" />
<title>${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载 - 狸猫资讯(LezoMao.com)</title>
</head>
<body>
  	<div class="container">
      <div class="article">
        ${oDoc.content}
      </div>
  	</div>
</body>
</html>