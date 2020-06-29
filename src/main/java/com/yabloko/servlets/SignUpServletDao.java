package com.yabloko.servlets;

import com.yabloko.dao.UsersDao;
import com.yabloko.dao.UsersDaoJdbcTemplateImpl;
import com.yabloko.models.User;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@WebServlet("/signup-dao")
public class SignUpServletDao extends HttpServlet {
    private UsersDao usersDao;

    @Override
    public void init() throws ServletException {
        Properties properties = new Properties();
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        try {
            properties.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/classes/db.properties")));
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");
            String driverClassName = properties.getProperty("db.driverClassName");

            driverManagerDataSource.setUsername(dbUsername);
            driverManagerDataSource.setPassword(dbPassword);
            driverManagerDataSource.setUrl(dbUrl);
            driverManagerDataSource.setDriverClassName(driverClassName);

            usersDao = new UsersDaoJdbcTemplateImpl(driverManagerDataSource);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Optional<User> user = usersDao.find(1);

        List<User> users = usersDao.findAll();

        int i = 0;

//        List<User> users = null;
        if (req.getParameter("firstname") != null) {
            String firstName = req.getParameter("firstname");
            users = usersDao.findAllByFirstName(firstName);
        }
        else if (req.getParameter("id") != null) {
            String id = req.getParameter("id");
            users = Collections.singletonList( usersDao.find(Integer.parseInt(id)).get() );
        }
        else {
            users = usersDao.findAll();
        }
        req.setAttribute("usersFromServer", users);
        req.getServletContext().getRequestDispatcher("/jsp/signupDao.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("first-name");
        String lastName = req.getParameter("last-name");
        String carModel = req.getParameter("car-model");

        usersDao.saveRaw( firstName, lastName, carModel );

        doGet(req, resp);

    }
}