package com.ai.gardening.controller;

import com.ai.gardening.dtos.*;
import com.ai.gardening.entity.Post;
import com.ai.gardening.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/post")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @PostMapping(path = "/create")
    public ResponseEntity<AddPostResponse> createPost(@RequestBody @Valid AddPostRequest addPostRequest, @RequestHeader("Authorization") String token) {
        return postService.addPost(addPostRequest, token);
    }

    @GetMapping(path = "/get/channel/{channelId}")
    public List<Post> findAllPosts(@PathVariable("channelId")long channelId) {
        return postService.findAllPosts(channelId);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<UpdatePostResponse> updatePost(@RequestBody @Valid UpdatePostRequest updatePostRequest, @RequestHeader("Authorization") String token) {
        return postService.updatePost(updatePostRequest, token);
    }

    @DeleteMapping( path = "/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId")long postId, @RequestHeader("Authorization") String token) {
        return postService.deletePost(postId, token);
    }
}
