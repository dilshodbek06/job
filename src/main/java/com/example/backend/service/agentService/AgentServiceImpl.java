package com.example.backend.service.agentService;

import com.example.backend.entity.Agent;
import com.example.backend.entity.Category;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.payload.request.ReqLogin;
import com.example.backend.payload.request.ReqAgent;
import com.example.backend.repositoy.*;
import com.example.backend.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public HttpEntity<?> loginAgent(ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        User user = userRepository.findByPhone(reqLogin.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String access_token;
        HashMap<String, String> stringMap = new HashMap<>();
        if (user.getRoles().get(0).getName().equals("ROLE_CLIENT") || user.getRoles().get(0).getName().equals("ROLE_AGENT")) {
            if (LocalDateTime.now().isAfter(user.getExpiration_date().toLocalDateTime())) {
                return ResponseEntity.ok().body("kechirasiz siz ushbu login parol bilan autentifikatsiya qilish vaqtingiz tugagan!");
            }
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            reqLogin.getPhone(),
                            reqLogin.getPassword()
                    )
            );

            if (user.getExpiration_date() != null) {
                access_token = jwtService.generateJWT(user, user.getExpiration_date());
                stringMap.put("access_token", access_token);
                Agent agent = agentRepository.findByUserPhone(reqLogin.getPhone());
                stringMap.put("categoryId", String.valueOf(agent.getCategory().getId()));
            } else {
                access_token = jwtService.generateJWT(user, null);
                stringMap.put("access_token", access_token);
            }
            return ResponseEntity.ok(stringMap);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json");
            response.getWriter().write("Wrong phone number or password!");
            response.getWriter().close();
            return null;
        }
    }

    @Override
    public HttpEntity<?> registerAgent(ReqAgent reqAgent) {
        if (LocalDateTime.now().isAfter(reqAgent.getExpiration_date().toLocalDateTime())) {
            return ResponseEntity.ok().body("userga berilayotgan vaqt hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        Category category = categoryRepository.findById(UUID.fromString(reqAgent.getCategory_id())).orElseThrow(null);
        List<Role> roles = new ArrayList<>();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPhone(reqAgent.getPhone());
        user.setPassword((passwordEncoder.encode(reqAgent.getPassword())));
        Role role = roleRepository.findById(3).orElseThrow(null);
        roles.add(role);
        user.setRoles(roles);
        user.setExpiration_date(reqAgent.getExpiration_date());
        Optional<User> checkUser = userRepository.findByPhone(reqAgent.getPhone());
        if (checkUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("telefon nomer band");
        }

        Agent checkAgentCategory = agentRepository.findByCategoryId(category.getId());
        if (checkAgentCategory != null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("kategoriya band");
        }
        User saved = userRepository.save(user);
        Agent agent = new Agent();
        agent.setUser(saved);
        agent.setChatId(reqAgent.getChatId());
        agent.setTelegram_token(reqAgent.getTelegram_token());
        agent.setCategory(category);
        agentRepository.save(agent);
        return ResponseEntity.ok("Qo'shildi");
    }


    @Override
    public HttpEntity<?> updateUser(UUID id, ReqAgent user) {
        if (user.getExpiration_date().before(Timestamp.valueOf(LocalDateTime.now()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("o'zgartirilayotgan userning vaqti hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        User findedUser = userRepository.findById(id).orElseThrow(null);
        Category category = categoryRepository.findById(UUID.fromString(user.getCategory_id())).orElseThrow(null);
        findedUser.setPhone(user.getPhone());
        if (!user.getPassword().equals("")) {
            findedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        findedUser.setExpiration_date(user.getExpiration_date());
        Agent agent = agentRepository.findByUserPhone(findedUser.getPhone());
        Agent checkAgentCategory = agentRepository.findByCategoryId(UUID.fromString(user.getCategory_id()));
        if (checkAgentCategory!=null){
            return ResponseEntity.ok("kategoriya band");
        }
        agent.setCategory(category);
        agent.setUser(findedUser);
        agent.setChatId(user.getChatId());
        agent.setTelegram_token(user.getTelegram_token());
        agentRepository.save(agent);
        userRepository.save(findedUser);
        return ResponseEntity.ok("Success");
    }

    @Override
    public HttpEntity<?> deleteUser(UUID id) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            Agent agent = agentRepository.findByUserPhone(user.getPhone());
            agentRepository.deleteById(agent.getId());
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("successful deleted");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("error ");
        }
    }
}
