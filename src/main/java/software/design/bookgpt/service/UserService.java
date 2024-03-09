package software.design.bookgpt.service;

import software.design.bookgpt.entity.User;

import java.util.List;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();
}