package ru.yandex.practicum.filmorate.storage.like;

import org.hibernate.validator.constraints.ru.INN;
import org.springframework.data.relational.core.sql.In;

import java.util.Map;
import java.util.Set;

public interface LikeStorage {

    public void addLike(int fid, int uid);

    public void deleteLike(int fid, int uid);

    public Set<Integer> getLikes(int fid);

    public Map<Integer, Integer> getMostPopular(int fid);
}
