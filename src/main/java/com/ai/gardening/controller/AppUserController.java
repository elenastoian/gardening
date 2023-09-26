package com.ai.gardening.controller;

import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class AppUserController {
    private AppUserService appUserService;



}
