package com.ai.gardening.controller;

import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class AppUserController {
    private AppUserService appUserService;
    private AppUserRepository appUserRepository;


}
