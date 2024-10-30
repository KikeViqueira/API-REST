package com.example.proyectoparte1.controller;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.service.AssessmentService;
import com.example.proyectoparte1.service.PatchUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    // Obtener comentarios de un usuario
    @GetMapping("/{email}")
    //Solo pueden llamar al endpoint el admin, el propio usuario y sus amigos
    //authentication.name: Identificador único del usuario (generalmente username o email), configurable en UserDetailsService.
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name or @userService.isAmigo(authentication.name, #email)")
    public ResponseEntity<Page<Assessment>> obtenerComentariosUsuario(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentariosUsuario(email, page, size, sortBy, direction);

        if (assessments != null && assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }


    // Obtener comentarios de una película
    @GetMapping("/{movieId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Assessment>> obtenerComentariosPelicula(
            @RequestParam(required = false) String movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Page<Assessment> assessments = assessmentService.obtenerComentariosPelicula(movieId,page, size, sortBy, direction);

        if (assessments != null && assessments.getTotalElements() > 0) {
            return ResponseEntity.ok(assessments);
        }
        return ResponseEntity.notFound().build();
    }



    // Añadir un nuevo comentario
    @PostMapping
    //Usuarios logeados
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Assessment> anhadirComentario(
            @RequestBody Assessment comentario) {
        Assessment assessment = assessmentService.crearComentario(comentario);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }

    // Modificar comentario utilizando PATCH y JsonPatch
    @PatchMapping(path = "/{commentId}")
    //Solo puede el propio usuario que ha hecho el comment
    @PreAuthorize("@assessmentService.checkCommentUser(#commentId, authentication.name)")
    public ResponseEntity<?> modificarComentarioParcialmente(
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
        }catch (JsonPatchException e) {
            //En caso de que lance excepción el patchUtils
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al aplicar el parche: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar comentario
    @DeleteMapping("/{commentId}")
    //Solo el propio usuario y los admin
    @PreAuthorize("hasRole('ADMIN') or @assessmentService.checkCommentUser(#commentId, authentication.name)")
    public ResponseEntity<Assessment> eliminarComentario(
            @PathVariable String commentId) {
        Assessment assessment = assessmentService.eliminarComentario(commentId);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }
}