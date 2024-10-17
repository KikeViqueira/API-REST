package com.example.proyectoparte1.model;

public interface MovieSummary {
    String getId();
    String getTitle();
    String getOverview();
    String getGenres();
    DateCustom getReleaseDate();  // Si `releaseDate` es una clase personalizada
    String getResources();
}
