<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ZAuthDemo - Message</title>
</head>
<body>

Message: <pre><%= request.getAttribute("timedMessage") %></pre>
<br/>
The page will automatically redirect in 10 seconds. Redirecting...

<script type="text/javascript">
setTimeout(function(){
	document.location.href="/";
}, 10000);
</script>
</body>
</html>