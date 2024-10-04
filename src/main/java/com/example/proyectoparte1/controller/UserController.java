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
    @GetMapping("/{email}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable String email){
        User usuario = userService.obtenerUsuario(email);
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

    @DeleteMapping("/{email}")
    public ResponseEntity<User> eliminarUsuario(@PathVariable String email){
        User usuario = userService.obtenerUsuario(email);
        if(usuario == null){
            ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(email);
        return ResponseEntity.ok(usuario);
    }


    @PutMapping
    public ResponseEntity<User> modificarUsuario(@RequestBody String email, @RequestBody User user){
        User usuario = userService.obtenerUsuario(email);
        if(usuario == null){
            ResponseEntity.notFound().build();
        }
        User usuarioModificado = userService.modificarUsuario(email,user);
        if (usuarioModificado != null){
            return ResponseEntity.ok(usuarioModificado);
        }
        return ResponseEntity.notFound().build();
    }





}
