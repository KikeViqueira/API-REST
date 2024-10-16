package com.example.proyectoparte1.service;


import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User obtenerUsuario(String id){
        return userRepository.findById(id).orElse(null);
    }

    public List<User> obtenerTodosUsuarios() {
        return userRepository.findAll();
    }

    public User crearUsuario(User user){
        return userRepository.save(user);
    }


    public void eliminarUsuario(String id){
        userRepository.deleteById(id);
    }

        public User modificarUsuario(String id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            if(userDetails.getName() != null) {
                user.setName(userDetails.getName());
            }
            if(userDetails.getCountry() != null) {
                user.setCountry(userDetails.getCountry());
            }
            if(userDetails.getPicture() != null) {
                user.setPicture(userDetails.getPicture());
            }
            return userRepository.save(user);
        }).orElse(null);
    }

    public User anhadirAmigo(String id, User amigo) {
        return userRepository.findById(id).map(user -> {
            user.getFriends().add(amigo);
            return userRepository.save(user);
        }).orElse(null);
    }

    public User eliminarAmigo(String id, String friendId) {
        return userRepository.findById(id).map(user -> {
            Optional<User> amigoOptional = userRepository.findById(friendId);

            // Comprobar si el amigo existe
            if (amigoOptional.isEmpty()) {
                return null;  // No se encuentra el amigo con el friendId
            }
            User amigo = amigoOptional.get();
            // Remover al amigo de la lista de amigos
            user.getFriends().remove(amigo);
            // Guardar el usuario actualizado
            return userRepository.save(user);
        }).orElse(null);
    }


}
