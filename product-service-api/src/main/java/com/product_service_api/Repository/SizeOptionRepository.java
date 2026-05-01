package com.product_service_api.Repository;

import com.product_service_api.Entity.SizeCategory;
import com.product_service_api.Entity.SizeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeOptionRepository extends JpaRepository<SizeOption, Long> {
    boolean existsBySizeNameAndSizeCategoryId(String sizeName, Long sizeCategoryId);
}
