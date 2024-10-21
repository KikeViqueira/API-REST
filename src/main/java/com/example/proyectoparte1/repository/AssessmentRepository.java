package com.example.proyectoparte1.repository;

import com.example.proyectoparte1.model.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends MongoRepository<Assessment, String> {

    Page<Assessment> findByMovieIdContaining(String movieId, Pageable pageable);
    Page<Assessment> findByUserEmailContaining(String email, Pageable pageable);

}
