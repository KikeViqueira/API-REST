package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Obtener todas las películas mediante los filtros del usuario
    @GetMapping
    public ResponseEntity<Page<Movie>> obtenerTodasPeliculas(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDate, // Usar LocalDate con @DateTimeFormat
            @RequestParam(required = false) String crew,
            @RequestParam(required = false) String cast,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Movie> peliculas = movieService.obtenerTodasMovies(keyword, genre, releaseDate, crew, cast,  page, size, sortBy, direction);
        return ResponseEntity.ok(peliculas);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> obtenerPelicula(@PathVariable String movieId) {
        Movie movie = movieService.obtenerMovie(movieId);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    public ResponseEntity<Movie> crearPelicula(@RequestBody Movie movie) {
        // Verificar que el campo 'title' esté presente
        if (movie.getTitle() == null || movie.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Retorna 400 Bad Request si falta el título
        }
        // Llamar al servicio para crear la película
        Movie nuevaPelicula = movieService.crearPelicula(movie);
        return ResponseEntity.ok(nuevaPelicula); // Retorna 200 OK con la nueva película creada
    }

    @PutMapping
    public ResponseEntity<Movie> modificarPelicula(@RequestBody Movie movieOld, @RequestBody Movie movieNew) {
        Movie peliculaModificada = movieService.modificarPelicula(movieOld.getId(), movieNew);
        if(peliculaModificada == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(peliculaModificada);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Movie> eliminarPelicula(@PathVariable String movieId){
        Movie target = movieService.obtenerMovie(movieId).get();
        if (target == null) {
            return ResponseEntity.notFound().build();
        }
        movieService.eliminarPelicula(movieId);
        return ResponseEntity.ok(target);
    }

}
