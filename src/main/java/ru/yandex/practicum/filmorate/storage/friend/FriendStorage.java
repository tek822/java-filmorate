package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendStorage {

    public void addFriend(int userId, int friendId);

    public void deleteFriend(int userId, int friendId);

    public Set<Integer> getFriends(int id);

    public Set<Integer> getCommonFriends(int user1Id, int users2id);
}
