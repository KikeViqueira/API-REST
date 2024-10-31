package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.UserService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;
    private final PatchUtils patchUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PatchUtils patchUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.patchUtils = patchUtils;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/{email}")
    //Solo pueden llamar al endpoint el admin, el propio usuario y sus amigos
    //authentication.name: Identificador único del usuario (generalmente username o email), configurable en UserDetailsService.
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name or @userService.isAmigo(authentication.name, #email)")
    // Obtener un usuario mediante su ID

    public ResponseEntity<EntityModel<User>> obtenerUsuario(@PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            //En el caso de que el usuario no se encuentre en la BD mostramos 404 ya que no se ha encontrado
            return ResponseEntity.notFound().build();
        }

        //Tenemos que devolver link a si mismo y a la lista de todos
        EntityModel<User> resource = EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuario(email)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
        );
        return ResponseEntity.ok(resource);
    }

    // Obtener todos los usuarios
    @GetMapping
    //Pueden llamar a este endpoint todos los usuarios logeados
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedModel<EntityModel<User>>> obtenerUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "email") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        //Comprobamos que el numero de pagina y el tamaño de cada una de ellas es mayor a cero, si lo es pondremos los valores por defecto para evitar errores
        if (page<0 || size <= 0) {
            page = 0;
            size = 10;
        }



        Page<User> usuariosObtenidos = userService.obtenerTodosUsuarios(page, size, sortBy, direction);
        //En el caso de que no se devuelvan usuarios al usuario que los solicita le mandamos un mensaje de que no hay el contenido solicitado
        if (usuariosObtenidos.isEmpty()) return ResponseEntity.noContent().build();


        // Transformamos cada User en un EntityModel<User>, para poder devolver un enlace a si mismo
        List<EntityModel<User>> usuarioModels = usuariosObtenidos.getContent().stream()
                .map(user -> EntityModel.of(user,
                        WebMvcLinkBuilder.linkTo(methodOn(UserController.class).obtenerUsuario(user.getEmail())).withSelfRel()))
                .collect(Collectors.toList());

        
        //Tenemos que devolver los siguientes links: A si mesmo, a primeira, seguinte, anterior e última páxina, e a un recurso concreto.
        // Creamos el PagedModel usando la lista de EntityModel y añadimos los links de navegación
        PagedModel<EntityModel<User>> pagedModel = PagedModel.of(
                usuarioModels,
                new PagedModel.PageMetadata(usuariosObtenidos.getSize(), usuariosObtenidos.getNumber(), usuariosObtenidos.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(page, size, sortBy, direction)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, size, sortBy, direction)).withRel("first-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(page + 1, size, sortBy, direction)).withRel("next-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(usuariosObtenidos.getTotalPages() - 1, size, sortBy, direction)).withRel("last-page"),
                WebMvcLinkBuilder.linkTo(UserController.class).slash("{email}").withRel("get-user")
        );

        // Condición para agregar el enlace de "página anterior" solo si page > 0
        if (page > 0) {
            pagedModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    // Crear un usuario.Puede acceder cualquier persona
    @PostMapping
    /*Si el User no cumple con las validaciones, Spring automáticamente devuelve una respuesta de error con un código de estado 400 (Bad Request)
     sin necesidad de escribir código adicional.*/
    public ResponseEntity<?> crearUsuario(@RequestBody @Valid User user) {

        // Verificar si el usuario ya existe
        if (userService.obtenerUsuario(user.getEmail()) != null) {
            return ResponseEntity.status(409).body("El usuario ya existe.");
        }

        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Guardar el nuevo usuario en la base de datos
        userService.crearUsuario(user);

        //tenemos que devolver los links a si mismo y a la lista de todos
         EntityModel<User> recurso = EntityModel.of(
                 user,
                 WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).crearUsuario(user)).withSelfRel(),
                 WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
         );

         //Creamos un mapa para enviar el contenido del body correspondiente y a mayores el mensaje de que el usuario se ha creado correctamente
        Map<String, Object> mapaRespuesta = Map.of(
                "mensaje", "Usuario registrado correctamente",
                "recurso", recurso
        );

        return ResponseEntity.status(201).body(mapaRespuesta);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{email}")
    //Solo el propio usuario
    @PreAuthorize("#email == authentication.name")
    public ResponseEntity<EntityModel<String>> eliminarUsuario(@PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(usuario);

        //El tipo del EntityModel siempre coincide con el del primer objeto que se le pasa, en este caso un String
        EntityModel<String> recurso = EntityModel.of(
                "Usuario eliminado correctamente",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
        );

        //Devolvemos al user un código de éxito, diciendo que el usuario se ha eliminado correctamente y un enlace a la lista de todos los users
        return ResponseEntity.ok(recurso);
    }

    @PatchMapping(path = "/{email}")
    //Solo el propio usuario
    @PreAuthorize("#email == authentication.name")
    public ResponseEntity<?> modificarUsuario(@PathVariable("email") String email, @RequestBody List<Map<String, Object>> updates) {
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

            //tenemos que devolver a mayores del usuario links a si mismo y a la lista de todos los usuarios
            //Tenemos que devolver link a si mismo y a la lista de todos
            EntityModel<User> resource = EntityModel.of(usuarioModificado,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).modificarUsuario(usuario.getEmail(), updates)).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
            );

            //Indicamos que la operación de actualización se ha realizado con éxito
            return ResponseEntity.ok(resource);

        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Eliminar un amigo del usuario
    @DeleteMapping("/{email}/friends/{friendEmail}")
    //Solo el propio usuario
    @PreAuthorize("#email == authentication.name")
    public ResponseEntity<?> eliminarAmigo(@PathVariable("email") String email, @PathVariable("friendEmail") String friendEmail) {
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
    //Solo el propio usuario
    @PreAuthorize("#email == authentication.name")
    public ResponseEntity<?> anhadirAmigo(@PathVariable String email, @RequestBody @Valid User friend ) {
        //Comprobamos que el user que esta intentando anhadir a amigo a otro user que existe y que no se esté intentando anhadir a el mismo como amigo
        
        System.out.println("Hola");
        
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