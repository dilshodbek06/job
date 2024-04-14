package com.example.backend.controller;

import com.example.backend.payload.request.ReqClient;
import com.example.backend.payload.request.ReqLogin;
import com.example.backend.payload.request.ReqAgent;
import com.example.backend.service.clientService.ClientService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/auth/client")
@RequiredArgsConstructor
public class ClientController{
    private final ClientService clientService;

    @PostMapping("/login/public")
    public HttpEntity<?> loginClient(@RequestBody ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        return clientService.loginClient(reqLogin, response);
    }

    @PostMapping("/register/user")
    public HttpEntity<?> registerClient(@RequestBody ReqClient reqClient) {
        return clientService.registerClient(reqClient);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updateUser(@PathVariable UUID id, @RequestBody ReqClient user) {
        return clientService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        return clientService.deleteUser(id);
    }

}
