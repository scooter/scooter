/*******************************************************************************
 * 
 * Application specific JavaScript.
 *
 * Note: 
 * 1. This is a place to add your own AJAX handling code.
 * 2. The AJAX scaffold code generator uses the following block of code.
 * 
 ******************************************************************************/

$(document).ready(function(){
	//Handling ajax
	$("*").live('ajax:success', function(event, data, status, xhr, source) {
		var jsrc = $(source);
		var handler = jsrc.attr("data-handler") || "html";
		var target = jsrc.attr("data-target");
		
		if (handler == "after") {
			$(target).after(data);
		} else if (handler == "before") {
			$(target).before(data);
		} else if (handler == "append") {
			$(target).append(data);
		} else if (handler == "prepend") {
			$(target).prepend(data);
		} else if (handler == "html") {
			$(target).html(data);
		} else if (handler == "text") {
			$(target).text(data);
		} else {
			alert("Thre is no handler of this event type: " + event.type);
		}
		event.stopImmediatePropagation();
	});
});