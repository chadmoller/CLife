package com.clife.identity.dao;

import com.clife.identity.domain.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class UserDao {
    public User findUser(String email) {
        Objectify objectify = ObjectifyService.ofy();

        return objectify
                .load()
                .type(User.class)
                .filter("email", email)
                .first()
                .now();
    }

    public User createUser(String email, String nickname) {
        Objectify objectify = ObjectifyService.ofy();

        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.fullName = nickname;
        objectify
                .save()
                .entity(user)
                .now();
        return user;
    }
}
