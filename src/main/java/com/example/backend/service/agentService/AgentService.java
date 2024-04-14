package com.example.backend.service.agentService;

import com.example.backend.payload.request.ReqLogin;
import com.example.backend.payload.request.ReqAgent;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.UUID;

public interface AgentService {
    HttpEntity<?> loginAgent(ReqLogin reqLogin, HttpServletResponse response) throws IOException;
    HttpEntity<?> registerAgent(ReqAgent reqAgent);
    HttpEntity<?> updateUser(UUID id, ReqAgent user);
    HttpEntity<?> deleteUser(UUID id);
}
