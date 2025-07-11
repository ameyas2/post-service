package org.post.controller;

import lombok.extern.log4j.Log4j2;
import org.post.service.PostService;
import org.posts.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public ResponseEntity<Collection<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Collection<PostDTO>> getPostsByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @PostMapping("/")
    public ResponseEntity<PostDTO> savePost(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.savePost(postDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

    @PutMapping("/")
    public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(postDTO));
    }

    @GetMapping("/load")
    public ResponseEntity<PostDTO> getRandomPost() {
        return ResponseEntity.ok(postService.getRandomPost());
    }

    @PostMapping("/load")
    public ResponseEntity<PostDTO> addRandomPost() {
        return ResponseEntity.ok(postService.addRandomPost());
    }
}
