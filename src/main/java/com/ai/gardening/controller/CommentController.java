package com.ai.gardening.controller;

import com.ai.gardening.dto.CreateCommentRequest;
import com.ai.gardening.dto.CreateCommentResponse;
import com.ai.gardening.dto.UpdateCommentRequest;
import com.ai.gardening.entity.Comment;
import com.ai.gardening.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @PostMapping(path = "/create")
    public ResponseEntity<CreateCommentResponse> createComment(@RequestBody @Valid CreateCommentRequest createCommentRequest, @RequestHeader("Authorization") String token) {
        return commentService.createComment(createCommentRequest, token);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> updateComment(@RequestBody @Valid UpdateCommentRequest updateCommentRequest, @RequestHeader("Authorization") String token) {
        return commentService.updateComment(updateCommentRequest, token);
    }

    @GetMapping(path = "/find-all")
    public List<Comment> findAllComments(Long postId) {
        return commentService.findAllComments(postId);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteComment(Long commentId, @RequestHeader("Authorization") String token) {
        return commentService.deleteComment(commentId, token);
    }
}
