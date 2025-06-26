package org.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.post.model.Post;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastname;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
    private String error;
    private Set<PostDTO> posts = new HashSet<>();

    public static UserDTO of(String firstName, String lastName, String username) {
        return UserDTO.builder()
                .id(UUID.randomUUID())
                .username(username)
                .firstName(firstName)
                .lastname(lastName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
