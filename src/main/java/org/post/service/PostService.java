package org.post.service;

import lombok.extern.log4j.Log4j2;
import org.post.dao.PostDAO;
import org.post.dto.PostDTO;
import org.post.mapper.PostMapper;
import org.post.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

@Service
@Log4j2
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostMapper postMapper;

    public Collection<PostDTO> posts() {
        log.info("Get all posts");
        return postDTOs(postDAO.posts());
    }

    public PostDTO get(UUID id) {
        log.info("Get post: {}", id);
        return postMapper.toPostDTO(postDAO.get(id));
    }

    public PostDTO post(PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        UUID id = UUID.randomUUID();
        post.setId(id);
        log.info("Adding new post with id: {}", id);
        postDAO.post(post);
        return postMapper.toPostDTO(post);
    }

    public PostDTO delete(UUID id) {
        log.info("Deleting post with id: {}", id);
        Post post = postDAO.delete(id);

        PostDTO postDTO = PostDTO.builder().build();
        if(post != null) {
            postDTO = postMapper.toPostDTO(post);
            postDTO.setMessage("post deleted");
        } else {
            postDTO.setMessage("No post available for id " + id);
        }

        return postDTO;
    }

    public PostDTO update(PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        Post updatedPost = postDAO.update(post);
        if(updatedPost == null) {
            postDTO.setError("Could not find existing post for the id: " + postDTO.getId());
            return postDTO;
        }
        log.info("Updating post with id: {}", post.getId());
        PostDTO updatedPostDTO = postMapper.toPostDTO(updatedPost);
        updatedPostDTO.setMessage("Post updated");
        return updatedPostDTO;
    }

    public PostDTO getRandomPost() {
        Random random = new Random();
        int index = (int)random.nextLong(postDAO.size());
        UUID postId = postDAO.getPostId(index);
        log.info("Get random post for id: {}", postId);
        return postMapper.toPostDTO(postDAO.get(postId));
    }

    public PostDTO addRandomPost() {
        Random random = new Random();
        String title = generateRandomSentences(random.nextInt(5, 16));
        String description = generateRandomSentences(random.nextInt(10,41));
        String username = generateRandomSentences(random.nextInt(2));
        Post post = Post.of(title, description);
        log.info("Adding new post with id: {}", post.getId());
        postDAO.post(post);
        return postMapper.toPostDTO(post);
    }

    private String generateRandomSentences(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        for(int i = 0; i < wordCount; i++) {
            sentence.append(generateRandomWord() + " ");
        }
        return sentence.toString().trim();
    }

    private String generateRandomWord() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        int wordLength = random.nextInt(5, 15);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < wordLength; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }

    private Collection<PostDTO> postDTOs(Collection<Post> posts) {
        return posts.stream().map(post -> postMapper.toPostDTO(post)).toList();
    }
}
