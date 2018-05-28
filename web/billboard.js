globGetMethod = 0; /* 0: html; 1: xyz */

function setGetMethod (val) {
    globGetMethod = val;
}

function $(id) {
    return document.getElementById(id);
}

function getXMLHttpRequest() {
    // XMLHttpRequest for Firefox, Opera, Safari
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    }
    if (window.ActveObject) { // Internet Explorer
        try { // for IE new
            return new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e)  {  // for IE old
            try {
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e)  {
                alert("Your browser does not support AJAX!");
                return null;
            }
        }
    }
    return null;
}

function getHttpRequest(url) {
    if (globGetMethod == 0)
        getHtmlHttpRequest(url);
    else
        getxyzHttpRequest(url);
}

function getHtmlHttpRequest(url) {
    var xmlhttp = getXMLHttpRequest();
    xmlhttp.open("GET", url, true);
    xmlhttp.onreadystatechange = function() {
        if(xmlhttp.readyState != 4) {
            $('posters').innerHTML = 'Seite wird geladen ...';
        }
        if(xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            $('posters').innerHTML = xmlhttp.responseText;
        }
        $('timestamp').innerHTML = new Date().toString();
    };
    xmlhttp.send(null);
}

function getxyzHttpRequest(context, data) {
    var output = '<table border="1" rules="none" cellspacing="4" cellpadding="5">';
    data.forEach(function(plakat){
        console.log(plakat);
        output += '<tr><td>'+plakat.id+'</td><td><input type="text" size="100" minlength="100" maxlength="100" ' +
            'id="input_field_'+plakat.id+'" value="'+plakat.text+'" '+((plakat.disable_edits) ? 'disabled' : '')+'></td><td>';
        if(!plakat.disable_edits){
            output += '<button onClick="putHttpRequest(\''+context+'\','+plakat.id+')">Update</button>';
        }
        output += '</td><td>';
        if(!plakat.disable_edits){
            output += '<button onClick="deleteHttpRequest(\''+context+'\','+plakat.id+')">Delete</button>';
        }
        output += '</td></tr>';
    });
    output += '</table>';
    $('posters').innerHTML = output;

}

function postHttpRequest(url) {
    var xhttp = getXMLHttpRequest();
    var message = $('contents').value;
    xhttp.open("POST", url, true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("message="+message);
    // TO BE IMPLEMENTED!!!
}

function putHttpRequest(url, id) {
    var xhttp = getXMLHttpRequest();
    var message = $('input_field_'+id).value;
    xhttp.open("PUT", url+"?idx="+id+"&message="+message, true);
    xhttp.send(null);
    // TO BE IMPLEMENTED!!!
}

function deleteHttpRequest(url, id) {
    var xhttp = getXMLHttpRequest();
    xhttp.open("DELETE", url+"?idx="+id, true);
    xhttp.send(null);
    // TO BE IMPLEMENTED!!!
}
