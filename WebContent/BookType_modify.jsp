<%@ page language="java" import="java.util.*"  contentType="text/html;charset=gb2312"%> 
<%@ page import="com.shuangyulin.domain.BookType" %>
 <%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    BookType bookType = (BookType)request.getAttribute("bookType");

    String username=(String)session.getAttribute("username");
    if(username==null){
        response.getWriter().println("<script>top.location.href='" + basePath + "login';</script>");
    }
%>
<HTML><HEAD><TITLE>�޸�ͼ������</TITLE>
<STYLE type=text/css>
BODY {
	MARGIN-LEFT: 0px; BACKGROUND-COLOR: #ffffff
}
.STYLE1 {color: #ECE9D8}
.label {font-style.:italic; }
.errorLabel {font-style.:italic;  color:red; }
.errorMessage {font-weight:bold; color:red; }
</STYLE>
 <script src="<%=basePath %>calendar.js"></script>
<script language="javascript">
/*��֤����*/
function checkForm() { 
 
    var bookTypeName = document.getElementById("bookTypeName").value;
    if(bookTypeName=="") {
        alert('�������������!');
        return false;
    }

    var days = document.getElementById("days");
	var re = /^[1-9]\d*$/; 
	if (!re.test(days.value))
    {
       alert("��������ȷ������!"); 
       days.focus();
       return false;
    }
 
    return true; 
}
 </script>
</HEAD>
<BODY background="<%=basePath %>images/adminBg.jpg"> 
<TABLE align="center" height="100%" cellSpacing=0 cellPadding=0 width="80%" border=0>
  <TBODY>
  <TR>
    <TD align="left" vAlign=top >
    <sf:form method="post" modelAttribute="bookType" onsubmit="return checkForm();">  
<table width='100%' cellspacing='1' cellpadding='3' class="tablewidth">
  <tr>
    <td width=30%>ͼ�����:</td>
    <td width=70%> <sf:input path="bookTypeId" size="8" readOnly="true" /> </td>
  </tr>

  <tr>
    <td width=30%>�������:</td>
    <td width=70%>    <sf:input path="bookTypeName" size="18"/> 
     <sf:errors path="bookTypeName" cssStyle="color:red" /></td>
  </tr>

  <tr>
    <td width=30%>�ɽ�������:</td>
    <td width=70%>   <sf:input path="days" size="18"/> 
     <sf:errors path="days" cssStyle="color:red" /></td>
  </tr>

  <tr bgcolor='#FFFFFF'>
      <td colspan="4" align="center">
        <input type='submit' name='button' value='����' >
        &nbsp;&nbsp;
        <input type="reset" value='��д' />
      </td>
    </tr>

</table>
</sf:form>
   </TD></TR>
  </TBODY>
</TABLE>
</BODY>
</HTML>