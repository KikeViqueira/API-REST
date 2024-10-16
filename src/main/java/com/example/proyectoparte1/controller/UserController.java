package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.repository.UserRepository;
import com.example.proyectoparte1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Obtener un usuario mediante su email
    /*** CREO QUE ESTO HAY QUE CAMBIARLO POR ID ***/
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable String id){
        User usuario = userService.obtenerUsuario(id);
        if(usuario == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }


    //Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> obtenerUsuarios(){
        List<User> usuariosObtenidos = userService.obtenerTodosUsuarios();

        //if(usuariosObtenidos.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuariosObtenidos);
    }

    @PostMapping
    public ResponseEntity<User> crearUsuario(@RequestBody User user){
        User usuario = userService.crearUsuario(user);

        //Cambiar reponse de error
        if(usuario == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }


    /*** CREO QUE ESTO HAY QUE CAMBIARLO POR ID ***/
    @DeleteMapping("/{id}")
    public ResponseEntity<User> eliminarUsuario(@PathVariable String id){
        User usuario = userService.obtenerUsuario(id);
        if(usuario == null){
            ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    /*** CREO QUE ESTO HAY QUE CAMBIARLO POR ID ***/
    @PatchMapping("/{userId}")
    public ResponseEntity<User> modificarUsuario(@PathVariable String userId, @RequestBody User user){
        User usuario = userService.obtenerUsuario(userId);
        if(usuario == null){
            ResponseEntity.notFound().build();
        }
        //En el caso de que los datos a cambiar sean el email o el aniversario damos un error por pantalla
        if(user.getEmail() != null || user.getBirthday() != null){
            return ResponseEntity.badRequest().build();
        }
        User usuarioModificado = userService.modificarUsuario(userId,user);
        if (usuarioModificado != null){
            return ResponseEntity.ok(usuarioModificado);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar un amigo del usuario
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<User> eliminarAmigo(@PathVariable String userId, @PathVariable String friendId) {
        User usuario = userService.obtenerUsuario(userId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        User eliminado = userService.eliminarAmigo(userId, friendId);
        if (eliminado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }
}
