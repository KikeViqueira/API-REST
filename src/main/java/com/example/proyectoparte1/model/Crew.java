package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "Crew",
        description = "Representación de un miembro del equipo de producción de una película, con información sobre su rol"
)
public class Crew extends Person {

    @Schema(
            description = "Rol o trabajo desempeñado por el miembro del equipo en la producción de la película",
            example = "Director"
    )
    private String job;

    // Constructor por defecto
    public Crew() {}

    // Getter y Setter con estilo encadenado
    public String getJob() { return job; }
    public Crew setJob(String job) { this.job = job; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return Objects.equals(job, crew.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(job);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", Crew.class.getSimpleName() + "[", "]")
                .add("job='" + job + "'")
                .toString();
    }
}
