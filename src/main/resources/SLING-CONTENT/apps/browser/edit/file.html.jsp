<%@page import="javax.jcr.Session"%>
<%@page session="false" contentType="text/html; charset=utf-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<sling:defineObjects />
<c:set var="staticRoot" value="/apps/browser/static" scope="request" />

<!DOCTYPE html>
<html lang="en">
<head>
<title>${currentNode.name}</title>
<style type="text/css" media="screen">
body {
	margin: 0px;
	padding: 0px;
}

#editor {
	top:0px;
    position: absolute;
    right: 0;
    bottom: 0px;
    left: 0;
    opacity:0;
}
nav {
	background-color: #FFF;
	border: 1px solid #999;
	border-radius: 0px 0px 10px 10px;
	position: fixed;
	right: 0px;
	padding: 5px;
	border-top: 0px;
	z-index: 2;
	margin-right: 20px;
	vertical-align:middle;
	border-top:0px;
}

nav>button{
	margin-right:10px;
	line-height:20px;
	border: 1px solid #999 !important;
	background-color:green;
	color:white;
}

nav>button:disabled {
	background-color:red;
}

.themeSelect {
	float:right;
}

#aceThemeSelect {
	float:right;
}

#aceThemeSelect {
	border: 1px solid #999 !important;  /*Removes border*/
	-webkit-appearance: none;  /*Removes default chrome and safari style*/
	-moz-appearance: none; /* Removes Default Firefox style*/
	padding: 5px;
	border-radius:0px;
	background: #fff url(${staticRoot}/select_arrow.png) no-repeat 90% center;
	width: 180px;
}


#aceThemeSelect:focus {
  outline:none;
}



	
</style>
</head>
<body>
<form>
	<nav>
		
			<button type="button" disabled="disabled" id="saveBtn">save</button>
			<div class="themeSelect">
				<select id="aceThemeSelect">
					<option>ambiance</option>
					<option>chaos</option>
					<option>chrome</option>
					<option>clouds_midnight</option>
					<option>clouds</option>
					<option>cobalt</option>
					<option>crimson_editor</option>
					<option>dawn</option>
					<option>dreamweaver</option>
					<option>eclipse</option>
					<option>github</option>
					<option>idle_fingers</option>
					<option>katzenmilch</option>
					<option>kr_theme</option>
					<option>kr</option>
					<option>kuroir</option>
					<option>merbivore_soft</option>
					<option>merbivore</option>
					<option>mono_industrial</option>
					<option>monokai</option>
					<option>pastel_on_dark</option>
					<option>solarized_dark</option>
					<option>solarized_light</option>
					<option>terminal</option>
					<option>textmate</option>
					<option>tomorrow_night_blue</option>
					<option>tomorrow_night_bright</option>
					<option>tomorrow_night_eighties</option>
					<option>tomorrow_night</option>
					<option>tomorrow</option>
					<option>twilight</option>
					<option>vibrant_ink</option>
					<option>xcode</option>
				</select>
			</div>
	</nav>
</form>
	<div id="editor"><c:out value="<%=currentNode.getProperty("jcr:content/jcr:data").getString() %>" escapeXml="true" /></div>
	<form method="POST" id="updateForm" 
		action="${currentNode.path}/_jcr_content"
		ENCTYPE="MULTIPART/FORM-DATA">
		<input type="hidden" name=":redirect" value="${slingRequest.requestURL}?fileType=${param.fileType}&editType=${param.editType}" />
		<input type="hidden" name="jcr:data" value="" id="jcrData"/>
	</form>

	<script>
		var aceMode = "ace/mode/${param.fileType}";
	</script>
	<script type="text/javascript" src="${staticRoot}/jquery-2.1.1.min.js"></script>
	<script src="${staticRoot}/ace-1.1.7/src-min/ace.js" type="text/javascript" charset="utf-8"></script>
	<script src="${staticRoot}/common.js?t=<%=new java.util.Date().getTime() %>" type="text/javascript" charset="utf-8"></script>
	<script src="${staticRoot}/edit/file.js?t=<%=new java.util.Date().getTime() %>" type="text/javascript" charset="utf-8"></script>
</body>
</html>

