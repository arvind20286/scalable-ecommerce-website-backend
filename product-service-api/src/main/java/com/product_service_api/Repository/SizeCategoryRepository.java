package com.product_service_api.Repository;

import com.product_service_api.Entity.SizeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeCategoryRepository extends JpaRepository<SizeCategory, Long> {
    boolean existsByCategoryName(String categoryName);
}
