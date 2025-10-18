package com.github.triprooty.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping("/me")
    public String me(@AuthenticationPrincipal UserDetails me) {
        return "hello, " + me.getUsername();
    }
}
