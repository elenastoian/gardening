package com.ai.gardening.service;

import com.ai.gardening.dto.CreateCommentRequest;
import com.ai.gardening.dto.CreateCommentResponse;
import com.ai.gardening.dto.UpdateCommentRequest;
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

import java.util.List;
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

                return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCommentResponse(createCommentRequest.getComment(), appUser.getName(), post.getId()));
            }

            LOGGER.info("The user has not joined the channel. No comment was added.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateCommentResponse());
        }

        LOGGER.info("The user that wants to add a comment was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateCommentResponse());
    }

    public ResponseEntity<String> updateComment(UpdateCommentRequest updateCommentRequest, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser != null) {

            Optional<Comment> comment = commentRepository.findById(updateCommentRequest.getCommentId());

            if (comment.isPresent() && comment.get().getOwner().equals(appUser)) {

                comment.get().setComment(updateCommentRequest.getComment());
                commentRepository.save(comment.get());

                LOGGER.info("The comment was updated by {}.", appUser.getName());
                return ResponseEntity.status(HttpStatus.OK).body("The comment was updated with:\n" + updateCommentRequest.getComment());
            }
            LOGGER.info("The user is not the owner of the comment.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user is not the owner of the comment.");
        }
        LOGGER.info("The user that update the add a comment was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user was not found.");
    }

    public List<Comment> findAllComments(Long postId) {

        List<Comment> commentList = commentRepository.findAllByPostId(postId);

        LOGGER.info("The user is not the owner of the comment.");
        return commentRepository.findAllByPostId(postId);
    }

    public ResponseEntity<String> deleteComment(Long commentId, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser != null && isAppUserTheOwner(appUser, token)) {
            commentRepository.deleteById(commentId);

            return ResponseEntity.status(HttpStatus.OK).body("The comment was deleted.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The comment was deleted because the owner was not found.");
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
