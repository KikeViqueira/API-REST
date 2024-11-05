package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.*;
import com.example.proyectoparte1.service.MovieService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final PatchUtils patchUtils;

    @Autowired
    public MovieController(MovieService movieService, PatchUtils patchUtils) {
        this.movieService = movieService;
        this.patchUtils = patchUtils;
    }

    // Obtener todas las películas mediante los filtros del usuario
    @GetMapping
    //Todos los usuarios logeados
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedModel<EntityModel<Movie>>> obtenerTodasPeliculas(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String crew,
            @RequestParam(required = false) String cast,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        DateCustom convertedReleaseDate = null;
        //verificamos si el releaseDate es distinto de null
        if(releaseDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(releaseDate, formatter);

           convertedReleaseDate =  new DateCustom(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        }


        //Comprobamos que el numero de pagina y el tamaño de cada una de ellas es mayor a cero, si lo es pondremos los valores por defecto para evitar errores
        if (page<0 || size <= 0) {
            page = 0;
            size = 10;
        }

        Page<Movie> peliculas = movieService.obtenerTodasMovies(keyword, genres, convertedReleaseDate, crew, cast, page, size, sortBy, direction);

        //En el caso de que no se devuelvan usuarios al usuario que los solicita le mandamos un mensaje de que no hay el contenido solicitado
        if (peliculas.isEmpty()) return ResponseEntity.noContent().build();


        // Transformamos cada Movie en un EntityModel<Movie>, para poder devolver un enlace a si mismo
        List<EntityModel<Movie>> movieModels = peliculas.getContent().stream()
                .map(pelicula -> EntityModel.of(pelicula,
                        WebMvcLinkBuilder.linkTo(methodOn(MovieController.class).obtenerPelicula(pelicula.getId())).withSelfRel()))
                .collect(Collectors.toList());


        //Tenemos que devolver los siguientes links: A si mesmo, a primeira, seguinte, anterior e última páxina, e a un recurso concreto.
        // Creamos el PagedModel usando la lista de EntityModel y añadimos los links de navegación
        PagedModel<EntityModel<Movie>> pagedModel = PagedModel.of(
                movieModels,
                new PagedModel.PageMetadata(peliculas.getSize(), peliculas.getNumber(), peliculas.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genres, releaseDate, crew, cast, page, size, sortBy, direction)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genres, releaseDate, crew, cast, 0, size, sortBy, direction)).withRel("first-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genres, releaseDate, crew, cast, page+1, size, sortBy, direction)).withRel("next-page"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genres, releaseDate, crew, cast, peliculas.getTotalPages()-1, size, sortBy, direction)).withRel("last-page"),
                WebMvcLinkBuilder.linkTo(UserController.class).slash("415263").withRel("get-movie")
        );

        // Condición para agregar el enlace de "página anterior" solo si page > 0
        if (page > 0) {
            pagedModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas(keyword, genres, releaseDate, crew, cast, page - 1, size, sortBy, direction)).withRel("prev-page"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{movieId}")
    //Todos los usuarios logeados
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<Movie>> obtenerPelicula(@PathVariable String movieId) {
        Movie movie = movieService.obtenerMovie(movieId);
        if(movie == null) return ResponseEntity.notFound().build();

        //Tenemos que devolver link a si mismo y a la lista de todos
        EntityModel<Movie> resource = EntityModel.of(movie,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerPelicula(movieId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
        );
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    //Solo los admin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Movie>> crearPelicula(@RequestBody @Valid Movie movie) {
        //Tenemos que saber si existen películas con el título que el usuario le ha dado a su película
        Page<Movie> moviesExistentes = movieService.obtenerMoviesPorTitulo(movie.getTitle());

        //En caso de que exita ya una película con ese nombre indicamos un estado de conflicto
        if(moviesExistentes != null && moviesExistentes.getTotalElements() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Llamar al servicio para crear la película
        Movie nuevaPelicula = movieService.crearPelicula(movie);

        EntityModel<Movie> resource = EntityModel.of(nuevaPelicula,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerPelicula(movie.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")

        );
        return ResponseEntity.ok(resource);

    }

    @PatchMapping("/{movieId}")
    //Solo los admin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarPelicula(@PathVariable String movieId , @RequestBody List<Map<String, Object>> updates) {
        //Tenemos que hacer el try para tener en cuenta que el formato del body puede ser erróneo para el patchUtils
        try {
            Movie movie = movieService.obtenerMovie(movieId);
            //Si no se encuentra la película se devuelve un código 404 notFound
            if (movie == null) {
                return ResponseEntity.notFound().build();
            }
            //Aplicamos las modificaciones a el objeto Movie deseado invocando al método patch
            Movie movieModificada = patchUtils.patch(movie, updates);
            movieModificada = movieService.modificarPelicula(movieModificada);

            //Tenemos que devolver link a si mismo y a la lista de todos
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

    @DeleteMapping("/{movieId}")
    //Solo los admin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Movie>> eliminarPelicula(@PathVariable String movieId) {
        Movie target = movieService.obtenerMovie(movieId);
        if (target == null) {
            return ResponseEntity.notFound().build();
        }
        movieService.eliminarPelicula(movieId);

        //Tenemos que devolver link a si mismo y a la lista de todos
        EntityModel<Movie> resource = EntityModel.of(target,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieController.class).obtenerTodasPeliculas("period", "Drama", "01-06-2015", "Jay Craven", "Jacqueline Bisset", 0, 10, "movieId", "DESC")).withRel("all-movies")
        );
        return ResponseEntity.ok(resource);
    }
}