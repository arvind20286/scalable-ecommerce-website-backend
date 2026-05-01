package com.product_service_api.Repository;

import com.product_service_api.Entity.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeTypeRepository extends JpaRepository<AttributeType, Long> {
    boolean existsByAttributeName(String attributeName);
}
