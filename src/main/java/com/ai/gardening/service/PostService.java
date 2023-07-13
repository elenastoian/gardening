package com.ai.gardening.service;

import com.ai.gardening.dtos.AddPostRequest;
import com.ai.gardening.dtos.AddPostResponse;
import com.ai.gardening.dtos.DeletePostRequest;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.entity.Post;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import com.ai.gardening.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private PostRepository postRepository;
    private AppUserRepository appUserRepository;
    private ChannelRepository channelRepository;

    public ResponseEntity<AddPostResponse> addPost(AddPostRequest addPostRequest) {
        Optional<AppUser> admin = appUserRepository.findById(addPostRequest.getAdminId());
        Optional<Channel> channel = channelRepository.findById(addPostRequest.getChannelId());

        if (admin.isPresent() && channel.isPresent()) {
            Post newPost = Post.builder()
                    .title(addPostRequest.getTitle())
                    .description(addPostRequest.getDescription())
                    .admin(admin.get())
                    .channel(channel.get())
                    .build();
            postRepository.save(newPost);
            LOGGER.info("New post was added.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddPostResponse(newPost.getTitle(), newPost.getDescription(), admin
                    .get().getName(), channel.get().getName()));
        }

        LOGGER.info("The user or channel was not found. No post was created.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddPostResponse());
    }

    public ResponseEntity<String> deletePost(DeletePostRequest deletePostRequest) {
        Optional<AppUser> admin = appUserRepository.findById(deletePostRequest.getAppUserId());
        Optional<Post> post = postRepository.findById(deletePostRequest.getPostId());

        if (admin.isPresent() && post.isPresent()) {
            if (post.get().getAdmin().equals(admin.get())) {
                postRepository.delete(post.get());
                LOGGER.info("The post with id {} was deleted by user with id {}", deletePostRequest.getPostId(), deletePostRequest.getAppUserId());
                return ResponseEntity.status(HttpStatus.OK).body("The post was deleted.");
            }

            LOGGER.info("A post is trying to be deleted by a non-admin user with id {}.", deletePostRequest.getAppUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not the admin of the post you want to delete.");
        }

        LOGGER.info("No user or post was found for deletion.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid app user or post.");
    }
}
