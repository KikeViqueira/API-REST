package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.model.User;
import com.example.proyectoparte1.service.MovieService;
import com.example.proyectoparte1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    //Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Movie>> obtenerTodasPeliculas(){
        List<Movie> moviesObtenidas = movieService.obtenerTodasMovies();
        if(moviesObtenidas.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(moviesObtenidas.subList(1, 10));
    }
}
