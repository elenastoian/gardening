package com.ai.gardening.controller;

import com.ai.gardening.dtos.AddPostRequest;
import com.ai.gardening.dtos.AddPostResponse;
import com.ai.gardening.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/post")
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @PostMapping(path = "/create")
    public ResponseEntity<AddPostResponse> addPost(@RequestBody AddPostRequest addPostRequest) {
        return postService.addPost(addPostRequest);
    }
}
