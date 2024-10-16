package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.UserService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;
    private final PatchUtils patchUtils;

    @Autowired
    public UserController(UserService userService, PatchUtils patchUtils) {
        this.userService = userService;
        this.patchUtils = patchUtils;
    }

    // Obtener un usuario mediante su ID
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable String id) {
        User usuario = userService.obtenerUsuario(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> obtenerUsuarios() {
        List<User> usuariosObtenidos = userService.obtenerTodosUsuarios();
        return ResponseEntity.ok(usuariosObtenidos);
    }

    // Crear un usuario
    @PostMapping
    public ResponseEntity<User> crearUsuario(@RequestBody @Valid User user) {
        User usuario = userService.crearUsuario(user);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        //Creamos la URI del recurso creado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").buildAndExpand(usuario).toUri();
        //Devolvemos el código 201para indicar que el recurso se ha creado con éxito
        return ResponseEntity.created(location).body(usuario);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        User usuario = userService.obtenerUsuario(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<?> modificarUsuario(@PathVariable String userId, @RequestBody List<Map<String, Object>> updates) {
        try {
            //Recorremos la lista de atributos a actualizar buscando que no esté el email o el aniversario
            for (Map<String, Object> update : updates) {
                String path = (String) update.get("path");
                if (path != null && path.equals("/email")) {
                    // Crear un mensaje de error para el caso del email
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "El campo 'email' no se puede modificar.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                } else if (path != null && path.equals("/birthday")) {
                    // Crear un mensaje de error para el caso de la fecha de cumpleaños
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "El campo 'birthday' no se puede modificar.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }

            User usuario = userService.obtenerUsuario(userId);
            //Si no se encuentra al usuario se devuelve un código 404 notFound
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            //Aplicamos las modificaciones a el objeto User deseado invocando al método patch
            User usuarioModificado = patchUtils.patch(usuario, updates);
            userService.actualizarUsuario(usuarioModificado);
            //Indicamos que la operación de actualización se ha realizado con éxito
            return ResponseEntity.ok(usuarioModificado);
        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
        return ResponseEntity.ok(eliminado);
    }

    @PostMapping("/{userId}/friends")
    public ResponseEntity<?> anhadirAmigo(@PathVariable String userId, @RequestBody @Valid User friend ) {
        //Comprobamos que los dos ususarios existen
        User usuario = userService.obtenerUsuario(userId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        User amigo = userService.obtenerUsuario(friend.getId());
        //Comprobamos que el amigo no sea null y si existe que todos los atributos tengan el mismo valor
        if(amigo == null || !amigo.getName().equals(friend.getName()) || !amigo.getEmail().equals(friend.getEmail())) {
            return ResponseEntity.notFound().build();
        }

        // Comprobar que todos los atributos del amigo proporcionado coinciden con el amigo de la base de datos, dependiendo si estos se han pasado o no en el Request ya que no son obligatorios.
        if (friend.getCountry() != null && !amigo.getCountry().equals(friend.getCountry())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El país del amigo no coincide.");
        }
        if (friend.getPicture() != null && !amigo.getPicture().equals(friend.getPicture())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La imagen del amigo no coincide.");
        }
        if (friend.getBirthday() != null && !amigo.getBirthday().equals(friend.getBirthday())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La fecha de nacimiento del amigo no coincide.");
        }

        //Intentamos anhadir el amigo al usuario que acepta la solicitud de amistad
        usuario = userService.anhadirAmigo(usuario, amigo);
        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El amigo ya existe en la lista de amigos.");
        }
        return ResponseEntity.ok(usuario);
    }
}