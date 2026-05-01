package com.product_service_api.Repository;

import com.product_service_api.Entity.Colour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColourRepository extends JpaRepository<Colour, Long> {
    boolean existsByColourNameIgnoreCase(String colourName);
}
