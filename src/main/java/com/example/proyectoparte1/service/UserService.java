package com.example.proyectoparte1.service;


import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User obtenerUsuario(String email){
        return userRepository.findById(email).orElse(null);
    }

    public List<User> obtenerTodosUsuarios() {
        return userRepository.findAll();
    }

    public User crearUsuario(User user){
        return userRepository.save(user);
    }


    public void eliminarUsuario(String email){
        userRepository.deleteById(email);
    }

    public User modificarUsuario(String email, User userDetails) {
        return userRepository.findById(email).map(user -> {
            user.setName(userDetails.getName());
            user.setCountry(userDetails.getCountry());
            user.setPicture(userDetails.getPicture());
            return userRepository.save(user);
        }).orElse(null);
    }

    public User anhadirAmigo(String email, User amigo) {
        return userRepository.findById(email).map(user -> {
            user.getFriends().add(amigo);
            return userRepository.save(user);
        }).orElse(null);
    }

    public User eliminarAmigo(String email, User amigo){
        return userRepository.findById(email).map(user -> {
            user.getFriends().remove(amigo);
            return userRepository.save(user);
        }).orElse(null);
    }

}
