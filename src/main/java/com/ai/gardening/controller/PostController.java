package com.ai.gardening.controller;

import com.ai.gardening.dtos.AddPostRequest;
import com.ai.gardening.dtos.AddPostResponse;
import com.ai.gardening.dtos.DeletePostRequest;
import com.ai.gardening.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/post")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @PostMapping(path = "/create")
    public ResponseEntity<AddPostResponse> addPost(@RequestBody AddPostRequest addPostRequest, @RequestHeader("Authorization") String token) {
        return postService.addPost(addPostRequest, token);
    }

    @DeleteMapping( path = "/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId")long postId, @RequestHeader("Authorization") String token) {
        return postService.deletePost(postId, token);
    }
}
