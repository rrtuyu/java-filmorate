package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Rating {

    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String description;

}
