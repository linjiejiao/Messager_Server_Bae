<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body><br><br>
    Server is running! Connect to : 111.206.45.12:30302<br><br>
    default test users:<br>
    id:123    password:123<br>
    id:456    password:456<br>
    id:789    password:789<br>
    id:888    password:abc<br>
    <br>
    test apk:				<a href="http://bcs.duapp.com/logsbucket/Messager.apk"><span>http://bcs.duapp.com/logsbucket/Messager.apk</span></a><br>
    test apk code:			<a href="https://github.com/linjiejiao/Messager.git"><span>https://github.com/linjiejiao/Messager.git</span></a><br>
    test server code(PC):	<a href="https://github.com/linjiejiao/Messager_Server.git"><span>https://github.com/linjiejiao/Messager_Server.git</span></a><br>
    test server code(BAE):	<a href="https://github.com/linjiejiao/Messager_Server_Bae.git"><span>https://github.com/linjiejiao/Messager_Server_Bae.git</span></a><br>
  </body>
</html>
