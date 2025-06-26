package org.post.mapper;

import org.mapstruct.Mapper;
import org.post.dto.PostDTO;
import org.post.dto.UserDTO;
import org.post.model.Post;
import org.post.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);
}
