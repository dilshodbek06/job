package com.example.backend.repositoy;

import com.example.backend.entity.Agent;
import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Agent findByUserPhone(String phone);

    Agent findByCategoryId(UUID id);
}
