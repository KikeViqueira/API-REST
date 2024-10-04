package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository){
        this.movieRepository=movieRepository;
    }

    public List<Movie> obtenerTodasMovies(){
        return movieRepository.findAll();
    }
}
