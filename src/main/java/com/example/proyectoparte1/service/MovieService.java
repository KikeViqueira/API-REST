package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.*;
import com.example.proyectoparte1.repository.AssessmentRepository;
import com.example.proyectoparte1.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final AssessmentRepository assessmentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public MovieService(MovieRepository movieRepository, AssessmentRepository assessmentRepository) {
        this.movieRepository = movieRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public Movie obtenerMovie(String id){
        Optional<Movie> movie = movieRepository.findById(id);
        if(movie.isEmpty()){
            return null;
        }
        return movie.get();
    }

    public Page<Movie> obtenerMoviesPorTitulo(String title){
        int size = 10, page = 0;
        String sortBy = "title", direction = "DESC";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        return movieRepository.findByTitle(title, pageRequest);
    }

    public Page<Movie> obtenerTodasMovies(String keyword, String genre, DateCustom releaseDate, String crew, String cast, int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Query query = new Query().with(pageRequest);

        if (keyword != null) {
            query.addCriteria(Criteria.where("keywords").regex(keyword, "i"));
        }

        if(genre != null) {
            query.addCriteria(Criteria.where("genres").regex(genre, "i"));
        }

        if(releaseDate != null) {
            query.addCriteria(Criteria.where("releaseDate").is(releaseDate));
        }

        if(crew != null) {
            query.addCriteria(Criteria.where("crew.name").regex(crew, "i"));
        }

        if(cast != null) {
            query.addCriteria(Criteria.where("cast.name").regex(cast, "i"));
        }

        // Obtener el total de documentos que coinciden con la consulta
        long total = mongoTemplate.count(query, Movie.class);
        
        // Obtener solo los documentos de la página actual
        List<Movie> movies = mongoTemplate.find(query, Movie.class);
        
        // Convertir los resultados al formato específico
        List<Movie> moviesFiltradas = movies.stream()
            .map(this::movieConParametrosEspecificos)
            .collect(Collectors.toList());

        return new PageImpl<>(moviesFiltradas, pageRequest, total);
    }



    public Movie movieConParametrosEspecificos(Movie movie){
        Movie movieConParametrosEspecificos = new Movie();
        movieConParametrosEspecificos.setId(movie.getId());
        movieConParametrosEspecificos.setTitle(movie.getTitle());
        movieConParametrosEspecificos.setOverview(movie.getOverview());
        movieConParametrosEspecificos.setGenres(movie.getGenres());
        movieConParametrosEspecificos.setReleaseDate(movie.getReleaseDate());
        movieConParametrosEspecificos.setResources(movie.getResources());
        return movieConParametrosEspecificos;
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

        //Una vez eliminamos al usuario tenemos que mirar todos los usqarios de nuestra BD para ver si el usario eliminado era amigo de alguien
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.fromString("DESC"), "rating");

        //Al eliminar una película tenemos que eliminar todos los comentarios relacionados con ella
        Page<Assessment> comentariosPelicula = assessmentRepository.findByMovieIdContaining(id, pageRequest);

        if(comentariosPelicula != null && !comentariosPelicula.isEmpty()){
            comentariosPelicula.forEach(assessment -> {
                assessmentRepository.deleteById(assessment.getId());
            });
        }

        movieRepository.deleteById(id);
    }
}
