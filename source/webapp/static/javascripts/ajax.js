// XMLHttpRequest related
/*
 * Returns a new XMLHttpRequest object if the browser supports it
 */
function getXMLHttpRequest() 
{
    var xmlRequest = false;
    
    if (window.ActiveXObject) {
        try {
            xmlRequest = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e1) {
            try {
                xmlRequest = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e2) {
                xmlRequest = false;
            }
        }
    } else if (window.XMLHttpRequest) {
        xmlRequest = new XMLHttpRequest();
    }
    
    return xmlRequest;
}

/*
 * Returns a function which processes the response Text message
 * req - The XMLHttpRequest
 * responseHandlers - methods to parse the response
 */
function handleResponse(req, responseHandlers, targetElementId, responseType) 
{
    return function () {
        if (req.readyState == 1) {
            responseHandlers.onLoading(targetElementId);
        }
        else if (req.readyState == 2) {
            responseHandlers.onLoaded(targetElementId);
        }
        else if (req.readyState == 3) {
            responseHandlers.onInteractive(targetElementId);
        }
        else if (req.readyState == 4) {
            // Check that we received a successful response from the server
            if (req.status == 200) {
                if (responseType == "TEXT") {
                    message = req.responseText;
                }
                else if (responseType == "XML") {
                    message = responseHandlers.parseXML(req.responseXML, targetElementId);
                }
                responseHandlers.onComplete(message, targetElementId);
            } else {
                responseHandlers.onError("HttpRequest error "+req.status+": " + req.statusText, targetElementId);
            }
        }
    }
}

function ajax_link4text(targetElementId, url, method, responseHandlers) {
    ajax_link(targetElementId, url, method, responseHandlers, "TEXT");
}

function ajax_link4xml(targetElementId, url, method, responseHandlers) {
    ajax_link(targetElementId, url, method, responseHandlers, "XML");
}

function ajax_link(targetElementId, url, method, responseHandlers, responseType) 
{
    if (responseHandlers == undefined) {
        responseHandlers = {};
    }

    if (responseHandlers.onLoading == undefined) {
        responseHandlers.onLoading = default_onLoading;
    }

    if (responseHandlers.onLoaded == undefined) {
        responseHandlers.onLoaded = default_onLoaded;
    }

    if (responseHandlers.onInteractive == undefined) {
        responseHandlers.onInteractive = default_onInteractive;
    }

    if (responseHandlers.onComplete == undefined) {
        responseHandlers.onComplete= default_onComplete;
    }

    if (responseHandlers.onError == undefined) {
        responseHandlers.onError = default_onError;
    }

    if (responseHandlers.parseXML == undefined) {
        responseHandlers.parseXML = default_parseXML;
    }

    //validate method
    if (method == undefined) {
        method = "GET";
    }
    else {
        method = method.toUpperCase();
        if (method != "GET" && method != "POST") 
            responseHandlers.onError("method error: method must be either GET or POST");
            return;
    }

    if (responseType == undefined) {
        responseType = responseHandlers.responseType.toUpperCase();
        if (responseType == undefined) {
            responseType = "TEXT";
        }
    }

    //validate reponseType
    if (responseType != "TEXT" && responseType != "XML") {
            responseHandlers.onError("responseType error: responseType must be either TEXT or XML");
            return;
    }

    var xmlRequest = getXMLHttpRequest();
    xmlRequest.onreadystatechange = handleResponse(xmlRequest, responseHandlers, targetElementId, responseType);
    
    if (method == "GET") {
        xmlRequest.open(method, url, true);
        xmlRequest.send("");
    }
    else if (method == "POST") {
        paramQueryString = "";
        pureURL = url;
        qmarkIndex = url.indexOf('?');
        if (qmarkIndex != -1) {
            pureURL = url.substring(0, qmarkIndex);
            paramQueryString = url.substring(qmarkIndex + 1);
        }
        
        xmlRequest.open(method, url, true);
        xmlRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xmlRequest.send(paramQueryString);
    }
}

function ajax_form(formId, targetElementId, url, method, responseHandlers, responseType) {
    var form = document.getElementById(formId);
    var queryString = getQueryString(form);
    qmarkIndex = url.indexOf('?');
    if (qmarkIndex != -1) {
        url = url + '&' + queryString;
    }
    else {
        url = url + '?' + queryString;
    }
    ajax_link(targetElementId, url, method, responseHandlers, responseType) 
}

function ajax_form4text(formId, targetElementId, url, method, responseHandlers) {
    ajax_form(targetElementId, url, method, responseHandlers, 'TEXT');
}

function ajax_form4xml(formId, targetElementId, url, method, responseHandlers) {
    ajax_form(targetElementId, url, method, responseHandlers, 'XML');
}

function default_parseXML(xmlDoc, targetElementId) {
    alert("default_parseXML is called.");
    alert("You need to add a xml parsing method by setting responseHandlers.parseXML:yourParseXMLMethodName");
    txt="need to parse xmlDoc in your own way";
    return txt;
}

function default_onLoading(targetElementId) {}

function default_onLoaded(targetElementId) {}

function default_onInteractive(targetElementId) {}

function default_onComplete(message, targetElementId) {
    if (targetElementId != undefined) {
        var targetType  = document.getElementById(targetElementId).type;
        var targetValue = document.getElementById(targetElementId).value;
        if (targetType == undefined || targetValue == undefined) {
            document.getElementById(targetElementId).innerHTML = message;
        }
        else {
            document.getElementById(targetElementId).value = message;
        }
    }
}

function default_onError(message, targetId) {
    if (message != undefined) {
        if (targetId != undefined) {
            alert("Error happened.\nDetails: " + message + ".\Element ID: " + targetId);
        }
        else {
            alert("Error happened.\nDetails: " + message);
        }
    }
    else {
        alert("Error happened.");
    }
}

function getQueryString(form) {
    if (form == undefined) return "";
    
    var formLength = form.elements.length;
    var queryString = "";
    for (var i=0;i<formLength;i++) {
        var el = form.elements[i];
        if (el.type=="button") {
            //skip button
        }
        else if (el.type=="reset") {
            //skip reset
        }
        else if (el.type=="hidden" || el.type=="text" || el.type=="textarea") {
            queryString += constructQueryStringPair(el.name, el.value);
        }
        else if (el.type=="checkbox" && el.checked) {
            queryString += constructQueryStringPair(el.name, el.value);
        }
        else if (el.type=="radio" && el.checked) {
            queryString += constructQueryStringPair(el.name, el.value);
        }
        else if (el.type=="select-one") {
            if (el.selectedIndex != -1) {
                var option = el.options[el.selectedIndex];
                queryString += constructQueryStringPair(el.name, option.value);
            }
        }
        else if (el.type=="select-multiple") {
            var opts = el.options;
            var opLen = opts.length;
            for(var o=0;o<opLen;o++) {
                var option = opts[o];
                if (option.selected) {
                    queryString += constructQueryStringPair(el.name, option.value);
                }
            }
        }
    }
    return queryString;
}

function constructQueryStringPair(name, value) {
    return encodeURIComponent(name) + "=" + encodeURIComponent(value) + "&";
}