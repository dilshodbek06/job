package com.example.backend.service.clientService;

import com.example.backend.payload.request.ReqAgent;
import com.example.backend.payload.request.ReqClient;
import com.example.backend.payload.request.ReqLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.UUID;

public interface ClientService {
    HttpEntity<?> loginClient(ReqLogin reqLogin, HttpServletResponse response) throws IOException;
    HttpEntity<?> registerClient(ReqClient reqClient);
    HttpEntity<?> updateUser(UUID id, ReqClient user);
    HttpEntity<?> deleteUser(UUID id);
}
