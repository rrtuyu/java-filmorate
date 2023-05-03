package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @NotEmpty
    @Email
    private final String email;

    @NotEmpty(message = "Login cannot be null or empty")
    @NotBlank(message = "Login cannot be blank")
    @EqualsAndHashCode.Exclude
    private final String login;

    @EqualsAndHashCode.Exclude
    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    @PastOrPresent
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
}
