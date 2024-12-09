package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.model.DateCustom;
import com.example.proyectoparte1.service.MovieService;
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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movie API", description = "Operaciones relacionadas con películas")
@SecurityRequirement(name = "JWT")
public class MovieController {

    private final MovieService movieService;
    private final PatchUtils patchUtils;

    @Autowired
    public MovieController(MovieService movieService, PatchUtils patchUtils) {
        this.movieService = movieService;
        this.patchUtils = patchUtils;
    }

    // Obtener todas las películas
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "obtenerTodasPeliculas",
            summary = "Obtener todas las películas",
            description = "Obtiene una lista paginada de todas las películas según los filtros de búsqueda aplicados",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de películas obtenida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedModel.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Enlace a la página actual",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "$request.query.sortBy"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "$request.query.direction")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "nextPage",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Enlace a la siguiente página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page + 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "prevPage",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Enlace a la página previa",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$request.query.page - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "firstPage",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Enlace a la primera página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "lastPage",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Enlace a la última página",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "$response.body.totalPages - 1"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "$request.query.size")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "movieDetails",
                                            operationId = "obtenerPelicula",
                                            description = "Link a una pelicula especifica",
                                            parameters = @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.content[0].movieId")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso", content = @Content)
            }
    )
    public ResponseEntity<PagedModel<EntityModel<Movie>>> obtenerTodasPeliculas(
            @Parameter(description = "Palabra clave") @RequestParam(required = false) String keyword,
            @Parameter(description = "Género de la película") @RequestParam(required = false) String genre,
            @Parameter(description = "Fecha de lanzamiento (formato: dd-MM-yyyy)") @RequestParam(required = false) String releaseDate,
            @Parameter(description = "Nombre del equipo de producción") @RequestParam(required = false) String crew,
            @Parameter(description = "Nombre del reparto") @RequestParam(required = false) String cast,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        DateCustom convertedReleaseDate = null;
        if(releaseDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(releaseDate, formatter);
            convertedReleaseDate = new DateCustom(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        }

        if (page < 0 || size <= 0) {
            page = 0;
            size = 10;
        }

        Page<Movie> peliculas = movieService.obtenerTodasMovies(keyword, genre, convertedReleaseDate, crew, cast, page, size, sortBy, direction);
        
        // Siempre devolver un 200 con la lista vacía en lugar de 204
        List<EntityModel<Movie>> movieModels = peliculas.getContent().stream()
                .map(pelicula -> EntityModel.of(pelicula,
                        WebMvcLinkBuilder.linkTo(methodOn(MovieController.class).obtenerPelicula(pelicula.getId())).withSelfRel()))
                .collect(Collectors.toList());

        PagedModel<EntityModel<Movie>> pagedModel = PagedModel.of(
                movieModels,
                new PagedModel.PageMetadata(peliculas.getSize(), peliculas.getNumber(), peliculas.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genre, releaseDate, crew, cast, page, size, sortBy, direction)).withSelfRel()
        );

        return ResponseEntity.ok(pagedModel);
    }

    // Obtener una película específica
    @GetMapping("/{movieId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            operationId = "obtenerPelicula",
            summary = "Obtener una película específica",
            description = "Obtiene los detalles de una película específica por su ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Detalles de la película obtenidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerPelicula",
                                            description = "Link al recurso actual",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allMovies",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Link a todas las películas",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "10"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "title"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "ASC")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<Movie>> obtenerPelicula(
            @Parameter(description = "ID de la película", required = true) @PathVariable String movieId) {
        Movie movie = movieService.obtenerMovie(movieId);
        if(movie == null) return ResponseEntity.notFound().build();

        EntityModel<Movie> resource = EntityModel.of(movie,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerPelicula(movieId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
        );
        return ResponseEntity.ok(resource);
    }

    // Crear una nueva película
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            operationId = "crearPelicula",
            summary = "Crear una nueva película",
            description = "Permite a los administradores crear una nueva película",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Película creada exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerPelicula",
                                            description = "Link al recurso creado",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$response.body.id")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allMovies",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Link a todas las películas",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "10"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "title"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "ASC")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "409", description = "Película ya existe", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para crear películas", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<Movie>> crearPelicula(
            @Parameter(description = "Detalles de la película a crear") @RequestBody @Valid Movie movie) {

        Page<Movie> moviesExistentes = movieService.obtenerMoviesPorTitulo(movie.getTitle());
        if(moviesExistentes != null && moviesExistentes.getTotalElements() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Movie nuevaPelicula = movieService.crearPelicula(movie);
        EntityModel<Movie> resource = EntityModel.of(nuevaPelicula,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerPelicula(movie.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
        );
        return ResponseEntity.ok(resource);
    }

    // Modificar una película
    @PatchMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            operationId = "modificarPelicula",
            summary = "Modificar una película existente",
            description = "Permite a los administradores actualizar ciertos atributos de una película",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Película modificada exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "self",
                                            operationId = "obtenerPelicula",
                                            description = "Link al recurso modificado",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "movieId", expression = "$request.path.movieId")
                                            }
                                    ),
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allMovies",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Link a todas las películas",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "10"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "title"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "ASC")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error al aplicar modificaciones", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para modificar películas", content = @Content)
            }
    )

    public ResponseEntity<?> modificarPelicula(
            @Parameter(description = "ID de la película a modificar", required = true) @PathVariable String movieId,
            @Parameter(description = "Lista de modificaciones") @RequestBody List<Map<String, Object>> updates) {
        System.out.println("Modificando pelicula...");
        try {
            Movie movie = movieService.obtenerMovie(movieId);
            if (movie == null) {
                return ResponseEntity.notFound().build();
            }

            Movie movieModificada = patchUtils.patch(movie, updates);
            movieModificada = movieService.modificarPelicula(movieModificada);

            EntityModel<Movie> resource = EntityModel.of(movieModificada,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerPelicula(movieId)).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
            );
            return ResponseEntity.ok(resource);

        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Eliminar una película
    @DeleteMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            operationId = "eliminarPelicula",
            summary = "Eliminar una película",
            description = "Permite a los administradores eliminar una película por su ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Película eliminada exitosamente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class)),
                            links = {
                                    @io.swagger.v3.oas.annotations.links.Link(
                                            name = "allMovies",
                                            operationId = "obtenerTodasPeliculas",
                                            description = "Link a todas las películas",
                                            parameters = {
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "page", expression = "0"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "size", expression = "10"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "sortBy", expression = "title"),
                                                    @io.swagger.v3.oas.annotations.links.LinkParameter(name = "direction", expression = "ASC")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content),
                    @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar películas", content = @Content)
            }
    )
    public ResponseEntity<EntityModel<Movie>> eliminarPelicula(
            @Parameter(description = "ID de la película a eliminar", required = true) @PathVariable String movieId) {

        Movie target = movieService.obtenerMovie(movieId);
        if (target == null) {
            return ResponseEntity.notFound().build();
        }
        movieService.eliminarPelicula(movieId);

        EntityModel<Movie> resource = EntityModel.of(target,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
        );
        return ResponseEntity.ok(resource);
    }
}
