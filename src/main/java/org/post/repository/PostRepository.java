package org.post.repository;

import org.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("select p.id from #{#entityName} p")
    List<UUID> getAllIds();

    @Query("SELECT p from Post p JOIN User u on u.id = p.user.id where u.id = :userId")
    List<Post> getPostsByUserId(UUID userId);
}
