package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.data.domain.Page;
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

    public Page<User> obtenerTodosUsuarios(int page, int size, String sortBy, String direction) {
        //Creamos el objeto Pageable
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        return userRepository.findAll(pageable);
    }

    public User crearUsuario(User user) {
        return userRepository.save(user);
    }

    public void eliminarUsuario(User user) {
        String sortBy = "id", direction = "DESC";
        //Una vez eliminamos al usuario tenemos que mirar todos los usqarios de nuestra BD para ver si el usario eliminado era amigo de alguien
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.fromString(direction), sortBy);
        Page<User> usuariosAmigos = userRepository.findByFriendsEmailContaining(user.getEmail(), pageRequest);

        if(usuariosAmigos!=null && !usuariosAmigos.isEmpty()){
            //Recorremos la page eliminando en cada uno de los amigos de los usuarios al user eliminado
            usuariosAmigos.forEach(userAmigo -> {
                eliminarAmigo(userAmigo, user);
            });
        }
        //Por ultimo eliminamos al usuario de todo
        userRepository.deleteById(user.getEmail());
    }

    public User actualizarUsuario(User usuarioActualizado) {
        /*volver a llamar a findById es innecesario si ya se ha verificado previamente que el usuario existe y no se ha modificado el email (que actúa como ID en la base de datos).
         Si la existencia del usuario ya se confirmó y los campos restringidos (email y birthday) no han cambiado, puedes simplemente llamar a save directamente con usuarioActualizado.*/

        //return userRepository.findById(usuarioActualizado.getEmail()).map(user -> userRepository.save(usuarioActualizado)).orElse(null);
        return userRepository.save(usuarioActualizado);
    }

    public User eliminarAmigo(User mainUser, User amigoEliminar) {
        //Obtenemos la lista de amigos del usuario
        List<User> friendList = mainUser.getFriends();

        //Verificamos si el amigo a eliminar está en la lista de amigos
        if (friendList != null && friendList.contains(amigoEliminar)) {
            friendList.remove(amigoEliminar);
            mainUser.setFriends(friendList);
            return userRepository.save(mainUser);
        }

        //Si el usuario a eliminar no estaba en la lista devolvemos null
        return null;

    }

    public User anhadirAmigo(User mainUser, User friend){

        List<User> friendsList = mainUser.getFriends(); // Obtenemos la lista actual de amigos

        // Si la lista de amigos es null, creamos una nueva lista
        if (friendsList == null) {
            friendsList = new ArrayList<>();

            //Creamos una nueva instacia de user para guardar en la lista solo el email y el nombre
            User friendFiltrado = new User();
            friendFiltrado.setEmail(friend.getEmail());
            friendFiltrado.setName(friend.getName());
            friendsList.add(friendFiltrado);


            mainUser.setFriends(friendsList);
            return userRepository.save(mainUser);
        }
        else{
            //En el caso de que ya exista la lista de amigos , miramos si el usuario que queremos anhadir a amigo no lo es ya
            if(!friendsList.contains(friend)){
                //Creamos una nueva instacia de user para guardar en la lista solo el email y el nombre
                User friendFiltrado = new User();
                friendFiltrado.setEmail(friend.getEmail());
                friendFiltrado.setName(friend.getName());
                friendsList.add(friendFiltrado);
                mainUser.setFriends(friendsList); // Establecemos la lista actualizada de nuevo en el objeto
                //Guardamos la lista de amigos en la BD
                return userRepository.save(mainUser);
            }
        }
        return null;
    }


    public Boolean isAmigo(String mainEmail, String friendEmail) {

        /*
        No es necesario comprobar si mainEmail y friendEmail son el mismo dentro de la función isAmigo, porque ya estás manejando ese caso en la condición
         de @PreAuthorize con #email == authentication.principal.username. Eso significa que la función isAmigo solo se llama si el usuario autenticado no
          es el propio usuario, y solo necesita verificar si el usuario es amigo del propietario de la información.
        */

        // Recuperamos a los usuarios
        User mainUser = userRepository.findById(mainEmail).orElse(null);
        User friendUser = userRepository.findById(friendEmail).orElse(null);

        // Si cualquiera de los usuarios no existe o si en la lista de amigos del user que estamos intentando obtener es null, devolvemos false
        if (mainUser == null || friendUser == null || friendUser.getFriends() == null) {
            return false;
        }

        // Verificamos si mainUser está en la lista de amigos de el usuario objetivo, en este caso friendUser
        for (User friend : friendUser.getFriends()) {
            if (friend.getEmail().equals(mainEmail)) { // Compara con mainEmail para ver si el usuario es amigo
                return true;
            }
        }

        // Si no está en la lista, no son amigos
        return false;
    }
}

