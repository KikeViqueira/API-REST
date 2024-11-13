package com.example.proyectoparte1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "DateCustom",
        description = "Representación personalizada de una fecha con día, mes y año"
)
public class DateCustom {

    @Schema(
            description = "Día del mes",
            example = "25",
            minimum = "1",
            maximum = "31"
    )
    private Integer day;

    @Schema(
            description = "Mes del año",
            example = "12",
            minimum = "1",
            maximum = "12"
    )
    private Integer month;

    @Schema(
            description = "Año de la fecha",
            example = "2024",
            minimum = "1900",
            maximum = "2100"
    )
    private Integer year;

    // Constructor por defecto
    public DateCustom() {}

    // Constructor completo
    public DateCustom(Integer day, Integer month, Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Getters y setters con estilo encadenado
    public Integer getDay() { return day; }
    public DateCustom setDay(Integer day) { this.day = day; return this; }
    public Integer getMonth() { return month; }
    public DateCustom setMonth(Integer month) { this.month = month; return this; }
    public Integer getYear() { return year; }
    public DateCustom setYear(Integer year) { this.year = year; return this; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateCustom that = (DateCustom) o;
        return Objects.equals(day, that.day) && Objects.equals(month, that.month) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    // toString
    @Override
    public String toString() {
        return new StringJoiner(", ", DateCustom.class.getSimpleName() + "[", "]")
                .add("day=" + day)
                .add("month=" + month)
                .add("year=" + year)
                .toString();
    }
}
