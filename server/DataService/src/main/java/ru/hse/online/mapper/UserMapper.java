package ru.hse.online.mapper;

import ru.hse.online.model.User;
import ru.hse.online.storage.UserData;

import java.util.Collections;

public class UserMapper {

    public static User toModel(UserData userData) {
        if (userData == null) {
            return null;
        }

        return User.builder()
                .userId(userData.getUserId())
                .username(userData.getUsername())
                .email(userData.getEmail())
                .friends(userData.getFriends() != null ? userData.getFriends() : Collections.emptyList())
                .build();
    }

    public static UserData toData(User user) {
        if (user == null) {
            return null;
        }

        return UserData.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .friends(user.getFriends())
                .build();
    }
}
