package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    public User obtenerUsuario(String email) {
        return userRepository.findById(email).orElse(null);
    }

    public List<User> obtenerTodosUsuarios() {
        return userRepository.findAll();
    }

    public User crearUsuario(User user) {
        return userRepository.save(user);
    }

    public void eliminarUsuario(String email) {
        userRepository.deleteById(email);
    }

    public User actualizarUsuario(User usuarioActualizado) {
        return userRepository.findById(usuarioActualizado.getEmail()).map(user -> userRepository.save(usuarioActualizado)).orElse(null);
    }

    public User eliminarAmigo(String email, String friendEmail) {
        return userRepository.findById(email).map(user -> {
            Optional<User> amigoOptional = userRepository.findById(friendEmail);

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

    public User anhadirAmigo(User mainUser, User friend){

        List<User> friendsList = mainUser.getFriends(); // Obtenemos la lista actual de amigos

        // Si la lista de amigos es null, creamos una nueva lista
        if (friendsList == null) {
            friendsList = new ArrayList<>();
        }
        else{
            //En el caso de que ya exista la lista de amigos , miramos si el usuario que queremos anhadir a amigo no lo es ya
            if(!friendsList.contains(friend)){
                friendsList.add(friend); // Añadimos el nuevo amigo a la lista
                mainUser.setFriends(friendsList); // Establecemos la lista actualizada de nuevo en el objeto
                //Guardamos la lista de amigos en la BD
                return userRepository.save(mainUser);
            }
        }
        return null;
    }
}

