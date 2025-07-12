package org.post.service;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.post.dao.PostDAO;
import org.post.http.UserServiceHTTP;
import org.posts.dto.PostDTO;
import org.posts.dto.UserDTO;
import org.posts.mapper.PostMapper;
import org.posts.mapper.UserMapper;
import org.posts.model.Post;
import org.posts.model.PostsEventInfo;
import org.posts.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.List;

@Service
@Log4j2
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceHTTP userServiceHTTP;

    @Autowired
    private KafkaTemplate<String, PostsEventInfo> kafkaTemplate;

    public Collection<PostDTO> getAllPosts() {
        log.info("Get all posts");
        PostsEventInfo postsEventInfo = PostsEventInfo.builder().event("Getting all posts").build();
        kafkaTemplate.send(new ProducerRecord<>("posts-events", String.valueOf(UUID.randomUUID()), postsEventInfo));
        return postDTOs(postDAO.getAllPosts());
    }

    public PostDTO getPostById(UUID id) {
        log.info("Get post: {}", id);
        Optional<Post> postOptional = postDAO.getPostById(id);
        if(postOptional.isEmpty()) {
            log.info("No post exist for the id : {}", id);
            PostDTO.builder().message("No post exist for the id : " + id).build();
        }
        Post post = postOptional.get();
        User user = post.getUser();
        PostDTO postDTO = postMapper.toPostDTO(post);
        postDTO.setUserDTO(userMapper.toUserDTO(user));
        return postDTO;
    }

    public PostDTO savePost(PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        UserDTO userDTO = userServiceHTTP.getUserById(postDTO.getUserDTO().getId());
        User user = userMapper.toUser(userDTO);
        post.setUser(user);
        postDAO.savePost(post);
        log.info("Added new post with id: {}", post.getId());
        postDTO = postMapper.toPostDTO(post);
        postDTO.setUserDTO(userDTO);
        return postDTO;
    }

    public PostDTO deletePost(UUID id) {
        log.info("Deleting post with id: {}", id);

        if(postDAO.exists(id)) {
            postDAO.deletePostById(id);
            return PostDTO.builder().message("Post deleted with id " + id).build();
        } else {
            return PostDTO.builder().message("Post not exists with id " + id).build();
        }
    }

    public PostDTO updatePost(PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        Optional<Post> oldPost = postDAO.getPostById(post.getId());
        if(oldPost.isEmpty()) {
            log.info("No post available for the id : {}", post.getId());
            return null;
        }
        Post updatedPost = oldPost.get();
        updatePost(updatedPost, post);
        log.info("Updating post with id: {}", updatedPost.getId());
        postDAO.savePost(updatedPost);
        PostDTO updatedPostDTO = postMapper.toPostDTO(updatedPost);
        updatedPostDTO.setMessage("Post updated");
        return updatedPostDTO;
    }

    public Collection<PostDTO> getPostsByUserId(UUID userId) {
        List<Post> posts = postDAO.getPostsByUserId(userId);
        return postDTOs(posts);
    }

    public PostDTO getRandomPost() {
        Random random = new Random();
        int index = (int)random.nextLong(postDAO.size());
        UUID postId = postDAO.getPostId(index);
        log.info("Get random post for id: {}", postId);
        Optional<Post> postOptional = postDAO.getPostById(postId);
        if(postOptional.isEmpty()) {
            log.info("No post exist for the id : {}", postId);
            PostDTO.builder().message("No post exist for the id : " + postId).build();
        }
        Post post = postOptional.get();
        PostDTO postDTO = postMapper.toPostDTO(post);
        postDTO.setUserDTO(userMapper.toUserDTO(post.getUser()));
        return postDTO;
    }

    public PostDTO addRandomPost() {
        Random random = new Random();
        String title = generateRandomSentences(random.nextInt(5, 16));
        String description = generateRandomSentences(random.nextInt(10,41));
        //String username = generateRandomSentences(random.nextInt(2));
        Post post = Post.of(title, description);
        UserDTO userDTO = userServiceHTTP.getRandomUser();
        User user = userMapper.toUser(userDTO);
        post.setUser(user);
        postDAO.savePost(post);
        log.info("Added new post with id: {}", post.getId());
        return postMapper.toPostDTO(post);
    }

    private void updatePost(Post existingPost, Post newPost) {
        existingPost.setTitle(newPost.getTitle());
        existingPost.setDescription(newPost.getDescription());
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
