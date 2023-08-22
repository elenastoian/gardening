package com.ai.gardening.controller;

import com.ai.gardening.dto.CreateCommentRequest;
import com.ai.gardening.dto.CreateCommentResponse;
import com.ai.gardening.dto.UpdateCommentRequest;
import com.ai.gardening.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void findAllComments() {

    }

    @DeleteMapping(path = "/delete")
    public void deleteComment() {

    }
}
