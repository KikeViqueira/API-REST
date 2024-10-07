package com.example.proyectoparte1.repository;

import com.example.proyectoparte1.model.Date;
import com.example.proyectoparte1.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    //Buscar peliculas mediante palabras claves en el titulo e en el contenido
    /*
    keyword1: Se utiliza para buscar en el campo title (el título de la película).
    keyword2: Se utiliza para buscar en el campo overview (el resumen o descripción de la película).
    */
    Page<Movie> findByTitleContainingOrOverviewContaining(String keyword1, String keyword2, Pageable pageable);

    //Buscar peliculas mediante el genero
    Page<Movie> findByGenresContaining(String genre, Pageable pageable);

    //Buscar mediante fecha de lanzamiento
    Page<Movie> findByReleaseDate(Date releaseDate, Pageable pageable);

    //Buscar mediante cargos de direccion, escritores ..-
    Page<Movie> findByCrewNameContaining(String crew, Pageable pageable);

    //Funcion para buscar peliculas en base a determinados actores y actrices
    Page<Movie> findByCastNameContaining(String cast, Pageable pageable);
}
