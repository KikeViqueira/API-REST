package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id  //Indicamos que el campo email en nuestro caso funciona como identificador único, tenemos que marcarlo con esta flag para que el findById funcione correctamente
    @NotBlank(message = "El email no puede estar vacio")
    @Email
    private String email;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 2, message = "El nombre debe de tener un minimo de 2 caracteres")
    private String name;
    private String country;
    private String picture;
    private DateCustom birthday;
    //Basta con guardar los atributos name y email, no toda la info del user
    private List<User> friends;
    private String password;
    private List<String> roles;

    public User() {
    }


    public User(String email, String name, String country, String picture, DateCustom birthday, List<User> friends, String password, List<String> roles) {
        this.email = email;
        this.name = name;
        this.country = country;
        this.picture = picture;
        this.birthday = birthday;
        this.friends = friends;
        this.password = password;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getPicture() {
        return picture;
    }

    public DateCustom getBirthday() {
        return birthday;
    }

    public List<User> getFriends() {
        return friends;
    }

    public String getPassword() {
        return password;
    }
    public List<String> getRoles() {
        return roles;
    }
    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setCountry(String country) {
        this.country = country;
        return this;
    }

    public User setPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public User setBirthday(DateCustom birthday) {
        this.birthday = birthday;
        return this;
    }

    public User setFriends(List<User> friends) {
        this.friends = friends;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
    public User setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(country, user.country) && Objects.equals(picture, user.picture) && Objects.equals(birthday, user.birthday) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, country, picture, birthday, friends);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("name='" + name + "'")
                .add("country='" + country + "'")
                .add("picture='" + picture + "'")
                .add("birthday=" + birthday)
                .add("friends=" + friends)
                .toString();
    }
}
