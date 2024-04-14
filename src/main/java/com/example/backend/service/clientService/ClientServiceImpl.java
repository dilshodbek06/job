package com.example.backend.service.clientService;

import com.example.backend.entity.*;
import com.example.backend.payload.request.ReqAgent;
import com.example.backend.payload.request.ReqClient;
import com.example.backend.payload.request.ReqLogin;
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
public class ClientServiceImpl implements ClientService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HttpEntity<?> loginClient(ReqLogin reqLogin, HttpServletResponse response) throws IOException {
        System.out.println(reqLogin);
        User user = userRepository.findByPhone(reqLogin.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        System.out.println(user);
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
                Client client = clientRepository.findByUserPhone(reqLogin.getPhone());

                stringMap.put("categoryId", String.valueOf(client.getCategory().getId()));
            } else {
                access_token = jwtService.generateJWT(user, null);
                stringMap.put("access_token", access_token);
            }
            return ResponseEntity.ok(stringMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json");
            response.getWriter().write("Wrong phone number or password!");
            response.getWriter().close();
            return null;
        }
    }

    @Override
    public HttpEntity<?> registerClient(ReqClient reqClient) {
        if (LocalDateTime.now().isAfter(reqClient.getExpiration_date().toLocalDateTime())) {
            return ResponseEntity.ok().body("userga berilayotgan vaqt hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        Category category = categoryRepository.findById(UUID.fromString(reqClient.getCategory_id())).orElseThrow(null);
        List<Role> roles = new ArrayList<>();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPhone(reqClient.getPhone());
        user.setPassword((passwordEncoder.encode(reqClient.getPassword())));
        Role role = roleRepository.findById(2).orElseThrow(null);
        roles.add(role);
        user.setRoles(roles);
        user.setExpiration_date(reqClient.getExpiration_date());
        Optional<User> checkUser = userRepository.findByPhone(reqClient.getPhone());
        if (checkUser.isPresent()) {
            return ResponseEntity.ok("telefon nomer band");
        }
        User saved = userRepository.save(user);
        Client client = new Client();
        client.setUser(saved);
        client.setCategory(category);
        clientRepository.save(client);
        return ResponseEntity.ok(saved);
    }

    @Override
    public HttpEntity<?> updateUser(UUID id, ReqClient user) {
        if (user.getExpiration_date().before(Timestamp.valueOf(LocalDateTime.now()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("o'zgartirilayotgan userning vaqti hozirgi vaqtdan keyinda bo'lishi kerak!");
        }
        User findedUser = userRepository.findById(id).orElseThrow(null);
        Category category = categoryRepository.findById(UUID.fromString(user.getCategory_id())).orElseThrow(null);
        findedUser.setPhone(user.getPhone());
        if (!user.getPassword().equals("")) {
            findedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Client client = clientRepository.findByUserPhone(findedUser.getPhone());
        client.setCategory(category);
        client.setUser(findedUser);
        findedUser.setExpiration_date(user.getExpiration_date());
        userRepository.save(findedUser);
        clientRepository.save(client);
        return ResponseEntity.ok("Success");
    }

    @Override
    public HttpEntity<?> deleteUser(UUID id) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            Client client = clientRepository.findByUserPhone(user.getPhone());
            clientRepository.deleteById(client.getId());
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("successful deleted");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("error ");
        }
    }
}
