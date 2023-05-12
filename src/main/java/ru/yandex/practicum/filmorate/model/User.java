package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @NotEmpty
    @Email
    @EqualsAndHashCode.Include
    private String email;

    @NotEmpty(message = "Login cannot be null or empty")
    @NotBlank(message = "Login cannot be blank")
    private String login;

    private int id;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
