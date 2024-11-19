package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.service.AssessmentService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/comments")
@Tag(name = "Assessment API", description = "Operaciones relacionadas con los comentarios de películas")
@SecurityRequirement(name = "JWT")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final PatchUtils patchUtils;

    @Autowired
    public AssessmentController(AssessmentService assessmentService, PatchUtils patchUtils) {
        this.assessmentService = assessmentService;
        this.patchUtils = patchUtils;
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name or @userService.isAmigo(authentication.name, #email)")
    @Operation(
            operationId = "obtenerComentariosUsuario",
            summary = "Obtener comentarios de un usuario",
            description = "Obtiene una lista paginada de los comentarios de un usuario específico. Solo accesible por el usuario, sus amigos o un administrador.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de comentarios del usuario obtenida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedModel.class)),
                            links = {

                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "usuario",
                                            operationId = "obtenerUsuario",
                                            description = "Enlace al usuario",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$response.path.email"),

                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "nextPage",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a la siguiente página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "prevPage",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a la página previa",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "firstPage",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a la primera página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "lastPage",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a la última página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$request.path.email"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "204", description = "No hay contenido"),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso")
            }
    )
    public ResponseEntity<PagedModel<Assessment>> obtenerComentariosUsuario(
            @Parameter(description = "Correo del usuario", required = true) @PathVariable String email,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "rating") String sortBy,
            @Parameter(description = "Dirección de la ordenación") @RequestParam(defaultValue = "ASC") String direction) {

        if (page < 0 || size <= 0) {
            page = 0;
            size = 10;
        }

        Page<Assessment> comentariosUsuario = assessmentService.obtenerComentariosUsuario(email, page, size, sortBy, direction);
        if (comentariosUsuario.isEmpty()) return ResponseEntity.noContent().build();

        List<Assessment> commentsModel = comentariosUsuario.getContent().stream().toList();
        PagedModel<Assessment> resource = PagedModel.of(
                commentsModel,
                new PagedModel.PageMetadata(comentariosUsuario.getSize(), comentariosUsuario.getNumber(), comentariosUsuario.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email, page, size, sortBy, direction)).withSelfRel()
        );

        if (page > 0) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(email, page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/movie/{movieId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "obtenerComentariosPelicula",
            summary = "Obtener comentarios de una película",
            description = "Obtiene una lista paginada de los comentarios de una película específica para los usuarios autenticados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de comentarios de la película obtenida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedModel.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "pelicula",
                                            operationId = "obtenerPelicula",
                                            description = "Enlace a la pelicula",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.path.movieId"),

                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "nextPage",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a la siguiente página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page + 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "prevPage",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a la página previa",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "firstPage",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a la primera página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "lastPage",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a la última página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$response.body.totalPages - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "204", description = "No hay contenido", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso", content = @Content)
            }
    )
    public ResponseEntity<PagedModel<Assessment>> obtenerComentariosPelicula(
            @Parameter(description = "ID de la película", required = true) @PathVariable String movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        if (page < 0 || size <= 0) {
            page = 0;
            size = 10;
        }

        Page<Assessment> comentariosPelicula = assessmentService.obtenerComentariosPelicula(movieId, page, size, sortBy, direction);
        if (comentariosPelicula.isEmpty()) return ResponseEntity.noContent().build();

        List<Assessment> moviesModel = comentariosPelicula.getContent().stream().toList();
        PagedModel<Assessment> resource = PagedModel.of(
                moviesModel,
                new PagedModel.PageMetadata(comentariosPelicula.getSize(), comentariosPelicula.getNumber(), comentariosPelicula.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId, page, size, sortBy, direction)).withSelfRel()
        );

        if (page > 0) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(movieId, page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(resource);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "anhadirComentario",
            summary = "Añadir un nuevo comentario",
            description = "Permite a un usuario autenticado añadir un comentario en su propio nombre.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comentario creado exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Assessment.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "movieDetails",
                                            operationId = "obtenerPelicula",
                                            description = "Enlace a la pelicula",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.movie.id")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "commentsMovie",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a los comentarios de la película",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.movie.id")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para añadir un comentario", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Datos de comentario no válidos", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<Assessment>> anhadirComentario(
            @Parameter(description = "Detalles del comentario a crear") @RequestBody Assessment comentario) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        if (comentario == null || comentario.getUser() == null || comentario.getMovie() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!comentario.getUser().getEmail().equals(authenticatedUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Assessment assessment = assessmentService.crearComentario(comentario);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Assessment> resource = EntityModel.of(
                assessment,
                WebMvcLinkBuilder.linkTo(MovieController.class).slash(assessment.getMovie().getId()).withRel("get-movie"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(assessment.getMovie().getId(), 0, 10, "rating", "DESC")).withRel("comments-movie")
        );

        return ResponseEntity.ok(resource);
    }

    @PatchMapping(path = "/{commentId}")
    @PreAuthorize("@assessmentService.checkCommentUser(#commentId, authentication.name)")
    @Operation(
            operationId = "modificarComentarioParcialmente",
            summary = "Modificar comentario parcialmente",
            description = "Permite a un usuario modificar parcialmente un comentario que ha realizado usando JSON Patch.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comentario modificado exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Assessment.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "modificarComentarioParcialmente",
                                            description = "Enlace al comentario modificado",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "commentId", expression = "$response.body.id")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "commentsMovie",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a los comentarios de la película",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.movie.id")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "commentsUser",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a los comentarios del usuario",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$response.body.user.email")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para modificar este comentario", content = @Content)
            }
    )
    public ResponseEntity<?> modificarComentarioParcialmente(
            @Parameter(description = "ID del comentario a modificar", required = true) @PathVariable String commentId,
            @Parameter(description = "Lista de cambios a aplicar") @RequestBody List<Map<String, Object>> updates) {
        try {
            Assessment comentarioExistente = assessmentService.obtenerComentario(commentId);
            if (comentarioExistente == null) {
                return ResponseEntity.notFound().build();
            }
            Assessment comentarioModificado = patchUtils.patch(comentarioExistente, updates);
            assessmentService.modificarComentario(commentId, comentarioModificado);

            EntityModel<Assessment> resource = EntityModel.of(
                    comentarioModificado,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).modificarComentarioParcialmente(commentId, updates)).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(comentarioModificado.getMovie().getId(), 0, 10, "rating", "DESC")).withRel("get-film-comments"),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(comentarioModificado.getUser().getEmail(), 0, 10, "rating", "DESC")).withRel("get-user-comments")
            );

            return ResponseEntity.ok(resource);

        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or @assessmentService.checkCommentUser(#commentId, authentication.name)")
    @Operation(
            operationId = "eliminarComentario",
            summary = "Eliminar comentario",
            description = "Permite a un administrador o al propio usuario eliminar un comentario",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comentario eliminado exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Assessment.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "commentsMovie",
                                            operationId = "obtenerComentariosPelicula",
                                            description = "Enlace a los comentarios de la película",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.movie.id")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "commentsUser",
                                            operationId = "obtenerComentariosUsuario",
                                            description = "Enlace a los comentarios del usuario",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "email", expression = "$response.body.user.email")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar este comentario", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<Assessment>> eliminarComentario(
            @Parameter(description = "ID del comentario a eliminar", required = true) @PathVariable String commentId) {

        Assessment assessment = assessmentService.eliminarComentario(commentId);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Assessment> resource = EntityModel.of(
                assessment,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosPelicula(assessment.getMovie().getId(), 0, 10, "rating", "DESC")).withRel("get-film-comments"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssessmentController.class).obtenerComentariosUsuario(assessment.getUser().getEmail(), 0, 10, "rating", "DESC")).withRel("get-user-comments")
        );
        return ResponseEntity.ok(resource);
    }
}
