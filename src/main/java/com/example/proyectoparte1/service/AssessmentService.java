package com.example.proyectoparte1.service;

import com.example.proyectoparte1.model.Assessment;
import com.example.proyectoparte1.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    @Autowired
    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public Page<Assessment> obtenerComentarioFiltrado(String movieId, String email, int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        if(movieId != null && !movieId.isEmpty()) {
            //Si hemos recibido el id de una película buscamos todos los comentarios relacionados con la película
            return assessmentRepository.findByMovieIdContaining(movieId, pageRequest);
        }

        if(email != null && !email.isEmpty()) {
            //Lo mismo pero en el caso de un user específico
            return assessmentRepository.findByUserEmailContaining(email, pageRequest);
        }
        //En el caso de que el usuario no haya especificado filtros tenemos que devolver todos los comments
        return assessmentRepository.findAll(pageRequest);
    }

    public Assessment obtenerComentario(String commentId){
        Optional<Assessment> optionalAssessment = assessmentRepository.findById(commentId);
        return optionalAssessment.orElse(null);
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
    public Assessment eliminarComentario(String assessmentId){
        Optional<Assessment> optional = assessmentRepository.findById(assessmentId);
        if(optional.isEmpty()){
            return null;
        }
        assessmentRepository.deleteById(assessmentId);
        return optional.get();
    }
}
