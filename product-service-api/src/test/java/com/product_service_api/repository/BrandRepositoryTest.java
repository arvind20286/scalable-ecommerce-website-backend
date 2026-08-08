package com.product_service_api.repository;

import com.product_service_api.Entity.Brand;
import com.product_service_api.Repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testExistsByBrandName() {
        // given
        Brand brand = new Brand();
        brand.setBrandName("Nike");
        brandRepository.save(brand);

        // when
        boolean actual = brandRepository.existsByBrandName("Nike");
        boolean notExists = brandRepository.existsByBrandName("Adidas");

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }
}
