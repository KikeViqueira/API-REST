package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.model.Movie;
import com.example.proyectoparte1.service.AssessmentService;
import com.example.proyectoparte1.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/comments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Autowired
    public AssessmentController(AssessmentService assesssment) {
        this.assessmentService = assesssment;
    }

    @GetMapping
    public ResponseEntity<Page<Assessment>> obtenerComentariosPelicula(
        @RequestParam(required = false) String movieId,
        @RequestParam(required = false) String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "rating") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentarios(movieId, userId, page, size, sortBy, direction);

        if(assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }


}
