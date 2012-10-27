<%@page import="java.util.List"%>
<%@page import="com.osintegrators.example.Address"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Granny's Addressbook -- Search </title>
        <link type="text/css" rel="stylesheet" href="resources/grannystyle.css">
    </head>

    <body>
        <div id="outer">

            <div id="inner">

                <div id="content">

                    <form method="POST" action="">

                        <div id="nameEntry">
                            <div id="nameLabel" class="label">Name</div><input id="nameField" name="name" type="text" value=""/>
                        </div>
                        <div id="addressEntry">
                            <div id="addressLabel" class="label">Address</div><input id="addressField" name="address" type="text" value=""/>
                        </div>
                        <div id="phoneEntry">
                            <div id="phoneLabel" class="label">Phone</div><input id="phoneField" name="phone" type="text" value=""/>
                        </div>
                        <div id="emailEntry">
                            <div id="emailLabel" class="label">Email</div><input id="emailField" name="email" type="text" value=""/>
                        </div>
                        <div id="searchEntry">
                            <input id="searchButton" value="Search" type="button"/>
                        </div>

                    </form>

                </div> <!-- end content-->

            </div> <!-- end inner -->

        </div> <!-- end outer -->

    </body>

</html>