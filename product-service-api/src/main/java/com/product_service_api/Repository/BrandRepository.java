package com.product_service_api.Repository;

import com.product_service_api.Entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    public boolean existsByBrandName(String name);
}
