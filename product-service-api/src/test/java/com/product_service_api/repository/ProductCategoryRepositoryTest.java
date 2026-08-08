package com.product_service_api.repository;

import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Repository.ProductCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest

public class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testExistsByCategory() {
        // given
        ProductCategory category = new ProductCategory();
        category.setCategory("Electronics");
        productCategoryRepository.save(category);

        // when
        boolean actual = productCategoryRepository.existsByCategory("Electronics");
        boolean notExists = productCategoryRepository.existsByCategory("Clothing");

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testFindAllByParentCategoryIsNull() {
        // given
        ProductCategory parentCategory = new ProductCategory();
        parentCategory.setCategory("Parent");
        productCategoryRepository.save(parentCategory);

        ProductCategory childCategory = new ProductCategory();
        childCategory.setCategory("Child");
        childCategory.setParentCategory(parentCategory);
        productCategoryRepository.save(childCategory);

        // when
        List<ProductCategory> rootCategories = productCategoryRepository.findAllByParentCategoryIsNull();

        // then
        assertThat(rootCategories).hasSize(1);
        assertThat(rootCategories.get(0).getCategory()).isEqualTo("Parent");
    }
}
