package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.DateCustom;
import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.model.MovieSummary;
import com.example.proyectoparte1.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public Optional<Movie> obtenerMovie(String id){
        return movieRepository.findById(id);
    }

    public Page<Movie> obtenerMoviesPorTitulo(String title){
        int size = 10, page = 0;
        String sortBy = "title", direction = "DESC";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        return movieRepository.findByTitle(title, pageRequest);
    }

    // Función para obtener todas las películas en base a palabras clave, género o mediante la fecha de lanzamiento
    public Page<Movie> obtenerTodasMovies(String keyword, String genre, LocalDate releaseDate, String crew, String cast, int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        // Filtro por palabras clave
        if (keyword != null && !keyword.isEmpty()) {
            return movieRepository.findByTitleContainingOrOverviewContaining(keyword, keyword, pageRequest);
        }

        // Filtro por género
        if (genre != null && !genre.isEmpty()) {
            return movieRepository.findByGenresContaining(genre, pageRequest);
        }

        // Filtro por fecha de lanzamiento (conversión de LocalDate a Date personalizada)
        if (releaseDate != null) {
            // Convertimos LocalDate a tu clase personalizada Date
            DateCustom customDateCustom = new DateCustom(releaseDate.getDayOfMonth(), releaseDate.getMonthValue(), releaseDate.getYear());
            return movieRepository.findByReleaseDate(customDateCustom, pageRequest);
        }

        if(crew != null && !crew.isEmpty()) {
            return movieRepository.findByCrewNameContaining(crew, pageRequest);
        }

        if(cast != null && !cast.isEmpty()) {
            return movieRepository.findByCastNameContaining(cast, pageRequest);
        }

        // Si no se han aplicado filtros, devolver todas las películas
        return movieRepository.findAll(pageRequest);

    }

    //Funcion para crear una nueva película, el único campo obligatorio es el título
    public Movie crearPelicula(Movie movie) {
        // Guardar la película en la base de datos
        return movieRepository.save(movie);
    }

    //Funcion para guardar los datos de los atributos de una película
    public Movie modificarPelicula(Movie movieNew) {
        return movieRepository.save(movieNew); // Guarda la película actualizada
    }

    //Funcion para eliminar una pelicula determinada
    public void eliminarPelicula(String id) {
        movieRepository.deleteById(id);
    }
}


