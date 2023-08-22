package com.ai.gardening.service;

import com.ai.gardening.dto.CreateCommentRequest;
import com.ai.gardening.dto.CreateCommentResponse;
import com.ai.gardening.entity.*;
import com.ai.gardening.repository.CommentRepository;
import com.ai.gardening.service.security.TokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private CommentRepository commentRepository;
    private TokenService tokenService;
    private AppUserService appUserService;
    private ChannelService channelService;
    private PostService postService;

    @Transactional
    public ResponseEntity<CreateCommentResponse> createComment(CreateCommentRequest createCommentRequest, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser != null) {
            boolean isAppUserMemberOfChannel = channelService.findChannelByJoinedAppUser(appUser);

            if (isAppUserMemberOfChannel) {
                Post post = postService.findPostById(createCommentRequest.getPostId());

                if (post.getId() == null) {
                    LOGGER.info("The post was not found. No comment was added.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateCommentResponse());
                }

                Comment comment = Comment.builder()
                        .comment(createCommentRequest.getComment())
                        .owner(appUser)
                        .post(post)
                        .build();
                commentRepository.save(comment);
                LOGGER.info("New comment was added by {}", appUser.getName());

                return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCommentResponse(createCommentRequest.getComment(), appUser.getName(), post));
            }

            LOGGER.info("The user has not joined the channel. No comment was added.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateCommentResponse());
        }

        LOGGER.info("The user that wants to add a comment was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateCommentResponse());
    }

    private boolean isAppUserTheOwner(AppUser appUser, String token) {
        token = token.substring(7);
        Optional<Token> tokenOptional = tokenService.findByTokenAndUser(appUser, token);

        if (tokenOptional.isPresent()) {
            LOGGER.info("User with id {} is the admin of this comment.", appUser.getId());
            return true;
        }

        LOGGER.info("User with id {} is not the admin of this comment.", appUser.getId());
        return false;
    }
}
