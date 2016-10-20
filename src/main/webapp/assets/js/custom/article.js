(function () {
	var ue = UE.getEditor('myEditor');
    /**$("#myEditor").on("mouseup",function(e){
        var oRange = ue.selection.getRange();
        ue.fireEvent("selectiondraw",'mouseup');
	});**/
	ue.addListener( 'ready', function( editor ) {
        $("#myEditor iframe[id^=ueditor_]").each(function(){
    		  var iframe =  $(this)[0];
    		  iframe.contentDocument.addEventListener('keydown', function(e) {
    		     if(e.ctrlKey && e.keyCode == 75) {
    		          //link dialog.ctr+k
				      return $EDITORUI["edui135"]._onClick(e, this);
				  }
    		  }, false);
    		});
     } );

    ue.addListener( 'selectiondraw', function(event,fireBy) {
            var oRange =  ue.selection.getRange();
            var domUtils = ue.dom.domUtils;
            var oAncestor = domUtils.getCommonAncestor(oRange.startContainer,oRange.endContainer);
	        if(!oAncestor 
	          || (oAncestor.nodeName && oAncestor.nodeName.toLowerCase()=='br')){
	          return false;
	        }
	        var parentEle = 1==oAncestor.nodeType?$(oAncestor):$(oAncestor).parent();
	        if(!parentEle.attr('selection') || !parentEle.attr('style')){
	           var sDashed = 'border:1px dashed red;';
	           var oReg = new RegExp(sDashed+'$','g');
	           $('[selection^=s][style]').each(function(){
	              var selectionEle = $(this);
	               var oldStyle = selectionEle.attr('style');
	               var sStyle = oldStyle.replace(oReg,'');
	               sStyle = sStyle.replace(/[\s]+$/,'');
	               selectionEle.attr('style',sStyle);
	               selectionEle.removeAttr('selection');
	           });
	           var oldStyle = parentEle.attr('style') || '';
	           var sStyle = oldStyle +' '+sDashed;
	           parentEle.attr('style',sStyle);
	           parentEle.attr('selection',"s"+new Date().getTime());
	        }
	        var startContainer;
	        var endContainer;
	        var oCurRange = ue.selection.getRange();
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
}());

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