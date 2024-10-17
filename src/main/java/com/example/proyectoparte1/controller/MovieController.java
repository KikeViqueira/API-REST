package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.MovieService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<Page<Movie>> obtenerTodasPeliculas(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate, // Usar LocalDate con @DateTimeFormat
            @RequestParam(required = false) String crew,
            @RequestParam(required = false) String cast,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Movie> peliculas = movieService.obtenerTodasMovies(keyword, genres, releaseDate, crew, cast, page, size, sortBy, direction);
        if(peliculas == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(peliculas);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> obtenerPelicula(@PathVariable String movieId) {
        Optional<Movie> movie = movieService.obtenerMovie(movieId);
        if (movie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie.get());
    }

    @PostMapping
    public ResponseEntity<Movie> crearPelicula(@RequestBody @Valid Movie movie) {
        //Tenemos que saber si existen películas con el título que el usuario le ha dado a su película
        Page<Movie> moviesExistentes = movieService.obtenerMoviesPorTitulo(movie.getTitle());

        //En caso de que exita ya una película con ese nombre indicamos un estado de conflicto
        if(moviesExistentes != null && moviesExistentes.getTotalElements() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Llamar al servicio para crear la película
        Movie nuevaPelicula = movieService.crearPelicula(movie);
        return ResponseEntity.ok(nuevaPelicula); // Retorna 200 OK con la nueva película creada
    }

    @PatchMapping("/{movieId}")
    public ResponseEntity<?> modificarPelicula(@PathVariable String movieId , @RequestBody List<Map<String, Object>> updates) {
        //Tenemos que hacer el try para tener en cuenta que el formato del body puede ser erróneo para el patchUtils
        try {
            Optional<Movie> movie = movieService.obtenerMovie(movieId);
            //Si no se encuentra la película se devuelve un código 404 notFound
            if (movie.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            //Aplicamos las modificaciones a el objeto Movie deseado invocando al método patch
            Movie movieModificada = patchUtils.patch(movie.get(), updates);
            movieModificada = movieService.modificarPelicula(movieModificada);
            //Indicamos que la operación de actualización se ha realizado con éxito
            return ResponseEntity.ok(movieModificada);

        } catch (JsonPatchException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Movie> eliminarPelicula(@PathVariable String movieId) {
        Optional<Movie> target = movieService.obtenerMovie(movieId);
        if (target.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        movieService.eliminarPelicula(movieId);
        return ResponseEntity.ok(target.get());
    }
}