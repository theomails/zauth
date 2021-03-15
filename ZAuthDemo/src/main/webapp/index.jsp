<!DOCTYPE html>
<%@page import="net.progressit.zauthdemo.servlets.OAuthServlet"%>
<%!
private Object nvl(Object str){
	return str==null?"":str;
}
%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="content-type"
	content="application/xhtml+xml; charset=UTF-8" />
<title>Zoho OAuth Demo</title>

<!-- All custom styles -->
<link rel="stylesheet" href="css/styles.css">

<!-- Server Data from HttpSession -->
<script>
	var GlobalConstants = {
			clientTypeClientId:'<%= OAuthServlet.clientTypeClientId %>',
			clientTypeClientSecret:'<%= OAuthServlet.clientTypeClientSecret %>', 
			clientTypeScope:'<%= OAuthServlet.clientTypeScope %>',
			clientTypeRedirectUri:'<%= OAuthServlet.clientTypeRedirectUri %>',
			clientTypeAuthUrl:'',
			clientTypeAuthRefreshUrl:'',
			//server
			serverTypeClientId:'<%= OAuthServlet.serverTypeClientId %>',
			serverTypeClientSecret:'<%= OAuthServlet.serverTypeClientSecret %>', 
			serverTypeScope:'<%= OAuthServlet.serverTypeScope %>',
			serverTypeRedirectUri:'<%= OAuthServlet.serverTypeRedirectUri %>',
			serverTypeAuthUrl:'',
			serverTypeAuthRefreshUrl:'',
			serverCode:'',
			serverRefreshToken:'',
			serverAccessToken:''
	};
	GlobalConstants.clientTypeAuthUrl = 'https://accounts.zoho.com/oauth/v2/auth'
			+ '?client_id=' + GlobalConstants.clientTypeClientId  
			+ '&response_type=token' 
			+ '&scope=' + GlobalConstants.clientTypeScope 
			+ '&redirect_uri=' + GlobalConstants.clientTypeRedirectUri
			+ '&state=101'; //Is this mandatory for refresh alone? 	
	GlobalConstants.clientTypeAuthRefreshUrl = 'https://accounts.zoho.com/oauth/v2/auth/refresh'
		+ '?client_id=' + GlobalConstants.clientTypeClientId  
		+ '&response_type=token' 
		+ '&scope=' + GlobalConstants.clientTypeScope 
		+ '&redirect_uri=' + GlobalConstants.clientTypeRedirectUri;
	
	GlobalConstants.serverTypeAuthUrl = 'https://accounts.zoho.com/oauth/v2/auth'
		+ '?client_id=' + GlobalConstants.serverTypeClientId  
		+ '&response_type=code' 
		+ '&scope=' + GlobalConstants.serverTypeScope 
		+ '&redirect_uri=' + GlobalConstants.serverTypeRedirectUri
		+ '&access_type=offline' //Is this allowed 	
		+ '&state=101' //Is this allowed 	
		+ '&prompt=consent'; //Is this mandatory? Can access_type - offline be used here?
	GlobalConstants.serverTypeAuthRefreshUrl = 'https://accounts.zoho.com/oauth/v2/auth/refresh'
		+ '?client_id=' + GlobalConstants.serverTypeClientId  
		+ '&response_type=token' 
		+ '&scope=' + GlobalConstants.serverTypeScope 
		+ '&redirect_uri=' + GlobalConstants.serverTypeRedirectUri;
	
	GlobalConstants.serverCode = '<%= nvl(session.getAttribute("server:code")) %>';
	GlobalConstants.serverRefreshToken = '<%= nvl(session.getAttribute("server:refresh_token")) %>';
	GlobalConstants.serverAccessToken = '<%= nvl(session.getAttribute("server:access_token")) %>';
</script>

<script src="https://cdn.jsdelivr.net/npm/vue@2/dist/vue.js"></script>
<jsp:include page="components.jspf"></jsp:include>
</head>

<body>
	<div id=app>
		<main-content></main-content>
	</div>
	
	<script>
        new Vue({
            el: '#app',
            data: { hello: 'Hello World!' }
        })
    </script>
</body>
</html>