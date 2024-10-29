package com.example.proyectoparte1.repository;


import com.example.proyectoparte1.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
//El segundo parametro del mongo repository es el id que va a buscar en relación al primero de los parámetros, en este caso la entidad User
public interface UserRepository extends MongoRepository<User, String> {

}
