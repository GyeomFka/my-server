package org.example.repository.impl;

import org.example.model.User;
import org.example.repository.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MemoryRepository {
    static ArrayList<User> userList = new ArrayList<>();

    static {
        userList.add(new User("aaa", "1234", "만겸", "aaaa@gmail.com"));

    }

    public static ArrayList<User> findAll() {
        return userList;
    }

    public static void addUser(User user) {
        userList.add(user);
    }

    public static User findUserById(String userId) {
        return userList.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElse(null);
    }

}
