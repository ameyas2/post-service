package org.post.dao;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.post.model.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Log4j2
public class PostDAO {

    private Map<UUID, Post> postsMap;
    private List<UUID> postIDs;

    @PostConstruct
    public void init() {
        postsMap = new HashMap<>();
        Post p1 = Post.of("post 1", "post 1 desc");
        Post p2 = Post.of("post 2", "post 2 desc");
        Post p3 = Post.of("post 3", "post 3 desc");
        postsMap.put(p1.getId(), p1);
        postsMap.put(p2.getId(), p2);
        postsMap.put(p3.getId(), p3);
        postIDs = new ArrayList<>(postsMap.keySet().stream().toList());
    }

    public Collection<Post> posts() {
        log.debug("Getting all posts");
        return postsMap.values().stream().toList();
    }

    public Post get(UUID id) {
        log.debug("Get post: {}", id);
        return postsMap.get(id);
    }

    public Post post(Post post) {
        postIDs.add(post.getId());
        log.debug("Adding new post with id: {}", post.getId());
        postsMap.put(post.getId(), post);
        return post;
    }

    public Post delete(UUID id) {
        Post post = postsMap.get(id);
        if(post == null) {
            log.info("No post available for the id : {}", id);
            return null;
        }

        postsMap.remove(id);
        deletePostId(id);
        return post;
    }

    public Post update(Post post) {
        Post existingPost = postsMap.get(post.getId());
        if(existingPost == null) {
            log.info("No post available for the id : {}", post.getId());
            return null;
        }
        update(existingPost, post);
        postsMap.put(existingPost.getId(), existingPost);
        return post;
    }

    private void update(Post existingPost, Post newPost) {
        existingPost.setTitle(newPost.getTitle());
        existingPost.setDescription(newPost.getDescription());
        existingPost.setUpdatedAt(LocalDateTime.now());
    }

    public long size() {
        return postsMap.size();
    }

    public UUID getPostId(int index) {
        return postIDs.get(index);
    }

    public void addPostId(UUID id) {
        postIDs.add(id);
    }

    public boolean deletePostId(UUID id) {
        return postIDs.remove(id);
    }
}
