package com.ai.gardening.service;

import com.ai.gardening.dtos.AddPostRequest;
import com.ai.gardening.dtos.AddPostResponse;
import com.ai.gardening.dtos.UpdatePostRequest;
import com.ai.gardening.dtos.UpdatePostResponse;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.entity.Post;
import com.ai.gardening.entity.Token;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import com.ai.gardening.repository.PostRepository;
import com.ai.gardening.service.security.TokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final AppUserRepository appUserRepository;
    private final ChannelRepository channelRepository; //TODO: change it with channelService
    private final ChannelService channelService;
    private final TokenService tokenService;
    private final AppUserService appUserService;

    @Transactional
    public ResponseEntity<AddPostResponse> addPost(AddPostRequest addPostRequest, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);
        Optional<Channel> channel = channelRepository.findById(addPostRequest.getChannelId());

        if (appUser != null && channel.isPresent()) {
            Post newPost = Post.builder()
                    .title(addPostRequest.getTitle())
                    .description(addPostRequest.getDescription())
                    .owner(appUser)
                    .channel(channel.get())
                    .build();
            postRepository.save(newPost);
            LOGGER.info("New post was added.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddPostResponse(newPost.getTitle(),
                    newPost.getDescription(), appUser.getName(), channel.get().getName()));
        }

        LOGGER.info("The user or channel was not found. No post was created.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddPostResponse());
    }

    public List<Post> findAllPosts(long channelId) {
       Channel channel = channelService.findChannelById(channelId);

       if (channel.getId() != null) {
           return postRepository.findAllByChannelId(channelId);
       }

       return Collections.emptyList();
    }

    public ResponseEntity<UpdatePostResponse> updatePost(UpdatePostRequest updatePostRequest, String token) {
        Post post = findPostById(updatePostRequest.getPostId());

        if(post.getId() != null && (isAppUserTheOwner(post.getOwner(), token))) {

                post.setTitle(updatePostRequest.getTitle());
                post.setDescription(updatePostRequest.getDescription());
                postRepository.save(post);
                LOGGER.info("The post was updated.");
                return ResponseEntity.status(HttpStatus.OK).body(new UpdatePostResponse(post.getTitle(), post.getDescription()));

        }

        LOGGER.info("The post was not found nor updated.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UpdatePostResponse());
    }

    public ResponseEntity<String> deletePost(long postId, String token) {

        Optional<AppUser> appUserOptional = appUserRepository.findByPostId(postId);

        Optional<Post> postOptional = postRepository.findById(postId);

        if (appUserOptional.isPresent() && postOptional.isPresent()) {

            if (isAppUserTheOwner(appUserOptional.get(), token)) {
                postRepository.delete(postOptional.get());

                LOGGER.info("The post with id {} was deleted by user with id {}", postId, appUserOptional.get().getId());
                return ResponseEntity.status(HttpStatus.OK).body("The post was deleted.");
            }

            LOGGER.info("A post is trying to be deleted by a non-admin user with id {}.", appUserOptional.get().getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the admin of the post you want to delete.");
        }

        LOGGER.info("No user or post was found for deletion.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid app user or post.");
    }

    public Post findPostById(long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        return postOptional.orElse(new Post());
    }

    private boolean isAppUserTheOwner(AppUser appUser, String token) {
        token = token.substring(7);
        Optional<Token> tokenOptional = tokenService.findByTokenAndUser(appUser, token);

        if (tokenOptional.isPresent()) {
            LOGGER.info("User with id {} is the admin of this post.", appUser.getId());
            return true;
        }

        LOGGER.info("User with id {} is the admin of this post.", appUser.getId());
        return false;
    }
}
