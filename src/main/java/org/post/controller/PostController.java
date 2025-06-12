package org.post.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.post.dto.PostDTO;
import org.post.mapper.PostMapper;
import org.post.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

    private Map<UUID, Post> postsMap;

    @Autowired
    private PostMapper postMapper;

    @PostConstruct
    public void init() {
        postsMap = new HashMap<>();
        Post p1 = Post.of("post 1", "post 1 desc", "john");
        Post p2 = Post.of("post 2", "post 2 desc", "jane");
        Post p3 = Post.of("post 3", "post 3 desc", "rachel");
        postsMap.put(p1.getId(), p1);
        postsMap.put(p2.getId(), p2);
        postsMap.put(p3.getId(), p3);
    }

    @GetMapping("/")
    public ResponseEntity<Collection<PostDTO>> posts() {
        log.info("Get all posts");
        return ResponseEntity.ok(postDTOs(postsMap.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> get(@PathVariable("id") UUID id) {
        log.info("Get post: {}", id);
        return ResponseEntity.ok(postMapper.toPostDTO(postsMap.get(id)));
    }

    @PostMapping("/")
    public ResponseEntity<PostDTO> post(@RequestBody PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        UUID id = UUID.randomUUID();
        post.setId(id);
        log.info("Adding new post with id: {}", id);
        return ResponseEntity.ok(postMapper.toPostDTO(postsMap.put(id, post)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> delete(@PathVariable("id") UUID id) {
        log.info("Deleting post with id: {}", id);
        return ResponseEntity.ok(postMapper.toPostDTO(postsMap.remove(id)));
    }

    @PutMapping("/")
    public ResponseEntity<PostDTO> update(@RequestBody PostDTO postDTO) {
        Post oldPost = postsMap.get(postDTO.getId());
        if(oldPost == null) {
            postDTO.setError("Existing post not found");
            return ResponseEntity.ok(postDTO);
        }
        log.info("Updating post with id: {}", oldPost.getId());
        Post newPost = postMapper.toPost(postDTO);
        newPost.setId(oldPost.getId());
        postsMap.remove(newPost.getId());
        return ResponseEntity.ok(postMapper.toPostDTO(postsMap.put(newPost.getId(), newPost)));
    }

    private Collection<PostDTO> postDTOs(Collection<Post> posts) {
        return posts.stream().map(post -> postMapper.toPostDTO(post)).toList();
    }
}
