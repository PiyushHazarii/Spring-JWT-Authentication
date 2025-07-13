package me.learning.service;

import me.learning.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private List<User> store = new ArrayList<>();

    public UserServiceImpl() {
        store.add(new User(
                UUID.randomUUID().toString(),
                "Durgesh Tiwari",
                "durgesh@gmail.com"
        ));store.add(new User(
                UUID.randomUUID().toString(),
                "Murgesh Tiwari",
                "murgesh@gmail.com"
        ));store.add(new User(
                UUID.randomUUID().toString(),
                "Babun Tiwari",
                "babun@gmail.com"
        ));store.add(new User(
                UUID.randomUUID().toString(),
                "Mamun Tiwari",
                "mamun@gmail.com"
        ));
    }

    public List<User> getUsers() {
        return store;
    }

}
