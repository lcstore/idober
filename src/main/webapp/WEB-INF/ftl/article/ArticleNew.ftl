<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <title>UMEDITOR 完整demo</title>
    <link href="/assets/umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
    <link href="//cdn.bootcss.com/bootstrap-datetimepicker/4.17.42/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet">
    <script type="text/javascript" charset="utf-8" src="/assets/umeditor/umeditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="/assets/umeditor/umeditor.js"></script>
    <script type="text/javascript" src="/assets/umeditor/lang/zh-cn/zh-cn.js"></script>
    
    
    <style>
      body.home {
		    background-color: #dde1e5;
	 }
	 .article-body {
		    width: 900px;
		    margin:20px auto 40px;
		    background-color: #fff;
		    
		    border: 1px solid #dadde1;
            border-radius: 5px;
            box-shadow: 2px 2px 5px #d3d6da;
      }
	 .article-header {
		    margin: 3% 6%;
		    padding: 10px;
      }
      .form-group.row:last-child{
             margin-bottom: 0px;
      }
      .stick-top{
         border: 1px solid #dadde1;
         border-radius: 0px 5px 5px 0px;
         padding-top: 2%;
         padding-bottom: 2%;
      }
    </style>
</head>
<body>
<div class="container" >
     <div class="article-body">
        <div class="article-header well">
	       <form target="_blank" action="${qAction!'/search/movie'}"   role="form" id="saveForm">
	         <div class="form-group row">
				  <div class="col-sm-4">
				    <div class="input-group">
				      <div class="input-group-addon">标题</div>
				      <input class="form-control" type="text" name="title" placeholder="文章标题">
				    </div>
				  </div>
				  <div class="col-sm-3">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <input class="form-control" type="text" name="source" placeholder="文章来源" value="狸猫资讯">
				    </div>
				  </div>
				  <div class="col-sm-3">
				    <div class="input-group">
				      <div class="input-group-addon">作者</div>
				      <input class="form-control" type="text" name="author"  placeholder="文章作者"  value="狸猫">
				    </div>
				  </div>
				  <div class="col-sm-2">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
				      <select class="form-control" name="type"  >
						  <option value="album">专辑</option>
						  <option value="joke">笑话</option>
						  <option value="news">新闻</option>
						  <option value="movie">电影</option>
					   </select>
				    </div>
				  </div>
			  </div>
	         <div class="form-group row">
				  <div class="col-sm-4">
				    <div class="input-group">
				      <div class="input-group-addon">置顶</div>
				      <div class="stick-top">
						<label class="radio-inline col-sm-offset-2">
						  <input type="radio" name="sticktop" id="stick-top-1" value="1">是
						</label>
				        <label class="radio-inline">
						  <input type="radio" name="sticktop" id="stick-top-2" value="0" checked="checked">否
						</label>
					  </div>
				    </div>
				  </div>
				  <div class="col-sm-4">
				    <div class="input-group date" id="startPicker">
				       <input class="form-control" name="start" type="text" >
				       <span class="input-group-addon">
				         <span class="glyphicon glyphicon-calendar"></span>
                       </span>
				    </div>
				  </div>
				  <div class="col-sm-4">
				    <div class="input-group" id="endPicker">
				      <input class="form-control" name="end" type="text">
				      <span class="input-group-addon">
				         <span class="glyphicon glyphicon-calendar"></span>
                      </span>
				    </div>
				  </div>
			  </div>
			  <div class="form-group row">
				  <div class="col-sm-12">
				    <div class="input-group">
				      <div class="input-group-addon">摘要</div>
				      <textarea class="form-control" rows="2" name="abstract"></textarea>
				    </div>
				  </div>
			  </div>
			  <div class="form-group row">
				  <div class="col-sm-12">
				    <div class="input-group">
				      <div class="input-group-addon">扩展</div>
				      <textarea class="form-control" rows="2" name="extend">{"link":"","cover":""}</textarea>
				    </div>
				  </div>
			  </div>
		 </form>
		</div>
	     <div style="width:800px;margin:20px auto 40px;">
	        <script type="text/plain" id="myEditor" style="width:100%;height:360px;">
	          <div><p>这里我可以写一些输入提示1</p></div>
	          <div><p>这里我可以写一些输入提示2</p></div>
	        </script>
	    </div>
	    <div class="article-header well">
	      <div class="row">
	       <div class="col-sm-3 col-md-3">
	        <button type="button" class="btn btn-success btn-xs " onclick="saveArticle(this)">保存</button>
	       </div>
	       <div class="col-sm-3 col-md-3">
	       <button type="button" class="btn btn-info btn-xs" onclick="previewArticle()">预览</button>
	        </div>
	       <div class="col-sm-6 col-md-6">
	       <button type="button" class="btn btn-danger btn-xs" onclick="saveAndRelease()">保存并发布</button>
	        </div>
     	  </div>
     	</div>
     </div>
</div>

<script type="text/javascript">
    //实例化编辑器
    var um = UM.getEditor('myEditor');
    um.addListener('blur',function(){
      console.log('UMEDITOR_CONFIG.toolbar:'+JSON.stringify(UMEDITOR_CONFIG.toolbar));
    });
    um.addListener('focus',function(){
       console.log('focus:XXXXXXXXX');
       var domUtils = UM.dom.domUtils;
       var oRange = um.selection.getRange();
    });
    $("#myEditor").on("click",function(e){
        var oRange = um.selection.getRange();
        um.fireEvent("selectiondraw",'mouseup');
	});
	$('#myEditor').on("keyup",function(e){
	    if(e.keyCode == 46) {
	      // $('[selection^=s][style]').remove();
	    }
	});
    
    um.addListener( 'selectiondraw', function(event,fireBy) {
            var oRange =  um.selection.getRange();
            var domUtils = UM.dom.domUtils;
            var oAncestor = domUtils.getCommonAncestor(oRange.startContainer,oRange.endContainer);
	        if(!oAncestor 
	          || (oAncestor.nodeName && oAncestor.nodeName.toLowerCase()=='br')){
	          return false;
	        }
	        var parentEle = 1==oAncestor.nodeType?$(oAncestor):$(oAncestor).parent();
	        if(!parentEle.attr('selection') || !parentEle.attr('style')){
	           $('[selection^=s][style]').each(function(){
	              var selectionEle = $(this);
	              selectionEle.after(selectionEle.children());
	              selectionEle.remove();
	           });
	           $('[rangeContainer=startContainer],[rangeContainer=endContainer]').each(function(){
	               $(this).removeAttr('rangeContainer');
	           });
	           $(oRange.startContainer).attr('rangeContainer','startContainer');
	           $(oRange.endContainer).attr('rangeContainer','endContainer');
	           var dashedEle=$('<p selection="s'+new Date().getTime()+'" style="border:1px dashed red;"></p>');
	           if(parentEle.attr('contenteditable') && parentEle.attr('class').indexOf('edui-body-container')>=0){
	              dashedEle.append(parentEle.children());
	              parentEle.children().remove();
	              parentEle.append(dashedEle);
	           }else {
	              dashedEle.append(parentEle.clone());
	              parentEle.replaceWith(dashedEle);
	           }
	        }
	        var startContainer;
	        var endContainer;
	        var oCurRange = um.selection.getRange();
	        $('[selection^=s][style]').find('*').contents().filter(function() {
	           var node = this;
	           if(oRange.startContainer.isEqualNode(node)){
	             startContainer = node;
	           }
	           if (oRange.endContainer.isEqualNode(node)){
	             endContainer=node;
	           }
			   return false;
			});
	        oCurRange = oCurRange.setStart(startContainer,oRange.startOffset);
	        oCurRange = oCurRange.setEnd(endContainer,oRange.endOffset);
	        oCurRange.setCursor(false, true);
	});
   
    function saveArticle(ele) {
       console.log("saveArticle");
       var errCount=0;
       var sExtend = $('#saveForm textarea[name=extend]').first().val();
       var oParam = $.parseJSON(sExtend) || {}; 
       oParam.abstract = $('#saveForm textarea[name=abstract]').first().val();
       oParam.type = $('#saveForm select[name=type] option:selected').first().val();
       $("#saveForm  div.form-group input[type=text][name],#saveForm  div.form-group input[type=radio][name]:checked").each(function(){
          var oEle = $(this);
          var sName = oEle.attr('name');
          var sVal = oEle.val() || '';
          sVal =  sVal.replace(/^\s+|\s+$/g,"");
          if(sVal){
              if('start'==sName || 'end'==sName){
	            sVal = moment(sVal,'YYYY-MM-DD HH:ss').format('YYYY-MM-DD[T]HH:ss:00.000[Z]');
	          }
          }else {
            if(/form-control/.test(oEle.attr('class'))){
                var oParentEle = oEle.parent();
	            var sCls = oParentEle.attr('class') || '';
	            sCls = sCls.replace(/has-error|has-success|has-warning/gm,'');
	            oParentEle.attr('class',sCls +' has-error');
            }
            errCount++;
          }
          oParam[sName]=sVal.trim();
           console.log("input "+sName+'='+sVal);
       });
       var sContent = UM.getEditor('myEditor').getContent();
       sContent = sContent.replace(/"Powered by 135editor.com"|_135editor|135编辑器/gm,'');
       oParam['content']=sContent;
       console.log("oParam:"+JSON.stringify(oParam));
       if(errCount==0){
		   	$.ajax({
				type : 'POST',
				url : '/console/article/save.action',
				dataType : 'json',
				contentType : 'application/json',
				data : JSON.stringify(oParam)
			}).done(function(oBack) {
				console.log('data:' + JSON.stringify(oBack));
			}).fail(function(data) {
				// window.location.reload();
			})
       }

    }
    function previewArticle(){
       var editor = UM.getEditor('myEditor');
       var oRange = editor.selection.getRange();
      //alert('editor.oRange:'+JSON.stringify(oRange));
      if (!oRange.collapsed) {
	        var img = oRange.getClosedNode();
	        alert('editor.oRange:'+JSON.stringify(oRange));
    	}
      
    }
   $(function () {
            $('#startPicker').datetimepicker({
                defaultDate: moment(),
                format:"YYYY-MM-DD HH:mm"
			});
            $('#endPicker').datetimepicker({
                defaultDate: moment().add(10, 'y'),
                format:"YYYY-MM-DD HH:mm"
			});
            $("#startPicker").on("dp.change",function (e) {
	            $('#endPicker').data("DateTimePicker").minDate(e.date);
	        });
	        $("#endPicker").on("dp.change",function (e) {
	            $('#startPicker').data("DateTimePicker").maxDate(e.date);
	        });
    });
    

</script>
<script type="text/javascript" src="//cdn.bootcss.com/moment.js/2.15.1/moment.min.js"></script>
<script type="text/javascript" src="//cdn.bootcss.com/bootstrap-datetimepicker/4.17.42/js/bootstrap-datetimepicker.min.js"></script>
</body>
</html>