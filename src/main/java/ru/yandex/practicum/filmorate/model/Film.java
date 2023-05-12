package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    private int id;

    @NotEmpty(message = "Name cannot be null or empty")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Size(max = 200, message = "Description length too long")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private long duration;
}
