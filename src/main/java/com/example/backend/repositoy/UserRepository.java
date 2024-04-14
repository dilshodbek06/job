package com.example.backend.repositoy;

import com.example.backend.entity.User;
import com.example.backend.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhone(String phone);

    @Query(value = """
            SELECT DISTINCT u.id,
                           u.phone,
                           u.expiration_date,
                           coalesce(a.category_id, c.category_id) AS category_id
            FROM users u
                    LEFT JOIN
                agent a ON u.id = a.user_id
                    LEFT JOIN
                client c ON u.id = c.user_id; 
            """,
            nativeQuery = true)
    Optional<List<UserProjection>> findUsersByRoles(@Param("roles") List<String> roles);

    @Query(value = """
               SELECT DISTINCT u.id,
                     u.phone,
                     u.expiration_date,
                     coalesce(a.category_id, c.category_id) AS category_id
                FROM users u
                      LEFT JOIN
                  agent a ON u.id = a.user_id
                      LEFT JOIN
                  client c ON u.id = c.user_id
                     INNER JOIN public.users_roles ur on u.id = ur.user_id
                      INNER JOIN public.role r on r.id = ur.roles_id
                WHERE u.phone LIKE concat('%',:search,'%')
                AND r.name IN ('ROLE_CLIENT','ROLE_AGENT');
            """,
            nativeQuery = true)
    Optional<List<UserProjection>> findUsersByRolesAndPhone(@Param("search") String search);


}
