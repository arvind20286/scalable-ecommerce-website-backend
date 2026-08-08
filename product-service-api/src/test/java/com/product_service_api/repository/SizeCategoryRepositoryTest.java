package com.product_service_api.repository;

import com.product_service_api.Entity.SizeCategory;
import com.product_service_api.Repository.SizeCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SizeCategoryRepositoryTest {

    @Autowired
    private SizeCategoryRepository sizeCategoryRepository;

    @Test
    public void testExistsByCategoryName() {
        // given
        SizeCategory sizeCategory = new SizeCategory();
        sizeCategory.setCategoryName("Shoes");
        sizeCategoryRepository.save(sizeCategory);

        // when
        boolean actual = sizeCategoryRepository.existsByCategoryName("Shoes");
        boolean notExists = sizeCategoryRepository.existsByCategoryName("Shirts");

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }
}
