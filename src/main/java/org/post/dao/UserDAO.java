package org.post.dao;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.post.model.Post;
import org.post.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Log4j2
public class UserDAO {

    private Map<UUID, User> userMap;
    private List<UUID> userIDs;

    @PostConstruct
    public void init() {
        userMap = new HashMap<>();
        User u1 = User.of("joe", "buddy", "jbuddy");
        User u2 = User.of("Samantha", "Karney", "skarney");
        User u3 = User.of("Ameya", "Sawant", "asawant");
        userMap.put(u1.getId(), u1);
        userMap.put(u2.getId(), u2);
        userMap.put(u3.getId(), u3);
        userIDs = new ArrayList<>(userMap.keySet().stream().toList());
    }

    public Collection<User> getAllUsers() {
        log.debug("Getting all users");
        return userMap.values().stream().toList();
    }

    public User getUserById(UUID id) {
        log.debug("Get user: {}", id);
        return userMap.get(id);
    }

    public User addUser(User user) {
        userIDs.add(user.getId());
        log.debug("Adding new user with id: {}", user.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    public User deleteUser(UUID id) {
        User user = userMap.get(id);
        if(user == null) {
            log.info("No user available for the id : {}", id);
            return null;
        }

        userMap.remove(id);
        deleteUserId(id);
        return user;
    }

    public User updateUser(User user) {
        User existingUser = userMap.get(user.getId());
        if(existingUser == null) {
            log.info("No user available for the id : {}", user.getId());
            return null;
        }
        update(existingUser, user);
        userMap.put(existingUser.getId(), existingUser);
        return user;
    }

    private void update(User existingUser, User newUser) {
        existingUser.setLastname(newUser.getLastname());
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setUsername(newUser.getUsername());
    }

    public long size() {
        return userMap.size();
    }

    public UUID getUserId(int index) {
        return userIDs.get(index);
    }

    public void addUserId(UUID id) {
        userIDs.add(id);
    }

    public boolean deleteUserId(UUID id) {
        return userIDs.remove(id);
    }
}
