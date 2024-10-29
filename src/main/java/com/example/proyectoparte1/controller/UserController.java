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
    @GetMapping("/{email}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            //En el caso de que el usuario no se encuentre en la BD mostramos 404 ya que no se ha encontrado
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> obtenerUsuarios() {
        List<User> usuariosObtenidos = userService.obtenerTodosUsuarios();
        //KWORD En el caso de que no se devuelvan usuarios al usuario que los solicita le mandamos un mensaje de que no hay el contenido solicitado
        if (usuariosObtenidos == null || usuariosObtenidos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(usuariosObtenidos);
    }

    // Crear un usuario
    @PostMapping
    /*Si el User no cumple con las validaciones, Spring automáticamente devuelve una respuesta de error con un código de estado 400 (Bad Request)
     sin necesidad de escribir código adicional.*/
    public ResponseEntity<User> crearUsuario(@RequestBody @Valid User user) {
        User usuario = userService.crearUsuario(user);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        //Creamos la URI del recurso creado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").buildAndExpand(usuario).toUri();
        //Devolvemos el código 201 para indicar que el recurso se ha creado con éxito
        return ResponseEntity.created(location).body(usuario);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(email);
        //Devolvemos al user un código de éxito y en este caso es un noContent ya que no hace falta devolverle info y se ahorra ancho de banda, lo cual nos hace un sistema más eficiente
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{email}")
    public ResponseEntity<?> modificarUsuario(@PathVariable String email, @RequestBody List<Map<String, Object>> updates) {
        /*Updates es una lista que contiene pares de clave valor, donde:
        * String hace referencia al atributo que se le va a aplicar las actualizaciones
        * El objecto en este caso es el nuevo valor que se meterá en dicho atributo del user*/
        try {
            /*Antes de ponernos a comprobar que campos del user se van a actualizar o no, tenemos que mirar
            * si el propio usuario al que se le van a aplicar las modificaciones existe, esto hace un código más eficiente en comprobaciones
            * y flujo de código*/
            User usuario = userService.obtenerUsuario(email);
            //Si no se encuentra al usuario se devuelve un código 404 notFound
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

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
            //Aplicamos las modificaciones a el objeto User deseado invocando al método patch
            User usuarioModificado = patchUtils.patch(usuario, updates);
            userService.actualizarUsuario(usuarioModificado);
            //Indicamos que la operación de actualización se ha realizado con éxito
            return ResponseEntity.ok(usuarioModificado);
        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Eliminar un amigo del usuario
    @DeleteMapping("/{email}/friends/{friendEmail}")
    public ResponseEntity<?> eliminarAmigo(@PathVariable String email, @PathVariable String friendEmail) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        User amigo = userService.obtenerUsuario(friendEmail);
        if (amigo == null) {
            return ResponseEntity.notFound().build();
        }
        //Comprobamos qie el amigo que se quiere eliminar no sea el propio user
        if (usuario.getEmail().equals(amigo.getEmail())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No puedes eliminarte a ti mismo de tu lista de amigos.");

        //Intentamos eliminar al amigo de la lista de amigos del user
        User usuarioConNuevaLista = userService.eliminarAmigo(usuario, amigo);
        if (usuarioConNuevaLista == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: El amigo no está en la lista de amigos.");
        }
        return ResponseEntity.ok(usuarioConNuevaLista);
    }

    @PostMapping("/{email}/friends")
    public ResponseEntity<?> anhadirAmigo(@PathVariable String email, @RequestBody @Valid User friend ) {
        //Comprobamos que el user que esta intentando anhadir a amigo a otro user existe y que no se esté intentando anhadir asi mismo como amigo
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        //En ela caso de que el user que envia amistad coincide con el destinatario, mostramos error al solicitante
        if (usuario.getEmail().equals(friend.getEmail())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No puedes añadirte a ti mismo como amigo.");

        //Comprobamos ahora si que el amigo exista
        User amigo = userService.obtenerUsuario(friend.getEmail());

        //Comprobamos que el amigo no sea null y si existe que todos los atributos tengan el mismo valor
        if(amigo == null || !amigo.getEmail().equals(friend.getEmail()) || !amigo.getName().equals(friend.getName())) {
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