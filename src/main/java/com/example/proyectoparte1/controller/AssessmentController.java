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

    @GetMapping("/film/{movieId}")
    public ResponseEntity<Page<Assessment>> obtenerComentariosPelicula(
        @RequestParam(required = false) String movieId,
        @RequestParam(required = false) String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "rating") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentariosPelicula(movieId, page, size, sortBy, direction);

        if(assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Assessment>> obtenerComentariosUsuario(
            @RequestParam(required = true) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
            Page<Assessment> assessments = assessmentService.obtenerComentariosUsuario(userId, page, size, sortBy, direction);

            if(assessments.getTotalElements() > 0) {
                return ResponseEntity.ok(assessments);
            }
            return ResponseEntity.notFound().build();
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<Assessment> anhadirComentario(
            @PathVariable String movieId,
            @RequestBody Assessment comentario){
        Assessment assessment = assessmentService.crearComentario(comentario);
        if (assessment == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }



    @PutMapping("/{movieId}")
    public ResponseEntity<Assessment> modificarComentario(
            @PathVariable String movieId,
            @RequestBody Assessment comentario
    ) {
        Assessment assessment = assessmentService.modificarComentario(movieId, comentario);
        if (comentario != null) {
            return ResponseEntity.ok(assessment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Assessment> eliminarComentario(
            @PathVariable String commentId
    ) {
        Assessment assessment = assessmentService.eliminarComentario(commentId);
        if (assessment == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }
}
