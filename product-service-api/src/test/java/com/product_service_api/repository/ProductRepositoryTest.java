package com.product_service_api.repository;

import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Repository.ProductCategoryRepository;
import com.product_service_api.Repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testFindAllByProductCategoryId() {
        // given
        ProductCategory category = new ProductCategory();
        category.setCategory("Electronics");
        productCategoryRepository.save(category);

        Product product = new Product();
        product.setName("Laptop");
        product.setProductCategory(category);
        productRepository.save(product);

        // when
        List<Product> products = productRepository.findAllByProductCategoryId(category.getId());

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
    }
}
