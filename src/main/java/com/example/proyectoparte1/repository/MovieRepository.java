package com.example.proyectoparte1.repository;

import com.example.proyectoparte1.model.DateCustom;
import com.example.proyectoparte1.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {


    //Buscamos la película por el título
    Page<Movie> findByTitle(String title, Pageable pageable);

    //Buscar peliculas mediante palabras claves en el titulo e en el contenido
    /*
    keyword1: Se utiliza para buscar en el campo title (el título de la película).
    keyword2: Se utiliza para buscar en el campo overview (el resumen o descripción de la película).
    */

    // Buscar películas por coincidencia parcial en el título o descripción (insensible a mayúsculas)
    // Utiliza $regex para buscar coincidencias parciales en los campos 'title' y 'overview'.
    // $options: 'i' hace que la búsqueda sea insensible a mayúsculas y minúsculas.
    // Solo se devuelven los campos id, title, overview, genres, releaseDate y resources.
    @Query(value = "{ '$or': [ { 'title': { $regex: ?0, $options: 'i' } }, { 'overview': { $regex: ?1, $options: 'i' } } ] }",
            fields = "{ 'id': 1, 'title': 1, 'overview': 1, 'genres': 1, 'releaseDate': 1, 'resources': 1 }")
    Page<Movie> findByTitleContainingOrOverviewContaining(String keyword1, String keyword2, Pageable pageable);

    // Buscar películas por coincidencia parcial en el campo 'genres' (insensible a mayúsculas)
    // Utiliza $regex para buscar en el campo 'genres', permitiendo coincidencias parciales.
    // $options: 'i' hace que la búsqueda sea insensible a mayúsculas.
    // Solo se devuelven los campos id, title, overview, genres, releaseDate y resources.
    @Query(value = "{ 'genres': { $regex: ?0, $options: 'i' } }",
            fields = "{ 'id': 1, 'title': 1, 'overview': 1, 'genres': 1, 'releaseDate': 1, 'resources': 1 }")
    Page<Movie> findByGenresContaining(String genre, Pageable pageable);

    // Buscar películas por coincidencia exacta en el campo 'releaseDate'
    // No se utiliza $regex, ya que la coincidencia es exacta por fecha.
    // Solo se devuelven los campos id, title, overview, genres, releaseDate y resources.
    @Query(value = "{ 'releaseDate': ?0 }",
            fields = "{ 'id': 1, 'title': 1, 'overview': 1, 'genres': 1, 'releaseDate': 1, 'resources': 1 }")
    Page<Movie> findByReleaseDate(DateCustom releaseDateCustom, Pageable pageable);

    // Buscar películas por coincidencia parcial en los nombres del equipo (crew) (insensible a mayúsculas)
    // Utiliza $regex para buscar coincidencias parciales en el campo 'crew.name'.
    // $options: 'i' hace que la búsqueda sea insensible a mayúsculas.
    // Solo se devuelven los campos id, title, overview, genres, releaseDate y resources.
    @Query(value = "{ 'crew.name': { $regex: ?0, $options: 'i' } }",
            fields = "{ 'id': 1, 'title': 1, 'overview': 1, 'genres': 1, 'releaseDate': 1, 'resources': 1 }")
    Page<Movie> findByCrewNameContaining(String crew, Pageable pageable);

    // Buscar películas por coincidencia parcial en los nombres del reparto (cast) (insensible a mayúsculas)
    // Utiliza $regex para buscar coincidencias parciales en el campo 'cast.name'.
    // $options: 'i' hace que la búsqueda sea insensible a mayúsculas.
    // Solo se devuelven los campos id, title, overview, genres, releaseDate y resources.
    @Query(value = "{ 'cast.name': { $regex: ?0, $options: 'i' } }",
            fields = "{ 'id': 1, 'title': 1, 'overview': 1, 'genres': 1, 'releaseDate': 1, 'resources': 1 }")
    Page<Movie> findByCastNameContaining(String cast, Pageable pageable);
}
