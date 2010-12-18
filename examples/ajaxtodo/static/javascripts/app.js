/*******************************************************************************
 * 
 * Application specific JavaScript.
 * 
 ******************************************************************************/

/*
 * Note: 
 * 1. The AJAX scaffold code generator uses the following block of code. 
 * 2. The following code uses "*" as a selector. In a real application, 
 * if the target is not dynamic, you may prefer to use the specific target #id 
 * for the place to show ajax response content, for example:
 * $(#id).live('ajax:success', function(event, data) {
 *   $("#id").html(data);
 *   event.stopImmediatePropagation();
 * });
 */
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
			$(target).text(data);
		}
		event.stopImmediatePropagation();
	});
});
