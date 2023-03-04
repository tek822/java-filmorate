package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public Set<Integer> getFriends() {
        return friends;
    }

    public boolean deleteFriend(int id) {
        return friends.remove(id);
    }
}
