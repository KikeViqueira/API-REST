package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public Page<Assessment> obtenerComentarios(String movieId, String userId, int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        if(movieId != null && !movieId.isEmpty()) {
            return obtenerComentariosPelicula(movieId, pageRequest);
        }

        if(userId != null && !userId.isEmpty()) {
            return obtenerComentariosUsuario(userId, pageRequest);
        }

        return null;
    }


    //Obtener los comentarios de una pelicula
    public Page<Assessment> obtenerComentariosPelicula(String id, PageRequest pageRequest) {
        Page<Assessment> assessments = assessmentRepository.findByMovieIdContaining(id, pageRequest);
        return assessments;
    }

    //Obtener comentarios de un usuario
    public Page<Assessment> obtenerComentariosUsuario(String id,PageRequest pageRequest) {
        Page<Assessment> assessments = assessmentRepository.findByUserIdContaining(id, pageRequest);
        return assessments;
    }

    //Anhadir un nuevo comentario a una pelicula
    public Assessment crearComentario(Assessment assessment) {
        return assessmentRepository.save(assessment);
    }

    //Modificar un comentario
    public Assessment modificarComentario(String id, Assessment assessmentNew) {
        Optional<Assessment> optional = assessmentRepository.findById(id);
        if (optional.isPresent()) {
            Assessment assessment = optional.get();
            assessment.setMovie(assessmentNew.getMovie());
            assessment.setUser(assessmentNew.getUser());
            assessment.setComment(assessmentNew.getComment());
            assessment.setRating(assessmentNew.getRating());

            return assessmentRepository.save(assessment);
        }
        return null;
    }

    //Eliminar un comentario
    public void eliminarComentario(Assessment assessment){
        assessmentRepository.deleteById(assessment.getId());
    }
}
