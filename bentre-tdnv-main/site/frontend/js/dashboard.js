import jQuery from 'jquery';

window.$ = jQuery;
$(document).ready(function(){
	$("body").on("click",".sum-detail",function(){
		var filter = {
			type: $(this).attr("type"),
			status: $(this).attr("status")
		}
		var element = document.getElementById("dashboardnew-view");
		element.$server.navigateTask(filter);   
	});  

	var intervalSetHeight;
	resizeNotifyPanel();
	function resizeNotifyPanel()
	{
		console.log("recursive for height");
		var heightRow1 = $("#dboard-row2").height();
		var heightRow2 = $("#dboard-row3").height();
	
		var heightDetail = heightRow1 + heightRow2 - 10;
		console.log(heightRow1);

		//console.log(heightFull+"--"+heightFormSearch+"--"+heightSVG+"--"+heightHeader+"--"+heightDetail);

		$("#dboard-notify").css("height",heightDetail+"px");
	}
	
	$(window).on('resize', function(){
     	resizeNotifyPanel();
	});
});