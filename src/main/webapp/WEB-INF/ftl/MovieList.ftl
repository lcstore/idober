<!DOCTYPE html>
<html lang="zh-cn">
<head>

<title>狸猫资讯,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<#assign oDocList = (oDocList![])>
<body>
	<div class="container">
		<div class="row">
		    <div class="col-md-9">
		        <div class="tags">
			        <div class="tag-list">
			        <label class="activate">热门<input type="radio" name="tag" value="热门" checked="checked"></label>
			        <label>最新<input type="radio" name="tag" value="最新"></label>
			        <label>悬疑<input type="radio" name="tag" value="悬疑"></label>
			        </div>
			        <div class="cta-tag">
		            <a class="cta-btn" href="/register/step1a">悬疑</a>
		            <a class="cta-btn" href="/register/step1">Twitter</a>
		            <a class="cta-btn" href="/register/step1">Instagram</a>
		          </div>
		        </div>
		        <hr>
		        
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
				<div class="nextPage">
				     <nav>
					  <ul class="pagination">
					    <li>
					      <a href="#" aria-label="Previous">
					        <span aria-hidden="true">&laquo;</span></a>
					    </li>
					    <li>
					      <a href="#">1</a></li>
					    <li>
					      <a href="#">2</a></li>
					    <li>
					      <a href="#">3</a></li>
					    <li>
					      <a href="#">4</a></li>
					    <li>
					      <a href="#">5</a></li>
					    <li>
					      <a href="#" aria-label="Next">
					        <span aria-hidden="true">&raquo;</span></a>
					    </li>
					  </ul>
					</nav>
				  
				</div>
				</div>
		    </div>
		    <div class="col-md-3">
		        <article class="hot-movie">
                    <h3> 正在热播 </h3>
                    <div class="hot-list">
                       <a  href="">
                         <div class="bottom-line hot-wp">
                         <img src="https://img1.doubanio.com/view/movie_poster_cover/lpst/public/p2359593888.jpg" alt="..." class="img-circle" width="60" height="60">
                           3r3242342
                           <div class="star-main bigstar40 star-loc"></div>
                         </div>
                       </a>
                    </div>
                </article>
		    </div>
		</div>
	</div>
</body>
</html>