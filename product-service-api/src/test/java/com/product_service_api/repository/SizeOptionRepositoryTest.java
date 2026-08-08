package com.product_service_api.repository;

import com.product_service_api.Entity.SizeCategory;
import com.product_service_api.Entity.SizeOption;
import com.product_service_api.Repository.SizeCategoryRepository;
import com.product_service_api.Repository.SizeOptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SizeOptionRepositoryTest {

    @Autowired
    private SizeOptionRepository sizeOptionRepository;

    @Autowired
    private SizeCategoryRepository sizeCategoryRepository;

    @Test
    public void testExistsBySizeNameAndSizeCategoryId() {
        // given
        SizeCategory sizeCategory = new SizeCategory();
        sizeCategory.setCategoryName("Shoes");
        sizeCategoryRepository.save(sizeCategory);

        SizeOption sizeOption = new SizeOption();
        sizeOption.setSizeName("9");
        sizeOption.setSizeCategory(sizeCategory);
        sizeOptionRepository.save(sizeOption);

        // when
        boolean actual = sizeOptionRepository.existsBySizeNameAndSizeCategoryId("9", sizeCategory.getId());
        boolean notExists = sizeOptionRepository.existsBySizeNameAndSizeCategoryId("10", sizeCategory.getId());

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }
}
