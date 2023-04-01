package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    final private Map<Integer, Boolean> friends = new HashMap<>();

    public void addFriend(int friendId, boolean status) {
        friends.put(friendId, status);
    }

    public Map<Integer, Boolean> getFriends() {
        return friends;
    }

    public  Boolean deleteFriend(int id) {
        return friends.remove(id);
    }
}
