package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder.Default
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer id) {
        this.friends.add(id);
    }

    public void removeFriend(Integer id) {
        this.friends.remove(id);
    }
}
