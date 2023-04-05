package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("FriendInMemoryStorage")
public class FriendInMemoryStorage implements FriendStorage {
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friendId);
        } else {
            HashSet<Integer> set = new HashSet<>();
            set.add(friendId);
            friends.put(userId, set);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
    }

    @Override
    public Set<Integer> getFriends(int id) {
        if (friends.containsKey(id)) {
            return friends.get(id);
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public Set<Integer> getCommonFriends(int user1Id, int user2Id) {
        Set<Integer> friends1 = getFriends(user1Id);
        Set<Integer> friends2 = getFriends(user2Id);
        Set<Integer> common = friends1.stream().filter(friends2::contains).collect(Collectors.toSet());
        return common;
    }
}
