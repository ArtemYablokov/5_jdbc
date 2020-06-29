package com.yabloko.dao;

import com.yabloko.models.Car;
import com.yabloko.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class UsersDaoJdbcImpl implements UsersDao {

    private Connection connection;

    public UsersDaoJdbcImpl(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //language=SQL
    private static String SQL_ALL =
            "SELECT apple_user.*, apple_car.id as car_id, apple_car.model " +
                    "FROM apple_user LEFT JOIN apple_car ON apple_user.id = apple_car.owner_id";


    //language=SQL
    private final String SQL_SELECT_ALL_BY_FIRST_NAME = SQL_ALL + " WHERE first_name = ?";

    @Override
    public List<User> findAllByFirstName(String firstName) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL_BY_FIRST_NAME);
            statement.setString(1, firstName);
            ResultSet resultSet = statement.executeQuery();

            Map<Integer, User> stringUserMap = fromResultSetByIdExtract(resultSet);

            List<User> userList = new ArrayList<>();
            for (Map.Entry<Integer, User> entry : stringUserMap.entrySet()) {
                if (firstName.equals(entry.getValue().getFirstName()))
                    userList.add(entry.getValue());
            }

            return userList;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //language=SQL
    private final String SQL_SELECT_BY_ID = SQL_ALL + " WHERE apple_user.id = ?";

    @Override
    public Optional<User> find(Integer id) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            Map<Integer, User> idUserMap = fromResultSetByIdExtract(resultSet);

            if (!idUserMap.isEmpty()) {

                return Optional.of(idUserMap.get(id));

            }
            return Optional.of(null);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_ALL);

            Map<Integer, User> idUserMap = fromResultSetByIdExtract(resultSet);
            return new ArrayList<>(idUserMap.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, User> fromResultSetByIdExtract(ResultSet resultSet) throws SQLException {

        Map<Integer, User> usersMap = new HashMap<>();
        while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            if (!usersMap.containsKey(id)) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(id, firstName, lastName, new ArrayList<>());
                usersMap.put(id, user);
            }
            Car car = new Car(resultSet.getInt("car_id"),
                    resultSet.getString("model"), usersMap.get(id));
            usersMap.get(id).getCars().add(car);
        }
        return usersMap;
    }

    //language=SQL
    private final String SQL_SAVE_RAW_USER =
            "INSERT INTO apple_user (first_name, last_name) VALUES (?,?)";
    //language=SQL
    private final String SQL_SAVE_RAW_CAR =
            "INSERT INTO apple_car (owner_id, model) VALUES (?, ?)";
    //language=SQL
    private final String SQL_MAX_ID_CAR =
            "SELECT * FROM apple_user ORDER BY id DESC LIMIT 1";

    @Override
    public boolean saveRaw(String firstName, String lastName, String carModel) {

        try {
            PreparedStatement preparedStatementUser = connection.prepareStatement(SQL_SAVE_RAW_USER);
            preparedStatementUser.setString(1, firstName);
            preparedStatementUser.setString(2, lastName);
            preparedStatementUser.execute();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_MAX_ID_CAR);
            resultSet.next();
            int owner_id = Integer.parseInt(resultSet.getString("id") );

            PreparedStatement preparedStatementCar = connection.prepareStatement(SQL_SAVE_RAW_CAR);
            preparedStatementCar.setInt(1, owner_id);
            preparedStatementCar.setString(2, carModel);
            preparedStatementCar.execute();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExist(String name, String password) {
        return false;
    }

    private Map<String, User> fromResultSetByFirstNameExtract(ResultSet resultSet) throws SQLException {

        Map<String, User> usersMap = new HashMap<>();
        while (resultSet.next()) {
            String firstName = resultSet.getString("first_name");
            if (!usersMap.containsKey(firstName)) {
                Integer id = resultSet.getInt("id");
                String lastName = resultSet.getString("last_name");

                User user = new User(id, firstName, lastName, new ArrayList<>());
                usersMap.put(firstName, user);
            }
            Car car = new Car(resultSet.getInt("car_id"),
                    resultSet.getString("model"), usersMap.get(firstName));

            usersMap.get(firstName).getCars().add(car);
        }
        return usersMap;
    }

    @Override
    public void save(User model) {

    }

    @Override
    public void update(User model) {

    }

    @Override
    public void delete(Integer id) {

    }
}