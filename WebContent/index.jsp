<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<title>智能健康状态评估中心-首页</title>
<link href="<%=basePath%>css/index.css" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<div id="container">
		<div id="banner">
			<img src="<%=basePath%>images/logo.png" />
		</div>
		<div id="globallink">
			<ul>

			</ul>
			<br />
		</div>
		<div id="main">
			<iframe id="frame1" src="<%=basePath%>Book/frontlist"
				name="OfficeMain" width="100%" height="100%" scrolling="yes"
				marginwidth=0 marginheight=0 frameborder=0 vspace=0 hspace=0>
			</iframe>
		</div>
		<div id="footer">
			<p>
				北京大学医学部 <a></a>&nbsp;&nbsp;<a href="<%=basePath%>login"><font
					color=red></font></a>
			</p>
		</div>
	</div>
</body>
</html>
