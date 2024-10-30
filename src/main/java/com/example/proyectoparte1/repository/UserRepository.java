package com.example.proyectoparte1.repository;


import com.example.proyectoparte1.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


@Repository
//El segundo parametro del mongo repository es el id que va a buscar en relación al primero de los parámetros, en este caso la entidad User
public interface UserRepository extends MongoRepository<User, String> {

    Page<User> findAll(Pageable pageable);
    Page<User> findByFriendsEmailContaining(String userId, Pageable pageable);
}