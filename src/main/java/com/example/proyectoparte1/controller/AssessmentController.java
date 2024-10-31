package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.AssessmentService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final PatchUtils patchUtils;

    @Autowired
    public AssessmentController(AssessmentService assessmentService, PatchUtils patchUtils) {
        this.assessmentService = assessmentService;
        this.patchUtils = patchUtils;
    }

    // Obtener comentarios de un usuario
    @GetMapping("/{email}")
    //Solo pueden llamar al endpoint el admin, el propio usuario y sus amigos
    //authentication.name: Identificador único del usuario (generalmente username o email), configurable en UserDetailsService.
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name or @userService.isAmigo(authentication.name, #email)")
    public ResponseEntity<PagedModel<Assessment>> obtenerComentariosUsuario(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        //Comprobamos que el numero de pagina y el tamaño de cada una de ellas es mayor a cero, si lo es pondremos los valores por defecto para evitar errores
        if (page<0 || size <= 0) {
            page = 0;
            size = 10;
        }

        //Creamos el objeto Pageable

        Page<Assessment> comentariosUsuario = assessmentService.obtenerComentariosUsuario(email, page, size, sortBy, direction);

        if (comentariosUsuario.isEmpty()) return ResponseEntity.noContent().build();


        // Obtenemos todos los comentarios de un usuario
        List<Assessment> commentsModel = comentariosUsuario.getContent().stream().toList();


        //Tenemos que devolver los siguientes links: Al usuario, a primeira, seguinte, anterior e última páxina, e a un recurso concreto.
        // Creamos el PagedModel usando la lista y añadimos los links de navegación
        PagedModel<Assessment> resource = PagedModel.of(
                commentsModel,
                new PagedModel.PageMetadata(comentariosUsuario.getSize(), comentariosUsuario.getNumber(), comentariosUsuario.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email, page, size, sortBy, direction)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email,0, size, sortBy, direction)).withRel("first-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email,page + 1, size, sortBy, direction)).withRel("next-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email,comentariosUsuario.getTotalPages() - 1, size, sortBy, direction)).withRel("last-page"),
                WebMvcLinkBuilder.linkTo(UserController.class).slash("{email}").withRel("get-user")
        );

        // Condición para agregar el enlace de "página anterior" solo si page > 0
        if (page > 0) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email, page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(resource);

    }


    // Obtener comentarios de una película
    @GetMapping("/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedModel<Assessment>> obtenerComentariosPelicula(
            @RequestParam(required = false) String movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        //Comprobamos que el numero de pagina y el tamaño de cada una de ellas es mayor a cero, si lo es pondremos los valores por defecto para evitar errores
        if (page<0 || size <= 0) {
            page = 0;
            size = 10;
        }

        //Creamos el objeto Pageable
        Page<Assessment> comentariosPelicula = assessmentService.obtenerComentariosUsuario(movieId, page, size, sortBy, direction);

        if (comentariosPelicula.isEmpty()) return ResponseEntity.noContent().build();

        // Obtenemos todos los comentarios de una pelicula
        List<Assessment> moviesModel = comentariosPelicula.getContent().stream().toList();


        //Tenemos que devolver los siguientes links: A la pelicula, a primeira, seguinte, anterior e última páxina, e a un recurso concreto.
        // Creamos el PagedModel usando la lista y añadimos los links de navegación
        PagedModel<Assessment> resource = PagedModel.of(
                moviesModel,
                new PagedModel.PageMetadata(comentariosPelicula.getSize(), comentariosPelicula.getNumber(), comentariosPelicula.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId, page, size, sortBy, direction)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId,0, size, sortBy, direction)).withRel("first-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId,page + 1, size, sortBy, direction)).withRel("next-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId,comentariosPelicula.getTotalPages() - 1, size, sortBy, direction)).withRel("last-page"),
                WebMvcLinkBuilder.linkTo(UserController.class).slash("{email}").withRel("get-user")
        );

        // Condición para agregar el enlace de "página anterior" solo si page > 0
        if (page > 0) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId, page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(resource);
    }



    // Añadir un nuevo comentario
    @PostMapping
    //Usuarios logeados
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<Assessment>> anhadirComentario(
            @RequestBody Assessment comentario) {
        Assessment assessment = assessmentService.crearComentario(comentario);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }

        //tenemos que devolver un link a la pelicula y a la lista de comentarios de la propia pelicula
        EntityModel<Assessment> resource = EntityModel.of(
                assessment,
                WebMvcLinkBuilder.linkTo(MovieController.class).slash(assessment.getMovie().getId()).withRel("get-movie"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(assessment.getMovie().getId(), 0, 10, "rating", "DESC")).withSelfRel()
        );

        return ResponseEntity.ok(resource);
    }

    // Modificar comentario utilizando PATCH y JsonPatch
    @PatchMapping(path = "/{commentId}")
    //Solo puede el propio usuario que ha hecho el comment
    @PreAuthorize("@assessmentService.checkCommentUser(#commentId, authentication.name)")
    public ResponseEntity<?> modificarComentarioParcialmente(
            @PathVariable String commentId,
            @RequestBody List<Map<String, Object>> updates) {
        try {
            Assessment comentarioExistente = assessmentService.obtenerComentario(commentId);
            if (comentarioExistente == null) {
                return ResponseEntity.notFound().build();
            }
            Assessment comentarioModificado = patchUtils.patch(comentarioExistente, updates);
            assessmentService.modificarComentario(commentId, comentarioModificado);

            //tenemos que decolver los siguientes links: A si mismo, a la lista de comentarios de la peicula y del user

            EntityModel<Assessment> resource = EntityModel.of(
                    comentarioModificado,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).modificarComentarioParcialmente(commentId, updates)).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(comentarioModificado.getMovie().getId(), 0, 10, "rating", "DESC")).withRel("get-film-comments"),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(comentarioModificado.getUser().getEmail(), 0, 10, "rating", "DESC")).withRel("get-user-comments")
            );

            return ResponseEntity.ok(resource);

        }catch (JsonPatchException e) {
            //En caso de que lance excepción el patchUtils
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar comentario
    @DeleteMapping("/{commentId}")
    //Solo el propio usuario y los admin
    @PreAuthorize("hasRole('ADMIN') or @assessmentService.checkCommentUser(#commentId, authentication.name)")
    public ResponseEntity<EntityModel<String>> eliminarComentario(
            @PathVariable String commentId) {
        Assessment assessment = assessmentService.eliminarComentario(commentId);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<String> resource = EntityModel.of(
                "Comentario eliminado correctamente",
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(assessment.getMovie().getId(), 0, 10, "rating", "DESC")).withRel("get-film-comments"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(assessment.getUser().getEmail(), 0, 10, "rating", "DESC")).withRel("get-user-comments")
        );
        return ResponseEntity.ok(resource);
    }
}