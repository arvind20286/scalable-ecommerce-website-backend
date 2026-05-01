package com.product_service_api.Repository;

import com.product_service_api.Entity.AttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {
    boolean existsByNameAndAttributeTypeId(String name, Long attributeTypeId);
}
