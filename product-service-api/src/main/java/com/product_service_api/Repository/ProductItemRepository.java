package com.product_service_api.Repository;

import com.product_service_api.Entity.ProductItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    List<ProductItem> findAllByProductIdIn(List<Long> productIds, Pageable pageable);
}

