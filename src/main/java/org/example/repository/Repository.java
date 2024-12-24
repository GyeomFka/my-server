package org.example.repository;

import org.example.model.User;

import java.util.ArrayList;

public interface Repository {

    ArrayList<User> findAll();

    void addUser(User user);

    User findUserById(String userId);
}
