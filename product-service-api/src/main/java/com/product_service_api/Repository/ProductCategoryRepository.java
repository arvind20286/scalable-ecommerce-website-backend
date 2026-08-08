package com.product_service_api.Repository;

import com.product_service_api.Entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
     boolean existsByCategory(String category);

     List<ProductCategory> findAllByParentCategoryIsNull();
}
