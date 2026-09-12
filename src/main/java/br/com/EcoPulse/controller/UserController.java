package br.com.EcoPulse.controller;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.service.UserService;
import java.util.List;

public class UserController {
    private final UserService userService;
    public UserController() { ConnectionFactory.initializeDatabase(); this.userService = new UserService(); }
    public User createUser(User user) { return userService.create(user); }
    public List<User> listUsers() { return userService.getAll(); }
    public User findUser(Long id) { return userService.findById(id).orElse(null); }
    public User updateUser(User user) { return userService.update(user); }
    public boolean removeUser(Long id) { return userService.delete(id); }
}
