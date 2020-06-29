package com.yabloko.dao;

import com.yabloko.models.User;

import java.util.List;

public interface UsersDao extends CrudDao<User> {
    List<User> findAllByFirstName(String firstName);

    boolean saveRaw(String firstName, String lastName, String carModel);


    boolean isExist(String name, String password);
}



