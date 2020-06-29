<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
    <link href="/css/styles.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="form-style-2">
    <div class="form-style-2-heading">
        Already registered!
    </div>
    <table>
        <tr>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Cars</th>
        </tr>
        <c:forEach items="${usersFromServer}" var="user">
            <tr>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>
                    <c:forEach items="${user.cars}" var="car">
                        ${car.model}
                    </c:forEach>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<div class="form-style-2">
    <div class="form-style-2-heading">
        Please add user
    </div>
    <form method="post" action="/users-dao">
        <label for="first-name">First Name
            <input class="input-field" type="text" id="first-name" name="first-name">
        </label>
        <label for="last-name">Last Name
            <input class="input-field" type="text" id="last-name" name="last-name">
        </label>
        <label for="car-model">Last Name
            <input class="input-field" type="text" id="car-model" name="car-model">
        </label>
        <input type="submit" value="Add user">
    </form>
</div>
</body>
</html>
