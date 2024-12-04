package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "User",
        description = "Representación completa de un usuario con sus datos y amigos"
)
public class User {

    @Id
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    @Schema(
            description = "Correo electrónico del usuario, que actúa como identificador único",
            example = "example@example.com",
            required = true
    )
    private String email;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    @Schema(
            description = "Nombre completo del usuario",
            example = "Juan Pérez",
            required = true
    )
    private String name;

    @Schema(
            description = "País de residencia del usuario",
            example = "España"
    )
    private String country;

    @Schema(
            description = "URL de la imagen de perfil del usuario",
            example = "https://example.com/profile.jpg"
    )
    private String picture;

    @Schema(
            description = "Fecha de nacimiento del usuario en un formato personalizado",
            example = "20-05-1990"
    )
    private DateCustom birthday;

    @Schema(
            description = "Lista de amigos del usuario, cada uno representado con sus datos básicos (nombre y email)",
            example = "[{\"email\": \"amigo1@example.com\", \"name\": \"Amigo Uno\"}, {\"email\": \"amigo2@example.com\", \"name\": \"Amigo Dos\"}]"
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "email")
    @JsonIdentityReference(alwaysAsId = true)
    private List<User> friends;

    @Schema(
            description = "Contraseña encriptada del usuario. No se muestra en las respuestas",
            example = "encrypted_password"
    )
    @JsonIgnore
    private String password;

    @Schema(
            description = "Roles asignados al usuario, como ADMIN o USER",
            example = "[\"USER\", \"ADMIN\"]"
    )
    private List<String> roles;

    // Constructor por defecto
    public User() {}

    // Constructor completo
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

    // Getters
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getPicture() { return picture; }
    public DateCustom getBirthday() { return birthday; }
    public List<User> getFriends() { return friends; }
    public String getPassword() { return password; }
    public List<String> getRoles() { return roles; }

    // Setters
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

    // Métodos de comparación y hash
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(name, user.name) &&
                Objects.equals(country, user.country) &&
                Objects.equals(picture, user.picture) &&
                Objects.equals(birthday, user.birthday) &&
                Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, country, picture, birthday, friends);
    }

    // toString
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
