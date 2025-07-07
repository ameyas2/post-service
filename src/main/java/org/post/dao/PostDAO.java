package org.post.dao;

import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.post.repository.PostRepository;
import org.posts.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Log4j2
public class PostDAO {

    private List<UUID> postIDs;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void init() {
        postIDs = hazelcastInstance.getList("post_ids");
        if(postIDs.isEmpty()) {
            List<UUID> ids = postRepository.getAllIds();
            postIDs.addAll(ids);
        }
        log.info("Total post ids loaded : {}", postIDs.size());
    }

    public Collection<Post> getAllPosts() {
        log.debug("Getting all posts");
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(UUID id) {
        log.debug("Get post: {}", id);
        return postRepository.findById(id);
    }

    public Post savePost(Post post) {
        log.debug("Adding new post with id: {}", post.getId());
        postRepository.save(post);
        postIDs.add(post.getId());
        return post;
    }

    public void deletePostById(UUID id) {
        log.debug("Deleting post with id: {}", id);
        postRepository.deleteById(id);
        postIDs.remove(id);
    }

    public boolean exists(UUID id) {
        return postRepository.existsById(id);
    }

    public long size() {
        return postRepository.count();
    }

    public UUID getPostId(int index) {
        return postIDs.get(index);
    }

    public void addPostId(UUID id) {
        postIDs.add(id);
    }


}
