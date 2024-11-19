package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.UserService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "User API", description = "Operaciones relacionadas con usuarios")
@SecurityRequirement(name = "JWT")
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
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name or @userService.isAmigo(authentication.name, #email)")
    @Operation(
            operationId = "obtenerUsuario",
            summary = "Obtener detalles de un usuario",
            description = "Obtiene los detalles de un usuario específico. Solo accesible por el administrador, el propio usuario o sus amigos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalles del usuario",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerUsuario",
                                            description = "Link al usuario actual",
                                            parameters = @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email")
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allUsers",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a todos los usuarios",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<User>> obtenerUsuario(
            @Parameter(description = "Correo del usuario", required = true) @PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<User> resource = EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuario(email)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
        );
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "obtenerUsuarios",
            summary = "Obtener lista de usuarios paginada",
            description = "Obtiene una lista paginada de usuarios, accesible para todos los usuarios autenticados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de usuarios",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedModel.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a la página actual",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "nextPage",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a la siguiente página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page + 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "prevPage",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a la página anterior",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "firstPage",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a la primera página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "lastPage",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a la última página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$response.body.totalPages - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "userDetails",
                                            operationId = "obtenerUsuario",
                                            description = "Link a un usuario específico",
                                            parameters = @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$response.body.content[0].email")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "204", description = "No hay contenido"),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso")
            }
    )
    public ResponseEntity<PagedModel<EntityModel<User>>> obtenerUsuarios(
            @Parameter(description = "Página a obtener") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual ordenar") @RequestParam(defaultValue = "email") String sortBy,
            @Parameter(description = "Dirección de la ordenación") @RequestParam(defaultValue = "DESC") String direction) {
        if (page < 0 || size <= 0) {
            page = 0;
            size = 10;
        }
        Page<User> usuariosObtenidos = userService.obtenerTodosUsuarios(page, size, sortBy, direction);
        if (usuariosObtenidos.isEmpty()) return ResponseEntity.noContent().build();
        List<EntityModel<User>> usuarioModels = usuariosObtenidos.getContent().stream()
                .map(user -> EntityModel.of(user, WebMvcLinkBuilder.linkTo(methodOn(UserController.class).obtenerUsuario(user.getEmail())).withSelfRel()))
                .collect(Collectors.toList());

        PagedModel<EntityModel<User>> pagedModel = PagedModel.of(
                usuarioModels,
                new PagedModel.PageMetadata(usuariosObtenidos.getSize(), usuariosObtenidos.getNumber(), usuariosObtenidos.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(page, size, sortBy, direction)).withSelfRel()
        );

        if (page > 0) {
            pagedModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(page - 1, size, sortBy, direction)).withRel("prev-page"));
        }
        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @Operation(
            operationId = "crearUsuario",
            summary = "Crear un nuevo usuario",
            description = "Permite a cualquier persona crear un nuevo usuario en la aplicación.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario creado con éxito",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "crearUsuario",
                                            description = "Link al usuario creado",
                                            parameters = {
                                                @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.body.email")
                                            }

                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allUsers",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a todos los usuarios",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Datos de usuario no válidos", content = @Content)
            }
    )
    public ResponseEntity<?> crearUsuario(
            @Parameter(description = "Detalles del usuario a crear") @RequestBody @Valid User user) {
        if (userService.obtenerUsuario(user.getEmail()) != null) {
            return ResponseEntity.status(409).body("El usuario ya existe.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.crearUsuario(user);
        EntityModel<User> recurso = EntityModel.of(user,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).crearUsuario(user)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
        );
        Map<String, Object> mapaRespuesta = Map.of("mensaje", "Usuario registrado correctamente", "recurso", recurso);
        return ResponseEntity.status(201).body(mapaRespuesta);
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("#email == authentication.name")
    @Operation(
            operationId = "eliminarUsuario",
            summary = "Eliminar un usuario",
            description = "Permite al propio usuario eliminar su cuenta.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario eliminado con éxito",
                            content = @Content(mediaType = "application/json"),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allUsers",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a todos los usuarios",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar este usuario", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<User>> eliminarUsuario(
            @Parameter(description = "Correo del usuario a eliminar") @PathVariable String email) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        userService.eliminarUsuario(usuario);
        EntityModel<User> recurso = EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
        );
        return ResponseEntity.ok(recurso);
    }

    @PatchMapping(path = "/{email}")
    @PreAuthorize("#email == authentication.name")
    @Operation(
            operationId = "modificarUsuario",
            summary = "Modificar usuario",
            description = "Permite modificar atributos de la cuenta del usuario.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario modificado con éxito",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "modificarUsuario",
                                            description = "Link al usuario modificado",
                                            parameters = @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email")
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allUsers",
                                            operationId = "obtenerUsuarios",
                                            description = "Link a todos los usuarios",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Modificación no permitida", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para modificar este usuario", content = @Content)
            }
    )
    public ResponseEntity<?> modificarUsuario(
            @Parameter(description = "Correo del usuario a modificar") @PathVariable("email") String email,
            @Parameter(description = "Lista de cambios") @RequestBody List<Map<String, Object>> updates) {
        try {
            User usuario = userService.obtenerUsuario(email);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            for (Map<String, Object> update : updates) {
                String path = (String) update.get("path");
                if (path != null && path.equals("/email")) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "El campo 'email' no se puede modificar.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                } else if (path != null && path.equals("/password")) {
                    String rawPassword = (String) update.get("value");
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    update.put("value", encodedPassword);
                }
            }
            User usuarioModificado = patchUtils.patch(usuario, updates);
            userService.actualizarUsuario(usuarioModificado);
            EntityModel<User> resource = EntityModel.of(usuarioModificado,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).modificarUsuario(usuario.getEmail(), updates)).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).obtenerUsuarios(0, 10, "email", "DESC")).withRel("all-users")
            );
            return ResponseEntity.ok(resource);
        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{email}/friends/{friendEmail}")
    @PreAuthorize("#email == authentication.name")
    @Operation(
            summary = "Eliminar un amigo",
            description = "Permite al usuario eliminar un amigo de su lista.",
            operationId = "eliminarAmigo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amigo eliminado con éxito", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario o amigo no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de validación al intentar eliminar al propio usuario", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar este amigo", content = @Content)
    })
    public ResponseEntity<?> eliminarAmigo(
            @Parameter(description = "Correo del usuario") @PathVariable("email") String email,
            @Parameter(description = "Correo del amigo a eliminar") @PathVariable("friendEmail") String friendEmail) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        User amigo = userService.obtenerUsuario(friendEmail);
        if (amigo == null) {
            return ResponseEntity.notFound().build();
        }
        if (usuario.getEmail().equals(amigo.getEmail())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No puedes eliminarte a ti mismo de tu lista de amigos.");

        User usuarioConNuevaLista = userService.eliminarAmigo(usuario, amigo);
        if (usuarioConNuevaLista == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: El amigo no está en la lista de amigos.");
        }
        return ResponseEntity.ok(usuarioConNuevaLista);
    }

    @PostMapping("/{email}/friends")
    @PreAuthorize("#email == authentication.name")
    @Operation(
            summary = "Añadir un amigo",
            description = "Permite al usuario añadir un amigo existente a su lista de amigos.",
            operationId = "anhadirAmigo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amigo añadido con éxito", content = @Content(mediaType = "application/json",schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o amigo no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Intento de añadir al propio usuario como amigo", content = @Content),
            @ApiResponse(responseCode = "409", description = "El amigo ya existe en la lista de amigos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para añadir este amigo", content = @Content)
    })
    public ResponseEntity<?> anhadirAmigo(
            @Parameter(description = "Correo del usuario") @PathVariable String email,
            @Parameter(description = "Datos del amigo a añadir") @RequestBody @Valid User friend) {
        User usuario = userService.obtenerUsuario(email);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        if (usuario.getEmail().equals(friend.getEmail())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No puedes añadirte a ti mismo como amigo.");

        User amigo = userService.obtenerUsuario(friend.getEmail());
        if (amigo == null || !amigo.getEmail().equals(friend.getEmail()) || !amigo.getName().equals(friend.getName())) {
            return ResponseEntity.notFound().build();
        }
        if (friend.getCountry() != null && !amigo.getCountry().equals(friend.getCountry())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El país del amigo no coincide.");
        }
        if (friend.getPicture() != null && !amigo.getPicture().equals(friend.getPicture())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La imagen del amigo no coincide.");
        }
        if (friend.getBirthday() != null && !amigo.getBirthday().equals(friend.getBirthday())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La fecha de nacimiento del amigo no coincide.");
        }
        usuario = userService.anhadirAmigo(usuario, amigo);
        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El amigo ya existe en la lista de amigos.");
        }
        return ResponseEntity.ok(usuario);
    }
}
