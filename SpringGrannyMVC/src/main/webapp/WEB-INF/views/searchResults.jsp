<%@page import="java.util.List"%>
<%@page import="com.osintegrators.example.Address"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Granny's Addressbook -- Search Results</title>
        <link type="text/css" rel="stylesheet" href="resources/grannystyle.css">
    </head>

    <body>
        <div id="outer">

            <div id="inner">

                <h2>Granny's Addressbook Search Results</h2>

                <div id="content">

                    <c:forEach var="result" items="${searchResults}">
                        <div>
                            Name: ${result.name}
                            <br/>
                            Address: ${result.address}
                            <br/>
                            Phone: ${result.phone}
                            <br/>
                            Email: ${result.email}
                            </div>
                        <hr/>
                    </c:forEach>



                    <a href="<c:url value='/'/>">Back</a>

                </div> <!-- end content-->

            </div> <!-- end inner -->

        </div> <!-- end outer -->

    </body>

</html>