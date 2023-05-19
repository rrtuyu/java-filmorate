package ru.yandex.practicum.filmorate.model;


import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder.Default
    private Set<Integer> usersWhoLikedFilm = new HashSet<>();

    public void addLike(Integer userId) {
        this.usersWhoLikedFilm.add(userId);
    }

    public void removeLike(Integer userId) {
        this.usersWhoLikedFilm.remove(userId);
    }
}
