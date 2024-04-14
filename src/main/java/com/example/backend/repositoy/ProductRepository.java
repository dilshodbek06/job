package com.example.backend.repositoy;

import com.example.backend.entity.Product;
import com.example.backend.projection.ProductProjection;
import com.example.backend.projection.ProductProjection2;
import com.example.backend.projection.TelegramUserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = """
            SELECT p.*, pc.id AS podCategoryId, pc.title AS podCategoryTitle, a.id AS attachmentId
            FROM products p
                     JOIN pod_category pc ON p.pod_category_id = pc.id
                     JOIN attachment a on a.id = p.photo_id
            WHERE (p.pod_category_id IN :podCategoryIds OR (:podCategoryIds IS NULL))
            ORDER BY p.ordered;
            """,
            nativeQuery = true)
    Page<ProductProjection> findAllByTitleAndDescriptionAndPodCategoryIds(
            @Param("podCategoryIds") List<UUID> podCategoryIds,
            Pageable pageable
    );

    @Query(value = """
            SELECT p.*
                            FROM products p
                                     INNER JOIN pod_category pc ON pc.id = p.pod_category_id
                                     WHERE p.pod_category_id=:podCategoryId ORDER BY p.ordered;
            """, nativeQuery = true)
    List<ProductProjection2> getProductsForFrontSelect(UUID podCategoryId);


    @Query(value = """
            SELECT p.*, pc.id AS podCategoryId, pc.title AS podCategoryTitle, a.id AS attachmentId
            FROM products p
                     JOIN attachment a on a.id = p.photo_id
                     JOIN pod_category pc ON p.pod_category_id = pc.id ORDER BY p.ordered;
            """, nativeQuery = true)
    Page<ProductProjection> getAllProducts(Pageable pageable);

    @Query(value = """
                SELECT p.*
                FROM products p
                         INNER JOIN pod_category pc ON pc.id = p.pod_category_id
                         WHERE p.pod_category_id=:podCategoryId ORDER BY p.ordered;
            """, nativeQuery = true)
    List<Product> getAllProductsByPodCategoryId(UUID podCategoryId);

    @Query(value = """
                SELECT p.*
                            FROM products p
                                     INNER JOIN pod_category pc ON pc.id = p.pod_category_id
                                     WHERE p.pod_category_id=:podCategoryId ORDER BY p.ordered DESC;
            """, nativeQuery = true)
    List<Product> forMe(UUID podCategoryId);

    @Query(value = """
            SELECT a.telegram_token, a.chat_id
              from categories c
                       inner join pod_category pc on c.id = pc.category_id
                       inner join products p on pc.id = p.pod_category_id
                       inner join agent a on c.id = a.category_id
              where c.id = :categoryId
              group by a.telegram_token, a.chat_id
            """, nativeQuery = true)
    TelegramUserProjection getTelegramUserProjectionForOrder(UUID categoryId);
}
