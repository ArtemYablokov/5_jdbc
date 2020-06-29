package com.yabloko.servlets;

import com.yabloko.models.UserDto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SignUpServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/classes/db.properties")));
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");
            String driverClassName = properties.getProperty("db.driverClassName");
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //language=SQL
        String SQL_SELECT_ALL =
                "SELECT * FROM apple_user";

        List<UserDto> users = new ArrayList<>();

        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL);

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                UserDto userDto = new UserDto(firstName, lastName);
                users.add(userDto);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        req.setAttribute("usersFromServer", users);
        req.getServletContext().getRequestDispatcher("/jsp/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("first-name");
        String lastName = req.getParameter("last-name");

        try {
//            Statement statement = connection.createStatement();
//            String sqlInsert = "INSERT INTO fix_user(first_name, last_name)" +
//                    "VALUES('" + firstName + "','" + lastName + "');";
//            System.out.println(sqlInsert);
//            statement.execute(sqlInsert);

            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO " +
                            "apple_user(first_name, last_name) VALUES (?, ?)");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        doGet(req, resp);

    }
}