package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotEmpty(message = "Name cannot be null or empty")
    @NotBlank(message = "Name cannot be blank")
    @EqualsAndHashCode.Exclude
    private String name;

    @Size(max=200, message = "Description length too long")
    @EqualsAndHashCode.Exclude
    private String description;

    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;

    @Positive
    @EqualsAndHashCode.Exclude
    private long duration;
}
