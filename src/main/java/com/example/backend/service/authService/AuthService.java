package com.example.backend.service.authService;

import com.example.backend.payload.request.ReqAgent;
import com.example.backend.payload.request.ReqLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.UUID;

public interface AuthService {

    HttpEntity<?> getUsers(String search);

    HttpEntity<?> loginClient(ReqLogin reqLogin, HttpServletResponse response) throws IOException;

    HttpEntity<?> login(ReqLogin reqLogin, HttpServletResponse response) throws IOException;

    HttpEntity<?> registerClient(ReqAgent reqAgent);

    HttpEntity<?> registerAgent(ReqAgent reqAgent);

    HttpEntity<?> updateUser(UUID id, ReqAgent user);

    HttpEntity<?> deleteUser(UUID id);

}
