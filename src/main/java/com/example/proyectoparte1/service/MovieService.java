package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.*;
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

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public Page<Movie> obtenerTodasMovies(String keyword, String genre, LocalDate releaseDate, String crew, String cast, int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Query query = new Query();

        //Hacemos los distintos criterios para nuestra consulta

        //Tenemos que mirar si el user ha buscado por el filtro de keywords
        if (keyword != null) {
            /*Dentro de la lista de keywords usamos la expresión regular para encontrar las películas que la contengan en la lista de keywords
            * La opción i significa que no distingue entre minúsculas y mayúsculas*/

            query.addCriteria(Criteria.where("keywords").regex(keyword, "i"));
        }

        if(genre != null) {
            query.addCriteria(Criteria.where("genres").regex(genre, "i"));
        }

        if(releaseDate != null) {
            DateCustom customDate = new DateCustom();
            customDate.setYear(releaseDate.getYear());
            customDate.setMonth(releaseDate.getMonthValue());
            customDate.setDay(releaseDate.getDayOfMonth());
            query.addCriteria(Criteria.where("releaseDate").is(customDate));
        }

        if(crew != null) {
            query.addCriteria(Criteria.where("crew.name").regex(crew, "i"));
        }

        if(cast != null) {
            query.addCriteria(Criteria.where("cast.name").regex(cast, "i"));
        }

        //Recuperamos las movies con la consulta personalizada
        List<Movie> movies = mongoTemplate.find(query, Movie.class);
        long count = mongoTemplate.count(query, Movie.class);

        //tenemos que pasar las movies al objeto movie específico para ensear los atributos que se piden en la salida
        List<Movie> moviesFiltradas = movies.stream().map(this::movieConParametrosEspecificos).toList();

        //Clase que en base a la info que tenemos crea automaticamente las pages en base a sus cálculos
        return new PageImpl<>(moviesFiltradas, pageRequest, count);

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
        movieRepository.deleteById(id);
    }
}


