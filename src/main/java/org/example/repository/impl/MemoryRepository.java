package org.example.repository.impl;

import org.example.model.User;
import org.example.repository.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MemoryRepository implements Repository {
    ArrayList<User> userList = new ArrayList<>();

    public MemoryRepository() {
        userList.add(new User("aaa", "1234", "만겸", "aaaa@gmail.com"));
    }

    @Override
    public ArrayList<User> findAll() {
        return userList;
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }

    @Override
    public User findUserById(String userId) {
        return userList.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElse(null);
    }

}
