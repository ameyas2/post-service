package org.post.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
public class User {
    private UUID id;
    private String firstName;
    private String lastname;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Post> posts = new HashSet<>();

    public static User of(String firstName, String lastName, String username) {
        return User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .firstName(firstName)
                .lastname(lastName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
