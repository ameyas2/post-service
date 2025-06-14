package org.post.controller;

import lombok.extern.log4j.Log4j2;
import org.post.dto.PostDTO;
import org.post.service.PostService;
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
    public ResponseEntity<Collection<PostDTO>> posts() {
        return ResponseEntity.ok(postService.posts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> get(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @PostMapping("/")
    public ResponseEntity<PostDTO> post(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.post(postDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> delete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(postService.delete(id));
    }

    @PutMapping("/")
    public ResponseEntity<PostDTO> update(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.update(postDTO));
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
