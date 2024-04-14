package com.example.backend.controller;

import com.example.backend.payload.request.ReqLogin;
import com.example.backend.payload.request.ReqAgent;
import com.example.backend.service.agentService.AgentServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AgentServiceImpl agentService;

    @PostMapping("/login/public")
    public HttpEntity<?> loginAgent(@RequestBody ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        return agentService.loginAgent(reqLogin, response);
    }

    @PostMapping("/register/user")
    public HttpEntity<?> registerAgent(@RequestBody ReqAgent reqAgent) {
        return agentService.registerAgent(reqAgent);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updateUser(@PathVariable UUID id, @RequestBody ReqAgent user) {
        return agentService.updateUser(id, user);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        return agentService.deleteUser(id);
    }

}
