package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.service.AssessmentService;
import com.example.proyectoparte1.service.PatchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final PatchUtils patchUtils;

    @Autowired
    public AssessmentController(AssessmentService assessmentService, PatchUtils patchUtils) {
        this.assessmentService = assessmentService;
        this.patchUtils = patchUtils;
    }

    // Obtener comentarios de una película
    @GetMapping("/film/{movieId}")
    public ResponseEntity<Page<Assessment>> obtenerComentariosPelicula(
            @RequestParam(required = false) String movieId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentariosPelicula(movieId, page, size, sortBy, direction);

        if (assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }

    // Obtener comentarios de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Assessment>> obtenerComentariosUsuario(
            @RequestParam(required = true) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentariosUsuario(userId, page, size, sortBy, direction);

        if (assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }

    // Añadir un nuevo comentario
    @PostMapping("/{movieId}")
    public ResponseEntity<Assessment> anhadirComentario(
            @PathVariable String movieId,
            @RequestBody Assessment comentario) {
        Assessment assessment = assessmentService.crearComentario(comentario);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }

    // Modificar comentario utilizando PATCH y JsonPatch
    @PatchMapping(path = "/{commentId}")
    public ResponseEntity<Assessment> modificarComentarioParcialmente(
            @PathVariable String commentId,
            @RequestBody List<Map<String, Object>> updates) {
        try {
            Assessment comentarioExistente = assessmentService.obtenerComentario(commentId);
            if (comentarioExistente == null) {
                return ResponseEntity.notFound().build();
            }
            Assessment comentarioModificado = patchUtils.patch(comentarioExistente, updates);
            assessmentService.modificarComentario(commentId, comentarioModificado);
            return ResponseEntity.ok(comentarioModificado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar comentario
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> eliminarComentario(
            @PathVariable String commentId) {
        Assessment assessment = assessmentService.eliminarComentario(commentId);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}