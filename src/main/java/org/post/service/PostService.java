package org.post.service;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.instancio.Instancio;
import org.instancio.Select;
import org.post.dao.PostDAO;
import org.post.http.UserServiceHTTP;
import org.posts.dto.PostDTO;
import org.posts.dto.UserDTO;
import org.posts.mapper.PostMapper;
import org.posts.mapper.UserMapper;
import org.posts.model.AbstractEntity;
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
            return PostDTO.builder().message("No post exist for the id : " + id).build();
        }
        Post post = postOptional.get();
        User user = post.getUser();
        post.setUser(user);
        return postMapper.toPostDTO(post);
    }

    public PostDTO savePost(PostDTO postDTO) {
        Post post = postMapper.toPost(postDTO);
        UserDTO userDTO = userServiceHTTP.getUserById(postDTO.getUserDTO().getId());
        User user = userMapper.toUser(userDTO);
        post.setUser(user);
        postDAO.savePost(post);
        log.info("Added new post with id: {}", post.getId());
        return postMapper.toPostDTO(post);
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
        log.info("Get random post");
        return postMapper.toPostDTO(postDAO.getAnyPost().orElse(null));
    }

    public PostDTO addRandomPost() {
        Post post = Instancio.of(Post.class)
                .generate(Select.field("title"),
                        gen -> gen.string().minLength(50).mixedCase())
                .generate(Select.field("description"),
                        gen -> gen.string().minLength(50).mixedCase())
                .ignore(Select.field("user"))
                .ignore(Select.field(AbstractEntity.class, "id"))
                .ignore(Select.field(AbstractEntity.class, "createdAt"))
                .ignore(Select.field(AbstractEntity.class, "updatedAt"))
                .create();
        post.setUser(getuser());
        postDAO.savePost(post);
        log.info("Added new post with id: {}", post.getId());
        return postMapper.toPostDTO(post);
    }

    private void updatePost(Post existingPost, Post newPost) {
        existingPost.setTitle(newPost.getTitle());
        existingPost.setDescription(newPost.getDescription());
    }

    private User getuser() {
        UserDTO userDTO = userServiceHTTP.getRandomUser();
        return userMapper.toUser(userDTO);
    }

    private Collection<PostDTO> postDTOs(Collection<Post> posts) {
        return posts.stream().map(post -> postMapper.toPostDTO(post)).toList();
    }
}
