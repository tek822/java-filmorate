package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Qualifier("LikeInMemoryStorage")
public class LikeInMemoryStorage implements LikeStorage {
    final private Map<Integer, Set<Integer>> likes = new HashMap<>();

    public void addLike(int fid, int uid) {
        if (likes.containsKey(fid)) {
            likes.get(fid).add(uid);
        } else {
            HashSet<Integer> set = new HashSet<>();
            set.add(uid);
            likes.put(fid, set);
        }
    }

    public void deleteLike(int fid, int uid) {
        if (likes.containsKey(fid)) {
            likes.get(fid).remove(uid);
        }
    }

    public Set<Integer> getLikes(int fid) {
        if (likes.containsKey(fid)) {
            return likes.get(fid);
        } else {
            return new HashSet<>();
        }
    }

    @Override
    public Map<Integer, Integer> getMostPopular(int amount) {
        Map<Integer, Integer> newMap = new HashMap<>();
            likes.entrySet().stream()
                .map(e -> (Map.entry(e.getKey(),e.getValue().size())))
                .sorted((o1,o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(amount)
                .map(e->newMap.put(e.getKey(), e.getValue()))
                .close();
        return newMap;
    }
}
