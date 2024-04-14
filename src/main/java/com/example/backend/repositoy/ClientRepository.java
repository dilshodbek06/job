package com.example.backend.repositoy;

import com.example.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Client findByUserPhone(String phone);
}
