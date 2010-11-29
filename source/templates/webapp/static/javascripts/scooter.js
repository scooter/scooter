$(document).ready(function(){
	/*
	 * Handling Ajax
	 */
	$('body').delegate('a[data-confirm], button[data-confirm], input[data-confirm]', 'click', function () {
		if (!confirm($(this).attr('data-confirm'))) {
			return false;
		}
	});

	$('a[data-ajax], input[data-ajax]').live('click', function (event) {
		ajax_call(this);
		event.preventDefault();
	});

	$('form[data-ajax]').live('submit', function (event) {
		ajax_call(this);
		event.preventDefault();
	});

	function ajax_call(source) {
		var jsrc = $(source);
		var isForm = jsrc.is("form");
		
		var url = (isForm)?jsrc.attr("action"):jsrc.attr("href");
		
		var data = null;
		var qindex = url.indexOf('?');
		if (qindex != -1) {
			data = url.substring(qindex + 1);
			url = url.substring(0, qindex);
		}
		data = (data != null)?(data + "&" + "_ajax=true"):"_ajax=true";
		if (isForm) data += "&" + jsrc.serialize();

		var method = (isForm)?jsrc.attr("method"):jsrc.attr("data-method");
		if (method == null) method = "GET";
		method = method.toUpperCase();
		if (method != "GET" && method != "POST") {
			data += "&" + "_method=" + method;
			method = "POST";
		}

		var dataType  = jsrc.attr('data-type')  || 'script';

		$.ajax({
			url: url,
			data: data,
			dataType: dataType,
			type: method,
			beforeSend: function (xhr) {
				jsrc.trigger('ajax:loading', [xhr, source]);
			},
			success: function (data, status, xhr) {
				jsrc.trigger('ajax:success', [data, status, xhr, source]);
			},
			complete: function (xhr, status) {
				jsrc.trigger('ajax:complete', [xhr, status, source]);
			},
			error: function (xhr, status, error) {
				jsrc.trigger('ajax:error', [xhr, status, error, source]);
			}
		});
	}
	
	/*
	 * Handling code highlight
	 * Note: requires jquery.snippet.min.js
	 */
	var style="darkblue";
	var showNumber=true;

	//The following types are from snippet.
	$("pre.c_code").snippet("c",{style:style,showNum:showNumber});
	$("pre.cpp_code").snippet("cpp",{style:style,showNum:showNumber});
	$("pre.cs_code").snippet("csharp",{style:style,showNum:showNumber});
	$("pre.css_code").snippet("css",{style:style,showNum:showNumber});
	$("pre.flex_code").snippet("flex",{style:style,showNum:showNumber});
	$("pre.html_code").snippet("html",{style:style,showNum:showNumber});
	$("pre.java_code").snippet("java",{style:style,showNum:showNumber});
	$("pre.js_code").snippet("javascript",{style:style,showNum:showNumber});
	$("pre.pl_code").snippet("perl",{style:style,showNum:showNumber});
	$("pre.php_code").snippet("php",{style:style,showNum:showNumber});
	$("pre.properties_code").snippet("properties",{style:style,showNum:showNumber});
	$("pre.py_code").snippet("python",{style:style,showNum:showNumber});
	$("pre.rb_code").snippet("ruby",{style:style,showNum:showNumber});
	$("pre.sql_code").snippet("sql",{style:style,showNum:showNumber});
	$("pre.xml_code").snippet("xml",{style:style,showNum:showNumber});

	//The following is the default case.
	$("pre[class$='_code']").snippet("html",{style:style,showNum:showNumber});
	
});