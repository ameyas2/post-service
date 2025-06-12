package org.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {
    private UUID id;
    private String title;
    private String description;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Post of(String title, String description, String username) {
        return Post.builder()
                .id(UUID.randomUUID())
                .username(username)
                .description(description)
                .title(title)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
