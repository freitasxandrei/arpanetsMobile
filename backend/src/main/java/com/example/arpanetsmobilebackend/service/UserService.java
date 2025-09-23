package com.example.arpanetsmobilebackend.service;

import com.example.arpanetsmobilebackend.model.User;
import com.example.arpanetsmobilebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.findByName(user.getName()) != null) {
            throw new RuntimeException("O nome de usuário '" + user.getName() + "' já está em uso.");
        }
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String name, String password) {
        User user = userRepository.findByName(name);

        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}

