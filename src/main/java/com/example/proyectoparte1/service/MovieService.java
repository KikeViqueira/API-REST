package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.Date;
import com.example.proyectoparte1.model.Movie;
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
            Date customDate = new Date(releaseDate.getDayOfMonth(), releaseDate.getMonthValue(), releaseDate.getYear());
            return movieRepository.findByReleaseDate(customDate, pageRequest);
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

    //Funcion para modificar los datos de una pelicula en concreto
    public Movie modificarPelicula(String id, Movie movieNew) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movieOld = optionalMovie.get();
            Movie updatedMovie = modificarPelicula(movieOld, movieNew);
            return movieRepository.save(updatedMovie); // Guarda la película actualizada
        } else {
            // Si la película no existe, puedes lanzar una excepción o retornar null
            return null;
        }
    }

    //Funcion aux para comparar y igualar los datos actualizados a la pelicula que buscamos
    public Movie modificarPelicula(Movie movieOld, Movie movieNew) {
        // Solo actualiza si el campo en la nueva película no es null
        if (movieNew.getTitle() != null) {
            movieOld.setTitle(movieNew.getTitle());
        }

        if (movieNew.getReleaseDate() != null) {
            movieOld.setReleaseDate(movieNew.getReleaseDate());
        }

        if (movieNew.getOverview() != null) {
            movieOld.setOverview(movieNew.getOverview());
        }

        if (movieNew.getTagline() != null) {
            movieOld.setTagline(movieNew.getTagline());
        }

        if (movieNew.getGenres() != null) {
            movieOld.setGenres(movieNew.getGenres());
        }

        if (movieNew.getKeywords() != null) {
            movieOld.setKeywords(movieNew.getKeywords());
        }

        if (movieNew.getProducers() != null) {
            movieOld.setProducers(movieNew.getProducers());
        }

        if (movieNew.getCrew() != null) {
            movieOld.setCrew(movieNew.getCrew());
        }

        if (movieNew.getCast() != null) {
            movieOld.setCast(movieNew.getCast());
        }

        if (movieNew.getResources() != null) {
            movieOld.setResources(movieNew.getResources());
        }

        if (movieNew.getBudget() != null) {
            movieOld.setBudget(movieNew.getBudget());
        }

        if (movieNew.getStatus() != null) {
            movieOld.setStatus(movieNew.getStatus());
        }

        if (movieNew.getRuntime() != null) {
            movieOld.setRuntime(movieNew.getRuntime());
        }

        if (movieNew.getRevenue() != null) {
            movieOld.setRevenue(movieNew.getRevenue());
        }

        // Devolver la película modificada
        return movieOld;
    }

    //Funcion para eliminar una pelicula determinada
    public void eliminarPelicula(String id) {
        movieRepository.deleteById(id);
    }
}


