package org.post.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.post.dao.PostDAO;
import org.post.dao.UserDAO;
import org.post.dto.PostDTO;
import org.post.dto.UserDTO;
import org.post.mapper.PostMapper;
import org.post.mapper.UserMapper;
import org.post.model.Post;
import org.post.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Log4j2
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserMapper userMapper;

    private List<String> names;

    @PostConstruct
    private void init() throws IOException {
        names = Files.readAllLines(Paths.get("src/main/resources/names.csv"));
    }

    public Collection<UserDTO> getAllUsers() {
        log.info("Get all users");
        return userDTOS(userDAO.getAllUsers());
    }

    public UserDTO getUserById(UUID id) {
        log.info("Get user: {}", id);
        return userMapper.toUserDTO(userDAO.getUserById(id));
    }

    public UserDTO addUser(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        log.info("Adding new user with id: {}", id);
        userDAO.addUser(user);
        return userMapper.toUserDTO(user);
    }

    public UserDTO deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        User user = userDAO.deleteUser(id);

        UserDTO userDTO = UserDTO.builder().build();
        if(user != null) {
            userDTO = userMapper.toUserDTO(user);
            userDTO.setMessage("user deleted");
        } else {
            userDTO.setMessage("No post available for id " + id);
        }

        return userDTO;
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userDAO.updateUser(user);
        if(updatedUser == null) {
            userDTO.setError("Could not find existing user for the id: " + userDTO.getId());
            return userDTO;
        }
        log.info("Updating user with id: {}", user.getId());
        UserDTO updatedUserDTO = userMapper.toUserDTO(updatedUser);
        updatedUserDTO.setMessage("User updated");
        return updatedUserDTO;
    }

    public UserDTO getRandomUser() {
        Random random = new Random();
        int index = (int)random.nextLong(userDAO.size());
        UUID userId = userDAO.getUserId(index);
        log.info("Get random user for id: {}", userId);
        return userMapper.toUserDTO(userDAO.getUserById(userId));
    }

    public UserDTO addRandomUser() {
        String name[] = names.stream().skip(names.isEmpty() ? 0 : new Random().nextInt(names.size()))
                .findFirst().get().split(",");
        String firstName = name[0];
        String lastName = name[1];
        String username = firstName.toLowerCase().charAt(0) + lastName.toLowerCase();
        User user = User.of(firstName, lastName, username);
        log.info("Adding new user with id: {}", user.getId());
        userDAO.addUser(user);
        return userMapper.toUserDTO(user);
    }



    private Collection<UserDTO> userDTOS(Collection<User> users) {
        return users.stream().map(post -> userMapper.toUserDTO(post)).toList();
    }
}
