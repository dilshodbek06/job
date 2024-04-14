package com.example.backend.controller;

import com.example.backend.payload.request.ReqAgent;
import com.example.backend.payload.request.ReqLogin;
import com.example.backend.security.JwtService;
import com.example.backend.service.authService.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @GetMapping("/get/me")
    public HttpEntity<?> getCurrentUser(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization");
        return jwtService.getMe(token, response);
    }

    @GetMapping("/users")
    public HttpEntity<?> getUsers(@RequestParam(defaultValue = "") String search) {
        return authService.getUsers(search);
    }

    @PostMapping("/login/client/public")
    public HttpEntity<?> loginClient(@RequestBody ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        return authService.loginClient(reqLogin, response);
    }

    @PostMapping("/login/public")
    public HttpEntity<?> login(@RequestBody ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        System.out.println("keldi");
        return authService.login(reqLogin, response);
    }

    @PostMapping("/register/client/user")
    public HttpEntity<?> registerClient(@RequestBody ReqAgent reqAgent) {
        return authService.registerClient(reqAgent);
    }

    @PostMapping("/register/agent/user")
    public HttpEntity<?> registerAgent(@RequestBody ReqAgent reqAgent) {
        return authService.registerAgent(reqAgent);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updateUser(@PathVariable UUID id, @RequestBody ReqAgent user) {
        return authService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        return authService.deleteUser(id);
    }

}
